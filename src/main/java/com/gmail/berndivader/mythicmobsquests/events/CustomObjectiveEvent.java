package com.gmail.berndivader.mythicmobsquests.events;

import org.bukkit.entity.Player;

public 
class 
CustomObjectiveEvent 
extends
AbstractEvent
{
	public static enum Action{
		COMPLETE,
		FAIL
	}
	
	private Player player;
	private Action action;
	
	public CustomObjectiveEvent(Player player,Action action) {
		this.setPlayer(player);
		this.setAction(action);
	}
	
	public Player getPlayer() {
		return player;
	}
	public void setPlayer(Player player) {
		this.player=player;
	}
	
	public Action getAction() {
		return action;
	}
	public void setAction(Action action) {
		this.action=action;
	}

}