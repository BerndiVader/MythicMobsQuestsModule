package com.gmail.berndivader.questsMythicMobsKillObjective;

import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.blackvein.quests.CustomObjective;
import me.blackvein.quests.Quest;
import me.blackvein.quests.Quester;
import me.blackvein.quests.Quests;
import net.elseland.xikage.MythicMobs.API.Bukkit.Events.MythicMobDeathEvent;

public class QuestMMKillObjective extends CustomObjective implements Listener {
	
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
	}
	
	@EventHandler
	public void onMythicMobsKillEvent (MythicMobDeathEvent e) {
		if (!(e.getKiller() instanceof Player)) {return;}
		Player p = (Player)e.getKiller();
		Quester qp = Quests.getInstance().getQuester(p.getUniqueId());
		if (qp.currentQuests.isEmpty()) return;
		for (Quest q : qp.currentQuests.keySet()) {
			Map<?, ?> m = QuestMMKillObjective.getDatamap(p, this, q);
			if (m == null) continue;
			String kt = m.get("Internal Mobnames").toString();
			if (kt.contains(e.getMobType().getInternalName())) {QuestMMKillObjective.incrementObjective(p, this, 1, q);}
		}
	}
}
