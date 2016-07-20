/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rgvm.gui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import properties_manager.PropertiesManager;
import rgvm.PropertyType;
import saf.components.AppWorkspaceComponent;
import rgvm.RegioVincoMapEditor;
import rgvm.controller.MapEditorController;
import rgvm.data.DataManager;
import rgvm.data.RegionItem;
import rgvm.dialog.renameDialog;
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
    double lowestX;
    double lowestY; 
    double highestY;
    double highestX;
    double zoom;
    double diffX;
    double diffY;
    Pane firstParent;
    

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
        firstParent = new Pane();
        first = new Pane();
        Rectangle myClip = new Rectangle(802,536);
        
        firstParent.setClip(myClip);
        firstParent.setBackground(new Background( new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
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
        gui.getThickness().addEventFilter(KeyEvent.ANY,e-> {
            
      
            e.consume();
            first.requestFocus();
        });
        gui.getZoomSlider().addEventFilter(KeyEvent.ANY,e-> {
            
      
            e.consume();
            first.requestFocus();
        });
        gui.getRenameButton().setOnMouseClicked(e-> {
            renameDialog myDiag = renameDialog.getSingleton();
        Stage newStage = new Stage();
        DataManager dm = (DataManager)app.getDataComponent();
        myDiag.init(newStage, dm.getName());

        myDiag.show("Rename Map");
        if (myDiag.getSelection().equalsIgnoreCase("yes")) {
            dm.setName(myDiag.getRename());
            String anthemPath = dm.getParent()+"/"+dm.getName()+" National Anthem.mid";
                File anthemFile = new File(anthemPath);
                if(anthemFile.exists()) {
                    System.out.println("Found anthem file: " +anthemPath);
                    app.getGUI().getPlayButton().setDisable(false);
                }
                else { 
                    System.out.println("Did not find anthem file: " +anthemPath);
                }
            System.out.println("new name = " + dm.getName());
        }
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
        firstParent.setOnMouseClicked(e -> {
            System.out.println("firstparent X: " + e.getX() + " Y: " + e.getY());
        });
        first.setOnMouseClicked(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY)) {
                System.out.println("X: " + e.getX() + " Y: " + e.getY());
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
        diffX = highestX-lowestX;
        diffY = highestY-lowestY;
        if(diffX > diffY) {
            //zoom = 137.149-.374803*diffX;//135, 2.22;
        //EXPONENTIAL SCALE
        
        zoom = 6716.945547*(Math.pow(diffX, -.022264));
        // 360 = 2.22
        //5.733737945556754 = 135
        //0.11345863342279472 = 
        }
        if(diffY> diffX) {
            zoom = 6716.945547*(Math.pow(diffY, -.022264));
        }
        
        System.out.println("Zoom: " +zoom);
        System.out.println("Lowest X: " +lowestX);
        System.out.println("DiffX: " +diffX);
        first.setTranslateX(-lowestX*zoom);
        //first.setTranslateX(((-180-lowestX)*zoom)+180*zoom);
        
        //first.setTranslateY(((-9999+highestY)*zoom)+9999*zoom);
       //this one was good first.setTranslateY((firstParent.getHeight()/2)+highestY*zoom);
       // first.setTranslateY((firstParent.getHeight()/2)+(((highestY-lowestY)/2)*zoom));
       first.setTranslateY(highestY*zoom);
        scale.setX(zoom);
        scale.setY(-zoom);
        first.getTransforms().add(scale);
        
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
                //System.out.println(myAr[0]);
                if(lowestX > myAr[0]) {
                    lowestX = myAr[0];
                    
                }
                if(highestX < myAr[0]) {
                    highestX = myAr[0];
                }
                if(highestY < myAr[1]) {
                    highestY = myAr[1];
                }
                if(lowestY> myAr[1]) {
                    lowestY = myAr[1];
                }
                myGon.getPoints().addAll(myAr);

            }
            dataManager.getItems().get(i).setPoly(myGon);
            myGon.setOnMouseClicked(e -> {
                for (int j = 0; j < dataManager.getItems().size(); j++) {
                    if (dataManager.getItems().get(j).getPoly().equals(myGon)) {
                        itemsTable.getSelectionModel().select(dataManager.getItems().get(j));
                        System.out.println("polygon clicked");
                    }

                }

            });
            //green fill, black outline
            myGon.setFill(Paint.valueOf("#21FF42"));
            myGon.setStroke(Paint.valueOf("#000000"));
            myGon.setStrokeWidth(.01);
            first.getChildren().add(myGon);
            //blue ocean
            // first.getParent().setStyle("-fx-background-color: #99d6ff;");
            first.setStyle("-fx-background-color: #99d6ff;");
       /*     DataManager dm = (DataManager) app.getDataComponent();
        first.resize(dm.getWidth(), dm.getHeight());
        firstParent.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
        WritableImage wi = new WritableImage((int)dm.getWidth(), (int)dm.getHeight());
        WritableImage snapshot = first.snapshot(new SnapshotParameters(), wi);
        File output = new File("snapshot" + ".png");
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(snapshot, null), "png", output);
            } catch (IOException ex) {
                System.out.println("Snapshot error");
            } */

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
        lowestX = 900;
        highestY = -900;
        lowestY = 900;
        highestX = -900;
        diffX = 0;
        diffY = 0;
        layoutMap();
        fixLayout();

    }

    public void center() {

    }

    public void moveRight() {
        //simply move the camera 5 pixels to the right
        counterRight = counterRight + 5;
        first.setTranslateX(-10 + first.getTranslateX());

    }

    public void moveLeft() {
        //simply move the camera 5 pixels to the left
        counterRight = counterRight - 5;
        first.setTranslateX(10 + first.getTranslateX());

    }

    public void moveUp() {
        //simply move the camera 5 pixels to the up
        counterUp = counterUp + 5;
        first.setTranslateY(6 + first.getTranslateY());
    }

    public void moveDown() {
        //simply move the camera 5 pixels to the down
        counterUp = counterUp - 5;
        first.setTranslateY(-6 + first.getTranslateY());
    }

    public void zoomIn(MouseEvent e) {
         //we grab the xlocation and ylocation in reference to our "center"
       /* xloc = (int) (e.getX() - app.getGUI().getWindow().getWidth() / 2);
        yloc = (int) (e.getY() - app.getGUI().getWindow().getHeight() / 2);
        //we move the screen to the clicked location
        counterRight = counterRight + xloc;
        counterUp = counterUp - yloc;
        first.setTranslateX(-10 - counterRight + app.getGUI().getWindow().getWidth() / 2);
        first.setTranslateY(6 + counterUp + app.getGUI().getWindow().getHeight() / 2); 
        //we remove all zoom effects, and then apply a new zoom based on counterzoom */
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
        System.out.println("zooming out" + zoom);
        first.getTransforms().clear();
        Scale scale = new Scale();
        counterZoom--;
        scale.setX(zoom + (counterZoom * .7));
        scale.setY(-zoom - (counterZoom * .7));
        first.getTransforms().add(scale);
    }
}
