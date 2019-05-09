package com.gmail.berndivader.mythicmobsquests;

import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.gmail.berndivader.mythicmobsquests.events.CustomObjectiveEvent;
import com.gmail.berndivader.mythicmobsquests.events.CustomObjectiveEvent.Action;

import me.blackvein.quests.CustomObjective;
import me.blackvein.quests.Quest;
import me.blackvein.quests.Quester;

public 
class 
DenizenCustomObjective 
extends
CustomObjective
implements
Listener
{
	
	public DenizenCustomObjective() {
		setName("Custom Denizen");
		setShowCount(false);
		
		addStringPrompt("Objective Name", "Name your objective (Optional)",new String());
		setDisplay("%Objective Name%");
	}

	@EventHandler
	public void onCustomObjectiveEvent(CustomObjectiveEvent event) {
		final Player player=event.getPlayer();
		final Quester quester=Utils.quests.get().getQuester(player.getUniqueId());
		final Action action=event.getAction();
		
		for (Quest quest:quester.getCurrentQuests().keySet()) {
			Map<String,Object>map=getDataForPlayer(player,this,quest);
			if (map==null) continue;
			
			switch(action) {
				case COMPLETE:
					quester.finishObjective(quest,"customObj",null,null,null,null,null,null,null,null,null,this);
				case FAIL:
					quest.failQuest(quester);
					break;
			}
		}
	}

}
