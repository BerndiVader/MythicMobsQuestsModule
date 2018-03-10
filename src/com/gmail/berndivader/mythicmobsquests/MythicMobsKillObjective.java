package com.gmail.berndivader.mythicmobsquests;

import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import io.lumine.xikage.mythicmobs.mobs.MobManager;
import me.blackvein.quests.CustomObjective;
import me.blackvein.quests.Quest;
import me.blackvein.quests.Quester;
import me.blackvein.quests.Quests;

public class MythicMobsKillObjective 
extends 
CustomObjective 
implements 
Listener {

	private static Quests quests = (Quests) Bukkit.getServer().getPluginManager().getPlugin("Quests");
	private MobManager mobmanager = MythicMobs.inst().getMobManager();
	
	public MythicMobsKillObjective() {
		setName("Kill MythicMobs Objective");
		setAuthor("BerndiVader");
		addData("Objective Name");
		addDescription("Objective Name", "Name your objective");
		addData("Internal Mobnames");
		addDescription("Internal Mobnames", "List of MythicMobs Types to use. Split with <,> or use ANY for any MythicMobs mobs.");
		addData("Mob Level");
		addDescription("Mob Level", "Level to match. 0 for every level, any singlevalue, or rangedvalue. Example: 2-5");
		addData("Mob Faction");
		addDescription("Mob Faction", "Faction of the mob to match. Split with <,> or use ANY for any mob faction");
		addData("Notifier enabled");
		addDescription("Enable notifier", "true/false send counter msg in chat.");
		addData("Notifier msg");
		addDescription("Notifier msg", "Notifier message. %c% = placeholder for counter %s% placeholder for amount.");
		setEnableCount(true);
		setShowCount(true);
		setCountPrompt("How many MythicMobs to kill");
		setDisplay("%Objective Name%, Counter: %count%");
	}

	public int getCounter() {
		return this.getCount();
	}
	
	@EventHandler
	public void onMythicMobDeathEvent (EntityDeathEvent e) {
		if (!(e.getEntity().getKiller() instanceof Player)) return;
		String mobtype = null;
		String f = "<NONE>";
		int moblevel = 0;
		Player p = e.getEntity().getKiller();
		Entity bukkitentity = e.getEntity();
		ActiveMob am=this.mobmanager.getMythicMobInstance(bukkitentity);
		if (am==null) return;
		mobtype = am.getType().getInternalName();
		moblevel = am.getLevel();
		if (am.hasFaction()) f = am.getFaction();
		if (mobtype == null || mobtype.isEmpty()) return;
		Quester qp = quests.getQuester(p.getUniqueId());
		if (qp.currentQuests.isEmpty()) return;
		for (Quest q : qp.currentQuests.keySet()) {
			Map<String, Object> m = getDatamap(p, this, q);
			if (m == null) continue;
			Optional<String>maybeKT=Optional.ofNullable((String)m.get("Internal Mobnames"));
			Optional<String>maybePARSE=Optional.ofNullable((String)m.get("Mob Level"));
			Optional<String>maybeFaction=Optional.ofNullable((String)m.get("Mob Faction"));
			Optional<String>maybeNotifier=Optional.ofNullable((String)m.get("NNotifier enabled"));
			Optional<String>maybeNotifierMsg=Optional.ofNullable((String)m.get("Notifier msg"));
			String[]kt=maybeKT.orElse("ANY").split(",");
			String[]parseLvl=maybePARSE.orElse("0").split("-");
			String[]faction=maybeFaction.orElse("ANY").split(",");
			boolean notifier = false;
			try {
				notifier=Boolean.parseBoolean(maybeNotifier.orElse("false"));
			} catch (Exception ex) {
				notifier = false;
			}
			String notifierMsg=maybeNotifierMsg.orElse("Killed %c% of %s%");
			int level = 0; int lmin = 0;int lmax=0;
			if (parseLvl.length==1) {
				level = 1; 
				lmin = Integer.valueOf(parseLvl[0]);
				if (lmin==0) level = 0;
			} else if (parseLvl.length==2) {
				level = 2;
				lmin = Integer.valueOf(parseLvl[0]);
				lmax = Integer.valueOf(parseLvl[1]);
				if (lmin>lmax) level = 0;
			}
			if ((level==0) || (level==1 && moblevel==lmin) || (level==2 && (lmin<=moblevel&&moblevel<=lmax))) {
				if (kt[0].toUpperCase().equals("ANY") || ArrayUtils.contains(kt, mobtype)) {
					if (faction[0].toUpperCase().equals("ANY") || ArrayUtils.contains(faction, f)) {
						if (notifier) this.notifyQuester(qp, q, p, notifierMsg);
							incrementObjective(p, this, 1, q);
					}
				}
			}
		}
	}
	
	private void notifyQuester(Quester qp, Quest q, Player p, String msg) {
        int index = -1;
        for (int i = 0; i < qp.getCurrentStage(q).customObjectives.size(); i++) {
            if (qp.getCurrentStage(q).customObjectives.get(i).getName().equals(this.getName())) {
                index = i;
                break;
            }
        }
        if (index>-1) {
        	int total = qp.getCurrentStage(q).customObjectiveCounts.get(index);
        	int count = 1+qp.getQuestData(q).customObjectiveCounts.get(this.getName());
        	msg = msg.replaceAll("\\%c\\%", Integer.toString(count));
        	msg = msg.replaceAll("\\%s\\%", Integer.toString(total));
        	p.sendMessage(msg);
        }
	}
	
}