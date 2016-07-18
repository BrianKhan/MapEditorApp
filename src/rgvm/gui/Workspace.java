/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rgvm.gui;

import java.util.ArrayList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
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
    boolean loaded;
    double lowest;
    double highest;
    double zoom;
    Pane firstParent;
    public Workspace(RegioVincoMapEditor initApp) {
        lowest = 0;
        highest = 0;
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
        firstParent = new Pane();
        first = new Pane();
        second = new FlowPane();
        firstParent.getChildren().add(first);

        workspace.getItems().addAll(firstParent, second);
        ImageView img = new ImageView();
        PropertiesManager props = PropertiesManager.getPropertiesManager();
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
        itemsTable.setItems(dataManager.getItems());
        second.getChildren().add(itemsTable);
        itemsTable.minWidthProperty().bind(app.getGUI().getWindow().widthProperty().multiply(.1));
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
            controller.processNewButton(app);
        });
        Button testLoad = new Button("Load RAW Json(To be done for save testing)");
        gui.getFreePane().getChildren().add(testLoad);
        testLoad.setOnMouseClicked(e -> {
            first.getChildren().clear();
            controller.processLoadTest(app);
        });
        Button testSave = new Button("Save to RVME with current RVM file loaded +hard coded values");
        gui.getFreePane().getChildren().add(testSave);
        testSave.setOnMouseClicked(e -> {
            controller.processSaveTest(app);
        });
        Button testLoader = new Button("Load RVME file with print statements");
        gui.getFreePane().getChildren().add(testLoader);
        testLoader.setOnMouseClicked(e -> {
            controller.processLoadBig(app);
        });
        Button testExport = new Button("Export to RVM file");
        gui.getFreePane().getChildren().add(testExport);
        testExport.setOnMouseClicked(e -> {
            controller.processExportTest(app);
        });
        Button testJunit = new Button("Choose file and execute test");
        gui.getFreePane().getChildren().add(testJunit);
        testJunit.setOnMouseClicked(e -> {
            controller.test(app);
        });
        first.setOnMouseClicked(e -> {
            first.requestFocus();
            System.out.println("X: " + e.getX() + "Y: " + e.getY());
            System.out.println("Width: " + itemsTable.getWidth());

        });
        gui.getAppPane().setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.RIGHT)) {
                moveRight();
            }
            if (e.getCode().equals(KeyCode.LEFT)) {
                moveLeft();
            }
            if (e.getCode().equals(KeyCode.UP)) {
                moveUp();
            }
            if (e.getCode().equals(KeyCode.DOWN)) {
                moveDown();
            }
        });
        itemsTable.setOnKeyPressed(e -> {
            itemsTable.getSelectionModel().clearSelection();
            if (e.getCode().equals(KeyCode.RIGHT)) {
                moveRight();
            }
            if (e.getCode().equals(KeyCode.LEFT)) {
                moveLeft();
            }
            if (e.getCode().equals(KeyCode.UP)) {
                moveUp();
            }
            if (e.getCode().equals(KeyCode.DOWN)) {
                moveDown();
            }
        });
        firstParent.setOnMouseClicked(e-> {
            System.out.println("firstparent X: " +e.getX() + " Y: " + e.getY());
        });
        first.setOnMouseClicked(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY)) {
                System.out.println("X: " +e.getX() + " Y: "+ e.getY());
                zoomIn(e);
            }
            if (e.getButton().equals(MouseButton.SECONDARY)) {

                zoomOut();
            }
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
        zoom = 5.3;//*(360/(highest-lowest));
        System.out.println(zoom);
        scale.setX(zoom);
        scale.setY(-zoom);
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
                if (myAr[0] > highest) {
                    highest = myAr[0];
                }
                if (myAr[0] < lowest) {
                    lowest = myAr[0];
                }
                myGon.getPoints().addAll(myAr);
                myGon.setOnMouseClicked(e-> {
                    System.out.println("polygon clicked");
                });
            }
            //green fill, black outline
            myGon.setFill(Paint.valueOf("#21FF42"));
            myGon.setStroke(Paint.valueOf("#000000"));
            myGon.setStrokeWidth(.01);
            first.getChildren().add(myGon);
            //blue ocean
            // first.getParent().setStyle("-fx-background-color: #99d6ff;");
            first.setStyle("-fx-background-color: #99d6ff;");
            

        }
    }

    @Override
    public void reloadWorkspace() {
        DataManager dataManager = (DataManager) app.getDataComponent();
        first.getTransforms().clear();
        first.getChildren().clear();
        counterZoom = 0;
        counterRight = 0;
        counterUp = 0;
        debug = 0;
        xloc = 0;
        yloc = 0;
        lowest = 0;
        highest = 0;
        layoutMap();
        fixLayout();

    }

    public void center() {

    }

    public void moveRight() {
        //simply move the camera 5 pixels to the right
        counterRight = counterRight + 5;
        first.setTranslateX(-10 - counterRight + app.getGUI().getWindow().getWidth() / 2);

    }

    public void moveLeft() {
        //simply move the camera 5 pixels to the left
        counterRight = counterRight - 5;
        first.setTranslateX(-10 - counterRight + app.getGUI().getWindow().getWidth() / 2);

    }

    public void moveUp() {
        //simply move the camera 5 pixels to the up
        counterUp = counterUp + 5;
        first.setTranslateY(6 + counterUp + app.getGUI().getWindow().getHeight() / 2);
    }

    public void moveDown() {
        //simply move the camera 5 pixels to the down
        counterUp = counterUp - 5;
        first.setTranslateY(6 + counterUp + app.getGUI().getWindow().getHeight() / 2);
    }

    public void zoomIn(MouseEvent e) {
        /* //we grab the xlocation and ylocation in reference to our "center"
        xloc = (int) (e.getX() - app.getGUI().getWindow().getWidth() / 2);
        yloc = (int) (e.getY() - app.getGUI().getWindow().getHeight() / 2);
        //we move the screen to the clicked location
        counterRight = counterRight + xloc;
        counterUp = counterUp - yloc;
        first.setTranslateX(-10 - counterRight + app.getGUI().getWindow().getWidth() / 2);
        first.setTranslateY(6 + counterUp + app.getGUI().getWindow().getHeight() / 2); */
        //we remove all zoom effects, and then apply a new zoom based on counterzoom
        first.getTransforms().clear();
        Scale scale = new Scale();
        //we increment counterZoom every time we zoom
        counterZoom++;
        
        scale.setX(zoom + (counterZoom * .7));
        scale.setY(-zoom - (counterZoom * .7));
        first.getTransforms().add(scale);
        System.out.println("zooming in");
    }

    public void zoomOut() {
        //siimilar to zoomIn(), but we don't move the camera, and we decrement counterZoom
        System.out.println("zooming out" +zoom);
        first.getTransforms().clear();
        Scale scale = new Scale();
        counterZoom--;
        scale.setX(zoom + (counterZoom * .7));
        scale.setY(-zoom - (counterZoom * .7));
        first.getTransforms().add(scale);
    }
}
