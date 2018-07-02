package com.gmail.berndivader.mythicmobsquests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
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
			MythicDropTable dt=null;
			if(!dropmanager.isPresent()) getDropManager();
			if(dropmanager.isPresent()) {
				dt=dropmanager.get().getDropTable(itemtype).orElse(new MythicDropTable(Arrays.asList(itemtype),null,null,null,null));
			} else {
				Bukkit.getLogger().info("DropManager wasnt present.");
				return false;
			}
			if (dt.hasConditions()) dt.conditions=new ArrayList<>();
			for (int a=0;a<amount;a++) {
				dt.parseTable(trigger);
				if (dt.getExp()>0) {
					player.giveExp(dt.getExp());
					lm.put(str_exp,lm.containsKey(str_exp)?lm.get(str_exp)+dt.getExp():dt.getExp());
				}
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
