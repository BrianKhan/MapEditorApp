/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rgvm.dialog;

import java.io.File;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import properties_manager.PropertiesManager;
import static saf.components.AppStyleArbiter.CLASS_SUBHEADING_LABEL;
import rgvm.PropertyType;
import rgvm.data.RegionItem;
import saf.ui.AppMessageDialogSingleton;

/**
 * This class is heavily based on AppYesNoCancelDialogSingleton with changes
 * made to it to allow for a custom add function
 *
 * @author Richard McKenna
 * @author Brian Khaneyan
 * @version 1.0
 */
public class NewDialog extends Stage {

    // HERE'S THE SINGLETON
    static NewDialog singleton;

    // GUI CONTROLS FOR OUR DIALOG
    VBox messagePane;

    HBox nameBox;
    HBox leaderBox;
    HBox capitalBox;
    HBox endBox;

    CheckBox checkField;

    TextField parentField;
    TextField leaderField;
    TextField capitalField;

    RegionItem myItem;

    Scene messageScene;

    Label nameLabel;
    Label leaderLabel;
    Label capitalLabel;

    Button yesButton;
    Button noButton;

    Button leftButton;
    Button rightButton;

    String selection;
    PropertiesManager props;
    String YES;
    String NO;
    Stage myStage;
    String name;
    TextField nameField;
    String parentDirectory;
    String dataDirectory;
    TextField fileField;

    /**
     *
     */
    /**
     *
     */
    /**
     * Note that the constructor is private since it follows the singleton
     * design pattern.
     *
     * @param primaryStage The owner of this modal dialog.
     */
    private NewDialog() {
        props = PropertiesManager.getPropertiesManager();
        YES = props.getProperty(PropertyType.YES);
        NO = props.getProperty(PropertyType.NO);

    }

    /**
     * The static accessor method for this singleton.
     *
     * @return The singleton object for this type.
     */
    public static NewDialog getSingleton() {
        if (singleton == null) {
            singleton = new NewDialog();
        }
        return singleton;
    }

    /**
     * This method initializes the singleton for use.
     *
     * @param primaryStage The window above which this dialog will be centered.
     */
    public void init(Stage primaryStage) {
        // MAKE THIS DIALOG MODAL, MEANING OTHERS WILL WAIT
        // FOR IT WHEN IT IS DISPLAYED
        // CHANGED SO THAT WE ONLY SET THIS WINDOW MODAL ONCE, AS WE 
        // CAN ONLY INITIALIZE IN OUR WORKSPACE CLASS EACH TIME WE HIT THE BUTTON
        myStage = primaryStage;

        if (primaryStage.getModality().toString() != "NONE") {

            initModality(Modality.WINDOW_MODAL);

            initOwner(primaryStage);
        }
        // LABELS AND TEXT FIELDS
        name = "";
        nameLabel = new Label(props.getProperty(PropertyType.NAME));
        leaderLabel = new Label(props.getProperty(PropertyType.LEADER));
        capitalLabel = new Label(props.getProperty(PropertyType.CAPITAL));

        //css
        nameLabel.getStyleClass().add(CLASS_SUBHEADING_LABEL);
        leaderLabel.getStyleClass().add(CLASS_SUBHEADING_LABEL);
        capitalLabel.getStyleClass().add(CLASS_SUBHEADING_LABEL);

        parentField = new TextField();
        capitalField = new TextField();
        leaderField = new TextField();

        // YES, NO, AND CANCEL BUTTONS
        yesButton = new Button(YES);
        noButton = new Button(NO);

        // MAKE THE EVENT HANDLER FOR THESE BUTTONS
        EventHandler yesNoCancelHandler = (EventHandler<ActionEvent>) (ActionEvent ae) -> {
            Button sourceButton = (Button) ae.getSource();
            NewDialog.this.selection = sourceButton.getText();
            name = nameField.getText();

            NewDialog.this.hide();
        };

        // AND THEN REGISTER THEM TO RESPOND TO INTERACTIONS
        yesButton.setOnAction(yesNoCancelHandler);
        noButton.setOnAction(yesNoCancelHandler);

        // CATEGORY HBOX
        HBox notherBox = new HBox();
        notherBox.getChildren().add(new Label("Name: "));
        notherBox.getChildren().add(nameField = new TextField());

        nameBox = new HBox();
        //TODO string literals
        nameBox.getChildren().add(new Label("Parent Directory: "));
        nameBox.getChildren().add(parentField = new TextField("Parent Directory"));

        parentField.setDisable(true);
        Button directButton = new Button("Choose Parent Directory");
        nameBox.getChildren().add(directButton);

        HBox dataBox = new HBox();
        dataBox.getChildren().add(new Label("Data File: "));
        dataBox.getChildren().add(fileField = new TextField("Data Directory"));
        fileField.setDisable(true);
        Button fileDirect = new Button("Choose File");
        dataBox.getChildren().add(fileDirect);
        directButton.setOnMouseClicked(e -> {
            DirectoryChooser directChoose = new DirectoryChooser();

            directChoose.setTitle("Open parent directory");
            File fil = new File(".");
            directChoose.setInitialDirectory(fil);

            File dc = directChoose.showDialog(myStage);
            if (dc != null) {
                String s1 = dc.getAbsolutePath();
                String s2 = fil.getAbsolutePath();

                try {

                    parentDirectory = "." + s1.substring(s2.length() - 2);
                    parentField.setText(parentDirectory);
                } catch (StringIndexOutOfBoundsException exc) {
                    AppMessageDialogSingleton single = AppMessageDialogSingleton.getSingleton();
                    single.show("bad directory", "Please choose a subdirectory within the initially shown folder");
                    this.requestFocus();
                }
            }
        });
        fileDirect.setOnMouseClicked(e -> {
            FileChooser myChoose = new FileChooser();
            myChoose.setTitle("Open Data File");
            File fil = new File(".");

            myChoose.setInitialDirectory(fil);
            myChoose.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("JSON File", "*.json"));
            File dc = myChoose.showOpenDialog(myStage);
            if (dc != null) {

                String s1 = dc.getAbsolutePath();
                String s2 = fil.getAbsolutePath();

                try {

                    dataDirectory = "." + s1.substring(s2.length() - 2);
                    fileField.setText(dataDirectory);
                } catch (StringIndexOutOfBoundsException exc) {
                    AppMessageDialogSingleton single = AppMessageDialogSingleton.getSingleton();
                    single.show("bad directory", "Please choose a subdirectory within the initially shown folder");
                    this.requestFocus();
                }
            }
        });
        this.setOnCloseRequest(e -> {
            selection = "no";
            e.consume();
            this.hide();
        });

        // END HBOX
        endBox = new HBox();

        // NOW ORGANIZE OUR BUTTONS
        HBox buttonBox = new HBox();
        buttonBox.getChildren().add(yesButton);
        buttonBox.getChildren().add(noButton);

        // WE'LL PUT EVERYTHING HERE
        messagePane = new VBox();
        messagePane.setAlignment(Pos.CENTER);
        messagePane.getChildren().add(notherBox);
        messagePane.getChildren().add(nameBox);
        messagePane.getChildren().add(dataBox);
        buttonBox.setAlignment(Pos.CENTER);

        messagePane.getChildren().add(buttonBox);

        // MAKE IT LOOK NICE
        messagePane.setPadding(new Insets(10, 20, 20, 20));
        messagePane.setSpacing(10);

        // AND PUT IT IN THE WINDOW
        messageScene = new Scene(messagePane);
        this.setWidth(500);
        this.setHeight(250);
        this.setScene(messageScene);
    }

    /**
     * Accessor method for getting the selection the user made.
     *
     * @return Either YES, NO, or CANCEL, depending on which button the user
     * selected when this dialog was presented.
     */
    public String getSelection() {
        return selection;
    }

    public String getName() {
        if (name.equals("")) {
            return "[](no name)";
        }
        return name;
    }

    public String getParentDirectory() {
        return parentDirectory;
    }

    public String getDataDirectory() {
        return dataDirectory;
    }

    /**
     * This method loads a custom message into the label then pops open the
     * dialog.
     *
     * @param title The title to appear in the dialog window bar.
     *
     *
     */
    public void show(String title) {
        // SET THE DIALOG TITLE BAR TITLE

        setTitle(title);

        // SET THE MESSAGE TO DISPLAY TO THE USER
        // AND OPEN UP THIS DIALOG, MAKING SURE THE APPLICATION
        // WAITS FOR IT TO BE RESOLVED BEFORE LETTING THE USER
        // DO MORE WORK.
        try {
            showAndWait();
        } catch (IllegalStateException e) {

        }
    }
}
