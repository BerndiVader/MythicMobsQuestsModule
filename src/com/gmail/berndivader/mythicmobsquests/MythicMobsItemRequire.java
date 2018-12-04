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
CustomRequirement
implements
IDataMap
{
	
	public MythicMobsItemRequire() {
		
		this.setName("MythicMobs Item Require");
		this.setAuthor("BerndiVader, idea Wahrheit");
		this.addDataAndDefault("MythicItem","NONE");
		this.addDescription("MythicItem","Internal MythicItem name (Optional)");
		this.addDataAndDefault("Material","ANY");
		this.addDescription("Material","Item material type (Optional)");
		this.addDataAndDefault("NameEnds","NONE");
		this.addDescription("NameEnds","Item name ends with (Optional)");
		this.addDataAndDefault("Lore","NONE");
		this.addDescription("Lore","Lore contains that string (Optional)");
		this.addDataAndDefault("Amount",">0");
		this.addDescription("Amount","How many items. Can be ranged like 1to3 (Default >0)");
		this.addDataAndDefault("Supply",false);
		this.addDescription("Supply","Supply player with missing item true/false(default)");
		this.addDataAndDefault("RemoveItem",false);
		this.addDescription("RemoveItem","Remove the required item from the players inventory true/false(default)");
		this.addDataAndDefault("HoldItem",false);
		this.addDescription("HoldItem","Player need to hold the item true/false(default)");
	}

	@Override
	public boolean testRequirement(Player player, Map<String, Object> data) {
		Material m1=null;
		String s2=(String)data.getOrDefault("NameEnds","NONE");
		String s3=(String)data.getOrDefault("MythicItem","NONE");
		String s4=(String)data.getOrDefault("Lore","NONE");
		String s1=s3;
		boolean bl2=Boolean.parseBoolean(data.getOrDefault("Supply","false").toString());
		boolean remove=data.getOrDefault("RemoveItem","FALSE").toString().toUpperCase().equals("TRUE");
		if (s1.toUpperCase().equals("NONE")) s1=null;
		if (s2.toUpperCase().equals("NONE")) s2=null;
		if (s3.toUpperCase().equals("NONE")) s3=null;
		if (s4.toUpperCase().equals("NONE")) s4=null;
		RangedDouble rd=new RangedDouble((String)data.get("Amount"));
		String str2=data.get("Material").toString().toUpperCase();
		if (!str2.equals("ANY")) {
			try {
				m1=Material.valueOf(str2);
			} catch (Exception ex) {
				Bukkit.getLogger().info("Error occured while init MythicMobsItemRequirement: "+ex.getMessage());
			}
		}
		boolean bl1=false;
		for(ListIterator<ItemStack>it=player.getInventory().iterator();it.hasNext();) {
			ItemStack is = it.next();
			if (is==null||is.getType().equals(Material.AIR)) continue;
			bl1=m1==null||is.getType()==m1;
			bl1&=s1==null||s1.equals(NMSUtils.getMeta(is,Utils.str_questitem));
			bl1&=s2==null||is.getItemMeta().hasDisplayName()&&is.getItemMeta().getDisplayName().endsWith(s2);
			bl1&=rd.equals(is.getAmount());
			if (s4!=null&&is.hasItemMeta()&&is.getItemMeta().hasLore()) {
				boolean bl3=false;
				for(ListIterator<String>it1=is.getItemMeta().getLore().listIterator();it1.hasNext();) {
					String str1=it1.next();
					if ((str1.contains(s4))) {
						bl3=true;
						break;
					};
				}
				bl1=bl3;
			}
			if (bl1) {
				if (remove) {
					ItemStack is1=new ItemStack(is);
					int i1=is1.getAmount()-(int)rd.getMin();
					if (i1<=0) i1=0;
					is1.setAmount(i1);
					it.set(new ItemStack(is1));
				}
				break;
			}
		}
		if (!bl1&&bl2) {
			bl1=Utils.createAndDropItemStack(new String[] {s3},s1,(int)rd.getMin(),player,false,true);
		}
		return bl1;
	}

	@Override
	public void addDataAndDefault(String key, Object value) {
		datamap.put(key, value);
	}

}