# Quest Addon for MythicMobs 2.5.1 or higher

Copy the jar file into your servers Quests/modules/ folder. Restart the server.

Example config:

          custom-objectives:
            custom1:
              name: Kill MythicMobs Objective
              count: 5
              data:
                Objective Name: Kill some MythicMobs
                Internal Mobnames: mm_zombie,mm_skeleton,mm_creeper
          finish-event: GoodJob
          death-event: DeathFail