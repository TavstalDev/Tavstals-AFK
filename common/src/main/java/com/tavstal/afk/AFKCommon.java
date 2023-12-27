package com.tavstal.afk;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Hashtable;

import com.mojang.brigadier.LiteralMessage;
import com.tavstal.afk.platform.Services;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

// This class is part of the common project meaning it is shared between all supported loaders. Code written here can only
// import and access the vanilla codebase, libraries used by vanilla, and optionally third party libraries that provide
// common compatible binaries. This means common code can not directly use loader specific concepts such as Forge events
// however it will be compatible with all supported mod loaders.
public class AFKCommon {

    private static List<String> AfkingPlayers = new ArrayList<String>();
    public static List<String> GetAfkingPlayers() {
        return AfkingPlayers;
    }
	public static Dictionary<String, List<String>> SleepingPlayers = new Hashtable<>();
	public static Dictionary<String, LocalDateTime> PlayerLastMovements = new Hashtable<>();
	private static String _lastWorldSleepReset;
    public static String GetLastWorldSleepReset() {
        return _lastWorldSleepReset;
    }
    private static MinecraftServer _server = null;

    // The loader specific projects are able to import and use any code from the common project. This allows you to
    // write the majority of your code here and load it from your loader specific projects. This example has some
    // code that gets invoked by the entry point of the loader specific projects.
    public static void init(MinecraftServer server, CommonConfig config) {

        _server = server;
        Constants.LOG.info("Hello from Common init on {}! we are currently in a {} environment!", Services.PLATFORM.getPlatformName(), Services.PLATFORM.isDevelopmentEnvironment() ? "development" : "production");
        if (config.EnableDebugMode)
            SetLogLevel("DEBUG");

        var scoreboard = server.getScoreboard();
		if (scoreboard.getPlayerTeam("afk") == null)
		{
			PlayerTeam team = scoreboard.addPlayerTeam("afk");
            team.setPlayerPrefix(Literal(config.Prefix));
            team.setPlayerSuffix(Literal(config.Suffix));
            team.setColor(ChatFormatting.WHITE);
		}

		for (var world : server.getAllLevels()) {
			SleepingPlayers.put(world.dimension().toString(), new ArrayList<String>());
		}
    }

    public static Component Literal(String text) {
        return net.minecraft.network.chat.ComponentUtils.fromMessage(new LiteralMessage(text));
    }

    public static void SendChatMessage(Entity entity, String text) {
        entity.sendSystemMessage(Literal(text));
    }

    public static void SendChatMessage(Entity entity, String text, Object ... args) {
        entity.sendSystemMessage(Literal(MessageFormat.format(text, args)));
    }
    
    public static int GetRequiredPlayersToReset(MinecraftServer server, String worldKey) {
		int playersSleeping = 0;
		int playersRequiredToResetTime = 0;

		for (var serverPlayer : server.getPlayerList().getPlayers()) {
			String playerWorld = serverPlayer.getLevel().dimension().toString();
            Constants.LOG.debug("Player World Key: {}", playerWorld);
			if (worldKey.equals(playerWorld)) {
				playersRequiredToResetTime++;
			}

			if ((serverPlayer.isSleeping() && worldKey.equals(playerWorld)) || AfkingPlayers.contains(serverPlayer.getStringUUID())) {
				playersSleeping++;
			}
		}

		double value = ((double)playersRequiredToResetTime / 100.0 * (double)100) - (double)playersSleeping;
		return (int)value; 
	}

    private static void SetLogLevel(String level) {
        // Set the logging level for the logger
        ((org.apache.logging.log4j.core.Logger)Constants.LOG).setLevel(org.apache.logging.log4j.Level.getLevel(level));
    }

    public static void ChangeAFKMode(Player player, boolean enable) {
        var uuid = player.getStringUUID();
        var playerName = player.getName().getString();
        if (enable) {
            if (!AfkingPlayers.contains(uuid))
            {
                AfkingPlayers.add(uuid);
                SendChatMessage(player, "§6{0} is now AFK.", playerName);
                var scoreboard = player.getServer().getScoreboard();
                scoreboard.addPlayerToTeam(playerName, scoreboard.getPlayerTeam("afk"));
            }
        }
        else
        {
            if (AfkingPlayers.contains(uuid)) {
                SendChatMessage(player, "§6{0} is no longer AFK.", playerName);
                AfkingPlayers.remove(uuid);
                var scoreboard = player.getServer().getScoreboard();
                scoreboard.removePlayerFromTeam(playerName, scoreboard.getPlayerTeam("afk"));
            }

            if (PlayerLastMovements.get(uuid) != null)
                PlayerLastMovements.remove(uuid);
            PlayerLastMovements.put(uuid, LocalDateTime.now());
        }
    }

    public static void WakeUp(ServerLevel world, MinecraftServer server) {
		var worldKey = world.dimension().toString();
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

		SleepingPlayers.get(worldKey).clear();

		executorService.schedule(() -> {
			if (_lastWorldSleepReset.equals(worldKey))
				_lastWorldSleepReset = null;
		}, 3, TimeUnit.SECONDS);
		executorService.shutdown();
	}
}