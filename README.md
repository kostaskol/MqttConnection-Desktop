+ Project 2016 Phase 2:
    + <a name="db">Data Base:</a>
        + The programme uses the MariaDB database which 
        successfully ran on Linux (ArchLinux).
        + The database's name is "project16".
        + The database contains the following tables:
            + clientAverages - Where the connected clients'
            information is stored
            + log - Where all incidents are stored
            + settings - Where all of the user's profiles
            are stored
            + settingsProfile - Where the currently selected
            profile is stored (name not very accurate. 
            Did not have time to change it)
    + <a name="topics">MQTT Topics used:</a>
        + log: Operation messages from the
        desktop client are sent here for better 
        readability
        + connections: Everything that has to
        do with a connection. 
        + connections/newConnections: When a new
        android client connects, they notify
        the desktop client by publishing a message
        containing their UUID to this topic
        + connections/connected: Already connected
        android clients publish to this + their UUID topic
        + connections/connected/\<Client UUID>:
        Already connected android clients publish
        to this topic messages of the following format: </br>
        <a name="message_format">\<"UUID"/"latitude"/"longitude"/"light 
        sensor value"/"proximity sensor value"> </a>
        + connections/connected/\<Client UUID>/
            + warning: The desktop client publishes
            the warning messages to this topic
            + danger: The desktop client publishes
            the danger messages to this topic
            + stopSounds: The desktop client publishes
            the stop all sounds messages to this topic
        + connections/requestAck: Before an android
        client goes into Online Mode, they request
        and acknowledgement message from the desktop 
        client (ensuring that it is running)
        + mainClient/disconnected: If the desktop
        application disconnects ungracefully 
        (it always does), all of the connected clients
        are notified and go into Offline Mode
        
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
            
        + A short description for each of the class's methods:
            + Main:
                + void main (String[]): <br />
                    + Starts the application's two main threads (WindowThread, MqttManagerThread)
                + void updateThresholds(SettingsBundle): <br />
                    + Notifies the MqttManager that the user changed the thresholds
                    in the Settings tab
                + void close(): <br />
                    + Only called by Windows$Window.stop(). Interrupts the threads 
                    and closes the application
            
            + WindowThread:
                + @Override void run(): </br>
                    + Starts Window inside a thread
            
            + Window:
                + void launchWindow(): </br>
                    + Launches the main FXML window
                + @Override void start(Stage): </br>
                    + Sets the stage according to the main_screen.fxml file
            
            + SearchController:
                + void search(): </br>
                    + Reads the values given by the user and queries the DataBase
                    accordingly
            
            + SettingsController:
                + @Override void initialise(URL, ResourceBundle): </br>
                    + Initialises the profile ComboBox with values from the 
                    database and fills in the settings according to the last
                    chosen profile by the user
                
                + SettingsBundle save(): </br>
                    + If the user is updating an existing profile:
                        + Checks for input errors and updates the Data Base
                    
                    + If the user is creating a new profile:
                        + Checks for input errors and inserts the new data
                        into the Data Base
                    + Returns the newly saved data (useful for apply)
        
                + SettingsBundle updateProfile((unwrapped) SettingsBundle): </br>
                    + Called by SettingsController.save 
                    when a profile is updated. 
                    + Updates the ComboBox
                    and the Data Base with the given data
                
                + void updateProfilesCombo():
                    + Updates the profile combo with any 
                    new additions to the profiles list
                
                + void deleteProfile():
                    + Deletes the currently selected profile
                    from the combo box and the database
                    (The default profile cannot be deleted)
                    
                + void handleComboBoxAction():
                    + Updates the TextFields whenever a new item
                    is chosen in the ComboBox
                
                + void apply():
                    + Saves the user's settings and notifies 
                    the MQTT client (through Main) of the change
                    
            + SearchResultsWindow:
                + TableView<Incident> createTable():
                    + Initialises a table view with the
                    contents of the incident list
                    + Node createPage(pageIndex):
                        + Creates the pagination of the
                        current TableView (does not work very well)
                    
                    + void showWindow():
                        + Opens the Search Results window
            
            + DateAndTimeUtility:
                + String getDate():
                    + Returns the current date
                + String getTime():
                    + Returns the current time
                    
                + int[] dateToParts(String):
                    + Splits the given date string
                    into 3 parts (yyyy-mm-dd)
                
                + int[] timeToParts(String):
                    + Splits the given time string
                    into 3 parts (hh:mm:ss)
                    
            + MqttManagerThread:
                + @Override void run():
                    + Creates a new MqttManager object
                    and starts it
                + void updateThresholds(SettingsBundle):
                    + Notifies the MqttManager of a change
                    in the thresholds
            
            + MqttManager:
                + MqttClient getClient():
                    + Returns a new MqttClient object
                + void connect(MqttConnOpts):
                    + Connects the client with the
                    given mqtt connection options
                + void connectClient():
                    + Calls getClient and connect to
                    connect to the MQTT broker (The 
                    URL, Port and clean session variables are
                    given as part of the SettingsBundle 
                    that must be provided with the initialisation
                    of the MqttManager object).
                    
                    + Subscribes to the newConnections and 
                    requestAcknowledgement topics (see [topics](topics))
                    
                + void publish(String, String):
                    + Publishes the given message
                    to the given topic
                    
                + void subscribe(String):
                    + Subscribes to the given 
                    topic (with a QoS of 2)
                    
                + @Override void connectionLost(Throwable):
                    + Clears the clientAverages table
                    from the Data Base
                
                + @Override void messageArrived(String, String):
                    + Parses the topic in which the
                    topic arrived
                        + If it's a new connection:
                        Subscribes to the 
                        connections/Connected/\<Incoming message(UUID)\>
                        + If it's an already connected client:
                        Parses the message ([For message format see here](#message_format)). 
                            + If the topic's UUID 
                            is different from the message's UUID, it does not accept the
                            incident. 
                            + If not, uses IncidentManager to handle
                            all of the incident's operations (save, notify client, etc)
                            by supplying it the current user's thresholds
                        
                        + If it's a request for acknowledgement:
                            Notifies the requesting client about
                            the applications availability
                    
                + void updateThresholds(SettingsBundle):
                    + Updates the object's thresholds to the new ones
                    
                + @Override void deliveryComplete(IMqttDeliveryToken):
                    + Not used
                    
            + IncidentManager:
                + @Override void run():
                    + If the provided light and proximity values are
                    within the thresholds:
                        + If another incident has occurred
                        with an at most 1 second difference, 
                        notifies both clients about 
                        imminent danger
                        
                        + If not, notifies the current client
                        about a possibility of impact **if** the 
                        client is not currently in 
                        warning/danger mode
                    + If not, notifies the client that
                    the danger signal should stop 
                    if one is playing (The android client
                    checks this)
                    
                + boolean checkValueLight():
                    + Queries the Data Base to find
                    the current client's average, times they 
                    have contacted the desktop application,
                    the current light value sum and whether
                    the client is currently ringing.
                    
                    + If they have provided enough
                    values (this can be changed in the
                    BundleClasses$Constants) and 
                        + Their current light sensor value is below 
                        the client's average lighting - the 
                        threshold, returns true
                        
                        + Their current light sensor value
                        is above the client's average lighting + the
                        threshold, resets the client in the 
                        clientAverages table and returns true.
                        (The client's
                        average will be recalculated 
                        the next Constants.AVERAGE_TIMES times they
                        contact the server)
                        
                        + Their current light sensor value is within
                        the client's average lighting +- the threshold, 
                        it returns false
                + boolean checkValueProx():
                    + Checks whether the given proximity value
                    is below the threshold. Returns true or false 
                    accordingly
                
                + ClientAverage reset(ClientAverage):
                    + Resets the given client average:
                        + Sets ringing to false
                        + Sets light average to 0
                        + Sets light sum to 0
                        + Sets times contancted to 0
                
                + boolean checkIncidentTime():
                    + Checks the last incident date and time
                    in the Data Base.
                    
                    + If the last incident did not occur 
                    on the same day as the current one, returns false
                    
                    + If the last incident did occur on the same day
                    but not in the same hour or minute, returns false
                    
                    + If the last incident did occur in the same 
                    hour and minute but not within a one second
                    difference, returns false
                    
                    + If the last incident occurred within the same 
                    hour, minute *and* second, returns true
            + DataBaseManagerThread:
                + Notes:
                    + + We provide to the constructor a String
                    that represents the operation we want the thread
                    to perform.
                    (Valid strings are 
                    defined in BundleClasses$Constants) 
                    + The class offers 6 constructors. We
                    call them according to the operation string 
                
                + Function names explain the operation they perform 
                          
            + DataBaseManager:
                + void saveIncident(Incident):
                    + Inserts the given incident into log
                
                + IncidentTime getLastIncidentTime():
                    + Returns the last incident's date and time
                    or null if the last date is not today
                    
                + void updateDanger(IncidentTime):
                    + Changes the level of danger to 1
                    for the specified UUID and incident date 
                    and time
                
                + List<Incident> searchDB(list of filters):
                    + Creates an SQL query by going through the 
                    supplied filter list and only inserts the ones
                    that are given by the user
                
                + int getSelectedProfile():
                    + Queries the settingsProfile table 
                    and returns the currently selected 
                    user profile 
                    
                + int getMaxProfileId():
                    + Returns the current max ID for the
                    settings table (Didn't use AUTO-INCREMENT 
                    for the profileID column at first and based
                    the entire profile creation logic on that.
                    Did not have time to fix.)
                
                + void switchProfile(int):
                    + Changes the currently selected
                    user profile in the settingsProfile table to newId. 
                
                + void updateProfile(SettingsBundle):
                    + Updates the settings of the specified ID in
                    the settings table
                    
                + void deleteProfile(Profile):
                    + Deletes the specified profile from the
                    settings table
                    
                + void saveNewProfile(SettingsBundle):
                    + Inserts a new profile into the settings table
                    
                + SettingsBundle getProfile(int):
                    + Returns the user settings for the specified profile
                
                + List<SettingsBundle> getAllProfiles():
                    + Returns a list of all the profiles
                    contained in the settings table
                    
                + ClientAverage getClientAverage(String):
                    + Returns the specified client's average if it exists.
                    If it does not, it inserts a new one with the input
                    UUID and returns that.
                    
                + void insertClientAverage(ClientAverage):
                    + Inserts the client average into the
                    clientAverages table
                    
                + void updateClientAverage(ClientAverage):
                    + Updates the specified client's average 
                    in the clientAverages table
                    
                + void clearClients():
                    + Deletes all the entries from the 
                    clientAverages table. Called when the 
                    programme closes
                    
                + void executeStatement(String):
                    + Executes the specified SQL statement
                    
                + ResultSet executeQuery(String):
                    + Executes the specified SQL query
                    and returns the results
                    
                + void closeConnection():
                    + Closes the connection with the 
                    Data Base
                
            + BundleClasses:
                + All of the bundle classes only contain their
                getter - setter methods