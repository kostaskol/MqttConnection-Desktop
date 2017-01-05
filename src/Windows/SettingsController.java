package Windows;


import BundleClasses.Constants;
import BundleClasses.Profile;
import BundleClasses.SettingsBundle;
import DataBaseManager.DataBaseManager;
import DataBaseManager.DataBaseManagerThread;
import Main.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.converter.NumberStringConverter;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {

    /*
     * The settings tab's FXML nodes
     */
    @FXML
    private TextField connUrlText;
    @FXML
    private TextField clientNameText;
    @FXML
    private CheckBox cleanSessCheck;
    @FXML
    private TextField lightThresText;
    @FXML
    private TextField proxThresText;
    @FXML
    private Label warningLabel;
    @FXML
    private ComboBox<String> profile;
    @FXML
    private TextField portText;

    private Profile selectedProfile;
    private List<SettingsBundle> profiles = null;
    private String startingUrl = null;
    private String startingPort = null;
    private String startingClientName = null;
    private boolean startingCleanSess;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        /*
         * We don't allow the light and proximity threshold text fields
         * have non - numeric values
         */
        lightThresText.setTextFormatter(new TextFormatter<>(new NumberStringConverter()));
        proxThresText.setTextFormatter(new TextFormatter<>(new NumberStringConverter()));

        /*
         * Show the selected profile's settings
         */
        DataBaseManager dbManager = new DataBaseManager();

        int selectedId = dbManager.getSelectedProfile();
        dbManager.closeConnection();

        /*
         * Insert all profiles into the profile combo box
         */
        updateProfilesCombo();

        /*
         * Set the window's settings values to the selected profile
         */
        for (SettingsBundle tmpProfile : profiles) {
            if (tmpProfile.getProfId() == selectedId) {
                profile.setValue(tmpProfile.getProfName());
                handleComboBoxAction();
                selectedProfile = new Profile(tmpProfile.getProfId(), tmpProfile.getProfName());
            }
        }
    }

    @FXML
    private void save() {

        String connUrl = connUrlText.getText();
        String clientName = clientNameText.getText();
        String port = portText.getText();
        boolean cleanSess = cleanSessCheck.isSelected();
        int lightThres;
        int proxThres;
        warningLabel.setText("");
        /*
         * If the user didn't enter a light or proximity threshold,
         * notify them and stop
         */
        try {
            lightThres = Integer.parseInt(lightThresText.getText());
            proxThres = Integer.parseInt(proxThresText.getText());
        } catch (NumberFormatException e) {
            warningLabel.setText("Please fill in all of the below form's fields");
            return;
        }


        if (connUrl.equals("") || clientName.equals("") || port.equals("")) {
            warningLabel.setText("Please fill in all of the below form's fields");
            return;
        }

        /*
         * We don't allow the light threshold to be greater than 75%
         */
        if (lightThres > 75) {
            warningLabel.setText("The light threshold cannot be greater than 75%");
            return;
        }
        /*
         * It is dangerous to set the proximity's threshold to a value above 5
         * due to the alert's text
         */
        if (proxThres > 5) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Warning!");
            alert.setHeaderText("Most android phones have a max proximity sensor range of 5." +
                    "\nSetting the threshold any higher than that could cause the device" +
                    "\nto continuously send out the warning signal.");
            alert.setContentText("Are you sure you wish to proceed?");
            Optional<ButtonType> result = alert.showAndWait();
            /*
             * We only proceed with saving the values
             * if the user has clicked the OK button
             */
            if (result.isPresent()) {
                if (result.get() == ButtonType.CANCEL) {
                    return;
                }
            } else {
                return;
            }
        }


        if (profile.getSelectionModel().getSelectedItem().equals(Constants.NEW_PROFILE)) {

            // If the user wants to create a new profile
            String profName;
            TextInputDialog dialog = new TextInputDialog("New Profile");
            boolean allGood = false;

            /*
             * Get the new profile's name.
             * If it's empty or it already exists, notify the user
             * and try again.
             * If the user cancels the alert, return
             */
            do {
                dialog.setTitle("Please enter new profile's name");
                dialog.setContentText("Profile name: ");

                Optional<String> result = dialog.showAndWait();
                if (result.isPresent()) {
                    profName = result.get();
                    if (profName.equals("")) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Error");
                        alert.setHeaderText("New Profile's name cannot be empty");
                        alert.showAndWait();
                    } else {
                        allGood = true;
                    }
                    for (SettingsBundle tmpProfile : profiles) {
                        if (profName.equals(tmpProfile.getProfName())) {
                            Alert alert = new Alert(Alert.AlertType.INFORMATION);
                            alert.setTitle("Error");
                            alert.setHeaderText("New Profile's name already exists");
                            alert.setContentText(profName);
                            alert.showAndWait();
                            allGood = false;
                        }
                    }
                } else {
                    return;
                }
            } while (!allGood);

            /*
             * TODO: Check if this code produces errors in different machines
             * (if the combo tries to update faster than the save profile thread has finished)
             */
            // Save the new profile
            DataBaseManager dbManager = new DataBaseManager();
            SettingsBundle bundle = new SettingsBundle(
                    connUrl, port, clientName, cleanSess, lightThres, proxThres,
                    dbManager.getMaxProfileId() + 1, profName);

            DataBaseManagerThread dbManagerThread = new DataBaseManagerThread(
                    "SAVE NEW PROFILE", Constants.SAVE_NEW_PROFILE, bundle);
            dbManagerThread.start();

            updateProfilesCombo();
        } else {
            /*
             * If we're only updating an existing profile
             */

            // If there has been a change in the MQTT Client's settings, we
            // notify the user that the client must reconnect
            if (!connUrl.equals(startingUrl)
                    || !clientName.equals(startingClientName)
                    || !port.equals(startingPort)
                    || cleanSess != startingCleanSess) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Warning");
                alert.setHeaderText("A change you have made requires the MQTT client to reconnect.\n " +
                        "This could cause problems if an android phone is connected.");
                alert.setContentText("Are you sure you wish to continue?");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent()) {
                    if (result.get() == ButtonType.OK) {
                        updateProfile(connUrl, port, clientName, cleanSess, lightThres, proxThres);
                    }
                }
            } else {
                updateProfile(connUrl, port, clientName, cleanSess, lightThres, proxThres);
            }
        }
    }

    /*
     * Called by save
     * Updates the selected profile
     */
    private void updateProfile(String connUrl, String port, String clientName, boolean cleanSess,
                               int lightThres, int proxThres) {
        int currId = selectedProfile.getProfileId();
        String profName = selectedProfile.getProfileName();
        SettingsBundle bundle = new SettingsBundle(
                connUrl, port, clientName, cleanSess, lightThres, proxThres, currId, profName);

        DataBaseManagerThread dbManagerThread = new DataBaseManagerThread(
                "UPDATE PROFILE", Constants.UPDATE_PROFILE, bundle);
        dbManagerThread.start();

        this.profiles.add(bundle);
    }

    /*
     * Updates the profile's combo with new values from
     * the database
     */
    private void updateProfilesCombo() {
        DataBaseManager dbManager = new DataBaseManager();

        profiles = dbManager.getAllProfiles();

        dbManager.closeConnection();

        List<String> profileNames = new ArrayList<>();
        for (SettingsBundle tmpProfile : profiles) {
            profileNames.add(tmpProfile.getProfName());
        }

        profileNames.add("New Profile");
        ObservableList<String> objList = FXCollections.observableList(profileNames);
        profile.setItems(objList);
    }

    /*
     * Delete the selected profile from the combo box and the data base
     * Note: The default profile cannot be deleted
     */
    @FXML
    private void deleteProfile() {
        String profileName = profile.getSelectionModel().getSelectedItem();
        for (SettingsBundle tmpProfile : profiles) {
            if (profileName.equals(tmpProfile.getProfName())) {
                if (profileName.equals(Constants.DEFAULT_PROFILE)) {
                    warningLabel.setText("You cannot delete the default profile");
                } else {
                    warningLabel.setText("");
                    System.out.println("Deleting profile: " + tmpProfile.getProfName());
                    DataBaseManager dbManager = new DataBaseManager();
                    Profile delProfile = new Profile(tmpProfile.getProfId()
                            , tmpProfile.getProfName());
                    dbManager.deleteProfile(delProfile);
                    int selectedItem = profile.getSelectionModel().getSelectedIndex();
                    profile.getSelectionModel().clearAndSelect(selectedItem - 1);
                    updateProfilesCombo();
                }
            }
        }
    }

    /*
     * If the user selects another profile,
     * the text fields are updated
     */
    @FXML
    private void handleComboBoxAction() {
        if (profile != null) {
            if (profiles != null) {
                String profileName = profile.getSelectionModel().getSelectedItem();
                for (SettingsBundle tmpProfile : profiles) {
                    if (profileName.equals(tmpProfile.getProfName())) {
                        connUrlText.setText(tmpProfile.getConnUrl());
                        startingUrl = tmpProfile.getConnUrl();
                        portText.setText(tmpProfile.getPort());
                        startingPort = tmpProfile.getPort();
                        clientNameText.setText(tmpProfile.getClientName());
                        startingClientName = tmpProfile.getClientName();
                        cleanSessCheck.setSelected(tmpProfile.getCleanSess());
                        startingCleanSess = tmpProfile.getCleanSess();
                        lightThresText.setText(String.valueOf(tmpProfile.getLightThres()));
                        proxThresText.setText(String.valueOf(tmpProfile.getProxThres()));
                        /*
                         * Update the chosen profile in the data base
                         */
                        DataBaseManager dbManager = new DataBaseManager();
                        dbManager.switchProfile(tmpProfile.getProfId());
                    }
                }
            }
        }
    }

    @FXML
    private void apply() {
        save();
        Main.settingsChanged();
    }

}
