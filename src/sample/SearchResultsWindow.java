package sample;

import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.List;

public class SearchResultsWindow {
    private final TableView<Incident> table = createTable();
    private List<Incident> results;

    public SearchResultsWindow(List<Incident> results) {
        this.results = results;
    }

    private TableView<Incident> createTable() {
        TableView<Incident> table = new TableView<>();

        TableColumn<Incident, String> idCol = new TableColumn<>("User ID");
        idCol.setCellValueFactory(cellData -> cellData.getValue().getIdProperty());

        TableColumn<Incident, Number> dangerCol = new TableColumn<>("Level of danger");
        dangerCol.setCellValueFactory(cellData -> cellData.getValue().getLevelOfDangerProperty());

        TableColumn<Incident, String> lightValCol = new TableColumn<>("Light value");
        lightValCol.setCellValueFactory(cellData -> cellData.getValue().getLightValProperty());

        TableColumn<Incident, String> proxValCol = new TableColumn<>("Proximity value");
        proxValCol.setCellValueFactory(cellData -> cellData.getValue().getProxValProperty());

        TableColumn<Incident, String> latCol = new TableColumn<>("Latitude");
        latCol.setCellValueFactory(cellData -> cellData.getValue().getLatitudeProperty());

        TableColumn<Incident, String> lngCol = new TableColumn<>("Longitude");
        lngCol.setCellValueFactory(cellData -> cellData.getValue().getLongitudeProperty());

        TableColumn<Incident, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cellData -> cellData.getValue().getDateProperty());

        TableColumn<Incident, String> timeCol = new TableColumn<>("Time");
        timeCol.setCellValueFactory(cellData -> cellData.getValue().getTimeProperty());

        table.setRowFactory(tv -> new TableRow<Incident>() {
            @Override
            public void updateItem(Incident item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null) {
                    setStyle("");
                } else if (item.getLevelOfDanger() == 1) {
                    setStyle("-fx-background-color: crimson");
                } else if (item.getLevelOfDanger() == 0) {
                    setStyle("-fx-background-color: yellow");
                }
            }
        });

        table.getColumns().addAll(idCol, dangerCol, lightValCol, proxValCol,
                latCol, lngCol, dateCol, timeCol);

        return table;
    }

    private Node createPage(int pageIndex) {
        int fromIndex = pageIndex * Constants.ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + Constants.ROWS_PER_PAGE, results.size());
        table.setItems(FXCollections.observableArrayList(results.subList(fromIndex, toIndex)));

        return new BorderPane(table);
    }

    void showWindow() {
        Pagination pages = new Pagination((results.size() / Constants.ROWS_PER_PAGE), 0);
        pages.setPageFactory(this::createPage);

        Scene scene = new Scene(new BorderPane(pages), 735, 735);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Search Results");
        stage.show();
    }
}
