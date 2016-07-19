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
import properties_manager.PropertiesManager;

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
import saf.ui.AppMessageDialogSingleton;

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

    public void processNewButton(RegioVincoMapEditor app) {
        //TODO string literals
        NewDialog myDiag = NewDialog.getSingleton();
        Stage newStage = new Stage();
        myDiag.init(newStage);

        myDiag.show("New Map");
        System.out.println(myDiag.getSelection());
        if (myDiag.getSelection().equalsIgnoreCase("yes")) {
            if (myDiag.getName().equals("[](no name)") || myDiag.getName().equals("") || myDiag.getParentDirectory() == null || myDiag.getDataDirectory() == null) {

                AppMessageDialogSingleton single = AppMessageDialogSingleton.getSingleton();
                single.show("error", "Please fill out all fields");
            } else {
                app.getDataComponent().reset();
                DataManager dm = (DataManager) app.getDataComponent();
                dm.setName(myDiag.getName());
                dm.setParent(myDiag.getParentDirectory());
                dm.setRawPath(myDiag.getDataDirectory());
                System.out.println("PD: " + myDiag.getParentDirectory());
                System.out.println("DD: " + myDiag.getDataDirectory());
                try {
                    processLoadNew(app);
                } catch (IOException ex) {
                    AppMessageDialogSingleton single = AppMessageDialogSingleton.getSingleton();
                    single.show("error loading data", "error loading data");
                }

                app.getWorkspaceComponent().reloadWorkspace();
            }
        }
    }

    public void processLoadNew(RegioVincoMapEditor app) throws IOException {
        DataManager dm = (DataManager) app.getDataComponent();
        //TODO make sure loaddata works with relative path
        app.getFileComponent().loadData(app.getDataComponent(), dm.getRawPath());
        dm.setBackgroundColor("#99d6ff");
        dm.setBigFlagPath(dm.getParent()+dm.getName()+" Flag.png");
        System.out.println("Flag path: " + dm.getBigFlagPath());
        dm.setBorderColor("#000000");
        dm.setHeight(536);
        dm.setSealPath(dm.getParent()+dm.getName()+" Seal.png");
        dm.setThickness(1.0);
        dm.setWidth(802);
        dm.setZoom(1.0);

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

    public void processExportTest(RegioVincoMapEditor app) {
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(new File("./export/"));
        fc.setTitle("Save Work to RVM File");
        fc.getExtensionFilters().addAll(
                new ExtensionFilter("Regio Vinco Map File", "*.rvm"));
        File selectedFile = fc.showSaveDialog(app.getGUI().getWindow());
        try {

            app.getFileComponent().exportData(app.getDataComponent(), selectedFile.getAbsolutePath());
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
        Workspace work = (Workspace) app.getWorkspaceComponent();
        work.polyLoad();

    }

    public void test(RegioVincoMapEditor app) {
        app.getDataComponent().reset();
        FileChooser fc = new FileChooser();
        fc.setInitialDirectory(new File(PATH_RAW_FILES));
        fc.setTitle("Raw Map File JSON");
        File selectedFile = fc.showOpenDialog(app.getGUI().getWindow());
        try {
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
            System.out.println("Writing to file: HARDCODED VALUES = 'background color = 010101' , 'big flag path = Big flag path' , 'border color = 101010' , 'height = 1000' , 'zoom = 2.0' , 'raw path value = Raw path value'");
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
        System.out.println("saving file");
        processSaveTest(app);
        System.out.println("Loading in new RVME file");
        processLoadBig(app);
        DataManager dm = (DataManager) app.getDataComponent();
        System.out.println("Current values: 'background color'= " + dm.getBackgroundColor() + " 'big flag path'= " + dm.getBigFlagPath() + " 'border color'= " + dm.getBorderColor() + " 'height'= " + dm.getHeight() + " 'zoom'= " + dm.getZoom() + " 'raw path value'= " + dm.getRawPath());

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

}
