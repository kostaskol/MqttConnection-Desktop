package sample;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

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

    private SettingsProfile selectedProfile;
    private List<SettingsBundle> profiles = null;
    private List<String> profileNames = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Initialise called");

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
        boolean cleanSess = cleanSessCheck.isSelected();
        int lightThres = Integer.parseInt(lightThresText.getText());
        int proxThres = Integer.parseInt(proxThresText.getText());

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
                    connUrl, clientName, cleanSess, lightThres, proxThres, dbmanager.getMaxProfileId() + 1
                    , profName
            );
            System.out.println("New profile id: " + bundle.getProfId());
            dbmanager.saveNewProfile(bundle);
        } else {
            int currId = selectedProfile.getId();
            String profName = selectedProfile.getProfileName();

            SettingsBundle bundle = new SettingsBundle(
                    connUrl, clientName, cleanSess, lightThres, proxThres, currId, profName);

            dbmanager.updateProfile(bundle);
        }
        updateProfilesCombo();
        dbmanager.closeConnection();
    }


    private void updateProfilesCombo() {
        DataBaseManager dbManager = new DataBaseManager();

        profiles = dbManager.getAllProfiles();


        dbManager.closeConnection();

        profileNames = new ArrayList<>();
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

    @FXML
    private void handleComboBoxAction() {
        if (profile != null) {
            String profileName = profile.getSelectionModel().getSelectedItem().toString();
            for (SettingsBundle tmpProfile : profiles) {
                if (profileName.equals(tmpProfile.getProfName())) {
                    connUrlText.setText(tmpProfile.getConnUrl());
                    clientNameText.setText(tmpProfile.getClientName());
                    cleanSessCheck.setSelected(tmpProfile.getCleanSess());
                    lightThresText.setText(String.valueOf(tmpProfile.getLightThres()));
                    proxThresText.setText(String.valueOf(tmpProfile.getProxThres()));
                }
            }
        }
    }
}
