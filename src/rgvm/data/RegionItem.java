/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rgvm.data;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author Brian
 */

public class RegionItem {
    final StringProperty name;
    final StringProperty capital;
    final StringProperty leader;
    public RegionItem(String myName, String myLeader, String myCapital) {
        name = new SimpleStringProperty(myName);
        capital = new SimpleStringProperty(myLeader);
        leader = new SimpleStringProperty(myCapital);
        
    }
    public StringProperty nameProperty() {
        return name;
        
    }
    public StringProperty leaderProperty() {
        return leader;
    }
    public StringProperty capitalProperty() {
        return capital;
                
    }
    
    
}
