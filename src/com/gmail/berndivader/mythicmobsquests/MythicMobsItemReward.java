package com.gmail.berndivader.mythicmobsquests;

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.blackvein.quests.CustomReward;

public class MythicMobsItemReward 
extends
CustomReward {
	
	public MythicMobsItemReward() {
		this.setName("MythicMobs Item Reward");
		this.setAuthor("BerndiVader");
		this.setRewardName("MythicMobs Item Reward");
		this.addData("RewardName");
		this.addDescription("RewardName","Add a reward description");
		this.addData("Item");
		this.addDescription("Item","Enter the item or droptable name or an array splited with ,");
		this.addData("Amount");
		this.addDescription("Amount","How many items. Can be ranged like 1to3");
		this.addData("ItemMarker");
		this.addDescription("ItemMarker","Mark the item as a MythicMobs Quests item");
		this.addData("Stackable");
		this.addDescription("Stackable","(true/false)");
		this.addData("Notify");
		this.addDescription("Notify","Announce recieved items (true/false)");
	}

	@Override
	public void giveReward(Player player, Map<String, Object> data) {
		try {
			String[]arr1=data.get("Item").toString().split(",");
			String s2=(String)data.get("Amount");
			String s1=(String)data.getOrDefault("ItemMarker",null);
			String s3=(String)data.getOrDefault("RewardName",null);
			boolean notify=Boolean.parseBoolean((String)data.getOrDefault("Notify","TRUE"));
			boolean stackable=Boolean.parseBoolean((String)data.getOrDefault("Stackable","TRUE"));
			if(s3!=null) player.sendMessage(ChatColor.GOLD+s3);
			Utils.createAndDropItemStack(arr1,s1,Utils.randomRangeInt(s2),player,notify,stackable);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}