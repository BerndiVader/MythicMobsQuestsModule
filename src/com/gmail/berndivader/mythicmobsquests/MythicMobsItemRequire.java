package com.gmail.berndivader.mythicmobsquests;

import java.util.ListIterator;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.lumine.xikage.mythicmobs.util.types.RangedDouble;
import me.blackvein.quests.CustomRequirement;

public class MythicMobsItemRequire 
extends
CustomRequirement {
	
	public MythicMobsItemRequire() {
		this.setName("MythicMobs Item Require");
		this.setAuthor("BerndiVader, idea Wahrheit");
		this.addData("Material");
		this.addDescription("Material","Item material type");
		this.addData("ItemMarker");
		this.addDescription("ItemMarker","ItemMarker defined by reward");
		this.addData("NameEnds");
		this.addDescription("NameEnds","Item name ends with...");
		this.addData("Amount");
		this.addDescription("Amount","How many items. Can be ranged like 1to3");
	}

	@Override
	public boolean testRequirement(Player player, Map<String, Object> data) {
		Material m1=null;
		String s1=(String)data.get("ItemMarker");
		String s2=(String)data.get("NameEnds");
		if (s1.toUpperCase().equals("NONE")) s1=null;
		if (s2.toUpperCase().equals("NONE")) s2=null;
		RangedDouble rd=new RangedDouble((String)data.get("Amount"));
		try {
			m1=Material.valueOf((String)data.get("Material"));
		} catch (Exception ex) {
			Bukkit.getLogger().info("Error occured while init MythicMobsItemRequirement: "+ex.getMessage());
		}
		boolean bl1=false;
		for(ListIterator<ItemStack>it=player.getInventory().iterator();it.hasNext();) {
			ItemStack is = it.next();
			if (is==null||is.getType().equals(Material.AIR)) continue;
			bl1=is.getType().equals(m1);
			bl1=bl1&(s1==null||s1.equals(NMSUtils.getMeta(is,MythicMobsItemReward.str_questitem)));
			bl1=bl1&(s2==null||is.getItemMeta().hasDisplayName()&&is.getItemMeta().getDisplayName().endsWith(s2));
			bl1=bl1&rd.equals(is.getAmount());
			if (bl1) break;
		}
		return bl1;
	}

}