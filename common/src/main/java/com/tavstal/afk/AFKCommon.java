package com.tavstal.afk;

import java.time.LocalDateTime;
import java.util.Dictionary;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Hashtable;

import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import com.tavstal.afk.commands.AFKCommand;
import com.tavstal.afk.models.PlayerData;
import com.tavstal.afk.utils.EntityUtils;
import com.tavstal.afk.utils.ModUtils;
import com.tavstal.afk.utils.PlayerUtils;
import com.tavstal.afk.utils.WorldUtils;

import net.minecraft.ChatFormatting;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;

// This class is part of the common project meaning it is shared between all supported loaders. Code written here can only
// import and access the vanilla codebase, libraries used by vanilla, and optionally third party libraries that provide
// common compatible binaries. This means common code can not directly use loader specific concepts such as Forge events
// however it will be compatible with all supported mod loaders.
public class AFKCommon {

    private static String _lastWorldSleepReset;
    public static String GetLastWorldSleepReset() {
        return _lastWorldSleepReset;
    }
    //#region Player Data
	private static Dictionary<String, PlayerData> _playerDataList = new Hashtable<>();
    public static Dictionary<String, PlayerData>  GetPlayerDataList() {
        return _playerDataList;
    }
    public static void PutPlayerData(String uuid, PlayerData newData) {
        _playerDataList.put(uuid, newData);
    }
    public static PlayerData GetPlayerData(String uuid) {
        return _playerDataList.get(uuid);
    }
    public static void RemovePlayerData(String uuid) {
        _playerDataList.remove(uuid);
    }
    ////#endregion
    private static CommonConfig _config = null;
    public static CommonConfig CONFIG() {
        return _config;
    }

    public static void init(MinecraftServer server, CommonConfig config) {
        _config = config;
        if (CONFIG().EnableDebugMode) {
            SetLogLevel("DEBUG");
        }

        // Register Commands
        var dispatcher = server.getCommands().getDispatcher();
        AFKCommand.register(dispatcher);

        // Create scoreboard team
        var scoreboard = server.getScoreboard();
		if (scoreboard.getPlayerTeam("afk") == null)
		{
			PlayerTeam team = scoreboard.addPlayerTeam("afk");
            team.setPlayerPrefix(ModUtils.Literal(config.Prefix));
            team.setPlayerSuffix(ModUtils.Literal(config.Suffix));
            team.setColor(ChatFormatting.WHITE);
		}
    }

    private static void SetLogLevel(String level) {
        // Set the logging level for the logger
        LoggerContext loggerContext = (LoggerContext) LogManager.getContext(false);
        LoggerConfig loggerConfig = loggerContext.getConfiguration().getLoggerConfig(Constants.LOG.getName());
        loggerConfig.setLevel(Level.valueOf(level.toUpperCase()));
        loggerContext.updateLoggers();
    }

    public static int GetRequiredPlayersToReset(MinecraftServer server, String worldKey) {
		int playersSleeping = 0;
		int playersRequiredToResetTime = 0;

		for (var serverPlayer : server.getPlayerList().getPlayers()) {
			String playerWorld = WorldUtils.GetName(EntityUtils.GetLevel(serverPlayer));
            Constants.LOG.debug("Player World Key: {}", playerWorld);
			if (worldKey.equals(playerWorld)) {
				playersRequiredToResetTime++;
			}

			if ((serverPlayer.isSleeping() && worldKey.equals(playerWorld)) || PlayerUtils.IsAFK(serverPlayer.getStringUUID())) {
				playersSleeping++;
			}
		}

		double value = ((double)playersRequiredToResetTime / 100.0 * (double)100) - (double)playersSleeping;
		return (int)value; 
	}

    public static void ChangeAFKMode(Player player, boolean enable) {
        var uuid = player.getStringUUID();
        var playerName = EntityUtils.GetName(player);
        var data = GetPlayerData(uuid);
        if (enable) {
            if (!data.IsAFK)
            {
                var server = player.getServer();
                if (server != null) {
                    var scoreboard = server.getScoreboard();
                    scoreboard.addPlayerToTeam(playerName, scoreboard.getPlayerTeam("afk"));
                }
                else
                    Constants.LOG.error("ChangeAFKMode -> Failed to get the server.");
                ModUtils.SendChatMessage(player, "§6{0} is now AFK.", playerName);
                data.IsAFK = true;
            }

            data.LastPosition = EntityUtils.GetPosition(player);
            data.LastBlockPosition = EntityUtils.GetBlockPosition(player);
            data.HeadRotation = player.yHeadRot;
            data.Date = LocalDateTime.now();
            PutPlayerData(uuid, data);
        }
        else
        {
            if (data.IsAFK) {
                var server = player.getServer();
                if (server != null) {
                    var scoreboard = server.getScoreboard();
                    scoreboard.removePlayerFromTeam(playerName, scoreboard.getPlayerTeam("afk"));
                }
                else
                    Constants.LOG.error("ChangeAFKMode -> Failed to get the server.");

                ModUtils.SendChatMessage(player, "§6{0} is no longer AFK.", playerName);
                data.IsAFK = false;
            }

            data.LastPosition = EntityUtils.GetPosition(player);
            data.LastBlockPosition = EntityUtils.GetBlockPosition(player);
            data.HeadRotation = player.yHeadRot;
            data.Date = LocalDateTime.now();
            PutPlayerData(uuid, data);
        }
    }

    public static void WakeUp(ServerLevel world, MinecraftServer server) {
		var worldKey = WorldUtils.GetName(world);
        Constants.LOG.debug("World Key: {}", worldKey);
		ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
		executorService.schedule(() -> {
			
			if (GetRequiredPlayersToReset(server, worldKey) <= 0)
			{
				_lastWorldSleepReset = worldKey;
				if (world.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)) {
					var currentDayTime = world.getDayTime();
					world.setDayTime(currentDayTime + 24000L - currentDayTime % 24000L);
				}

				if (world.getGameRules().getBoolean(GameRules.RULE_WEATHER_CYCLE)) {
					world.setWeatherParameters(0, 0, false, false);
				}

				
				for (var serverPlayerEntity : server.getPlayerList().getPlayers()) {
					if (serverPlayerEntity.isSleeping())
                    {
                        serverPlayerEntity.stopSleepInBed(true, true);
                    }
				}
			}
		}, 3, TimeUnit.SECONDS);

		executorService.schedule(() -> {
			if (_lastWorldSleepReset.equals(worldKey))
				_lastWorldSleepReset = null;
		}, 3, TimeUnit.SECONDS);
		executorService.shutdown();
	}
}