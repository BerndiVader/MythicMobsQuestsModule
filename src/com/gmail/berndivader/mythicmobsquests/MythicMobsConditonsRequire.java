package com.gmail.berndivader.mythicmobsquests;

import java.util.Map;

import org.bukkit.entity.Player;

import me.blackvein.quests.CustomRequirement;

public class MythicMobsConditonsRequire 
extends
CustomRequirement 
implements
IDataMap 
{
	
	public MythicMobsConditonsRequire() {
		this.setName("MythicMobs Conditions Require");
		this.setAuthor("BerndiVader, idea Wahrheit");
		this.addDataAndDefault("Conditions",new String());
		this.addDescription("Conditions","List of conditions to check");
	}

	@Override
	public boolean testRequirement(Player player, Map<String, Object> data) {
		String[]arr1=data.get("Conditions").toString().split("\\{\\+\\+\\}");
		MythicCondition mc=null;
		boolean bl1=false;
		for(int i1=0;i1<arr1.length;i1++) {
			if ((mc=new MythicCondition(player,player,arr1[i1],null))!=null) {
				if(!(bl1=mc.check())) break;
			}
		}
		return bl1;
	}

	@Override
	public void addDataAndDefault(String key, Object value) {
		this.getData().put(key,value);
	}
}