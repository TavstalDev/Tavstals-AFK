package com.tavstal.afk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tavstal.afk.models.TeamData;

public class Constants {

	public static final String MOD_ID = "tafk";
	public static final String MOD_NAME = "Tavstals-AFK";
	public static final Logger LOG = LoggerFactory.getLogger(MOD_NAME);

	public static final TeamData TEAM_AFK = new TeamData("afk", 10);
	public static final TeamData TEAM_SLEEP = new TeamData("sleep", 1);
	// World team priority: 0
}