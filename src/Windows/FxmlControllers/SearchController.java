package Windows.FxmlControllers;


import BundleClasses.Constants;
import BundleClasses.Incident;
import Managers.DataBaseManager.DataBaseManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller class
 */
public class SearchController {

    @FXML private TextField userIdText;
    @FXML private ComboBox<String> levelOfDangerCombo;
    @FXML private ComboBox<String> lightValueCombo;
    @FXML private ComboBox<String> proxValueCombo;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> timeCombo;

    public void search() {
        String userId = userIdText.getText();
        String levelOfDanger = levelOfDangerCombo.getSelectionModel().getSelectedItem();
        String lightValue = lightValueCombo.getSelectionModel().getSelectedItem();
        String proxValue = proxValueCombo.getSelectionModel().getSelectedItem();

        LocalDate localDate = null;
        if (datePicker != null) {
            localDate = datePicker.getValue();
        }

        String date = null;

        if (localDate != null) {
            date = localDate.getYear() + "-" + localDate.getMonthValue() + "-" +
                    localDate.getDayOfMonth();
        }

        String time = timeCombo.getSelectionModel().getSelectedItem();
        if (time == null) {
            time = Constants.NONE;
        }
        if (date != null && !time.equals(Constants.NONE) && !time.equals("null")) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Incorrect field values");
            alert.setHeaderText("You cannot search by both date AND time");
            alert.show();
            return;
        }

        DataBaseManager dbManager = new DataBaseManager();

        List<Incident> incidents = dbManager.searchDb(userId, levelOfDanger, lightValue,
                proxValue, date, time);
        System.out.println("SearchDB complete");
        if (incidents != null) {
            try {
                SearchResultsWindow searchWindow = new SearchResultsWindow(incidents);
                searchWindow.showWindow();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Result is null");
        }


    }
}
