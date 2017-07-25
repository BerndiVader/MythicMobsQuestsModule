# Quest Addon for MythicMobs 2.4.5 or any higher version and Spigot 1.8.8 - 1.12

### Changelog

- 25.7.2017 Update: added Notifier enabled and Notifier msg option.
- 23.6.2017 Update: added Faction option. Build against Spigot 1.12, Quests 2.8.6 & MythicMobs 4.1.0
- 29.5.2017 Update: fixed NPE's if there are no values & added "ANY" for MythicMobs mobtype.
- 6.5.2017 Update: build against Quests 2.7.6 & MythicMobs 4.1.0
- 4.3.2017 Update: fixed bug that only ActiveMob livingentity instances count & added level support

### Installation Instructions

Install (upload, extract, copy) the jar into plugins/Quests/Modules/ and restart the server.

### Configuration Examples

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
                Internal Mobnames: MythicMobName
```

#### Configuration Notes

You **must** include all "data" variables, or the module will not work. 

If you want a notifier message displayed, set *Notifier enabled* to true and use *Notifier msg* to customize the message. You can use **%s%** and **%c%** as placeholder for whole amount and actual count.

If your mobs do not utilize the "level" feature of MythicMobs, you may use *Mob Level: '0'*, but it **must** be included. This field also accepts ranges, e.g. *Mob Level: '0-10'*.

If you want to filter per faction use *Mob Faction* option. Valid is **ANY** to match all factions or a single value or a arraylist eg: *faction1,faction2,faction3*

The *Objective Name* is what will be displayed to the player, and may be set to whatever you like accordingly.

The *Internal Mobnames* field references the names of MythicMobs as specified in your MythicMobs config files. In the above examples, such MythicMobs would be internally named SkeletalDarkKnight and MythicMobName, e.g.:

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
