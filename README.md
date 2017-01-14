+ Project 2016 - 2017 - Phase 2 - Desktop Client:  
    + Index:     
        + [**Data Base**](#db)    
        + [**Topics**](#topics)    
        + [**Classes**](#classes)     
        + [**Class Methods**](#methods)  
    _____________________________________________________________________________________________________________________________________
    + <a name="db">Data Base:</a>
        + The programme uses the MariaDB database and JDBC driver which 
        successfully ran on Linux (ArchLinux).
        + The database is *not* created by the application and must be imported from a self contained .sql file.
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
        + Diagram:  
         <p align="center">
            <img src="https://lh3.googleusercontent.com/RTBO6ryXwmlIhg1kr3BfixPynJuC79nL6hjNR1uR4LidBf9gBFgxOL1oZtDBHf-AsQSq1UsrTIu4TzB0mXuFlSmLPb-gc7lrdg0aIHdDzguRSlgbkXNtr4Dl4AodJB6UAhekBYdMVv2BwMDYR8WwHQZhObpK3N8FNBP2TU5-7AQ-hU3FZBslaWaJ8k7Mkvg3O24fR-UZzMEhO5kWONZbELptQ1dMduMOLFyaLRSK1HIPsSvYqz1zlaF8tP8VaZFo1lonsdiG8HHAxW0PNFYCt8t8VcwB313GCYC79Ht5TFLALYX-50hiaHLuQZaC640CWsKzG-yKrj1L8FInw2vBBPdFi1XY5wgMnWr9LxJ19ZfGJc08sfA2959WsVzCYJz9pos6sjMK3vSddt5dpolruu16KveMpS2JL2mVGnbuXyeZuOyGX7EKesTO7UjYYSZoRuHDWFwalqHzEEE5M8VZQSx2WWRzp09fBf8cuTuOPbehUUTatVwYxu4dy9k4aKXhL0IITnc0vNT6s2Xs0wvCPcpjb9evHULdZI-gx2Mnv06tICMrZ83iyoBdIx7tBfKD2thyMJVC8rJWPaNzfSprVmfP0R85FMRihIqNY5KmMg12NJQJXQOg=w609-h659-no"
            />
        </P>
    + <a name="topics">MQTT Topics used:</a>
        + **log:** Operation messages from the
        desktop client are sent here for better 
        readability
        + **connections:** Everything that has to
        do with a connection. 
        + **connections/newConnections:** When a new
        android client connects, they notify
        the desktop client by publishing a message
        containing their UUID to this topic
        + **connections/connected:** Already connected
        android clients publish to this + their UUID topic
        + **connections/connected/\<Client UUID>:**
        Already connected android clients publish
        to this topic.
        
        + **connections/connected/\<Client UUID>/**
            + **warning:** The desktop client publishes
            the warning messages to this topic
            + **danger:** The desktop client publishes
            the danger messages to this topic
            + **stopSounds:-** The desktop client publishes
            the stop all sounds messages to this topic  
            + **acknowledges:** The client publishes to this topic of a specific UUID to inform the android client that the desktop client is online
        + **connections/requestAck:** Before an android
        client goes into Online Mode, they request
        and acknowledgement message from the desktop 
        client (ensuring that it is running)
        + **mainClient/disconnected:** If the desktop
        application disconnects ungracefully 
        (it always does), all of the connected clients
        are notified and go into Offline Mode  
        <p align="center">
            <img src="https://lh3.googleusercontent.com/EMTDe2A2bCl9MuYJKZhCLX_tX7UdKtdf8fYdxdcuCFidDoLNWDPSGSRl4oVtERlpl-79DWBNPMcfacocgttfsDVU6azZSdBP_WkuWFD2jJ2F8gW6waPMZqLLN0g3wm8OvVqph2EdKa19BJYjsqYHRyAvq9kLpfUcwLtEaH2fTq80pnvMS5q1wzfTGcRKl0gtI-GgZoyINp9rIRpnKFovbLAk6h2PP1O4wwLJT99YfX0txHF1OFchctcVTubwk4zjAbNMjcq3cL0Ad6cVlIBt_71riQmHjeu2gCpdY2uXs68ZlwlUXNOzh9X7C2BwyIrDisLDZmPhtLoNUSl5eCoe_j5sL4CZAVWomGBtwjofZsbiA3fe1D4qyizQ1eRNseiFzk5f9Rcaoa3z4FiseBP9KKZbIyibwM7xOWOv-6B3U-l-9cg8B4FxBR1hLd7QfB9xZuQ80dU0NywqltCgg1tsRDz2-pbNJ1aL095OfSZeZwjX290c1as0ZkAkJZhaa1_eKt1ZajAqfI2CZdJIfhzdQIbiKmLn-NgJ6fQ4Mh6Ve1OXOlOXtiuRcmVbKGWFeoQXeBHu_o1KupHzSX5Xff5GLd_zgDs7cP_fIN0VhO6HFdMsm4GmYExz=w1063-h501-no"
            />
        </P>
        + <a name="message_format">Message format by topic:</a>
            +  **log** -> No standard format
            +  **connections/newConnections** -> \<"Client's UUID"\>
            +  **connections/connected/\<Client's UUID\>** -> \<"Client UUID"/"latitude"/"longitude"/"light sensor value"/"proximity sensor value">
            +  **connections/connected/\<Client's UUID\>/**
                +  **warning** -> "warning"
                +  **danger** -> "danger"
                +  **stopSounds** -> "stop warning"
                +  **acknowledged** -> \<The frequency at which the android client should contact the desktop client\>
            +  **connections/requestAck** -> \<"Client UUID"\>
            +  **mainClient/disconnected** -> "disconnecting"
    + <a name="classes">Classes:</a>
        + The Class Diagram:
        <p align="center">
            <img src="https://lh3.googleusercontent.com/hBPg0P4vuwBFw9Cb8hQXO7Hj5EueNqNw8gkcC9WfQ20RM0dZM-AQlmuT5DvB6Stu_-i2mEclZqaeJF0DsjUi8CRt-r3ekyh28T2R5_waEQZx2TyVVrC4l_j9UvK9Eyzb1osZAGn9qxOJqlPPTFZZcnolDGo6oqbgC0iyTlQTIrRY8Wl7QcufnNaPiKXq4eKRMR2p1rpi8G783yAf-lwImOGFXzqeqYJYVdu2l3QTpmg_vlq-0PKga3UDQZDVCQeYBYz-vD6bi2FRU_mAx-Vm49SPMqHdcewcSZEbTo6lhFDPUhGrS-zt0COMAt6HrLTgbcOFQ6bAvhY4ONf6TzG5FZpr4QxggbC7JyMkhOIAzy0vQi2-HtQ8wE4CcOlSER-SMyn5NCjERYlTSI_BHE5Jf1h-JXKBY77CUOp-5nuOiQATAEujl9g15cZq8HzMwjzRlXnyPfMfEOO2xgNwpPb0mWWiCDOzzFLqySwUrOIjwRv7waxn1x7LHQTjQakGOT70UTP4bvg0tu2RuAvBTeEv7tWKBpsf_I3SlDyCZqs0ZRWyA9fWALoecenBYMFcszWkFgvC4Qsfkc3HqmRHvCX1O4K27geGknJ6mhKQ4u_0nDnHKEcBi52Q=w965-h659-no"
            />
        </P>
        + A brief description of all the classes and their methods:
    
        + There are 4 Class Types (in this project):
            + Managers: They simplify or automate an operation
            + Utilities: They provide some useful methods
            + Bundles: They pack data for easy transfer
            + Threads: They start other operations inside a thread
            + Controllers: FXML Controllers
            + Windows: FXML Windows
    
        + A description for all the classes can also be found 
        at the beginning of the corresponding file 
    
            |Class Name           |(Outer) Package | Class Type   | Description |
            |---------------------|----------------|--------------|-------------|
            |ClientAverage        |BundleClasses   |Bundle        |Holds some client specific info about the environment's lighting, etc.
            |Constants            |BundleClasses   |Bundle        |Self explanatory|
            |Incident             |BundleClasses   |Bundle        |Holds all the information about an incident|
            |IncidentTime         |BundleClasses   |Bundle        |We use this when we don't need all the information about an incident, but only the date/time and the client's id
            |Profile              |BundleClasses   |Bundle        |Holds the id and profile name of a user's profile
            |SettingsBundle       |BundleClasses   |Bundle        |Holds all the information about a given user's profile
            |Main                 |-Main-          |None          |Starts and stops the application's main threads(MqttManagerThread, WindowThread)
            |DataBaseManager      |Managers        |Manager       |Handles **all** of the applications DataBase communication
            |DataBaseManagerThread|Managers        |Thread        |Handles all of the **fire and forget** DataBase communication (calls the corresponding DataBaseManager functions)
            |DateAndTimeManager   |Utilities       |Utility       |Provides some useful date and time functions (current date/time, splitting date/time into parts)
            |IncidentManager      |Managers        |Manager/Thread|Handles all of the operations that need to take place to manage an incident
            |MqttManager          |Managers        |Manager       |Handles all of the communication with the MQTT broker (receives messages, publishes warnings, etc)
            |MqttManagerThread    |Managers        |Thread        |Starts the MqttManager inside a thread and handles the communication of the main application with it (if required)
            |SearchController     |Windows         |Controller    |FXML Controller for the Search tab
            |SearchResultsWindow  |Windows         |Window        |FXML Window that presents the results in a table
            |SettingsController   |Windows         |Controller    |FXML Controller for the Settings tab
            |Window               |Windows         |Window        |The main application window
            |WindowThread         |Windows         |Thread        |Starts the Main application window inside a thread
        + <a name="methods">A short description for each of the class's methods:</a>
            
            |Class Name             |Method              |Description|
            |-----------------------|--------------------|-----------|
            |Main                   |main                | Starts the application's two main threads (WindowThread, MqttManagerThread)
            |                       |updateThresholds    | Notifies the MqttManager that the user changed the thresholds in the Settings tab
            |                       |close               | Only called by Windows$Window.stop(). Interrupts the threads and closes the application
            |WindowThread           |@Over run           | Starts Window inside a thread
            |Window                 |launchWindow        | Launches the main FXML window
            |                       |start               | Sets the stage according to the main_screen.fxml file
            |SearchController       |search              | Reads the values given by the user and queries the DataBase accordingly
            |SettingsController     |@Over initialise    | Initialises the profile ComboBox with values from the database and fills in the settings according to the last chosen profile by the user
            |                       |save                | If the user is updating an existing profile -> Checks for input errors and updates the Data Base. If the user is creating a new profile -> Checks for input errors and inserts the new data into the Data Base. Returns the newly saved data (useful for apply)
            |                       |updateProfile       | Called by settingsController.save when a profile is being updated. Updates the ComboBox and the Data Base with the given data
            |                       |updateProfilesCombo | Updates the profile combo with any new additions to the profiles list
            |                       |deleteProfile       | Deletes the currently selected profile from the combo box and the database (The default profile cannot be deleted)
            |                       |handleComboBoxAction| Updates the TextFields whenever a new item is chosen in the ComboBox
            |                       |apply               | Saves the user's settings and notifies the MQTT client (through Main) of the change
            |SearchResultsWindow    |createTable         | Initialises a table view with the contents of the incident list
            |                       |createPage          | Creates the pagination of the current TableView (does not work very well)
            |                       |showWindow          | Displays the Search Results Window
            |DateAndTimeUtility     |getDate             | Returns the current date
            |                       |getTime             | Returns the current time
            |                       |dateToParts         | Splits the given date String into 3 parts (yyyy-mm-dd)
            |                       |timeToParts         | Splits the given time string into 3 parts (hh\:mm\:ss)
            |MqttManagerThread      |@Over run           | Creates a new MqttManager object and starts it
            |                       |updateThresholds    | Notifies the MqttManager of a change in the thresholds
            |MqttManager            |getClient           | Creates and returns a new MqttClient object
            |                       |connect             | Connects the client with the given mqtt connection options
            |                       |connectClient       | Calls getClient and connect to connect to the MQTT broker (The URL, Port and clean session variables are given as part of the SettingsBundle that must be provided with the initialisation of the MqttManager object). Also subscribes to the newConnections and requestAcknowledgement topics (see [topics](#topics))
            |                       |publish             | Publishes the given message to the given topic
            |                       |subscribe           | Subscribes to the given topic (with a QoS of 2)
            |                       |@Over connectionLost| Clears the clientAverages table from the Data Base
            |                       |messageArrived      | Parses the topic in which the topic arrived. If it's a new connection -> Subscribes to the connections/Connected/\<Incoming message(UUID)\>. If it's an already connected client -> Parses the message ([For message format see here](#message_format)). If the topic's UUID is different from the message's UUID, it does not accept the incident. If not, uses IncidentManager to handle all of the incident's operations (save, notify client, etc) by supplying it the current user's thresholds. If it's a request for acknowledgement -> Notifies the requesting client about the applications availability
            |                       |updateThresholds    | Updates the object's thresholds to the new ones
            |                       |deliveryComplete    | Not Used
            |IncidentManager        |@Over run           | If the provided light and proximity values are within the thresholds -> If another incident has occurred with an at most 1 second difference, notifies both clients about imminent danger. If not, notifies the current client about a possibility of impact **if** the client is not currently in warning/danger mode. If not, notifies the client that the danger signal should stop if one is playing (The android client checks this)
            |                       |checkValueLight     | Queries the Data Base to find the current client's average, times they have contacted the desktop application, the current light value sum and whether the client is currently ringing. If they have provided enough values (this can be changed in the BundleClasses$Constants) and their current light sensor value is below the client's average lighting - the threshold, returns true. If their current light sensor value is above the client's average lighting + the threshold, resets the client in the clientAverages table and returns true. (The client's average will be recalculated the next Constants.AVERAGE_TIMES times they contact the server). If their current light sensor value is within the client's average lighting +- the threshold, it returns false
            |                       |checkValueProx      | Checks whether the given proximity value is below the threshold. Returns true or false accordingly
            |                       |reset               | Resets the given client's information
            |                       |checkIncidentTime   | Returns true if the last incident occurred within the same second as the new incident
            |DataBaseManagerThread  |**Note 1**          | We provide to the constructor a String that represents the operation we want the thread to perform.(Valid strings are defined in BundleClasses$Constants). The class offers 6 constructors. We call them according to the operation string
            |                       |**Note 2**          | All of the methods' names explain the operation they perform
            |DataBaseManager        |saveIncident        | Inserts the given incident into log
            |                       |getLastIncident     | Returns the last incident's date and time or null if the last date is not today
            |                       |updateDanger        | Changes the level of danger to 1 for the specified UUID and incident date and time
            |                       |searchDB            | Constructs an SQL query by going through the supplied filter list and only inserts the ones that are given by the user
            |                       |getSelectedProfile  | Queries the settingsProfile table and returns the currently selected user profile
            |                       |getMaxProfilesID    | Returns the current max ID for the settings table (Didn't use AUTO-INCREMENT for the profileID column at first and based the entire profile creation logic on that. Did not have time to fix.)
            |                       |switchProfile       | Changes the currently selected user profile in the settingsProfile table to newId.
            |                       |updateProfile       | Updates the settings of the specified ID in the settings table
            |                       |deleteProfile       | Deletes the specified profile from the settings table
            |                       |saveNewProfile      | Inserts a new profile into the settings table
            |                       |getProfile          | Returns the user settings for the specified profile
            |                       |getAllProfiles      | Returns a list of all the profiles contained in the settings table
            |                       |getClientAverage    | Returns the specified client's average if it exists. If it does not, it inserts a new one with the input UUID and returns that.
            |                       |insertClientAverage | Inserts the client average into the clientAverages table
            |                       |updateClientAverage | Updates the specified client's average in the clientAverages table
            |                       |clearClients        | Deletes all the entries from the clientAverages table. Called when the programme closes
            |                       |executeStatement    | Executes the specified SQL statement
            |                       |executeQuery        | Executes the specified SQL query and returns the results
            |                       |closeConnections    | Closes the connection with the Data Base
            |BundleClasses          |*                   | All of the bundle classes only contain their getter - setter methods

    + **Screenshots**  
        + The application starts in the Main screen which contains 2 tabs:
            + The Search Tab:
                <p align="center">
                    <img src="https://lh3.googleusercontent.com/gpCrFCW23B5RzEcTW1zD45VoHURVFCwqtrsNbA7_wW5VsdDS6sPlrS_XMAHlBJNY5s6XTqJ0dEUEtPV0cATqYdO7NP_GKhLT1JP1iCvEKSRc0X8uDsZC0Kermb99TaXfkfn8-nN6EAubCAeV61h6jQoUrvl-WfMVUWgWkujqDezkx4-KTd5F10_tSvdCKZVH0OmFNaKc_bW0QlbG1lBTmsK1dgxUV-JfIHfa0U5_y3S8W7S0DmM6TSHoZCaDL96p33-bFEjfC9vcM_6fUjwt2rl_zLVg9PyzVMOkDR378UxMxjQ_zf-LkydLIZDTnb5Rqabkff1i5OAnJUzlT7oJeWFOAmrW_5KfXp7kb4Sy-fB9tbhuyYxVlANhiF4O86mtkZ2grBhUtRYzMI9Nyl4W9-YhXmdmEJ0ylaDAX6vxAv0_TtDHkKugKFWMUwEBnU_oNNNb2RVLrwXg1Gpowx4QVSVmCx2ZxEE4a-DDK1SwwZxrjWKJJ8DNWtz1RtnEm-qE7AqYKEZe_l8Ube1gNXPHljSmH4H0TZcxqc8Wgnut5_i3Bgj5bEQ5cFc1Zi1oKoeKzgTDfdxlCx_KaBrcK3iZwcy4Ki4rGAP69X_V2ICSQBDrLcJZfHH4=w597-h489-no" />
                </p>
                Here, the user can query the Data base by
                filling in the given filters. They can search 
                by any and all fields, except for Date and Time together.
                
                + An example:    
                We search for incidents that happened 
                    + To users with a UUID that contains the characters:
                    "f28"
                    + On the 7th of January, 2017
                    
                    <p align="center">
                        <img src="https://lh3.googleusercontent.com/A-053ZZvedUWmwCMGW8_qv2VBEmcsC7JoFW0K871pbOPQGsMQSbPYS3AdX7VL_fros75pT0kmz7TuSecgFLcZ3K6xF_x1sTf7B-wp36f4y3GLj0YklWtFZU7hfYgBDIZX1xzwfC6_wL8GPbgj1TUOfdFKw_EenqWt5y5q1MTNVkP_Tb2yJQjGBL2rKtdBGKE-r0o4Tb30Zdhi27PH_Gi0mfY6BYK280p-5dwwY0bFR-uX2F1OMQIQ3fjliI4DxStGesLz-8p6pk-avtqMzUFxHZmesVDVygWbONzQCNChqlUU77Ufl6rqyNdgCT5LNouZPLZ4BhyVTddAEAnvFGlnByTXXCIzxRvxOMCz523CVS8vAExY2Vg7EfNg604XD0yu_7WzoBYxhe-s8ke5M625AvOLPUINmSED6obsOz8rFfM2DEuflYuPcXbbR8kCW-PfqDLXnP0fQMc6I1yCtLc5r_7u_4YNe9a19rPBew34a9FMh1gZTR8AEloRFzlrtjxMbh9wEWDQ2WUlaLOnalXa3vzZx8LSul3izqzkLFfShMlNgXZfm2Z07ni19-IqRDWQJbR7x3Z5kauYeynrMcCiVlVQn-5_iGnBM-w3mDBWn3xFKPrzrKc=w597-h487-no" />
                    </p>
                    + The results are:
                    
                    <p align="center">
                        <img src="https://lh3.googleusercontent.com/ydnmG4LYeLqGYQ4GAfeEQ7J3-GBq6uel51rC7Jm6yzzl2RyaDDHBrkUI4PZAPMpyBNpo8kSBLvqtPoUT6yhnr-ebOFWdZbvY54yUMm7UVHDsp-aqfyJpOe6vRtIVBe8DqVnbZHu2eRDsRWLSPG_jl91yKH-VHJ0RdlfZeD66Zr5RuN32AsLY0yKrxvi7GBL23pB5cv1wBFwPF1qd_NZgMODDhbUnO4hWLm_o2eSYNe7S7-gSyEvQqchPzw3bVpyxbb-ESjGQ5G0hXdVTETiAsGlmbaSz8Bah0VULXl6_O1SJwdrlNOn7bm6PWvpAiLdWJR7MCspWaJPHO-SlCDFiyogJthujoaL0HqbVAup7ZgQKuwScrdg_Dvl5G5xOaBoDPaH_L8j6919UzwyPsbqijM71IjI_lyFlbcyy7i0jqu-OYL_iKRZwbyypARpcqNgf6FoxSvLBj3O9tnfq3po1VUcmMxUiw6FU64CTcfCTANPjY0KXH__oVf6OaGhJ6XW0FVs-a5n8Mz-F1QJC1Ve2983IuWGOhNqRygfPKrb5RMrpbwylTLLd4sYciR2qVLmOTSTIHeR398YSZp3AmWRrpSmW_rTHBggyOlV__hNQYlPEseTevdhv=w737-h574-no" />
                    </p>
                    
                    <p align="center">
                        <img src="" />
                    </p>
                        
            + The Settings Tab:
                <p align="center">
                	<img src="https://lh3.googleusercontent.com/OdcrOkLP-f5WpzVh9oPdLBcPJ5zVg6BQQMiggBYQoYKgAXgbaJV3lpeh8ZLhrMRm9HHkvSZFM2B_Isz4k3IPOEaiwLDzuc2ZKXtEZK2RqI3B1r1NEnHqbCEyQ3p3JEOr2pFxbV9NVgilELEGLVzQ0BG-eue7vQVj_ZAOkH5l5F6kqBQPy6tLicoKG7X7JvHdCSq7jJjngAZcV79bjV0yizdKUVP2cuY8d4seQtz3wMlAp4q6M0U--WCRQeGjIXBrKPdnlEU4csu8ZtK4pmsedimcqQ9UljhUM_Dp3y8dWXLsZ0TDzAhZzB6Ot2NgUkd0JFuvaPa1KzlwXpaKK6tMgbEexniiL3tLxV61utJYAxF5_cwScK4o6vSmNPrTEqdyMyjmTDgO7J6_SfKOZgN9l6VwoIrIPorBQaUy6vZyLJVVzr0-uBQCPGoPUTvarsZ2T21nci4c09lfFLiIRLAav0bKSlXGDNgqp0FoYr4qJc2LjUNy8tL-UIYKivX8I12aYtLE6GzwQ-ufds9O5bcyo7ONm8xaSS3ALXN2dhK0sBWgCf2hyU74cW7QvS08IFDLEII90nhoMhZ9O5z69yW2Z-iz2R2JTt7tB-nUCbxLQG2_b2nwZEzK=w598-h486-no" />
                </p>
                
                + On the upper right corner, we can see the
                currently chosen profile (Default) and
                on the rest of the window, the user's current settings
               
               + We can also create a new profile:
                <p align="center">
                	<img src="https://lh3.googleusercontent.com/Ew-WyQe2zYDScc2vShZW7B1XLAhcrZY3byV5NGosIaLWvVtjlbfKJO184s5TYhcFbbIwgGW57R0LMYX_5J4iPAL2Si16YU6d6V88hzyHFPBDxQ_vkz-eIHNdOqdLWn_d4TWMA8YO82hMNoolWZuKH77j2Ol5YQE9LcrvQ321LLBWldCVGofdvSowUeKQfnUD3f0efjAGMYTP6mF_Au1SPjd_A9kMG3FrgGKqd_p0C7Ss5YM_IDJ0k1ovyenchLfSd1Dz29ZeKaFGHHuGcyklCpZSnsVxMzY_6UpNMgZpQ-83uodLokTc5eyB-cwO16AAHaySc84qApPGByHrJMYbomhugwe0n3G2q-R66GLgR4e658PIhh-gWqHN3ylaLnnhpoAxXxEbq7KwOvr9N4h66I0PhxmfsliKQmj_Dqqpr2dNtClIB0GKtmimRjzyNt-_EYSzCDrU_LsPx5MEBSrvAb8XTsOjDDQils2qFpIavJdJ5aFGAB8a9BzFX3M3c4e3rnVh6KK72cryoakaBuv5OO7Q4FSSy0LeQDlvugJzp5tZJ3i1TA8qH0c85iugfwWLr09kik2BwRdtBS2esekvriig58UEnNBfzzTTjo5EWxmKQt_WFI_q=w600-h486-no" />
                </p>
                
This is the end of the desktop application's README file.
For information regarding the android client, please 
see the relative repository [here](https://anapgit.scanlab.gr/1200058-1200066/phase2-Android)