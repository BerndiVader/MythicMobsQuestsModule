package com.gmail.berndivader.questsMythicMobsKillObjective;

import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import me.blackvein.quests.CustomObjective;
import me.blackvein.quests.Quest;
import me.blackvein.quests.Quester;
import me.blackvein.quests.Quests;


public class QuestMMKillObjective extends CustomObjective implements Listener {

	private String strMMVer;
	private int mmVer;
	
	
	public QuestMMKillObjective() {
		setName("Kill MythicMobs Objective");
		addData("Objective Name");
		addDescription("Objective Name", "Name your objective");
		addData("Internal Mobnames");
		addDescription("Internal Mobnames", "List of MythicMobs Types to use. Split with <,>");
		setEnableCount(true);
		setShowCount(true);
		setCountPrompt("How many MythicMobs to kill");
		setDisplay("%Objective Name%, Counter: %count%");
		strMMVer = Bukkit.getServer().getPluginManager().getPlugin("MythicMobs").getDescription().getVersion();
		mmVer = Integer.valueOf(strMMVer.replaceAll("\\.", ""));
	}
	
	@EventHandler
	public void onMythicMobDeathEvent (EntityDeathEvent e) {
		if (!(e.getEntity() instanceof LivingEntity)) return;
		LivingEntity bukkitentity = e.getEntity();
		if (!(bukkitentity.getKiller() instanceof Player)) return;
		String mobtype = null;
		Player p;
		if ((mmVer > 244 && mmVer < 252 || mmVer == 2511)) {
			net.elseland.xikage.MythicMobs.Mobs.ActiveMob am = 
					net.elseland.xikage.MythicMobs.MythicMobs.inst().getAPI().getMobAPI().getMythicMobInstance(bukkitentity);
			if (am==null) return;
			mobtype = am.getType().getInternalName();
		} else if (mmVer > 259 && mmVer < 2511) {
			io.lumine.xikage.mythicmobs.mobs.ActiveMob am = 
					io.lumine.xikage.mythicmobs.MythicMobs.inst().getMobManager().getMythicMobInstance(bukkitentity);
			if (am==null) return;
			mobtype = am.getType().getInternalName();
		}
		if (mobtype.isEmpty()) return;
		p = bukkitentity.getKiller();
		Quester qp = Quests.getInstance().getQuester(p.getUniqueId());
		if (qp.currentQuests.isEmpty()) return;
		for (Quest q : qp.currentQuests.keySet()) {
			Map<?, ?> m = QuestMMKillObjective.getDatamap(p, this, q);
			if (m == null) continue;
			String[] kt = m.get("Internal Mobnames").toString().split(",");
			if (ArrayUtils.contains(kt, mobtype)) {QuestMMKillObjective.incrementObjective(p, this, 1, q);}
		}
	}
}
