package com.gmail.berndivader.mythicmobsquests;

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import me.blackvein.quests.CustomReward;

public class MythicMobsItemReward 
extends
CustomReward 
implements
IDataMap
{
	
	public MythicMobsItemReward() {
		
		this.setName("MythicMobs Item Reward");
		this.setAuthor("BerndiVader");
		this.setRewardName("MythicMobs Item Reward");
		this.addDataAndDefault("RewardName","NONE");
		this.addDescription("RewardName","Reward message send to quester on reward (Optional)");
		this.addDataAndDefault("MythicItem",new String());
		this.addDescription("MythicItem","Enter the item or droptable name or an array splited with ',' (Optional)");
		this.addDataAndDefault("Amount","1");
		this.addDescription("Amount","How many items. Can be ranged like 1to3 (Default 1)");
		this.addDataAndDefault("ItemMarker","NONE");
		this.addDescription("ItemMarker","Mark the item as a MythicMobs Quests item (Optional)");
		this.addDataAndDefault("Stackable",false);
		this.addDescription("Stackable","true/false(default)");
		this.addDataAndDefault("Notify",false);
		this.addDescription("Notify","Announce recieved items true/false(default)");
	}

	@Override
	public void giveReward(Player player, Map<String, Object> data) {
		try {
			String[]arr1=data.get("MythicItem").toString().split(",");
			String s2=(String)data.getOrDefault("Amount","1");
			String s1=(String)data.getOrDefault("ItemMarker","NONE");
			String s3=(String)data.getOrDefault("RewardName",null);
			if (s1.equals("NONE")) s1=null;
			boolean notify=Boolean.parseBoolean((String)data.getOrDefault("Notify","TRUE"));
			boolean stackable=Boolean.parseBoolean((String)data.getOrDefault("Stackable","TRUE"));
			if(s3!=null) player.sendMessage(ChatColor.GOLD+s3);
			Utils.createAndDropItemStack(arr1,s1,Utils.randomRangeInt(s2),player,notify,stackable);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void addDataAndDefault(String key, Object value) {
		this.getData().put(key, value);
	}

}