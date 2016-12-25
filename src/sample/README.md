Project 2016 Phase 2:

+: Added new<br>
✓: Completed<br>
~: Started. Not completed<br>
--: Removed<br>
!!: Changed

+ 21/12/16:
    + (+) Created and tested sample MQTT Manager
    + (+) Created some of the DBManager
    + (+) Created a sample window
    + (+) Created project16 DataBase in MariaDB
        + Tables: 
            + log: Where incidents are stored 
            + settings: Where user settings are stored
            + settingsProfile: Currently selected settings 
            profile

+ 22-23/12/16:
    + (✓ +) Completed DataBaseManger (Perhaps not.
    Need to recheck)
    + (✓ +) Created profiles for settings
    + (~) Added search results window and 
    functionality. Haven't finished pages yet 
    (how do I do that??)
        + Changed SearchResult's member variables
        to type String/Integer Property
        (useful for adding data to the TableView 
        node)

    + Plans for tomorrow (24/12/16):
        + Create search result window's pages
        + Add complete MQTT functionality
        + Perhaps (not very likely), start working 
        on offlineMode for Android
    
+ 24/12/16:
    + (✓) Completed the search results screen
        + (+) Added colour to rows according to level of danger
        + (+) Added pagination and full functionality to it
        + (!!) Added two more columns to search
        results (latitude - longitude). These columns 
        cannot be used for searching (values are too wide)
    + (✓) Added full MQTT client functionality
        + Note: Only one android device gets 
        notified on DANGER warning
       
    + (!!) Changed some of the DataBase's tables
        + (+) Added port option to settings
        + (+) Created a new table which holds
        each client's average lighting
        (ClientAverages)
        + (!!) Added two more columns to table
        "log" (latitude - longitude)
        
    + Plants for tomorrow (25/12/16)
        + Notify both clients of a high
        possibility of impact
        + Code cleanup - Comments
        + Done (?)
   