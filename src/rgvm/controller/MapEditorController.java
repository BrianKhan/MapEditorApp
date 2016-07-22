/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rgvm.controller;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Modality;

import javafx.stage.Stage;
import javafx.stage.StageStyle;
import properties_manager.PropertiesManager;

import rgvm.RegioVincoMapEditor;
import rgvm.data.DataManager;
import rgvm.data.RegionItem;
import rgvm.dialog.ColorDialog;
import rgvm.dialog.EditDialog;
import rgvm.dialog.NewDialog;
import rgvm.dialog.ResizeDialog;
import rgvm.file.FileManager;
import rgvm.gui.Workspace;
import static saf.settings.AppPropertyType.LOAD_WORK_TITLE;
import static saf.settings.AppStartupConstants.PATH_RAW_FILES;
import static saf.settings.AppStartupConstants.PATH_WORK;
import saf.ui.AppMessageDialogSingleton;

/**
 *
 * @author Brian
 */
public class MapEditorController {

    public MapEditorController() {

    }

    public String processEditItem(RegionItem e, RegioVincoMapEditor app) {
        DataManager dm = (DataManager) app.getDataComponent();
        EditDialog myDiag = EditDialog.getSingleton();
        Stage newStage = new Stage();
        e.setLeaderPath(dm.getParent() + "/" + dm.getName() + "/" + e.getLeader() + ".png");
        e.setFlagPath(dm.getParent() + "/" + dm.getName() + "/" + e.getName() + " Flag.png");

        myDiag.init(newStage, e, dm.getParent() + "/" + dm.getName() + "/" + e.getName());
        myDiag.show("Edit Item");

        if (myDiag.getSelection().equalsIgnoreCase("yes") || myDiag.getSelection().equalsIgnoreCase("right") || myDiag.getSelection().equalsIgnoreCase("left")) {
            app.getGUI().getSaveButton().setDisable(false);
            e.setName(myDiag.getName());
            e.setLeader(myDiag.getLeader());
            e.setCapital(myDiag.getCapital());

        }
        return myDiag.getSelection();
    }

    public void processOpenButton(RegioVincoMapEditor app) throws IOException {
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(new File(PATH_WORK));
        fc.setTitle("Open RVME File");
        fc.getExtensionFilters().addAll(
                new ExtensionFilter("Regio Vinco Map Editor File", "*.rvme"));
        File selectedFile = fc.showOpenDialog(app.getGUI().getWindow());
        if (selectedFile != null) {
            FileManager fm = (FileManager) app.getFileComponent();
            Workspace work = (Workspace) app.getWorkspaceComponent();
            app.getDataComponent().reset();
            DataManager dm = (DataManager) app.getDataComponent();
            fm.openData(app.getDataComponent(), selectedFile.getAbsolutePath());
            app.getGUI().getSaveButton().setDisable(true);
            app.getGUI().getThickness().setDisable(false);
            app.getGUI().getZoomSlider().setDisable(false);
            app.getGUI().getColorButton().setDisable(false);
            app.getGUI().getSaveButton().setDisable(false);
            app.getGUI().getExportButton().setDisable(false);
            app.getGUI().getRenameButton().setDisable(false);
            app.getGUI().getAddButton().setDisable(false);
            // app.getGUI().getRemoveButton().setDisable(false);
            app.getGUI().getBackgroundButton().setDisable(false);
            app.getGUI().getBorderColorButton().setDisable(false);
            app.getGUI().getReassignButton().setDisable(false);
            app.getGUI().getResizeButton().setDisable(false);
            System.out.println("Name: " + dm.getName());
            System.out.println("PD: " + dm.getParent());
            System.out.println("DD: " + dm.getRawPath());
            String anthemPath = dm.getParent() + "/" + dm.getName() + "/" + dm.getName() + " National Anthem.mid";
            File anthemFile = new File(anthemPath);
            if (anthemFile.exists()) {
                System.out.println("Found anthem file: " + anthemPath);
                app.getGUI().getPlayButton().setDisable(false);
            } else {
                System.out.println("Did not find anthem file: " + anthemPath);
            }
            app.getWorkspaceComponent().reloadWorkspace();
            for (int i = 0; i < dm.getItems().size(); i++) {
                RegionItem item = dm.getItems().get(i);
                item.setLeaderPath(dm.getParent() + "/" + dm.getName() + "/" + item.getLeader() + ".png");
                item.setFlagPath(dm.getParent() + "/" + dm.getName() + "/" + item.getName() + " Flag.png");
                if (dm.getItems().size() > 255) {

                } else {

                    String hex = String.format("#%02x%02x%02x", dm.getItems().get(i).getRed(), dm.getItems().get(i).getGreen(), dm.getItems().get(i).getBlue());
                    dm.getItems().get(i).getPoly().setFill(Paint.valueOf(hex));
                    dm.getItems().get(i).getPoly().setStrokeWidth(dm.getThickness());
                    // System.out.println("Grey value for index " +i+" " +hex);
                }
            }
            app.getGUI().getColorButton().setValue(Color.valueOf(dm.getBackgroundColor()));
            app.getGUI().getBorderColorButton().setValue(Color.valueOf(dm.getBorderColor()));
        }
    }

    public void processNewButton(RegioVincoMapEditor app) {
        //TODO string literals
        NewDialog myDiag = NewDialog.getSingleton();
        Stage newStage = new Stage();

        myDiag.init(newStage);
        myDiag.show("New Map");
        System.out.println(myDiag.getSelection());
        try {
            if (myDiag.getSelection().equalsIgnoreCase("yes")) {
                if (myDiag.getName().equals("[](no name)") || myDiag.getName().equals("") || myDiag.getParentDirectory() == null || myDiag.getDataDirectory() == null) {

                    AppMessageDialogSingleton single = AppMessageDialogSingleton.getSingleton();
                    single.show("error", "Please fill out all fields");
                } else {
                    Workspace work = (Workspace) app.getWorkspaceComponent();
                    app.getDataComponent().reset();
                    DataManager dm = (DataManager) app.getDataComponent();
                    dm.setName(myDiag.getName());
                    dm.setParent(myDiag.getParentDirectory().replace("\\", "/"));
                    dm.setRawPath(myDiag.getDataDirectory().replace("\\", "/"));
                    dm.setBackgroundColor("#99d6ff");
                    dm.setBorderColor("#000000");
                    System.out.println("Name: " + dm.getName());
                    System.out.println("PD: " + myDiag.getParentDirectory());
                    System.out.println("DD: " + myDiag.getDataDirectory());
                    try {
                        processLoadNew(app);
                    } catch (IOException ex) {
                        AppMessageDialogSingleton single = AppMessageDialogSingleton.getSingleton();
                        single.show("error loading data", "error loading data");
                    }
                    app.getGUI().getColorButton().setDisable(false);
                    app.getGUI().getSaveButton().setDisable(false);
                    app.getGUI().getExportButton().setDisable(false);
                    app.getGUI().getRenameButton().setDisable(false);
                    app.getGUI().getAddButton().setDisable(false);
                    // app.getGUI().getRemoveButton().setDisable(false);
                    app.getGUI().getBackgroundButton().setDisable(false);
                    app.getGUI().getBorderColorButton().setDisable(false);

                    app.getGUI().getResizeButton().setDisable(false);
                    String anthemPath = dm.getParent() + "/" + dm.getName() + "/" + dm.getName() + " National Anthem.mid";
                    File anthemFile = new File(anthemPath);
                    if (anthemFile.exists()) {
                        System.out.println("Found anthem file: " + anthemPath);
                        app.getGUI().getPlayButton().setDisable(false);
                    } else {
                        System.out.println("Did not find anthem file: " + anthemPath);
                    }
                    app.getGUI().getZoomSlider().setDisable(false);

                    app.getGUI().getZoomSlider().setValue(50);
                    dm.setZoom(0);
                    app.getGUI().getThickness().setDisable(false);
                    dm.setThickness(.01);
                    dm.setX(0);
                    dm.setY(0);
                    app.getGUI().getReassignButton().setDisable(false);
                    app.getGUI().getResizeButton().setDisable(false);
                    app.getWorkspaceComponent().reloadWorkspace();
                    int x = 0;
                    for (int i = 0; i < dm.getItems().size(); i++) {
                        RegionItem item = dm.getItems().get(i);
                        item.setLeaderPath(dm.getParent() + "/" + dm.getName() + "/" + item.getLeader() + ".png");
                        item.setFlagPath(dm.getParent() + "/" + dm.getName() + "/" + item.getName() + " Flag.png");
                        if (dm.getItems().size() > 255) {

                        } else {
                            x = x + 253 / dm.getItems().size();

                            dm.getItems().get(i).setRed(x);
                            dm.getItems().get(i).setBlue(x);
                            dm.getItems().get(i).setGreen(x);
                            String hex = String.format("#%02x%02x%02x", dm.getItems().get(i).getRed(), dm.getItems().get(i).getGreen(), dm.getItems().get(i).getBlue());
                            dm.getItems().get(i).getPoly().setFill(Paint.valueOf(hex));
                            // System.out.println("Grey value for index " +i+" " +hex);
                        }
                    }
                    x = 0;
                    app.getGUI().getColorButton().setValue(Color.valueOf(dm.getBackgroundColor()));
                    app.getGUI().getBorderColorButton().setValue(Color.valueOf(dm.getBorderColor()));
                }

            }
        } catch (NullPointerException e) {

        }
    }

    public void processSaveButton(RegioVincoMapEditor app) {
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(new File(PATH_WORK));
        fc.setTitle("Save Work to File");
        fc.getExtensionFilters().addAll(
                new ExtensionFilter("Regio Vinco Map Editor File", "*.rvme"));
        DataManager dm = (DataManager) app.getDataComponent();
        fc.setInitialFileName(dm.getName());
        File selectedFile = fc.showSaveDialog(app.getGUI().getWindow());

        try {

            app.getFileComponent().saveData(app.getDataComponent(), selectedFile.getAbsolutePath());
        } catch (Exception ex) {
            System.out.println("errors");
        }

        app.getGUI().getSaveButton().setDisable(true);
    }

    public void processLoadNew(RegioVincoMapEditor app) throws IOException {
        DataManager dm = (DataManager) app.getDataComponent();
        //TODO make sure loaddata works with relative path
        app.getFileComponent().loadData(app.getDataComponent(), dm.getRawPath());
        dm.setBackgroundColor("#99d6ff");
        dm.setBigFlagPath(dm.getParent() + "/" + dm.getName() + "/" + dm.getName() + " Flag.png");
        System.out.println("Flag path: " + dm.getBigFlagPath());
        dm.setBorderColor("#000000");
        dm.setHeight(536);
        dm.setSealPath(dm.getParent() + "/" + dm.getName() + "/" + dm.getName() + " Seal.png");
        System.out.println("Seal path: " + dm.getSealPath());
        dm.setThickness(.01);
        dm.setWidth(802);
        dm.setZoom(1.0);
        dm.setX(0);
        dm.setY(0);

        for (RegionItem myit : dm.getItems()) {
            myit.setBlue(0);
            myit.setRed(0);
            myit.setGreen(256);
            myit.setCapital("[](no capital)");
            myit.setFlagPath("[](no flag path)");
            myit.setLeader("[](no leader)");
            myit.setLeaderPath("[](no leader path)");
            myit.setName("[](no name)");

        }
        Workspace work = (Workspace) app.getWorkspaceComponent();
        work.reloadWorkspace();
    }

    public void processSaveTest(RegioVincoMapEditor app) {
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(new File(PATH_WORK));
        fc.setTitle("Save Work to File");
        fc.getExtensionFilters().addAll(
                new ExtensionFilter("Regio Vinco Map Editor File", "*.rvme"));
        File selectedFile = fc.showSaveDialog(app.getGUI().getWindow());

        try {

            app.getFileComponent().saveData(app.getDataComponent(), selectedFile.getAbsolutePath());
        } catch (IOException ex) {
            System.out.println("errors");
        }

    }

    public void processExportButton(RegioVincoMapEditor app, String absolutePath) {
        try {
            DataManager dm = (DataManager) app.getDataComponent();
            app.getFileComponent().exportData(app.getDataComponent(), absolutePath);
        } catch (IOException ex) {
            System.out.println("errors");
        }
    }

    public void processLoadTest(RegioVincoMapEditor app) {
        //  try{
        app.getDataComponent().reset();
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(new File(PATH_RAW_FILES));
        fc.setTitle("Raw Map File JSON");
        File selectedFile = fc.showOpenDialog(app.getGUI().getWindow());
        try {
            app.getDataComponent().reset();
            app.getFileComponent().loadData(app.getDataComponent(), selectedFile.getAbsolutePath());
            DataManager dm = (DataManager) app.getDataComponent();
            dm.setBackgroundColor("#010101");
            dm.setBigFlagPath("Big flag path");
            dm.setBorderColor("#101010");
            dm.setHeight(1000);
            dm.setName("name value");
            dm.setParent("Parent directory value");
            dm.setRawPath("Raw path value");
            dm.setSealPath("seal path value");
            dm.setThickness(2.0);
            dm.setWidth(1000);
            dm.setZoom(2.0);

            for (RegionItem myit : dm.getItems()) {
                myit.setBlue((int) (Math.random() * 0x1000000));
                myit.setRed((int) (Math.random() * 0x1000000));
                myit.setGreen((int) (Math.random() * 0x1000000));
                myit.setCapital("Capital value");
                myit.setFlagPath("FLAG_PATH_VALUE");
                myit.setLeader("LEADER  VALUE");
                myit.setLeaderPath("LEADER_PATH_VALUE");
                myit.setName("NAME VALUE");
                for (Double[] myar : myit.getList()) {
                    //    System.out.println("X: " + myar[0]);
                    //    System.out.println("Y: " + myar[1]);
                }
            }
        } catch (IOException ex) {
            System.out.println("errors");
        }
        Workspace work = (Workspace) app.getWorkspaceComponent();
        work.reloadWorkspace();
    }

    public void processResizeButton(RegioVincoMapEditor app) {
        DataManager dm = (DataManager) app.getDataComponent();
        ResizeDialog myDiag = ResizeDialog.getSingleton();
        Stage newStage = new Stage();
        myDiag.init(newStage, dm.getHeight(), dm.getWidth());
        myDiag.show("Choose Dimensions");
        if (myDiag.getSelection().equalsIgnoreCase("yes")) {
            app.getGUI().getSaveButton().setDisable(false);
            try {
                dm.setHeight(Double.valueOf(myDiag.getResizeHeight()));
                dm.setWidth(Double.valueOf(myDiag.getResizeWidth()));
            } catch (Exception e) {
                AppMessageDialogSingleton single = AppMessageDialogSingleton.getSingleton();
                single.show("error", "Please make sure you have entered a digit value");
            }

        }
    }

}
