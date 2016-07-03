/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rgvm.gui;

import javafx.scene.layout.Pane;
import saf.components.AppWorkspaceComponent;
import rgvm.RegioVincoMapEditor;

/**
 *
 * @author McKillaGorilla
 */
public class Workspace extends AppWorkspaceComponent {
    RegioVincoMapEditor app;
    
    public Workspace(RegioVincoMapEditor initApp) {
        app = initApp;
        workspace = new Pane();
    }

    @Override
    public void reloadWorkspace() {
        
    }

    @Override
    public void initStyle() {
        
    }
}
