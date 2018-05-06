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
		this.addData("MythicItem");
		this.addDescription("MythicItem","Internal MythicItem name");
		this.addData("Material");
		this.addDescription("Material","Item material type");
		this.addData("ItemMarker");
		this.addDescription("ItemMarker","NBT Tag defined by reward or NONE");
		this.addData("NameEnds");
		this.addDescription("NameEnds","Item name ends with or NONE");
		this.addData("Amount");
		this.addDescription("Amount","How many items. Can be ranged like 1to3");
		this.addData("Supply");
		this.addDescription("Supply","Supply player with missing item (true/false)");
	}

	@Override
	public boolean testRequirement(Player player, Map<String, Object> data) {
		Material m1=null;
		String s1=(String)data.getOrDefault("ItemMarker","NONE");
		String s2=(String)data.getOrDefault("NameEnds","NONE");
		String s3=(String)data.getOrDefault("MythicItem","NONE");
		boolean bl2=Boolean.parseBoolean(data.getOrDefault("Supply","false").toString());
		if (s1.toUpperCase().equals("NONE")) s1=null;
		if (s2.toUpperCase().equals("NONE")) s2=null;
		if (s3.toUpperCase().equals("NONE")) s3=null;
		RangedDouble rd=new RangedDouble((String)data.get("Amount"));
		try {
			m1=Material.valueOf(data.get("Material").toString().toUpperCase());
		} catch (Exception ex) {
			Bukkit.getLogger().info("Error occured while init MythicMobsItemRequirement: "+ex.getMessage());
		}
		boolean bl1=false;
		for(ListIterator<ItemStack>it=player.getInventory().iterator();it.hasNext();) {
			ItemStack is = it.next();
			if (is==null||is.getType().equals(Material.AIR)) continue;
			bl1=is.getType()==m1;
			bl1&=s1==null||s1.equals(NMSUtils.getMeta(is,Utils.str_questitem));
			bl1&=s2==null||is.getItemMeta().hasDisplayName()&&is.getItemMeta().getDisplayName().endsWith(s2);
			bl1&=rd.equals(is.getAmount());
			if (bl1) break;
		}
		if (!bl1&&bl2) {
			bl1=Utils.createAndDropItemStack(new String[] {s3},s2,(int)rd.getMax(),player,false,true);
		}
		return bl1;
	}

}