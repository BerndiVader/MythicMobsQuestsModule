package com.gmail.berndivader.mythicmobsquests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.drops.DropManager;
import io.lumine.xikage.mythicmobs.drops.DropMetadata;
import io.lumine.xikage.mythicmobs.drops.DropTable;
import io.lumine.xikage.mythicmobs.drops.LootBag;
import io.lumine.xikage.mythicmobs.mobs.GenericCaster;
import io.lumine.xikage.mythicmobs.mobs.MobManager;
import io.lumine.xikage.mythicmobs.util.types.RangedDouble;
import me.blackvein.quests.Quests;

public class Utils {
	
	public static Optional<DropManager>dropmanager=Optional.ofNullable(null);
	public static Optional<MobManager>mobmanager=Optional.ofNullable(null);
	public static Optional<Quests>quests=Optional.ofNullable(null);
	static String str_questitem,str_money,str_exp,str_hexp,str_mexp;
	
	static {
		quests=Optional.ofNullable((Quests)Bukkit.getServer().getPluginManager().getPlugin("Quests"));
		if(Bukkit.getServer().getPluginManager().isPluginEnabled("MythicMobs")) {
			mobmanager=Optional.ofNullable(MythicMobs.inst().getMobManager());
			dropmanager=Optional.ofNullable(MythicMobs.inst().getDropManager());
		} else {
			Bukkit.getLogger().warning("Not able to get MythicMobs.");
		}
		str_questitem="MythicQuestItem";
		str_money="Money: ";
		str_exp="Exp: ";
		str_hexp="Heroes Exp: ";
		str_mexp="McMMO Exp: ";
	}
	
	public static Optional<MobManager> getMobManager() {
		if(mobmanager.isPresent()) return mobmanager;
		if(Bukkit.getServer().getPluginManager().isPluginEnabled("MythicMobs")) {
			Utils.mobmanager=Optional.ofNullable(MythicMobs.inst().getMobManager());
		}
		return mobmanager;
	}
	
	public static Optional<DropManager> getDropManager() {
		if(dropmanager.isPresent()) return dropmanager;
		if(Bukkit.getServer().getPluginManager().isPluginEnabled("MythicMobs")) {
			Utils.dropmanager=Optional.ofNullable(MythicMobs.inst().getDropManager());
		}
		return dropmanager;
	}
	
	public static Optional<Quests> getQuests() {
		if(quests.isPresent()) return quests;
		if(Bukkit.getServer().getPluginManager().isPluginEnabled("Quests")) {
			quests=Optional.ofNullable((Quests)Bukkit.getServer().getPluginManager().getPlugin("Quests"));
		}
		return quests;
	}

	public static boolean createAndDropItemStack(String[]arr1,String itemMarker,int amount,Player player,boolean notify,boolean stackable) {
		boolean bl1=false;
		HashMap<String,Integer>lm=new HashMap<>();
		List<String>nm=new ArrayList<String>();
		for(int i1=0;i1<arr1.length;i1++) {
			String itemtype;
			if (arr1[i1].contains(":")) {
				String[]arr2=arr1[i1].split(":");
				itemtype=arr2[0];
				amount=Integer.parseInt(arr2[1]);
			} else {
				itemtype=arr1[i1];
			}
			nm.add(itemtype);
			DropTable dt;
			if(!dropmanager.isPresent()) getDropManager();
			if(dropmanager.isPresent()) {
				dt=new DropTable("QuestDrop","QuestDrop",nm);
			} else {
				Bukkit.getLogger().info("DropManager wasnt present.");
				return false;
			}
			for (int a=0;a<amount;a++) {
				GenericCaster dropper=new GenericCaster(BukkitAdapter.adapt(player));
		        LootBag lb=dt.generate(new DropMetadata(dropper,dropper.getEntity()));
		        lb.drop(BukkitAdapter.adapt(player).getLocation());
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
		nm.clear();
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
	
	public static boolean chk(String[]materials,String[]itemMarker,String[]nameEnds,String lore,RangedDouble rd,ItemStack is) {
		boolean bl1=false;
		bl1=rd.equals(is.getAmount());
		bl1&=materials[0].equals("ANY")||arrContains(materials,is.getType().toString());
		bl1&=itemMarker[0].equals("NONE")||arrContains(itemMarker,NMSUtils.getMeta(is,Utils.str_questitem));
		bl1&=nameEnds[0].equals("NONE")||arrContains(nameEnds,is.hasItemMeta()&&is.getItemMeta().hasDisplayName()?is.getItemMeta().getDisplayName():"");
		if(!lore.equals("NONE")&&is.hasItemMeta()&&is.getItemMeta().hasLore()) {
			boolean bl3=false;
			for(ListIterator<String>it1=is.getItemMeta().getLore().listIterator();it1.hasNext();) {
				String str1=it1.next();
				if ((str1.contains(lore))) {
					bl3=true;
					break;
				};
			}
			bl1&=bl3;
		}
		return bl1;
	}
	
	public static boolean arrContains(String[]arr1,String s1) {
		if (s1!=null&&arr1!=null) {
			for(int i1=0;i1<arr1.length;i1++) {
				if(s1.endsWith(arr1[i1])) return true;
			}
		}
		return false;
	}
	
	static void removeItemstackAmount(boolean holdItem,Player player,RangedDouble rd,ItemStack fis,ListIterator<ItemStack>lit) {
		if (holdItem) {
			ItemStack is1=new ItemStack(player.getInventory().getItemInMainHand());
			int i1=is1.getAmount()-(int)rd.getMin();
			if (i1<=0) i1=0;
			is1.setAmount(i1);
			player.getInventory().setItemInMainHand(new ItemStack(is1));
		} else {
			ItemStack is1=new ItemStack(fis);
			int i1=is1.getAmount()-(int)rd.getMin();
			if (i1<=0) i1=0;
			is1.setAmount(i1);
			lit.set(new ItemStack(is1));;
		}
	}
	
	
}
