package com.gmail.berndivader.mythicmobsquests;

import java.util.ListIterator;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.drops.DropManager;
import io.lumine.xikage.mythicmobs.util.types.RangedDouble;
import me.blackvein.quests.CustomObjective;
import me.blackvein.quests.Quest;
import me.blackvein.quests.Quester;
import me.blackvein.quests.Quests;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCRightClickEvent;

public class MythicItemDeliverObjective 
extends
CustomObjective 
implements
Listener {

	static Quests quests; 
	static DropManager dropmanager;
	static CitizensAPI citizens;
	boolean bb;
	
	static {
		quests=(Quests)Bukkit.getPluginManager().getPlugin("Quests");
		dropmanager=((MythicMobs)Bukkit.getPluginManager().getPlugin("MythicMobs")).getDropManager();
	}
	
	public MythicItemDeliverObjective() {
		setName("MythicItem NPC Deliver Objective");
		setAuthor("BerndiVader, idea Wahrheit");
		setEnableCount(false);
		this.setDisplay(" ");
		this.addData("NPC IDs");
		this.addDescription("NPC IDs","NPC ID or ID list like 1,2,3,4");
		this.addData("Conditions");
		this.addDescription("Conditions","Enter a mythicmobs conditions for npc");
		this.addData("TargetConditions");
		this.addDescription("TargetConditions","Enter a mythicmobs conditions for player");
		this.addData("Material");
		this.addDescription("Material","Item material type or ANY");
		this.addData("ItemMarker");
		this.addDescription("ItemMarker","ItemMarker defined by reward or NONE");
		this.addData("NameEnds");
		this.addDescription("NameEnds","Item name ends with or NONE");
		this.addData("Lore");
		this.addDescription("Lore","Lore contains that string or NONE");
		this.addData("Amount");
		this.addDescription("Amount","How many items. Can be ranged like 1to3");
		this.addData("HoldItem");
		this.addDescription("HoldItem","Player need to hold the item (true/false)");
		this.addData("TimeLimit");
		this.addDescription("TimeLimit","Limited time in seconds or -1 for unlimit");
	}
	
	@EventHandler
	synchronized public void onNPCInteract(NPCRightClickEvent e) {
		if (e.isCancelled()||e.getClicker().isConversing()) return;
		final Player player=e.getClicker();
		final Quester quester=quests.getQuester(player.getUniqueId());
		for (Quest quest:quester.currentQuests.keySet()) {
			Map<String,Object>map=getDatamap(player,this,quest);
			if (map==null) continue;
			if (npcID(map,e.getNPC().getId())>-1) {
				String[]materials=map.getOrDefault("Material","ANY").toString().toUpperCase().split(",");
				String[]itemMarker=map.getOrDefault("ItemMarker","NONE").toString().split(",");
				String[]nameEnds=map.getOrDefault("NameEnds","NONE").toString().split(",");
				String lore=map.getOrDefault("Lore","NONE").toString();
				RangedDouble rd=new RangedDouble(map.get("Amount").toString());
				String cC=map.getOrDefault("Conditions","NONE").toString();
				String tC=map.getOrDefault("TargetConditions","NONE").toString();
				if (cC.toUpperCase().equals("NONE")) cC=null;
				if (tC.toUpperCase().equals("NONE")) tC=null;
				boolean useConditions=cC!=null||tC!=null;
				final MythicCondition mc=useConditions?new MythicCondition(e.getNPC().getEntity(),player,cC,tC):null;
				boolean bl1=false;
				if (map.get("HoldItem").toString().toUpperCase().equals("TRUE")) {
					ItemStack is=player.getInventory().getItemInMainHand().clone();
					bl1=chk(materials,itemMarker,nameEnds,lore,rd,is);
				} else {
					ListIterator<ItemStack>lit=player.getInventory().iterator();
					while(lit.hasNext()) {
						ItemStack is=lit.next();
						if(is==null||is.getType()==Material.AIR) continue;
						if ((bl1=chk(materials,itemMarker,nameEnds,lore,rd,is))) break;
					}
				}
				this.bb=bl1;
				if (mc!=null) {
					new BukkitRunnable() {
						@Override
						public void run() {
							bb&=mc.check();
							if (bb) quester.finishObjective(quest,"customObj",null,null,null,null,null,null,null,null,null,MythicItemDeliverObjective.this);
						}
					}.runTaskLater(MythicItemDeliverObjective.quests,1);
				} else {
					if (bl1) {
						quester.finishObjective(quest,"customObj",null,null,null,null,null,null,null,null,null,this);
					}
				}
			}
		}
	}
	
	static boolean chk(String[]materials,String[]itemMarker,String[]nameEnds,String lore,RangedDouble rd,ItemStack is) {
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
			bl1=bl3;
		}
		return bl1;
	}
	
	static boolean arrContains(String[]arr1,String s1) {
		if (s1!=null&&arr1!=null) {
			for(int i1=0;i1<arr1.length;i1++) {
				if(s1.endsWith(arr1[i1])) return true;
			}
		}
		return false;
	}
	
	static int npcID(Map<String,Object>map,int id) {
		String[]arr1=map.get("NPC IDs").toString().split(",");
		for(int i1=0;i1<arr1.length;i1++) {
			if(arr1[i1].equals(Integer.toString(id))) return id;
		}
		return -1;
	}
	
}
