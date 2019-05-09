package com.gmail.berndivader.mythicmobsquests;

import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.gmail.berndivader.mythicdenizenaddon.events.CustomObjectiveEvent;
import com.gmail.berndivader.mythicdenizenaddon.events.CustomObjectiveEvent.Action;

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
		
		addStringPrompt("Objective Name", "Name your objective (Optional)","");
		
		addStringPrompt("Objective Type", "Type of you objective. (used as filter for denizen)","");
		addStringPrompt("Notify player", "true/false(default) send counter msg in chat.",false);
		addStringPrompt("Notify Message", "%c% = placeholder for counter %s% placeholder for amount. (Optional)","%c% %s%");
		
		setCountPrompt("How many repeats until completed?");
		setShowCount(true);
		
		setDisplay("%Objective Name%");
	}

	@EventHandler
	public void onCustomObjectiveEvent(CustomObjectiveEvent event) {
		final Player player=event.getPlayer();
		final Quester quester=Utils.quests.get().getQuester(player.getUniqueId());
		final Action action=event.getAction();
		final String objective_type=event.getObjectiveType().toUpperCase();
		
		for (Quest quest:quester.getCurrentQuests().keySet()) {
			Map<String,Object>map=getDataForPlayer(player,this,quest);
			if(map==null||!objective_type.equals(map.getOrDefault("Objective Type","").toString().toUpperCase())) continue;

			boolean notify=false;
			try {
				notify=Boolean.parseBoolean(map.getOrDefault("Notify player","FALSE").toString());
			} catch (Exception e) {
				//
			}
			String notify_message=map.getOrDefault("Notify Message","").toString();
			
			switch(action) {
				case COMPLETE:
					quest.completeQuest(quester);
					break;
				case FAIL:
					quest.failQuest(quester);
					break;
				case INCREMENT:
					if(notify) notifyQuester(quester,quest,notify_message);
					incrementObjective(player,this,1,quest);
			}
		}
	}
	
	private void notifyQuester(Quester quester,Quest quest, String message) {
        int index = -1;
        for (int i = 0; i < quester.getCurrentStage(quest).getCustomObjectives().size(); i++) {
            if (quester.getCurrentStage(quest).getCustomObjectives().get(i).getName().equals(this.getName())) {
                index = i;
                break;
            }
        }
        if (index>-1) {
        	int total = quester.getCurrentStage(quest).getCustomObjectiveCounts().get(index);
        	int count = 1+quester.getQuestData(quest).customObjectiveCounts.get(this.getName());
        	message=message.replaceAll("\\%c\\%", Integer.toString(count));
        	message=message.replaceAll("\\%s\\%", Integer.toString(total));
        	quester.getPlayer().sendMessage(message);
        }
	}

}
