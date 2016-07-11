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
    String mapName;
    String parentDirectory;
    Double width;
    Double height;
    double borderThickness;
    double zoom;
    int backgroundColor;
    int borderColor;
    String rawPath;
    String bigFlagPath;
    String sealPath;

    public DataManager(RegioVincoMapEditor initApp) {
        app = initApp;
        data = FXCollections.observableArrayList();
        Workspace workspace = (Workspace) app.getWorkspaceComponent();
    }

    @Override
    public void reset() {
        data.clear();
        mapName = null;
        parentDirectory = null;
        width = null;
        height = null;
        borderThickness = 0;
        zoom = 0;
        backgroundColor = 0;
        borderColor = 0;
        rawPath = null;
        bigFlagPath = null;
        sealPath = null;

    }

    public ObservableList<RegionItem> getItems() {
        return data;
    }

    public void addItem(RegionItem item) {
        data.add(item);
    }

    public String getName() {
        return mapName;
    }

    public void setName(String name) {
        mapName = name;
    }

    public String getParent() {
        return parentDirectory;
    }

    public void setParent(String parent) {
        parentDirectory = parent;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double with) {
        width = with;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double hei) {
        height = hei;
    }

    public double getThickness() {
        return borderThickness;

    }

    public void setThickness(double thick) {
        borderThickness = thick;
    }

    public double getZoom() {
        return zoom;
    }

    public void setZoom(double zom) {
        zoom = zom;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int color) {
        backgroundColor = color;
    }

    public int getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(int color) {
        borderColor = color;
    }

    public String getRawPath() {
        return rawPath;
    }

    public void setRawPath(String path) {
        rawPath = path;
    }

    public String getBigFlagPath() {
        return bigFlagPath;
    }

    public void setBigFlagPath(String path) {
        bigFlagPath = path;
    }

    public String getSealPath() {
        return sealPath;
    }

    public void setSealPath(String path) {
        sealPath = path;
    }
}
