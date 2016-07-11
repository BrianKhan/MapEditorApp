/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test_bed;
import java.io.IOException;
import rgvm.RegioVincoMapEditor;
import java.util.Locale;
import static javafx.application.Application.launch;
import javafx.collections.ObservableList;
import rgvm.data.DataManager;
import rgvm.data.RegionItem;

/**
 *
 * @author Brian
 */
public class TestSave {
    public static void main(String[] args) {
        RegioVincoMapEditor map = new RegioVincoMapEditor();
        
        map.main(args);
        try{
        map.getFileComponent().loadData(map.getDataComponent(),"/raw_map_data/Andorra.json");
        DataManager dm = (DataManager)map.getDataComponent();
        for(RegionItem myit: dm.getItems()) {
            for(Double[] myar: myit.getList()) {
                System.out.println("X: " +myar[0]);
                System.out.println("Y: " +myar[1]);
            }
        }
        } catch (IOException ex) {
            System.out.println("ok");
        }
    }
}
