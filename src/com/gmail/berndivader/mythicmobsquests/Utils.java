package com.gmail.berndivader.mythicmobsquests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
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

public class Utils {
	
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

	public static boolean createAndDropItemStack(String[]arr1,String itemMarker,int amount,Player player,boolean notify,boolean stackable) {
		boolean bl1=false;
		World w=player.getWorld();
		AbstractEntity trigger=BukkitAdapter.adapt(player);
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
				player.giveExp(dt.getExp());
				lm.put(str_exp,lm.containsKey(str_exp)?lm.get(str_exp)+dt.getExp():dt.getExp());
				if (dt.heroesexp>0&&CompatibilityManager.Heroes!=null) {
					lm.put(str_hexp,lm.containsKey(str_hexp)?lm.get(str_hexp)+dt.getHeroesExp():dt.getHeroesExp());
					CompatibilityManager.Heroes.giveHeroesExp(null,player,dt.heroesexp);
				}
				if (dt.mcmmoexp>0&&CompatibilityManager.mcMMO!=null) {
					lm.put(str_mexp,lm.containsKey(str_mexp)?lm.get(str_mexp)+dt.getMcMMOExp():dt.getMcMMOExp());
					CompatibilityManager.mcMMO.giveExp(player,dt.getChampionsExp(),"unarmed");
				}
				if (dt.getMoney()>0&&MythicMobs.inst().getCompatibility().getVault().isPresent()) {
					lm.put(str_money,lm.containsKey(str_money)?lm.get(str_money)+(int)dt.getMoney():(int)dt.getMoney());
					MythicMobs.inst().getCompatibility().getVault().get().giveMoney(player,dt.getMoney());
				}
				for (ItemStack is:dt.getDrops()) {
					if (is==null||is.getType()==Material.AIR) continue;
					bl1=true;
					String nn=is.hasItemMeta()&&is.getItemMeta().hasDisplayName()?is.getItemMeta().getDisplayName():is.getType().toString();
					nn+=": ";
					lm.put(nn,lm.containsKey(nn)?lm.get(nn)+is.getAmount():is.getAmount());
					if (itemMarker!=null) NMSUtils.setMeta(is,str_questitem,itemMarker);
					if (!stackable) {
						UUID uuid=UUID.randomUUID();
						String most=Long.toString(uuid.getMostSignificantBits()),least=Long.toString(uuid.getLeastSignificantBits());
						NMSUtils.setMeta(is,"RandomMost",most);
						NMSUtils.setMeta(is,"RandomLeast",least);
					}
					NMSUtils.setMeta(is,"MythicMobsItem","true");
					if ((player.getInventory().firstEmpty())>-1) {
						player.getInventory().addItem(is.clone());
						player.updateInventory();
					} else {
						w.dropItem(player.getLocation(),is.clone());
					}
				}
			}
			if (notify) {
				String ll=ChatColor.DARK_GREEN+"";
				Iterator<Map.Entry<String,Integer>>it=lm.entrySet().iterator();
				while(it.hasNext()) {
					Map.Entry<String,Integer>e=it.next();
					ll+=e.getKey()+e.getValue()+" ";
				}
				player.sendMessage(ll);
			}
		}
		return bl1;
	}
	
	public static int randomRangeInt(String range) {
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
