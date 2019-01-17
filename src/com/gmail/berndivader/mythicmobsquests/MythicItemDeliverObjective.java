package com.gmail.berndivader.mythicmobsquests;

import java.util.ListIterator;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import io.lumine.xikage.mythicmobs.util.types.RangedDouble;
import me.blackvein.quests.CustomObjective;
import me.blackvein.quests.Quest;
import me.blackvein.quests.Quester;
import net.citizensnpcs.api.event.NPCRightClickEvent;

public class MythicItemDeliverObjective 
extends
CustomObjective 
implements
Listener,
IDataMap 
{

	public MythicItemDeliverObjective() {
		setName("MythicItem NPC Deliver Objective");
		setAuthor("BerndiVader, idea Wahrheit");
		setEnableCount(false);
		this.addDataAndDefault("Objective Name",new String());
		this.addDescription("Objective Name","What's displayed for the player (Optional)");
		this.addDataAndDefault("NPC IDs","-1");
		this.addDescription("NPC IDs","NPC ID or ID list like 1,2,3,4 (Optional)");
		this.addDataAndDefault("Conditions","NONE");
		this.addDescription("Conditions","Enter a mythicmobs conditions for npc (Optional)");
		this.addDataAndDefault("TargetConditions","NONE");
		this.addDescription("TargetConditions","Enter a mythicmobs conditions for player (Optional)");
		this.addDataAndDefault("Material","ANY");
		this.addDescription("Material","Item material type (Optional)");
		this.addDataAndDefault("MythicItem","NONE");
		this.addDescription("MythicItem","MythicMobs internal item name (Optional)");
		this.addDataAndDefault("NameEnds","NONE");
		this.addDescription("NameEnds","Item name ends with (Optional)");
		this.addDataAndDefault("Lore","NONE");
		this.addDescription("Lore","Lore contains that string (Optional)");
		this.addDataAndDefault("Amount",">0");
		this.addDescription("Amount","The required size of the stack. Like 10to64 (Optional)");
		this.addDataAndDefault("HoldItem",false);
		this.addDescription("HoldItem","Player need to hold the item true/false(default)");
		this.addDataAndDefault("RemoveItem",false);
		this.addDescription("RemoveItem","Remove the delivered item from the players inventory true/false(default)");
		setDisplay("%Objective Name%");
	}
	
	@EventHandler
	public void onNPCInteract(NPCRightClickEvent e) {
		final Player player=e.getClicker();
		final Quester quester=Utils.quests.get().getQuester(player.getUniqueId());
		for (Quest quest:quester.getCurrentQuests().keySet()) {
			Map<String,Object>map=getDatamap(player,this,quest);
			if (map==null) continue;
			if (npcID(map,e.getNPC().getId())>-1) {
				String[]materials=map.getOrDefault("Material","ANY").toString().toUpperCase().split(",");
				String[]itemMarker=map.getOrDefault("MythicItem","NONE").toString().split(",");
				String[]nameEnds=map.getOrDefault("NameEnds","NONE").toString().split(",");
				String lore=map.getOrDefault("Lore","NONE").toString();
				RangedDouble rd=new RangedDouble(map.get("Amount").toString());
				String cC=map.getOrDefault("Conditions","NONE").toString();
				String tC=map.getOrDefault("TargetConditions","NONE").toString();
				boolean remove=map.getOrDefault("RemoveItem","FALSE").toString().toUpperCase().equals("TRUE");
				boolean holdItem=map.getOrDefault("HoldItem","FALSE").toString().toUpperCase().equals("TRUE");
				if (cC.toUpperCase().equals("NONE")) cC=null;
				if (tC.toUpperCase().equals("NONE")) tC=null;
				boolean useConditions=cC!=null||tC!=null;
				final MythicCondition mc=useConditions?new MythicCondition(e.getNPC().getEntity(),player,cC,tC):null;
				boolean bl1=false;
				ListIterator<ItemStack>lit=player.getInventory().iterator();
				ItemStack is=null;
				if (holdItem) {
					is=player.getInventory().getItemInMainHand();
					bl1=Utils.chk(materials,itemMarker,nameEnds,lore,rd,is);
				} else {
					while(lit.hasNext()) {
						is=lit.next();
						if(is==null||is.getType()==Material.AIR) continue;
						if ((bl1=Utils.chk(materials,itemMarker,nameEnds,lore,rd,is))) break;
					}
				}
				final ItemStack fis=is;
				final boolean bb=bl1;
				if (useConditions&&mc!=null) {
					new BukkitRunnable() {
						@Override
						public void run() {
							boolean bl1=bb;
							if (bl1&=mc.check()) {
								if(remove) Utils.removeItemstackAmount(holdItem,player,rd,fis,lit);
								quester.finishObjective(quest,"customObj",null,null,null,null,null,null,null,null,null,MythicItemDeliverObjective.this);
							}
						}
					}.runTaskLater(Utils.quests.get(),1);
				} else {
					if (bl1) {
						if (remove) Utils.removeItemstackAmount(holdItem,player,rd,fis,lit);
						quester.finishObjective(quest,"customObj",null,null,null,null,null,null,null,null,null,this);
					}
				}
			}
		}
	}
	
	static int npcID(Map<String,Object>map,int id) {
		String[]arr1=map.get("NPC IDs").toString().split(",");
		for(int i1=0;i1<arr1.length;i1++) {
			if(arr1[i1].equals(Integer.toString(id))) return id;
		}
		return -1;
	}

	@Override
	public void addDataAndDefault(String key, Object value) {
		this.getData().put(key,value);
	}
	
}
