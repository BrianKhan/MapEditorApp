/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rgvm.data;

import java.util.ArrayList;
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
    String flagPath;
    String leaderPath;
    int red;
    int blue;
    int green;
    ArrayList<Double[]> myList;
    Double[] tempList;

    public RegionItem(String myName, String myLeader, String myCapital) {
        myList = new ArrayList<Double[]>();
        name = new SimpleStringProperty(myName);
        capital = new SimpleStringProperty(myLeader);
        leader = new SimpleStringProperty(myCapital);

    }

    public RegionItem() {
        myList = new ArrayList<Double[]>();
        name = new SimpleStringProperty("");
        capital = new SimpleStringProperty("");
        leader = new SimpleStringProperty("");
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

    public void add(double x, double y) {
        tempList = new Double[2];
        tempList[0] = x;
        tempList[1] = y;
        myList.add(tempList);
    }
    public ArrayList<Double[]> getList() {
        return myList;
    }

    public String getName() {
        return name.getValue();
    }
    public void setName(String nam) {
        name.setValue(nam);
    }

    public String getLeader() {
        return leader.getValue();

    }
    public void setLeader(String lead) {
        leader.setValue(lead);
    }

    public String getCapital() {
        return capital.getValue();

    }
    public void setCapital(String cap) {
        capital.setValue(cap);
    }
    public String getFlagPath() {
        return flagPath;
        
    }
    public void setFlagPath(String path) {
        flagPath = path;
    }
    public String getLeaderPath() {
        return leaderPath;
    }
    public void setLeaderPath(String path) {
        leaderPath = path;
    }
    public int getRed() {
        return red;
    }
    public void setRed(int re) {
        red = re;
    }
    public int getBlue() {
        return blue;
    }
    public void setBlue(int blu) {
        blue = blu;
    }
    public int getGreen() {
        return green;
    }
    public void setGreen(int gree) {
        green = gree;
    }
}
