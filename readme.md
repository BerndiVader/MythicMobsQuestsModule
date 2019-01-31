# Quest Addon for MythicMobs 4.0.1 or any higher version and Spigot 1.8.8 - 1.13.2

# [DOWNLOAD](http://mc.hackerzlair.org:8080/job/MythicMobsQuests/) [![Build Status](http://mc.hackerzlair.org:8080/job/MythicMobsQuests/badge/icon)] <br>

# [FOR QUESTS 3.2.7 AND BELOW] (https://github.com/BerndiVader/MythicMobsQuestsModule/releases/tag/285)

#### Changelog

- 01.23.2019 update: update to Quests 3.6.0
- 04.12.2018 update: allows optional data entries.
- 15.11.2018 Update: added Objective Name to Mythicitem deliver objective && counter for the objective name is now optional
- 09.5.2018 Update: added remove item option to deliver and require. Changed ItemMarker to MythicItem in deliver.
- 08.5.2018 Update: added Conditions & TargetConditions to the objectives.
- 07.5.2018 Update: added mythicmobsitem requirement, deliver objective & improved reward.
- 03.5.2018 Update: added lootlist notifier to reward.
- 03.5.2018 Update: added money, exp, heroesxp, mcmmoxp to droptable reward.
- 02.5.2018 Update: fixed notifier in kill objective.
- 02.5.2018 Update: some more work on droptable issues.
- 02.5.2018 Update: added requirement.
- 02.5.2018 Update: added tag argument for reward to mark the item as questitem.
- 01.5.2018 Update: fixed droptable conditions issue in reward.
- 28.4.2018 Update: improved reward objective.
- 17.3.2018 Update: updated killobjective to Quests 3.2.7
- 10.3.2018 Update: ignore case sensitive for mobtype and faction.
- 9.02.2018 Update: added MythicMobsItem custom reward.
- 7.02.2018 Update: some tweaks and build against Quests 3.2.4
- 5.11.2017 Update: dropped support for all MythicMobs Versions < 4.0.1
- 3.11.2017 Update: build against Quests 3.0.0 & MythicMobs 4.3.2
- 25.7.2017 Update: added Notifier enabled and Notifier msg option.
- 23.6.2017 Update: added Faction option. Build against Spigot 1.12, Quests 2.8.6 & MythicMobs 4.1.0
- 29.5.2017 Update: fixed NPE's if there are no values & added "ANY" for MythicMobs mobtype.
- 6.5.2017 Update: build against Quests 2.7.6 & MythicMobs 4.1.0
- 4.3.2017 Update: fixed bug that only ActiveMob livingentity instances count & added level support

#### Installation Instructions

Install (upload, extract, copy) the jar into plugins/Quests/Modules/ and restart the server.

#### Kill MythicMobs Objective for Quests

```yaml
quests:
  custom1:
    name: For the though guys
    npc-giver-id: 0
    redo-delay: 3
    ask-message: Kill some mobs while being naked and just equipped with an wooden_axe!
    finish-message: WOW!! You're truly a tough guy!
    gui-display: name-DIRT:amount-1
    stages:
      ordered:
        '1':
          custom-objectives:
            custom1:
              name: Kill MythicMobs Objective
              count: 3
                data:
                Objective Name: kill 3 mobs naked but only equiped with an axe made out of wood
                TargetConditions: ownsitemsimple{where=HAND;Material=wood_axe} && testfor{vc=(Inventory:[(Slot:100b)]);action=false} && testfor{vc=(Inventory:[(Slot:101b)]);action=false} && testfor{vc=(Inventory:[(Slot:102b)]);action=false} && testfor{vc=(Inventory:[(Slot:103b)]);action=false}
                Notifier enabled: 'true'
                Notifier msg: You killed %c% out of %s% mobs while naked with an wooden axe!
  custom2:
    name: Kill 10 of these MythicMobs
    ask-message: Will you kill 10 of these MythicMobs for us?
    finish-message: Thank you for your help!
    stages:
      ordered:
        '1':
          custom-objectives:
            custom1:
              name: Kill MythicMobs Objective
              count: 10
              data:
                Notifier enabled: 'true'
                Notifier msg: Killed %c% out of %s%
                Objective Name: Kill 10 of any MythicMob
```

##### Configuration Notes

If you want the player to see a notification message tracking their progress, set **Notifier enabled** to true and use **Notifier msg** to customize the message. You can use **%s%** and **%c%** as placeholder for whole amount and actual count.

If your mobs do not utilize the "level" feature of MythicMobs, you may use **Mob Level: '0'**, but it **must** be included. This field also accepts ranges, e.g. *Mob Level: '0-10'*.

If you want to filter per faction use *Mob Faction* option. Valid is **ANY** to match all factions or a single value or a arraylist eg: *faction1,faction2,faction3*

The **Objective Name** is what will be displayed to the player, and may be set to whatever you like accordingly.

The **Internal Mobnames** field references the names of MythicMobs as specified in your MythicMobs config files. In the above examples, such a MythicMob would be internally named SkeletalDarkKnight, e.g.:

```yaml
SkeletalDarkKnight:
  Type: WITHER_SKELETON
  Display: '&dSkeletal Dark Knight'
  Health: 150
  Damage: 7
```

Note that this is the **internal name** of the mob (used to initiate the YAML block), *not* its *Display Name* or *Type*. This field also supports multiple options, separated by commas, or ANY, e.g.:

```yaml
  custom3:
    name: Kill various MythicMobs
    ask-message: Will you kill 10 MythicMobs of any kind for us?
    finish-message: Thank you for your help!
    stages:
      ordered:
        '1':
          custom-objectives:
            custom1:
              name: Kill MythicMobs Objective
              count: 10
              data:
                Notifier enabled: 'true'
                Notifier msg: Killed %c% out of %s%
                Objective Name: Kill 10 of any MythicMob
```

#### MythicItem rewards for Quests

Quests can offer MythicItems as rewards for completing them. Plus add special tags to the item so that they 
can be identified as mythicmobs- and, or quest items. Syntax example:

```yaml
    rewards:
      custom-rewards:
        req1:
          name: MythicMobs Item Reward
          data:
            MythicItem: MMDIRT
            Notify: 'true'
            RewardName: You received a MythicMobs item
            Amount: '1'
            Stackable: 'false'
            ItemMarker: MMDIRT
```

##### Configuration Notes

The mythicitem reward uses the following data options and all attributes are required:

+ **MythicItem:** The internal mythicmobs itemname or droptable name. Can be an array like item1,item2,droptable1
+ **Notifiy:** *true/false* If true the quester recieve a msg with a list of all items.
+ **RewardName:** Message displayed at reward.
+ **Amount:** Amount. In case of droptable how many times the droptable is parsed.
+ **Stackable:** *true/false* if true items are not stackable.
+ **ItemMarker:** Add a NBT tag to the item to have it marked. Recommended is to use the mythicmobs internal item name. This tag is used in *deliver objective* and *customrequire* to identify the item as an mythicmobs item. Its also good for refer to the mythicmobs item. So if you change the item in the mythicmobs yaml you dont need to change all the quests if you use the ItemMarker instead of material, lore and displayname.

#### MythicItem requirement

Quests require an special item to continue. Check the item with Lore, Material, DisplayName or just simple by an Tag refering to a mythicmobs item.

##### Configuration Notes

If the item was created by reward or requirement itself or by mythicmobsext's dropmythicitem mechanic you can use  ItemMarker to refer to a mythicmobs item. Alternatively use Material, DisplayName and Lore. All attributes and options are required:

+ **Lore:** If one of the items lore lines contains this text.
+ **Amount:** A ranged value, can be >0 or <3 or 1to4 or just a single number. *Note if Supply is used its recommended to use single numbers or atleast something with >0*
+ **Supply:** *true/false* If true the requirement supplies the quester if the required mythicmobsitem.
+ **Material:** The items material name or *ANY* for all materials match.
+ **NameEnds:** String the items displayname ends with or *NONE*.
+ **MythicItem:** The internal name of the mythicmobs item.
+ **ItemMarker:** The name the item will be tagged with. Recommended is to just put in the internal mythicitem name.
+ **RemoveItem:** *true/false* Removes the required item from the players inventory.

```yaml
    requirements:
      custom-requirements:
        req1:
          name: MythicMobs Item Require
          data:
            Lore: NONE
            Amount: '1'
            Supply: 'true'
            Material: ANY
            NameEnds: NONE
            MythicItem: MMDIRT
            ItemMarker: MMDIRT
            RemoveItem: 'false'
      fail-requirement-message: Go and find the MMDIRT item!
```

#### MythicItem NPC Deliver Objective

Quests stages can be completed by deliver an item or an MythicMobs item to an citizens NPC. Requires citizens plugin to be installed.

##### Configuration Notes

If the item or items were created by reward, requirement or mythicmobsext's dropmythicitem mechanic you can use ItemMarker to refer to the mythicmobs item. Alternativly use Material, DisplayName and Lore. All attributes and options are required:

+ **NPC IDs:** The integer id of the citizens NPC or an arry like 1,2,3,4
+ **Lore:** Text that needs to be contained in one of the items lore lines or ANY.
+ **HoldItem:** *true/false* If true the item the player need to hold the item to complete the objective.
+ **Amount:** The amount the player needs. Like >0 or 1to4 or <20 or just a single number like 1 for exact match.
+ **Material:** The items material name or ANY.
+ **NameEnds:** DisplayName of the item ends with this string or NONE.
+ **TimeLimit:** Unused
+ **MythicItem:** The internal mythicmobs item name. Stored in the items tag MythicMobsQuestItem, which is created by mme's dropmythicitem, reward and require.
+ **TargetConditions:** Targetcondition is always the player. Use && and || to build a boolean expression out of some conditions.
+ **Conditions:** Conditions the npc entity should have. Not sure how useful this is. Same as TargetConditions but compare the quester npc.
+ **RemoveItem** *(true/false)* Removes the delivered item from the players inventory/hand.

This example use the MythicItem to refer to a mythicmobsitem. There is no need to use material, lore or displayname.

```yaml
    stages:
      ordered:
        '1':
          custom-objectives:
            custom1:
              name: MythicItem NPC Deliver Objective
              count: -999
              data:
                NPC IDs: '0'
                Lore: ANY
                HoldItem: 'true'
                Amount: '>0'
                Material: ANY
                NameEnds: NONE
                TimeLimit: '-1'
                MythicItem: MMDIRT
                TargetConditions: NONE
                Conditions: NONE
          start-message: Bring the questitem MMDIRT to my friend bubu
          complete-message: Thank you for your help!
```

Please submit issues or pull requests as appropriate. Happy questing!
