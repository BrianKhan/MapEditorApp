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
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

import javafx.stage.Stage;
import rgvm.RegioVincoMapEditor;
import rgvm.data.DataManager;
import rgvm.data.RegionItem;
import rgvm.dialog.ColorDialog;
import rgvm.dialog.EditDialog;
import rgvm.dialog.NewDialog;
import rgvm.file.FileManager;
import rgvm.gui.Workspace;
import static saf.settings.AppPropertyType.LOAD_WORK_TITLE;
import static saf.settings.AppStartupConstants.PATH_RAW_FILES;
import static saf.settings.AppStartupConstants.PATH_WORK;

/**
 *
 * @author Brian
 */
public class MapEditorController {

    public MapEditorController() {

    }

    public void processEditItem(RegionItem e) {
        EditDialog myDiag = EditDialog.getSingleton();
        Stage newStage = new Stage();
        myDiag.init(newStage);
        myDiag.show();
    }

    public void processColorButton() {
        ColorDialog myDiag = ColorDialog.getSingleton();
        Stage newStage = new Stage();
        myDiag.init(newStage);
        myDiag.show();
    }

    public void processNewButton() {
        NewDialog myDiag = NewDialog.getSingleton();
        Stage newStage = new Stage();
        myDiag.init(newStage);
        myDiag.show();
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
    public void processLoadBig(RegioVincoMapEditor app) {
        app.getDataComponent().reset();
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(new File(PATH_WORK));
        fc.setTitle("RVME file");
        File selectedFile = fc.showOpenDialog(app.getGUI().getWindow());
        
            FileManager fm = (FileManager) app.getFileComponent();
        try {
            fm.loadRVME(app.getDataComponent(), selectedFile.getAbsolutePath());
        } catch (IOException ex) {
            System.out.println("error");
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
            app.getFileComponent().loadData(app.getDataComponent(), selectedFile.getAbsolutePath());
            DataManager dm = (DataManager) app.getDataComponent();
            dm.setBackgroundColor(010101);
            dm.setBigFlagPath("Big flag path");
            dm.setBorderColor(101010);
            dm.setHeight(1000);
            dm.setName("name value");
            dm.setParent("Parent directory value");
            dm.setRawPath("Raw path value");
            dm.setSealPath("seal path value");
            dm.setThickness(2.0);
            dm.setWidth(1000);
            dm.setZoom(2.0);
            System.out.println(dm.getItems().size());
            for (RegionItem myit : dm.getItems()) {
                myit.setBlue((int)(Math.random() * 0x1000000));
                myit.setRed((int)(Math.random() * 0x1000000));
                myit.setGreen((int)(Math.random() * 0x1000000));
                myit.setCapital("Capital value");
                myit.setFlagPath("FLAG_PATH_VALUE");
                myit.setLeader("LEADER  VALUE");
                myit.setLeaderPath("LEADER_PATH_VALUE");
                myit.setName("NAME VALUE");
                for (Double[] myar : myit.getList()) {
                    System.out.println("X: " + myar[0]);
                    System.out.println("Y: " + myar[1]);
                }
            }
        } catch (IOException ex) {
            System.out.println("errors");
        }
        Workspace work = (Workspace)app.getWorkspaceComponent();
        work.polyLoad();
    }

}
