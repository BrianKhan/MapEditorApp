/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rgvm.gui;

import java.util.ArrayList;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.Scale;
import properties_manager.PropertiesManager;
import rgvm.PropertyType;
import saf.components.AppWorkspaceComponent;
import rgvm.RegioVincoMapEditor;
import rgvm.controller.MapEditorController;
import rgvm.data.DataManager;
import rgvm.data.RegionItem;
import static saf.settings.AppStartupConstants.FILE_PROTOCOL;
import static saf.settings.AppStartupConstants.PATH_IMAGES;
import static saf.settings.AppPropertyType.*;
import saf.ui.AppGUI;

/**
 *
 * @author McKillaGorilla
 */
public class Workspace extends AppWorkspaceComponent {

    RegioVincoMapEditor app;
    TableView<RegionItem> itemsTable;
    TableColumn nameColumn;
    TableColumn capitalColumn;
    TableColumn leaderColumn;
    AppGUI gui;

    int counterZoom;
    int counterRight;
    int counterUp;
    int debug;
    int xloc;
    int yloc;
    Pane first;
    Pane second;
    ProgressBar pb;

    public Workspace(RegioVincoMapEditor initApp) {
        app = initApp;
        gui = app.getGUI();
        layoutGUI();
        workspace.setDividerPosition(0, .885);
        setupHandlers();
        FlowPane progPane = new FlowPane();
        app.getGUI().getAppPane().setBottom(progPane);
        Label progress = new Label("Progress");
        pb = new ProgressBar(0);
        progPane.getChildren().add(progress);
        progPane.getChildren().add(pb);

    }
    public ProgressBar getPB() {
        
        return pb;
    }

    public void layoutGUI() {
        workspace = new SplitPane();
        first = new Pane();
        second = new Pane();
        workspace.getItems().addAll(first, second);
        ImageView img = new ImageView();
        PropertiesManager props = PropertiesManager.getPropertiesManager();

        // dummy image
        String imagePath = FILE_PROTOCOL + PATH_IMAGES + props.getProperty(IMAGE_TEST.toString());
        Image holder = new Image(imagePath);
        img.setImage(holder);
        first.getChildren().add(img);

        itemsTable = new TableView();

        nameColumn = new TableColumn(props.getProperty(PropertyType.NAME_COLUMN_HEADING));
        capitalColumn = new TableColumn(props.getProperty(PropertyType.CAPITAL_COLUMN_HEADING));
        leaderColumn = new TableColumn(props.getProperty(PropertyType.LEADER_COLUMN_HEADING));
        nameColumn.setCellValueFactory(new PropertyValueFactory<String, String>("name"));
        capitalColumn.setCellValueFactory(new PropertyValueFactory<String, String>("leader"));
        leaderColumn.setCellValueFactory(new PropertyValueFactory<String, String>("capital"));
        itemsTable.getColumns().clear();
        itemsTable.getColumns().add(nameColumn);
        itemsTable.getColumns().add(leaderColumn);
        itemsTable.getColumns().add(capitalColumn);

        DataManager dataManager = (DataManager) app.getDataComponent();
        RegionItem dummy = new RegionItem("test name", "test leader", "test capital");
        dataManager.addItem(dummy);
        itemsTable.setItems(dataManager.getItems());
        second.getChildren().add(itemsTable);
        itemsTable.minWidthProperty().bind(app.getGUI().getWindow().widthProperty().multiply(.5));
        itemsTable.minHeightProperty().bind(app.getGUI().getWindow().heightProperty().multiply(.9));
    }

    @Override
    public void initStyle() {

    }

    public void setupHandlers() {
        MapEditorController controller = new MapEditorController();
        gui.getColorButton().setOnMouseClicked(e -> {
            controller.processColorButton();
        });
        gui.getNewButton().setOnMouseClicked(e -> {
            controller.processNewButton();
        });
        Button testLoad = new Button("Load RAW Json(To be done for save testing)");
        gui.getFreePane().getChildren().add(testLoad);
        testLoad.setOnMouseClicked(e -> {
            controller.processLoadTest(app);
        });
        Button testSave = new Button("Save to RVME with current RVM file loaded +hard coded values");
        gui.getFreePane().getChildren().add(testSave);
        testSave.setOnMouseClicked(e -> {
            controller.processSaveTest(app);
        });
        Button testLoader = new Button("Load RVME file with print statements");
        gui.getFreePane().getChildren().add(testLoader);
        testLoader.setOnMouseClicked(e-> {
            controller.processLoadBig(app);
        });
        Button testExport = new Button("Export to RVM file");
        gui.getFreePane().getChildren().add(testExport);
        testExport.setOnMouseClicked(e-> {
            controller.processExportTest(app);
        });
        itemsTable.setOnMouseClicked(e -> {

            if (e.getClickCount() == 2) {
                if (itemsTable.getSelectionModel().getSelectedItem() != null) {

                    controller.processEditItem(itemsTable.getSelectionModel().getSelectedItem());
                }
            }

        });
    }

    public void polyLoad() {
        counterZoom = 0;
        counterRight = 0;
        counterUp = 0;
        debug = 0;
        xloc = 0;
        yloc = 0;
        first.getChildren().clear();
        layoutMap();
        fixLayout();
    }

    public void fixLayout() {
        // apply layout functions based on size
        Scale scale = new Scale();
        scale.setX(5.3);
        scale.setY(-5.3);
        first.getTransforms().add(scale);
        first.setTranslateX(-10 + app.getGUI().getWindow().getWidth() / 2);
        first.setTranslateY(6 + app.getGUI().getWindow().getHeight() / 2);
    }

    public void layoutMap() {
        //this is the initial load method for our polygons
        DataManager dataManager = (DataManager) app.getDataComponent();
        //we iterate over our arraylist of arrays and make a polygon for each entry in the arraylist
        for (int i = 0; i < dataManager.getItems().size(); i++) {
            ArrayList<Double[]> listy = dataManager.getItems().get(i).getList();
            Polygon myGon = new Polygon();
            for (int j = 0; j < listy.size(); j++) {
                Double[] myAr = listy.get(j);
                myGon.getPoints().addAll(myAr);
            }
            //green fill, black outline
            myGon.setFill(Paint.valueOf("#21FF42"));
            myGon.setStroke(Paint.valueOf("#000000"));
            myGon.setStrokeWidth(.01);
            first.getChildren().add(myGon);
            //blue ocean
            first.getScene().setFill(Paint.valueOf("#add8e6"));
            app.getGUI().getPrimaryScene().setFill(Paint.valueOf("#add8e6"));
        }
    }

    @Override
    public void reloadWorkspace() {
        DataManager dataManager = (DataManager) app.getDataComponent();
       // first.getTransforms().clear();
       // first.getChildren().clear();
        counterZoom = 0;
        counterRight = 0;
        counterUp = 0;
        debug = 0;
        xloc = 0;
        yloc = 0;
        
    }
}
