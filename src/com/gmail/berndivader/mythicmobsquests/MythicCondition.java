package com.gmail.berndivader.mythicmobsquests;

import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Pattern;

import org.bukkit.entity.Entity;

import com.gmail.berndivader.mythicmobsquests.jboolexpr.BooleanExpression;
import com.gmail.berndivader.mythicmobsquests.jboolexpr.MalformedBooleanException;

import io.lumine.xikage.mythicmobs.adapters.AbstractEntity;
import io.lumine.xikage.mythicmobs.adapters.AbstractLocation;
import io.lumine.xikage.mythicmobs.adapters.bukkit.BukkitAdapter;
import io.lumine.xikage.mythicmobs.mobs.GenericCaster;
import io.lumine.xikage.mythicmobs.skills.SkillCondition;
import io.lumine.xikage.mythicmobs.skills.SkillMetadata;
import io.lumine.xikage.mythicmobs.skills.SkillString;
import io.lumine.xikage.mythicmobs.skills.SkillTrigger;
import io.lumine.xikage.mythicmobs.skills.conditions.InvalidCondition;

public class MythicCondition {

	String cConditionLine,tConditionLine;
	HashMap<Integer,String>tConditionLines,cConditionLines;
	HashMap<Integer,SkillCondition>targetConditions,casterConditions;
	GenericCaster caster;
	AbstractEntity trigger;
	AbstractLocation location;
	SkillMetadata data;
	
	
	public MythicCondition(Entity caster,Entity trigger,String cC,String tC) {
		this.caster=new GenericCaster(BukkitAdapter.adapt(caster));
		this.trigger=BukkitAdapter.adapt(trigger);
        HashSet<AbstractEntity>targets=new HashSet<AbstractEntity>();
        targets.add(this.trigger);
        HashSet<AbstractLocation>locations=new HashSet<AbstractLocation>();
        locations.add(this.trigger.getLocation());
        this.data=new SkillMetadata(SkillTrigger.API,this.caster,this.trigger,this.caster.getLocation(),targets,null,1);
		this.tConditionLines=new HashMap<>();
		this.cConditionLines=new HashMap<>();
		this.targetConditions=new HashMap<>();
		this.casterConditions=new HashMap<>();
		parseConditionLines(cC,false);
		parseConditionLines(tC,true);
		if (this.cConditionLines!=null&&!this.cConditionLines.isEmpty())this.casterConditions=this.getConditions(this.cConditionLines);
		if (this.tConditionLines!=null&&!this.tConditionLines.isEmpty())this.targetConditions=this.getConditions(this.tConditionLines);
	}
	
	public boolean check() {
		return this.handleConditions(this.data);
	}

	private void parseConditionLines(String ms, boolean istarget) {
		if (ms!=null) {
			ms=SkillString.parseMobVariables(ms,data.getCaster(),data.getTrigger(),data.getTrigger());
			if (istarget) {
				this.tConditionLine=ms;
			} else {
				this.cConditionLine=ms;
			}
			String[] parse = ms.split("\\&\\&|\\|\\|");
			if (parse.length>0) {
				for (int i=0;i<parse.length;i++) {
					String p=parse[i];
					if (istarget) {
						this.tConditionLines.put(i,p);
					} else {
						this.cConditionLines.put(i,p);
					}
				}
			}
		}
	}
	
	private HashMap<Integer, SkillCondition> getConditions(HashMap<Integer, String> conditionList) {
		HashMap<Integer, SkillCondition> conditions = new HashMap<Integer, SkillCondition>();
		for (int a = 0; a < conditionList.size(); a++) {
			SkillCondition sc;
			String s = conditionList.get(a);
			if (s.startsWith(" ")) s=s.substring(1);
			if ((sc=SkillCondition.getCondition(s)) instanceof InvalidCondition) {
				System.err.println("Invalid Condition!");
			}
			conditions.put(a,sc);
		}
		return conditions;
	}
	
	boolean expressBoolean(String expr) {
		BooleanExpression be;
		try {
			be = BooleanExpression.readLR(expr);
		} catch (MalformedBooleanException e) {
			System.err.println("error in boolean expr "+e.getMessage());
			return false;
		}
		return be.booleanValue();
	}	
	
	private boolean checkConditions(SkillMetadata data, HashMap<Integer, SkillCondition> conditions, boolean isTarget) {
		String cline = isTarget ? this.tConditionLine : this.cConditionLine;
		for (int a=0;a<conditions.size(); a++) {
			SkillMetadata sdata;
			sdata=data.deepClone();
			SkillCondition condition = conditions.get(a);
			if (isTarget) {
				cline=cline.replaceFirst(Pattern.quote(this.tConditionLines.get(a)),
						Boolean.toString(condition.evaluateTargets(sdata)));
			} else {
				cline=cline.replaceFirst(Pattern.quote(this.cConditionLines.get(a)),
						Boolean.toString(condition.evaluateCaster(sdata)));
			}
		}
		return expressBoolean(cline);
	}	

	public boolean handleConditions(SkillMetadata data) {
		boolean meet = true;
		if (!this.casterConditions.isEmpty()) {
			meet = this.checkConditions(data, this.casterConditions, false);
		}
		if (!this.targetConditions.isEmpty() && meet) {
			meet = this.checkConditions(data, this.targetConditions, true);
		}
		return meet;
	}	
	
}
