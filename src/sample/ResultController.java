package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ResultController implements Initializable {


    @FXML
    private TableView<SearchResult> table;
    @FXML
    private TableColumn<SearchResult, String> idCol;
    @FXML
    private TableColumn<SearchResult, Number> dangerCol;
    @FXML
    private TableColumn<SearchResult, String> lightValCol;
    @FXML
    private TableColumn<SearchResult, String> proxValCol;
    @FXML
    private TableColumn<SearchResult, String> dateCol;
    @FXML
    private TableColumn<SearchResult, String> timeCol;


    List<SearchResult> results;

    void setResults(List<SearchResult> results) {
        this.results = results;
        for (SearchResult result : results) {
            System.out.println("HERE");
            result.print();
        }
        showResults();
    }

    private void showResults() {
        if (results == null) {
            System.out.println("Results is null");
        } else {
            ObservableList<SearchResult> data = FXCollections.observableArrayList();
            for (SearchResult result : results) {
                data.add(result);
            }
            idCol.setCellValueFactory(cellData -> cellData.getValue().getIdProperty());
            dangerCol.setCellValueFactory(cellData -> cellData.getValue().getLevelOfDangerProperty());
            lightValCol.setCellValueFactory(cellData -> cellData.getValue().getLightValProperty());
            proxValCol.setCellValueFactory(cellData -> cellData.getValue().getProxValProperty());
            dateCol.setCellValueFactory(cellData -> cellData.getValue().getDateProperty());
            timeCol.setCellValueFactory(cellData -> cellData.getValue().getTimeProperty());
            table.setItems(data);

        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
