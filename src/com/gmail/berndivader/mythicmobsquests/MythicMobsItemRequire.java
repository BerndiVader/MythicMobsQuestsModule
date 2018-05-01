package com.gmail.berndivader.mythicmobsquests;

import java.util.Map;

import org.bukkit.entity.Player;

import me.blackvein.quests.CustomRequirement;

public class MythicMobsItemRequire 
extends
CustomRequirement {
	
	public MythicMobsItemRequire() {
		this.setName("MythicMobs Item Require");
		this.setAuthor("BerndiVader, idea Wahrheit");
		this.addData("Material");
		this.addDescription("Material","Item material type");
		this.addData("Name");
		this.addDescription("Name","Contains in displayname");
		this.addData("Amount");
		this.addDescription("Amount","How many items. Can be ranged like 1to3");
	}

	@Override
	public boolean testRequirement(Player player, Map<String, Object> data) {
		
		return false;
	}

}