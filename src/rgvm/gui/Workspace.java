/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rgvm.gui;

import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import properties_manager.PropertiesManager;
import rgvm.PropertyType;
import saf.components.AppWorkspaceComponent;
import rgvm.RegioVincoMapEditor;
import rgvm.controller.MapEditorController;
import rgvm.data.DataManager;
import rgvm.data.RegionItem;
import static saf.settings.AppStartupConstants.FILE_PROTOCOL;
import static saf.settings.AppStartupConstants.PATH_IMAGES;
import static saf.settings.AppPropertyType.*;

/**
 *
 * @author McKillaGorilla
 */
public class Workspace extends AppWorkspaceComponent {

    RegioVincoMapEditor app;
    TableView<RegionItem> itemsTable;
    TableColumn nameColumn;
    TableColumn capitalColumn;
    TableColumn leaderColumn;

    public Workspace(RegioVincoMapEditor initApp) {
        app = initApp;
        layoutGUI();
        workspace.setDividerPosition(0,.885);
        setupHandlers();

    }

    public void layoutGUI() {
        workspace = new SplitPane();
        Pane first = new Pane();
        Pane second = new Pane();
        workspace.getItems().addAll(first, second);
        ImageView img = new ImageView();
        PropertiesManager props = PropertiesManager.getPropertiesManager();

        // dummy image
        String imagePath = FILE_PROTOCOL + PATH_IMAGES + props.getProperty(IMAGE_TEST.toString());
        Image holder = new Image(imagePath);
        img.setImage(holder);
        first.getChildren().add(img);

        itemsTable = new TableView();
        
        nameColumn = new TableColumn(props.getProperty(PropertyType.NAME_COLUMN_HEADING));
        capitalColumn = new TableColumn(props.getProperty(PropertyType.CAPITAL_COLUMN_HEADING));
        leaderColumn = new TableColumn(props.getProperty(PropertyType.LEADER_COLUMN_HEADING));
        nameColumn.setCellValueFactory(new PropertyValueFactory<String, String>("name"));
        capitalColumn.setCellValueFactory(new PropertyValueFactory<String, String>("leader"));
        leaderColumn.setCellValueFactory(new PropertyValueFactory<String, String>("capital"));
        itemsTable.getColumns().clear();
        itemsTable.getColumns().add(nameColumn);
        itemsTable.getColumns().add(leaderColumn);
        itemsTable.getColumns().add(capitalColumn);
        
        
        DataManager dataManager = (DataManager) app.getDataComponent();
        RegionItem dummy = new RegionItem("test name", "test leader" , "test capital");
        dataManager.addItem(dummy);
        itemsTable.setItems(dataManager.getItems());
        second.getChildren().add(itemsTable);
        itemsTable.minWidthProperty().bind(app.getGUI().getWindow().widthProperty().multiply(.5));
        itemsTable.minHeightProperty().bind(app.getGUI().getWindow().heightProperty().multiply(.9));
    }

    @Override
    public void reloadWorkspace() {

    }

    @Override
    public void initStyle() {

    }
    
    public void setupHandlers() {
        MapEditorController controller = new MapEditorController();
        
        itemsTable.setOnMouseClicked(e -> {
            
            if (e.getClickCount() == 2) {
                if (itemsTable.getSelectionModel().getSelectedItem() != null) {
                    
                    controller.processEditItem(itemsTable.getSelectionModel().getSelectedItem());
                }
            }

        });
    }
}
