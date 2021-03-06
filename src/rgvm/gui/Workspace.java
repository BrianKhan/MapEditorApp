/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rgvm.gui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Scale;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import properties_manager.PropertiesManager;
import rgvm.PropertyType;
import saf.components.AppWorkspaceComponent;
import rgvm.RegioVincoMapEditor;
import rgvm.controller.MapEditorController;
import rgvm.data.DataManager;
import rgvm.data.RegionItem;
import rgvm.dialog.renameDialog;
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
    double sliderValueInitial;
    double sliderValueFinal;
    Rectangle myClip;
    boolean ignore;
    Sequence audio;
    Sequencer sqr;
    double mouseX;
    double mouseY;

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

    public void setClip(double x, double y) {
        myClip.setHeight(y);
        myClip.setWidth(x);
        firstParent.setClip(myClip);
    }

    public void layoutGUI() {
        workspace = new SplitPane();
        firstParent = new Pane();
        first = new Pane();

        DataManager dataManager = (DataManager) app.getDataComponent();

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
        capitalColumn.setCellValueFactory(new PropertyValueFactory<String, String>("capital"));
        leaderColumn.setCellValueFactory(new PropertyValueFactory<String, String>("leader"));
        itemsTable.getColumns().clear();
        itemsTable.getColumns().add(nameColumn);
        itemsTable.getColumns().add(leaderColumn);
        itemsTable.getColumns().add(capitalColumn);

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
        DataManager dm = (DataManager) app.getDataComponent();

        gui.getNewButton().setOnMouseClicked(e -> {
            controller.processNewButton(app);
            for (int i = 0; i < firstParent.getChildren().size(); i++) {
                if (firstParent.getChildren().get(i) instanceof ImageView) {
                    firstParent.getChildren().remove(i);
                }
            }
            gui.getRemoveButton().setDisable(true);
        });
        gui.getSaveButton().setOnMouseClicked(e -> {
            controller.processSaveButton(app);
        });
        gui.getOpenButton().setOnMouseClicked(e -> {
            try {
                ignore = true;

                controller.processOpenButton(app);
                for (int i = 0; i < firstParent.getChildren().size(); i++) {
                    if (firstParent.getChildren().get(i) instanceof ImageView) {
                        firstParent.getChildren().remove(i);
                    }
                }
                ignore = false;

            } catch (IOException ex) {
                System.out.println("errors");
            }
            gui.getRemoveButton().setDisable(true);
        });
        //addddd
        gui.getExportButton().setOnMouseClicked(e -> {
            gui.getRemoveButton().setDisable(true);
            itemsTable.getSelectionModel().clearSelection();
            DirectoryChooser directChoose = new DirectoryChooser();

            directChoose.setTitle("Directory to save RVM and PNG file");
            File fil = new File("./export");
            directChoose.setInitialDirectory(fil);
            String dcPath;
            try {
                dcPath = directChoose.showDialog(app.getGUI().getWindow()).getAbsolutePath();
            } catch (NullPointerException ee) {
                dcPath = null;
            }
            if (dcPath != null) {
                String mdcPath = dcPath + "/" + dm.getName() + ".png";
                System.out.println("Saving png file as: " + mdcPath);
                WritableImage img = firstParent.snapshot(new SnapshotParameters(), null);
                File image = new File(mdcPath);

                try {
                    ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png", image);
                    System.out.println("image write successful");
                } catch (IOException ex) {
                    Logger.getLogger(Workspace.class.getName()).log(Level.SEVERE, null, ex);
                }
                String absPath = dcPath + "/" + dm.getName() + ".rvm";
                controller.processExportButton(app, absPath);
            }

        });

        gui.getAddButton().setOnMouseClicked(e -> {
            FileChooser fc = new FileChooser();
            fc.setInitialDirectory(new File("./"));
            fc.setTitle("Open Image File");
            fc.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("PNG File", "*.png"));
            File selectedFile = fc.showOpenDialog(app.getGUI().getWindow());
            if (selectedFile == null) {

            } else {
                Image image = new Image("FILE:" + selectedFile.getAbsolutePath());
                ImageView img = new ImageView(image);
                img.setOnMouseClicked(ex -> {
                    img.requestFocus();
                    gui.getRemoveButton().setOnMouseClicked(exx -> {
                        System.out.println("running");
                        firstParent.getChildren().remove(img);
                        gui.getRemoveButton().setDisable(true);
                    });
                    firstParent.setOnMouseClicked(exxx -> {
                        if (!img.focusedProperty().get()) {
                            gui.getRemoveButton().setDisable(true);
                        }
                    });
                });
                /*      img.setOnMouseDragged(exx -> {
                    
                    img.relocate(zoom, zoom);
                    img.setLayoutX(exx.getScreenX());
                    img.setLayoutY(exx.getScreenY());
                }); */
                img.setOnMousePressed(event -> {
                    mouseX = event.getSceneX();
                    mouseY = event.getSceneY();
                });

                img.setOnMouseDragged(events -> {
                    img.requestFocus();
                    double deltaX = events.getSceneX() - mouseX;
                    double deltaY = events.getSceneY() - mouseY;
                    img.relocate(img.getLayoutX() + deltaX, img.getLayoutY() + deltaY);
                    mouseX = events.getSceneX();
                    mouseY = events.getSceneY();
                });
                img.focusedProperty().addListener(new ChangeListener<Boolean>() {
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                        if (newValue.booleanValue()) {
                            DropShadow ds = new DropShadow(20, Color.YELLOW);
                            img.setEffect(ds);
                            gui.getRemoveButton().setDisable(false);
                        } else {
                            img.setEffect(null);
                        }
                    }
                });

                firstParent.getChildren().add(img);

            }
        });
        gui.getPlayButton().setOnMouseClicked(e -> {
            gui.getRemoveButton().setDisable(true);
            String anthemPath = dm.getParent() + "/" + dm.getName() + "/" + dm.getName() + " National Anthem.mid";
            File anthemFile = new File(anthemPath);
            if (anthemFile.exists()) {
                try {
                    if (sqr.isRunning()) {
                        sqr.stop();
                    } else {
                        audio = MidiSystem.getSequence(anthemFile);
                        sqr.setSequence(audio);
                        sqr.start();

                    }
                } catch (InvalidMidiDataException ex) {
                    Logger.getLogger(Workspace.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(Workspace.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        });
        gui.getZoomSlider()
                .setOnMouseReleased(e -> {
                    gui.getRemoveButton().setDisable(true);
                    ignore = true;
                    gui.getZoomSlider().setValue(50);
                    ignore = false;
                }
                );
        gui.getResizeButton()
                .setOnMouseClicked(e -> {
                    gui.getRemoveButton().setDisable(true);
                    controller.processResizeButton(app);
                    myClip.setHeight(dm.getHeight());
                    myClip.setWidth(dm.getWidth());
                    firstParent.setClip(myClip);
                }
                );
        gui.getReassignButton()
                .setOnMouseClicked(e -> {
                    gui.getSaveButton().setDisable(false);
                    gui.getRemoveButton().setDisable(true);
                    if (dm.getItems().size() < 256) {
                        Random rand = new Random();
                        int rnd;
                        boolean[] check = new boolean[254];
                        for (int i = 0; i < dm.getItems().size(); i++) {
                            rnd = rand.nextInt(254);
                            while (check[rnd] || rnd == 0) {
                                rnd = rand.nextInt(254);
                            }
                            check[rnd] = true;
                            dm.getItems().get(i).setRed(rnd);
                            dm.getItems().get(i).setBlue(rnd);
                            dm.getItems().get(i).setGreen(rnd);
                            String hex = String.format("#%02x%02x%02x", dm.getItems().get(i).getRed(), dm.getItems().get(i).getGreen(), dm.getItems().get(i).getBlue());
                            dm.getItems().get(i).getPoly().setFill(Color.valueOf(hex));
                        }
                    }
                }
                );

        gui.getThickness()
                .addEventFilter(KeyEvent.ANY, e -> {

                    e.consume();
                    first.requestFocus();
                }
                );
        gui.getZoomSlider()
                .addEventFilter(KeyEvent.ANY, e -> {
                    e.consume();
                    first.requestFocus();
                }
                );
        gui.getColorButton().valueProperty().addListener(new ChangeListener<Color>() {

            public void changed(ObservableValue<? extends Color> ov, Color old_val, Color new_val) {
                DataManager dm = (DataManager) app.getDataComponent();
                app.getGUI().getSaveButton().setDisable(false);
                dm.setBackgroundColor(new_val.toString());
                firstParent.setBackground(new Background(new BackgroundFill(Color.valueOf(dm.getBackgroundColor()), CornerRadii.EMPTY, Insets.EMPTY)));
            }
        }
        );
        gui.getBorderColorButton().valueProperty().addListener(new ChangeListener<Color>() {
            public void changed(ObservableValue<? extends Color> ov, Color old_val, Color new_val) {
                DataManager dm = (DataManager) app.getDataComponent();
                app.getGUI().getSaveButton().setDisable(false);
                dm.setBorderColor(new_val.toString());
                app.getGUI().getSaveButton().setDisable(false);
                for (int x = 0; x < dm.getItems().size(); x++) {
                    dm.getItems().get(x).getPoly().setStroke(Color.valueOf(dm.getBorderColor()));
                }

            }
        });
        // workspace.get
        itemsTable.getSelectionModel()
                .selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {

                    if (oldSelection != null) {
                        oldSelection.getPoly().setStroke(Paint.valueOf(dm.getBorderColor()));
                    }

                    if (newSelection != null) {
                        newSelection.getPoly().setStroke(Color.YELLOW);
                    }

                }
                );
        gui.getThickness().valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
                if (!ignore) {
                    app.getGUI().getSaveButton().setDisable(false);
                    dm.setThickness(new_val.doubleValue() * 2 * .0001);
                    try {
                        for (int k = 0; k < dm.getItems().size(); k++) {
                            dm.getItems().get(k).getPoly().setStrokeWidth(dm.getThickness());
                        }
                    } catch (Exception e) {
                        //Squelch :)
                    }
                }
            }
        });
        gui.getZoomSlider().setOnMouseClicked(e -> {
            System.out.println("Zoom: " + zoom);
        });
        gui.getZoomSlider().valueProperty().addListener(new ChangeListener<Number>() {

            public void changed(ObservableValue<? extends Number> ov,
                    Number old_val, Number new_val) {
                if (!ignore) {
                    app.getGUI().getSaveButton().setDisable(false);
                    if (new_val.doubleValue() > old_val.doubleValue()) {
                        zoom = zoom + zoom * .01;
                        boolean manual = false;
                        Scale scale = new Scale();
                        first.getTransforms().clear();
                        first.setTranslateX(-lowestX * zoom);
                        first.setTranslateY(highestY * zoom);
                        dm.setX(first.getTranslateX());
                        dm.setY(first.getTranslateY());
                        scale.setX(zoom);
                        scale.setY(-zoom);
                        first.getTransforms().add(scale);
                    }
                    if (new_val.doubleValue() < old_val.doubleValue()) {
                        zoom = zoom - zoom * .01;
                        boolean manual = false;
                        Scale scale = new Scale();
                        first.getTransforms().clear();
                        first.setTranslateX(-lowestX * zoom);
                        first.setTranslateY(highestY * zoom);
                        dm.setX(first.getTranslateX());
                        dm.setY(first.getTranslateY());
                        scale.setX(zoom);
                        scale.setY(-zoom);
                        first.getTransforms().add(scale);

                    }
                    app.getGUI().getSaveButton().setDisable(false);
                    dm.setZoom(zoom);
                }
            }
        });
        gui.getRenameButton().setOnMouseClicked(e -> {
            gui.getRemoveButton().setDisable(true);
            renameDialog myDiag = renameDialog.getSingleton();
            Stage newStage = new Stage();

            myDiag.init(newStage, dm.getName());

            myDiag.show("Rename Map");
            if (myDiag.getSelection().equalsIgnoreCase("yes")) {
                if (!dm.getName().equalsIgnoreCase(myDiag.getRename())) {
                    app.getGUI().getSaveButton().setDisable(false);
                }
                dm.setName(myDiag.getRename());
                String anthemPath = dm.getParent() + "/" + dm.getName() + "/" + dm.getName() + " National Anthem.mid";
                File anthemFile = new File(anthemPath);
                if (anthemFile.exists()) {
                    System.out.println("Found anthem file: " + anthemPath);
                    app.getGUI().getPlayButton().setDisable(false);
                } else {
                    System.out.println("Did not find anthem file: " + anthemPath);
                }
                dm.setBigFlagPath(dm.getParent() + "/" + dm.getName() + "/" + dm.getName() + " Flag.png");
                dm.setSealPath(dm.getParent() + "/" + dm.getName() + "/" + dm.getName() + " Seal.png");
                System.out.println("new name = " + dm.getName());
                System.out.println("new Flag Path: " + dm.getParent() + "/" + dm.getName() + "/" + dm.getName() + " Flag.png");
                System.out.println("new Seal Path: " + dm.getParent() + "/" + dm.getName() + "/" + dm.getName() + " Seal.png");
            }
        });
        first.setOnMouseClicked(e -> {
            first.requestFocus();
        });
        gui.getAppPane().setOnKeyPressed(e -> {
            gui.getRemoveButton().setDisable(true);
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
            if (e.getCode().equals(KeyCode.NUMPAD2)) {

                boolean manual = true;
                // zoomIn(manual);
            }
            if (e.getCode().equals(KeyCode.NUMPAD1)) {
                boolean manual = true;
                //  zoomOut(manual);
            }
        });

        itemsTable.setOnKeyPressed(e -> {
            gui.getRemoveButton().setDisable(true);
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
        itemsTable.setOnMouseClicked(e -> {
            gui.getRemoveButton().setDisable(true);
            if (e.getClickCount() == 2) {
                editItem();
            }

        });

    }

    public void editItem() {
        if (itemsTable.getSelectionModel().getSelectedItem() != null) {
            MapEditorController controller = new MapEditorController();
            itemsTable.getSelectionModel().getSelectedItem().getPoly().toFront();
            String x = controller.processEditItem(itemsTable.getSelectionModel().getSelectedItem(), app);
            if (x.equalsIgnoreCase("right")) {
                itemsTable.getSelectionModel().selectNext();
                gui.getSaveButton().setDisable(false);
                editItem();
            }
            if (x.equalsIgnoreCase("left")) {
                itemsTable.getSelectionModel().selectPrevious();
                gui.getSaveButton().setDisable(false);
                editItem();
            }
        }
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
        diffX = highestX - lowestX;
        diffY = highestY - lowestY;
        DataManager dm = (DataManager) app.getDataComponent();
        if (dm.getZoom() == 0) {
            if (diffX > diffY) {
                //zoom = 137.149-.374803*diffX;//135, 2.22;
                //EXPONENTIAL SCALE
                zoom = 767.8464 * (Math.pow(diffX, -.9953849));
                dm.setZoom(zoom);
                // 360 = 2.22
                //5.733737945556754 = 135
                //0.11345863342279472 = 6700
            }
            if (diffY > diffX) {
                zoom = 767.8464 * (Math.pow(diffY, -.9953849));
                dm.setZoom(zoom);

            }

        }
        if (dm.getX() == 0 && dm.getY() == 0) {
            first.setTranslateX(-lowestX * zoom);
            dm.setX(first.getTranslateX());
            first.setTranslateY(highestY * zoom);
            dm.setY(first.getTranslateY());
        }
        first.setTranslateX(dm.getX());
        first.setTranslateY(dm.getY());
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
                if (lowestX > myAr[0]) {
                    lowestX = myAr[0];

                }
                if (highestX < myAr[0]) {
                    highestX = myAr[0];
                }
                if (highestY < myAr[1]) {
                    highestY = myAr[1];
                }
                if (lowestY > myAr[1]) {
                    lowestY = myAr[1];
                }
                myGon.getPoints().addAll(myAr);

            }
            myGon.setStroke(Color.valueOf(dataManager.getBorderColor()));
            myGon.setStrokeWidth(dataManager.getThickness());
            dataManager.getItems().get(i).setPoly(myGon);
            myGon.setOnMouseClicked(e -> {
                gui.getRemoveButton().setDisable(true);
                for (int j = 0; j < dataManager.getItems().size(); j++) {
                    if (dataManager.getItems().get(j).getPoly().equals(myGon)) {
                        itemsTable.getSelectionModel().select(dataManager.getItems().get(j));
                        myGon.toFront();
                        if (e.getClickCount() == 2) {
                            MapEditorController controller = new MapEditorController();
                            //highlight
                            String x = controller.processEditItem(itemsTable.getSelectionModel().getSelectedItem(), app);
                            if (x.equalsIgnoreCase("right")) {
                                itemsTable.getSelectionModel().selectNext();
                                editItem();
                            }
                            if (x.equalsIgnoreCase("left")) {
                                itemsTable.getSelectionModel().selectPrevious();
                                editItem();
                            }
                        }
                    }

                }

            });
            //green fill, black outline
            myGon.setFill(Paint.valueOf("#21FF42"));
            myGon.setStroke(Paint.valueOf("#000000"));
            myGon.setStrokeWidth(.01);
            first.getChildren().add(myGon);
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
        try {
            sqr = MidiSystem.getSequencer();
            if (sqr == null) {
                // Error -- sequencer device is not supported.
                // Inform user and return...
            } else {
                // Acquire resources and make operational.
                sqr.open();
            }
        } catch (MidiUnavailableException ex) {
            Logger.getLogger(Workspace.class.getName()).log(Level.SEVERE, null, ex);
        }
        app.getGUI().getPrimaryScene().getWindow().setOnCloseRequest(e -> {
            try {
                sqr.close();
                System.exit(0);
            } catch (Exception eeee) {

            }
        });

        gui.getThickness().setValue((dataManager.getThickness() * 10000) / 2);
        gui.getZoomSlider().setValue(50);

        zoom = dataManager.getZoom();
        layoutMap();
        fixLayout();

        myClip = new Rectangle(dataManager.getWidth(), dataManager.getHeight());

        firstParent.setClip(myClip);
        firstParent.setBackground(new Background(new BackgroundFill(Color.valueOf(dataManager.getBackgroundColor()), CornerRadii.EMPTY, Insets.EMPTY)));

    }

    public void moveRight() {
        //simply move the camera 5 pixels to the right
        counterRight = counterRight + 5;
        first.setTranslateX(-10 + first.getTranslateX());
        DataManager dm = (DataManager) app.getDataComponent();
        dm.setX(first.getTranslateX());
        app.getGUI().getSaveButton().setDisable(false);

    }

    public void moveLeft() {
        //simply move the camera 5 pixels to the left
        counterRight = counterRight - 5;
        first.setTranslateX(10 + first.getTranslateX());
        DataManager dm = (DataManager) app.getDataComponent();
        dm.setX(first.getTranslateX());
        app.getGUI().getSaveButton().setDisable(false);

    }

    public void moveUp() {
        //simply move the camera 5 pixels to the up
        counterUp = counterUp + 5;
        first.setTranslateY(6 + first.getTranslateY());
        DataManager dm = (DataManager) app.getDataComponent();
        dm.setX(first.getTranslateY());
        app.getGUI().getSaveButton().setDisable(false);
    }

    public void moveDown() {
        //simply move the camera 5 pixels to the down
        counterUp = counterUp - 5;
        first.setTranslateY(-6 + first.getTranslateY());
        DataManager dm = (DataManager) app.getDataComponent();
        dm.setX(first.getTranslateY());
        app.getGUI().getSaveButton().setDisable(false);
    }

}
