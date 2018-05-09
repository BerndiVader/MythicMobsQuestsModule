package com.gmail.berndivader.mythicmobsquests;

import java.util.Map;

import org.bukkit.entity.Player;

import me.blackvein.quests.CustomRequirement;

public class MythicMobsConditonsRequire 
extends
CustomRequirement {
	
	public MythicMobsConditonsRequire() {
		this.setName("MythicMobs Conditions Require");
		this.setAuthor("BerndiVader, idea Wahrheit");
		this.addData("Conditions");
		this.addDescription("Conditions","List of conditions to check");
	}

	@Override
	public boolean testRequirement(Player player, Map<String, Object> data) {
		return true;
	}

}