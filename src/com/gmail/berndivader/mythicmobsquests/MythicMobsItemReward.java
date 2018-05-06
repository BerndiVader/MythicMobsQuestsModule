package com.gmail.berndivader.mythicmobsquests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
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
import io.lumine.xikage.mythicmobs.compatibility.CompatibilityManager;
import io.lumine.xikage.mythicmobs.drops.DropManager;
import io.lumine.xikage.mythicmobs.drops.MythicDropTable;
import me.blackvein.quests.CustomReward;

public class MythicMobsItemReward 
extends
CustomReward {
	
	static DropManager dropmanager;
	static String str_questitem,str_money,str_exp,str_hexp,str_mexp;
	
	static {
		dropmanager=MythicMobs.inst().getDropManager();
		str_questitem="MythicQuestItem";
		str_money="Money: ";
		str_exp="Exp: ";
		str_hexp="Heroes Exp: ";
		str_mexp="McMMO Exp: ";
	}
	
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
			if(s3!=null) player.sendMessage(ChatColor.GOLD+s3);
			createAndDropItemStack(arr1,s1,randomRangeInt(s2),player,notify);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	static void createAndDropItemStack(String[]arr1,String s1,int amount,Player p,boolean bl1) {
		World w=p.getWorld();
		AbstractEntity trigger=BukkitAdapter.adapt(p);
		HashMap<String,Integer>lm=new HashMap<>();
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
			for (int a=0;a<amount;a++) {
				dt.parseTable(trigger);
				p.giveExp(dt.getExp());
				lm.put(str_exp,lm.containsKey(str_exp)?lm.get(str_exp)+dt.getExp():dt.getExp());
				if (dt.heroesexp>0&&CompatibilityManager.Heroes!=null) {
					lm.put(str_hexp,lm.containsKey(str_hexp)?lm.get(str_hexp)+dt.getHeroesExp():dt.getHeroesExp());
					CompatibilityManager.Heroes.giveHeroesExp(null,p,dt.heroesexp);
				}
				if (dt.mcmmoexp>0&&CompatibilityManager.mcMMO!=null) {
					lm.put(str_mexp,lm.containsKey(str_mexp)?lm.get(str_mexp)+dt.getMcMMOExp():dt.getMcMMOExp());
					CompatibilityManager.mcMMO.giveExp(p,dt.getChampionsExp(),"unarmed");
				}
				if (dt.getMoney()>0&&MythicMobs.inst().getCompatibility().getVault().isPresent()) {
					lm.put(str_money,lm.containsKey(str_money)?lm.get(str_money)+(int)dt.getMoney():(int)dt.getMoney());
					MythicMobs.inst().getCompatibility().getVault().get().giveMoney(p,dt.getMoney());
				}
				for (ItemStack is:dt.getDrops()) {
					if (is==null||is.getType()==Material.AIR) continue;
					String nn=is.hasItemMeta()&&is.getItemMeta().hasDisplayName()?is.getItemMeta().getDisplayName():is.getType().toString();
					nn+=": ";
					lm.put(nn,lm.containsKey(nn)?lm.get(nn)+is.getAmount():is.getAmount());
					if (s1!=null) NMSUtils.setMeta(is,str_questitem,s1);
					if ((p.getInventory().firstEmpty())>-1) {
						p.getInventory().addItem(is.clone());
						p.updateInventory();
					} else {
						w.dropItem(p.getLocation(),is.clone());
					}
				}
			}
			if (bl1) {
				String ll=ChatColor.DARK_GREEN+"";
				Iterator<Map.Entry<String,Integer>>it=lm.entrySet().iterator();
				while(it.hasNext()) {
					Map.Entry<String,Integer>e=it.next();
					ll+=e.getKey()+e.getValue()+" ";
				}
				p.sendMessage(ll);
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