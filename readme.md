# Quest Addon for MythicMobs 4.0.1 or any higher version and Spigot 1.8.8 - 1.12

#### if you use Quests version below 3.2.7 use the *MythicMobsQuests285.jar* module!

### Changelog

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

### Installation Instructions

Install (upload, extract, copy) the jar into plugins/Quests/Modules/ and restart the server.

### Configuration Examples

#### Kill MythicMobs Objective for Quests

```
quests:
  custom1:
    name: Hunt the Skeletal Dark Knight
    ask-message: Will you help us kill a Skeletal Dark Knight?
    finish-message: Thank you for your help!
    stages:
      ordered:
        '1':
          custom-objectives:
            custom1:
              name: Kill MythicMobs Objective
              count: 1
              data:
                Mob Level: '0'
                Mob Faction: ANY
                Notifier enabled: 'true'
                Notifier msg: Killed %c% out of %s%
                Objective Name: Kill a Skeletal Dark Knight
                Internal Mobnames: SkeletalDarkKnight
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
                Mob Level: '0'
                Mob Faction: ANY
                Internal Mobnames: ANY
                Notifier enabled: 'true'
                Notifier msg: Killed %c% out of %s%
                Objective Name: Kill 10 of any MythicMob
```

#### Configuration Notes

You **must** include **Mob Level, Internal Mobnames, and Objective Name,** or the module will not work. The other options can be omitted.

If you want the player to see a notification message tracking their progress, set **Notifier enabled** to true and use **Notifier msg** to customize the message. You can use **%s%** and **%c%** as placeholder for whole amount and actual count.

If your mobs do not utilize the "level" feature of MythicMobs, you may use **Mob Level: '0'**, but it **must** be included. This field also accepts ranges, e.g. *Mob Level: '0-10'*.

If you want to filter per faction use *Mob Faction* option. Valid is **ANY** to match all factions or a single value or a arraylist eg: *faction1,faction2,faction3*

The **Objective Name** is what will be displayed to the player, and may be set to whatever you like accordingly.

The **Internal Mobnames** field references the names of MythicMobs as specified in your MythicMobs config files. In the above examples, such a MythicMob would be internally named SkeletalDarkKnight, e.g.:

```
SkeletalDarkKnight:
  Type: WITHER_SKELETON
  Display: '&dSkeletal Dark Knight'
  Health: 150
  Damage: 7
```

Note that this is the **internal name** of the mob (used to initiate the YAML block), *not* its *Display Name* or *Type*. This field also supports multiple options, separated by commas, or ANY, e.g.:

```
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
                Mob Level: '0'
                Mob Faction: ANY
                Internal Mobnames: ANY
                Notifier enabled: 'true'
                Notifier msg: Killed %c% out of %s%
                Objective Name: Kill 10 of any MythicMob
```

Please submit issues or pull requests as appropriate. Happy questing!

#### MythicItem rewards for Quests

Quests can offer MythicItems as rewards for completing them. The syntax looks like this:

```
    rewards:
      custom-rewards:
        req1:
          name: MythicMobs Item Reward
          data:
            Item: ImbuedHelm
            Amount: '1'
```
