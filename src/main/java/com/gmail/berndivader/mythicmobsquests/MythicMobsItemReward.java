package com.gmail.berndivader.mythicmobsquests;

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.blackvein.quests.CustomReward;

public class MythicMobsItemReward 
extends
CustomReward 
{
	
	public MythicMobsItemReward() {
		
		this.setName("MythicMobs Item Reward");
		this.setAuthor("BerndiVader");
		this.setRewardName("MythicMobs Item Reward");
		this.addStringPrompt("RewardName","Reward message send to quester on reward (Optional)","NONE");
		this.addStringPrompt("MythicItem","Enter the item or droptable name or an array splited with ',' (Optional)",new String());
		this.addStringPrompt("Amount","How many items. Can be ranged like 1to3 (Default 1)","1");
		this.addStringPrompt("ItemMarker","Mark the item as a MythicMobs Quests item (Optional)","NONE");
		this.addStringPrompt("Stackable","true/false(default)",false);
		this.addStringPrompt("Notify","Announce recieved items true/false(default)",false);
	}

	@Override
	public void giveReward(Player player, Map<String, Object> data) {
		try {
			String[]arr1=data.get("MythicItem").toString().split(",");
			String s2=data.getOrDefault("Amount","1").toString();
			String s1=data.getOrDefault("ItemMarker","NONE").toString();
			String s3=data.getOrDefault("RewardName",null).toString();
			if (s1.equals("NONE")) s1=null;
			boolean notify=Boolean.parseBoolean(data.getOrDefault("Notify","TRUE").toString());
			boolean stackable=Boolean.parseBoolean(data.getOrDefault("Stackable","TRUE").toString());
			if(s3!=null) player.sendMessage(ChatColor.GOLD+s3);
			Utils.createAndDropItemStack(arr1,s1,Utils.randomRangeInt(s2),player,notify,stackable);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}