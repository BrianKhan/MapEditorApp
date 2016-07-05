/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rgvm.controller;

import javafx.stage.Stage;
import rgvm.data.RegionItem;
import rgvm.dialog.ColorDialog;
import rgvm.dialog.EditDialog;
import rgvm.dialog.NewDialog;

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
}
