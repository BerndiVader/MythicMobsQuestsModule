package com.gmail.berndivader.mythicmobsquests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.drops.DropManager;
import io.lumine.xikage.mythicmobs.drops.MythicDropTable;
import me.blackvein.quests.CustomReward;

public class MythicMobsItemReward 
extends
CustomReward {
	
	static DropManager dropmanager;
	static String str_questitem;
	
	static {
		dropmanager=MythicMobs.inst().getDropManager();
		str_questitem="MythicQuestItem";
	}
	
	public MythicMobsItemReward() {
		this.setName("MythicMobs Item Reward");
		this.setAuthor("BerndiVader");
		this.setRewardName("");
		this.addData("RewardName");
		this.addDescription("RewardName","Add a reward description");
		this.addData("Item");
		this.addDescription("Item","Enter the item or droptable name or an array splited with ,");
		this.addData("Amount");
		this.addDescription("Amount","How many items. Can be ranged like 1to3");
		this.addData("ItemMarker");
		this.addDescription("ItemMarker","Mark the item as a MythicMobs Quests item");
	}

	@Override
	public void giveReward(Player player, Map<String, Object> data) {
		try {
			String[]arr1=data.get("Item").toString().split(",");
			String s2=(String)data.get("Amount");
			String s1=(String)data.getOrDefault("ItemMarker",null);
			String s3=(String)data.getOrDefault("RewardName",null);
			if(s3!=null) player.sendMessage(ChatColor.AQUA+s3);
			createAndDropItemStack(arr1,s1,randomRangeInt(s2),player);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	static void createAndDropItemStack(String[]arr1,String s1,int amount,Player p) {
		World w=p.getWorld();
		AbstractEntity trigger=BukkitAdapter.adapt(p);
		for(int i1=0;i1<arr1.length;i1++) {
			String itemtype;
			if (arr1[i1].contains(":")) {
				String[]arr2=arr1[i1].split(":");
				itemtype=arr2[0];
				amount=Integer.parseInt(arr2[1]);
			} else {
				itemtype=arr1[i1];
			}
			MythicDropTable dt=dropmanager.getDropTable(itemtype).orElse(new MythicDropTable(Arrays.asList(itemtype),null,null,null,null));
			if (dt.hasConditions()) dt.conditions=new ArrayList<>();
			dt.parseTable(trigger);
			for (int a=0;a<amount;a++) {
				for (ItemStack i:dt.getDrops()) {
					if (i==null||i.getType()==Material.AIR) continue;
					ItemStack is=i.clone();
					if (s1!=null) NMSUtils.setMeta(is,str_questitem,s1);
					if ((p.getInventory().firstEmpty())>-1) {
						p.getInventory().addItem(is);
					} else {
						w.dropItem(p.getLocation(),is);
					}
				}
			}
		}
	}
	
	static int randomRangeInt(String range) {
		ThreadLocalRandom r=ThreadLocalRandom.current();
		int amount=0;
		String[]split;
		int min,max;
		if (range.contains("to")) {
			split=range.split("to");
			min=Integer.parseInt(split[0]);
			max=Integer.parseInt(split[1]);
			if (max<min) max=min;
			amount=r.nextInt(min, max+1);
		} else amount=Integer.parseInt(range);
		return amount;
	}
}