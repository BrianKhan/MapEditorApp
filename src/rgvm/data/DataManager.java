package rgvm.data;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import saf.components.AppDataComponent;
import rgvm.RegioVincoMapEditor;
import rgvm.gui.Workspace;

/**
 *
 * @author McKillaGorilla
 */
public class DataManager implements AppDataComponent {
    RegioVincoMapEditor app;
    ObservableList<RegionItem> data;
    
    public DataManager(RegioVincoMapEditor initApp) {
        app = initApp;
        data = FXCollections.observableArrayList();
        Workspace workspace = (Workspace)app.getWorkspaceComponent();
    }
    
    @Override
    public void reset() {
        
    }
    public ObservableList<RegionItem> getItems() {
        return data;
    }
    public void addItem(RegionItem item) {
        data.add(item);
    }
}
