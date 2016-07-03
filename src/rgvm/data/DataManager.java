package rgvm.data;

import saf.components.AppDataComponent;
import rgvm.RegioVincoMapEditor;

/**
 *
 * @author McKillaGorilla
 */
public class DataManager implements AppDataComponent {
    RegioVincoMapEditor app;
    
    public DataManager(RegioVincoMapEditor initApp) {
        app = initApp;
    }
    
    @Override
    public void reset() {
        
    }
}
