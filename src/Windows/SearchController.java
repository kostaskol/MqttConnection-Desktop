package Windows;


import BundleClasses.Incident;
import DataBaseManager.DataBaseManager;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.util.List;

public class SearchController {

    @FXML private TextField userIdText;
    @FXML private ComboBox<String> levelOfDangerCombo;
    @FXML private ComboBox<String> lightValueCombo;
    @FXML private ComboBox<String> proxValueCombo;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> timeCombo;
    @FXML private Label messageLabel;

    public void search() {
        String userId = userIdText.getText();
        String levelOfDanger = levelOfDangerCombo.getSelectionModel().getSelectedItem();
        String lightValue = lightValueCombo.getSelectionModel().getSelectedItem();
        String proxValue = proxValueCombo.getSelectionModel().getSelectedItem();

        LocalDate localDate = datePicker.getValue();
        String date = null;
        if (localDate != null) {
            date = localDate.getYear() + "/" + localDate.getMonthValue() + "/" +
                    localDate.getDayOfMonth();
        }

        String time = timeCombo.getSelectionModel().getSelectedItem();

        if (localDate != null && !time.equals("")) {
            messageLabel.setText("You cannot search by both Date and Time");
        } else {
            messageLabel.setText("");
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
