package com.gmail.berndivader.mythicmobsquests;

import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

import io.lumine.xikage.mythicmobs.mobs.ActiveMob;
import io.lumine.xikage.mythicmobs.mobs.MobManager;
import me.blackvein.quests.CustomObjective;
import me.blackvein.quests.Quest;
import me.blackvein.quests.Quester;

public class MythicMobsKillObjective 
extends 
CustomObjective 
implements 
Listener
{
	
	public MythicMobsKillObjective() {
		setName("Kill MythicMobs Objective");
		setAuthor("BerndiVader");
		addStringPrompt("Objective Name", "Name your objective (Optional)",new String());
		addStringPrompt("Conditions","Enter a mythicmobs conditions for npc (Optional)","NONE");
		addStringPrompt("TargetConditions","Enter a mythicmobs conditions for player (Optional)","NONE");
		addStringPrompt("Internal Mobnames", "List of MythicMobs Types to use. Split with <,> or use ANY for any MythicMobs mobs. (Optional)","ANY");
		addStringPrompt("Mob Level", "Level to match. 0 for every level, any singlevalue, or rangedvalue. Example: 2-5 (Optional)","0");
		addStringPrompt("Mob Faction", "Faction of the mob to match. Split with <,> (Optional)","ANY");
		addStringPrompt("Notifier enabled", "true/false(default) send counter msg in chat.",false);
		addStringPrompt("Notifier msg", "Notifier message. %c% = placeholder for counter %s% placeholder for amount. (Optional)",new String());
		setShowCount(true);
		setCountPrompt("How many MythicMobs to kill");
		setDisplay("%Objective Name%");
	}

	public int getCounter() {
		return this.getCount();
	}
	
	@EventHandler
	public void onMythicMobDeathEvent (EntityDeathEvent e) {
		if (!(e.getEntity().getKiller() instanceof Player)) return;
		Optional<MobManager>mobmanager=Utils.getMobManager();
		if (!mobmanager.isPresent()) return;
		String mobtype=null,f="";
		int moblevel = 0;
		final Player p = e.getEntity().getKiller();
		final Entity bukkitEntity = e.getEntity();
		final ActiveMob am=mobmanager.get().getMythicMobInstance(bukkitEntity);
		if (am==null) return;
		mobtype = am.getType().getInternalName();
		moblevel = am.getLevel();
		if (am.hasFaction()) f = am.getFaction();
		if (mobtype == null || mobtype.isEmpty()) return;
		final Quester qp = Utils.quests.get().getQuester(p.getUniqueId());
		if (qp.getCurrentQuests().isEmpty()) return;
		for (Quest q : qp.getCurrentQuests().keySet()) {
			Map<String, Object> m = getDataForPlayer(p, this, q);
			if (m == null) continue;
			Optional<String>maybeKT=Optional.ofNullable((String)m.get("Internal Mobnames"));
			Optional<String>maybePARSE=Optional.ofNullable((String)m.get("Mob Level"));
			Optional<String>maybeFaction=Optional.ofNullable((String)m.get("Mob Faction"));
			Optional<String>maybeNotifier=Optional.ofNullable((String)m.get("Notifier enabled"));
			Optional<String>maybeNotifierMsg=Optional.ofNullable((String)m.get("Notifier msg"));
			String[]kt=maybeKT.orElse("ANY").split(",");
			String[]parseLvl=maybePARSE.orElse("0").split("-");
			String[]faction=maybeFaction.orElse("ANY").split(",");
			String cC=m.getOrDefault("Conditions","NONE").toString();
			String tC=m.getOrDefault("TargetConditions","NONE").toString();
			if (cC.toUpperCase().equals("NONE")) cC=null;
			if (tC.toUpperCase().equals("NONE")) tC=null;
			final boolean useConditions=cC!=null||tC!=null;
			final MythicCondition mc=useConditions?new MythicCondition(bukkitEntity,p,cC,tC):null;
			final boolean notifier=Boolean.parseBoolean(maybeNotifier.orElse("FALSE"));
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
						if (useConditions&&mc!=null) {
							new BukkitRunnable() {
								@Override
								public void run() {
									if(mc.check()) {
										if (notifier) MythicMobsKillObjective.this.notifyQuester(qp, q, p, notifierMsg);
										MythicMobsKillObjective.this.incrementObjective(p,MythicMobsKillObjective.this,1,q);
									}
								}
							}.runTaskLater(Utils.quests.get(),1);
						} else {
							if (notifier) this.notifyQuester(qp, q, p, notifierMsg);
							incrementObjective(p, this, 1, q);
						}
					}
				}
			}
		}
	}
	
	private void notifyQuester(Quester qp, Quest q, Player p, String msg) {
        int index = -1;
        for (int i = 0; i < qp.getCurrentStage(q).getCustomObjectives().size(); i++) {
            if (qp.getCurrentStage(q).getCustomObjectives().get(i).getName().equals(this.getName())) {
                index = i;
                break;
            }
        }
        if (index>-1) {
        	int total = qp.getCurrentStage(q).getCustomObjectiveCounts().get(index);
        	int count = 1+qp.getQuestData(q).customObjectiveCounts.get(this.getName());
        	msg = msg.replaceAll("\\%c\\%", Integer.toString(count));
        	msg = msg.replaceAll("\\%s\\%", Integer.toString(total));
        	p.sendMessage(msg);
        }
	}
}