package com.gmail.berndivader.mythicmobsquests;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

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
	
	public MythicMobsItemReward() {
		this.setName("MythicMobs Item Reward");
		this.setAuthor("BerndiVader");
		this.setRewardName("MythicMobsItem");
		this.addData("Item");
		this.addDescription("Item","Enter the item or droptable name or an array splited with ,.");
		this.addData("Amount");
		this.addDescription("Amount","How many items. Can be ranged like 1to3");
	}

	@Override
	public void giveReward(Player player, Map<String, Object> data) {
		try {
			String[] arr1=data.get("Item").toString().split(",");
			String s2=data.get("Amount").toString();
			ArrayList<ItemStack>drops=createItemStack(arr1,randomRangeInt(s2),BukkitAdapter.adapt(player));
			reward(drops,player);
		} catch (Exception ex) {
			//
		}
	}
	
	static ArrayList<ItemStack> createItemStack(String[]arr1,int amount,AbstractEntity trigger) {
		DropManager dropmanager=MythicMobs.inst().getDropManager();
		ArrayList<ItemStack>loot=new ArrayList<>();
		for(int i1=0;i1<arr1.length;i1++) {
			String itemtype;
			if (arr1[i1].contains(":")) {
				String[]arr2=arr1[i1].split(":");
				itemtype=arr2[0];
				amount=Integer.parseInt(arr2[1]);
			} else {
				itemtype=arr1[i1];
			}
			Optional<MythicDropTable>maybeDropTable=dropmanager.getDropTable(itemtype);
			List<String>droplist=new ArrayList<>();
			MythicDropTable dt;
			if (maybeDropTable.isPresent()) {
				dt=maybeDropTable.get();
			} else {
				droplist.add(itemtype);
				dt=new MythicDropTable(droplist,null,null,null,null);
			}
//			if (bl1) Collections.shuffle(dt.strDropItems);
			for (int a=0;a<amount;a++) {
				dt.parseTable(null,trigger);
				for (ItemStack is:dt.getDrops()) {
					loot.add(is);
				}
			}
		}
		return loot;
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
	
	static void reward(ArrayList<ItemStack> drops, Player p) {
		World w=p.getWorld();
		for (ItemStack is:drops) {
			if (is==null||is.getType()==Material.AIR) continue;
			if ((p.getInventory().firstEmpty())>-1) {
				p.getInventory().addItem(is.clone());
			} else {
				w.dropItem(p.getLocation(),is.clone());
			}
		}
	}	
}