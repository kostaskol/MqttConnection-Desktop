<h1> Software development for network systems - Desktop: </h1>
    <h2> Index: </h2>
    
+ [**Data Base**](#db)
+ [**Topics**](#topics)
  + [**Message Format**](#message_format)
+ [**Classes**](#classes)
    + [**Class Methods**](#methods)
+ [**Screenshots**](#ss)
+ [**Algorithm**](#algorithm)

<hr>

+ <a name="db">**Data Base:**</a>
    + The programme uses the MariaDB database and JDBC driver which
        successfully ran on Linux (ArchLinux).
    + For the application to run successfully, [this sql dump](https://drive.google.com/open?id=0B103a5xnSqfobzhpQmgyc1JJUVk) must be imported into the MariaDB server running at
          the location you specify in BundleClasses$Constants.DB_URL.
    + Note that the database is *not* created by the application.
    + **_Note:_** To connect to the database, a username and password must be provided in the BundleClasses$Constants.USER - PASS
    + The database's name we used was "project16". To use a different name, please change the value of BundleClasses$Constants.DB_URL
    + The database contains the following tables:
            + clientAverages - Where the connected clients'
            information is stored
            + log - Where all incidents are stored
            + settings - Where all of the user's profiles
            are stored
            + settingsProfile - Where the currently selected
            profile is stored (name not very accurate.
            Did not have time to change it)  
            
    + <a name="topics">**Mqtt Topics:**</a>
    <table>
    <td>Topic</td>
    <td>Subtopics</td>
    <td>Usage</td>
    </tr><tr>
    <td>log</td>
    <td>-None-</td>
    <td>Operation messages from the desktop client are sent here for better readability</td>
    </tr><tr>
    <td>connections</td>
    <td>-None-</td>
    <td>Everything that has to do with a connection.</td>
    </tr><tr>
    <td>connections/newConnections</td>
    <td>-None-</td>
    <td>When a new android client connects, they notify the desktop client by publishing a message containing their UUID to this topic</td>
    </tr><tr>
    <td>connections/connected/</td>
    <td>-None-</td>
    <td>Already connected android clients publish to this + their UUID topic</td>
    </tr><tr>
    <td>connections/connected/\<Client's UUID</td>
    <td>-None-</td>
    <td>Already connected android clients publish to this topic.</td>
    </tr><tr>
    <td></td>
    <td>warning</td>
    <td>The desktop client publishes the warning messages to this topic</td>
    </tr><tr>
    <td></td>
    <td>danger</td>
    <td>The desktop client publishes the danger messages to this topic</td>
    </tr><tr>
    <td>stopSounds</td>
    <td>The desktop client publishes the stop all sounds messages to this topic</td>
    </tr><tr>
    <td></td>
    <td>acknowledged</td>
    <td>The client publishes to this topic of a specific UUID to inform the android client that the desktop client is online</td>
    </tr><tr>
    <td>connections/requestAck</td>
    <td>-None-</td>
    <td>Before an android client goes into Online Mode, they request and acknowledgement message from the desktop client (ensuring that it is running)</td>
    </tr><tr>
    <td>mainClient/disconnected</td>
    <td>-None-</td>
    <td>If the desktop application disconnects ungracefully (it always does), all of the connected clients are notified and go into Offline Mode</td>
    </tr></table>

    + <h3> <a name="message_format">**Message format:**</a> </h3>

    <table>
    <tr>
    <td>Topic</td>
    <td>Subtopics</td>
    <td>Message format</td>
    </tr><tr>
    <td>log</td>
    <td>-None-</td>
    <td>\<"Log message type" - "message"\></td>
    </tr><tr>
    <td>connections/newConnections</td>
    <td>-None-</td>
    <td>\<"Client's UUID"\></td>
    </tr><tr>
    <td></td>
    <td>connections/connected/\<Client's UUID></td>
    <td>-None-</td>
    <td>\<"Client UUID"/"latitude"/"longitude"/"light sensor value"/"proximity sensor value"></td>
    </tr><tr>
    <td></td>
    <td>warning</td>
    <td>"warning"</td>
    </tr><tr>
    <td></td>
    <td>danger</td>
    <td>"danger"</td>
    </tr><tr>
    <td></td>
    <td>stopSounds</td>
    <td>"stop warning"</td>
    </tr><tr>
    <td></td>
    <td>acknowledged</td>
    <td>\<The frequency at which the android client should contact the desktop client\></td>
    </tr><tr>
    <td>connections/requestAck</td>
    <td>-None-</td>
    <td>\<"Client's UUID"\></td>
    </tr><tr>
    <td>mainClient/disconnected</td>
    <td>-None-</td>
    <td>"disconnecting"</td>
    </tr></table>

    + <a name="algorithm">**Algorithm**</a>
        1. A threshold and a current lighting value are supplied to the algorithm.
        2. For the first set amount of times we receive a lighting value, we add it to a sum and do nothing else.  
           When we have enough values, we calculate the user's current
           environment average
        3. We calculate the actual threshold ((100 - threshold) / 100)
        4. If the lighting value is greater than the user's current average + the actual threshold, we recalculate the average
        5. If the lighting value is less than the user's current average - the actual threshold, we warn them about a possibility of danger
        6. If the lighting value is within these two limits, the user is persumed in a safe state

This is the end of the desktop application's README file.
For information regarding the android client, please
see the relative repository [here](https://github.com/kostaskol/SDNS-Android)
