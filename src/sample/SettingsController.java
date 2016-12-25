package sample;


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
    private ComboBox profile;
    @FXML
    private Button save;
    @FXML
    private Button apply;
    @FXML
    private Button delete;
    @FXML
    private TextField portText;

    private SettingsProfile selectedProfile;
    private List<SettingsBundle> profiles = null;
    private String startingUrl = null;
    private String startingPort = null;
    private String startingClientName = null;
    private boolean startingCleanSess;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        lightThresText.setTextFormatter(new TextFormatter<>(new NumberStringConverter()));
        proxThresText.setTextFormatter(new TextFormatter<>(new NumberStringConverter()));

        DataBaseManager dbManager = new DataBaseManager();

        int selectedId = dbManager.getSelectedProfile();


        dbManager.closeConnection();

        updateProfilesCombo();

        for (SettingsBundle tmpProfile : profiles) {
            if (tmpProfile.getProfId() == selectedId) {
                profile.setValue(tmpProfile.getProfName());
                handleComboBoxAction();
                selectedProfile = new SettingsProfile(tmpProfile.getProfId(), tmpProfile.getProfName());
            }
        }
    }

    @FXML
    private void save() {
        DataBaseManager dbmanager = new DataBaseManager();

        String connUrl = connUrlText.getText();
        String clientName = clientNameText.getText();
        String port = portText.getText();
        boolean cleanSess = cleanSessCheck.isSelected();
        int lightThres = Integer.parseInt(lightThresText.getText());
        if (lightThres > 75) {
            warningLabel.setText("The light threshold cannot be greater than 75%");
            return;
        }
        int proxThres = Integer.parseInt(proxThresText.getText());
        if (proxThres > 5) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Warning!");
            alert.setHeaderText("Most android phones have a max proximity sensor range of 5." +
                    "\nSetting the threshold any higher than that could cause the device" +
                    "\nto continuously send out the warning signal.");
            alert.setContentText("Are you sure you wish to proceed?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent()) {
                if (result.get() == ButtonType.CANCEL) {
                    return;
                }
            }
        }

        if (connUrl.equals("") || clientName.equals("")) {
            warningLabel.setText("Please fill in all of the forms below");
            return;
        } else {
            warningLabel.setText("");
        }

        if (profile.getSelectionModel().getSelectedItem().equals(Constants.NEW_PROFILE)) {
            String profName = null;
            TextInputDialog dialog = new TextInputDialog("Profile");

            dialog.setTitle("Please enter profile name");
            dialog.setContentText("Profile name: ");

            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                profName = result.get();
            }

            SettingsBundle bundle = new SettingsBundle(
                    connUrl, port, clientName, cleanSess, lightThres, proxThres, dbmanager.getMaxProfileId() + 1
                    , profName
            );
            dbmanager.saveNewProfile(bundle);
            updateProfilesCombo();
        } else {
            if (!connUrl.equals(startingUrl)
                    || !clientName.equals(startingClientName)
                    || !port.equals(startingPort)
                    || cleanSess != startingCleanSess) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Warning");
                alert.setHeaderText("A change you have made requires the MQTT client to reconnect. " +
                        "This could cause problems if an android phone is connected.");
                alert.setContentText("Are you sure you wish to continue?");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    int currId = selectedProfile.getId();
                    String profName = selectedProfile.getProfileName();

                    SettingsBundle bundle = new SettingsBundle(
                            connUrl, port, clientName, cleanSess, lightThres, proxThres, currId, profName);
                    bundle.print();
                    dbmanager.updateProfile(bundle);
                    this.profiles = dbmanager.getAllProfiles();
                }
            }


        }
        dbmanager.closeConnection();
    }


    private void updateProfilesCombo() {
        DataBaseManager dbManager = new DataBaseManager();

        profiles = dbManager.getAllProfiles();


        dbManager.closeConnection();

        List<String> profileNames = new ArrayList<>();
        for (SettingsBundle tmpProfile : profiles) {
            profileNames.add(tmpProfile.getProfName());
        }

        profileNames.add("New Profile");
        ObservableList objList = FXCollections.observableList(profileNames);
        profile.setItems(objList);
    }

    @FXML
    private void deleteProfile() {
        String profileName = profile.getSelectionModel().getSelectedItem().toString();
        for (SettingsBundle tmpProfile : profiles) {
            if (profileName.equals(tmpProfile.getProfName())) {
                if (profileName.equals(Constants.DEFAULT_PROFILE)) {
                    warningLabel.setText("You cannot delete the default profile");
                } else {
                    warningLabel.setText("");
                    System.out.println("Deleting profile: " + tmpProfile.getProfName());
                    DataBaseManager dbManager = new DataBaseManager();
                    SettingsProfile delProfile = new SettingsProfile(tmpProfile.getProfId()
                            , tmpProfile.getProfName());
                    dbManager.deleteProfile(delProfile);
                    int selectedItem = profile.getSelectionModel().getSelectedIndex();
                    profile.getSelectionModel().clearAndSelect(selectedItem - 1);
                    updateProfilesCombo();
                }
            }
        }
    }

    @FXML
    private void handleComboBoxAction() {
        if (profile != null) {
            if (profiles != null) {
                String profileName = profile.getSelectionModel().getSelectedItem().toString();
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
                        DataBaseManager dbManager = new DataBaseManager();
                        dbManager.switchProfile(tmpProfile.getProfId());
                    }
                }
            }
        }
    }

    @FXML
    private void filterText(String text) {
        if (!text.matches("\\d*")) {

        }
    }

    @FXML
    private void apply() {
        save();
        Main.settingsChanged();
    }

}
