/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rgvm.dialog;

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
import javafx.stage.Modality;
import properties_manager.PropertiesManager;
import rgvm.PropertyType;
import rgvm.data.RegionItem;

/**
 * This class is heavily based on AppYesNoCancelDialogSingleton with changes
 * made to it to allow for a custom add function
 *
 * @author Richard McKenna
 * @author Brian Khaneyan
 * @version 1.0
 */
public class ResizeDialog extends Stage {

    // HERE'S THE SINGLETON
    static ResizeDialog singleton;

    // GUI CONTROLS FOR OUR DIALOG
    VBox messagePane;

    HBox nameBox;
    HBox leaderBox;
    HBox capitalBox;
    HBox endBox;

    CheckBox checkField;

    TextField heightField;
    TextField leaderField;
    TextField capitalField;

    RegionItem myItem;

    Scene messageScene;

    Label heightLabel;
    Label leaderLabel;
    Label capitalLabel;
    Label widthLabel;
    TextField widthField;

    Button yesButton;
    Button noButton;

    Button leftButton;
    Button rightButton;

    String selection;
    PropertiesManager props;
    String YES;
    String NO;
    String name;

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
    private ResizeDialog() {
        props = PropertiesManager.getPropertiesManager();
        YES = props.getProperty(PropertyType.YES);
        NO = props.getProperty(PropertyType.NO);
    }

    /**
     * The static accessor method for this singleton.
     *
     * @return The singleton object for this type.
     */
    public static ResizeDialog getSingleton() {
        if (singleton == null) {
            singleton = new ResizeDialog();
        }
        return singleton;
    }

    /**
     * This method initializes the singleton for use.
     *
     * @param primaryStage The window above which this dialog will be centered.
     */
    public void init(Stage primaryStage, double currentHeight, double currentWidth) {
        // MAKE THIS DIALOG MODAL, MEANING OTHERS WILL WAIT
        // FOR IT WHEN IT IS DISPLAYED
        // CHANGED SO THAT WE ONLY SET THIS WINDOW MODAL ONCE, AS WE 
        // CAN ONLY INITIALIZE IN OUR WORKSPACE CLASS EACH TIME WE HIT THE BUTTON
        name = "[](no name)";
        if (primaryStage.getModality().toString() != "NONE") {

            initModality(Modality.WINDOW_MODAL);

            initOwner(primaryStage);
        }
        // LABELS AND TEXT FIELDS
        //TODO string literal
        heightLabel = new Label("Height: ");
        heightField = new TextField(String.valueOf(currentHeight));
        widthLabel = new Label("Width: ");
        widthField = new TextField(String.valueOf(currentWidth));

        // YES, NO, AND CANCEL BUTTONS
        yesButton = new Button(YES);

        noButton = new Button(NO);

        // MAKE THE EVENT HANDLER FOR THESE BUTTONS
        EventHandler yesNoCancelHandler = (EventHandler<ActionEvent>) (ActionEvent ae) -> {
            Button sourceButton = (Button) ae.getSource();
            ResizeDialog.this.selection = sourceButton.getText();
            if (selection.equalsIgnoreCase("yes")) {
                name = heightField.getText();
            }
            ResizeDialog.this.hide();
        };
        this.setOnCloseRequest(e -> {
            selection = "no";
            e.consume();
            this.hide();
        });

        // AND THEN REGISTER THEM TO RESPOND TO INTERACTIONS
        yesButton.setOnAction(yesNoCancelHandler);
        noButton.setOnAction(yesNoCancelHandler);

        // CATEGORY HBOX
        nameBox = new HBox();
        nameBox.setAlignment(Pos.CENTER);
        nameBox.getChildren().add(heightLabel);
        //nameBox.getChildren().add(nameField = new TextField("CurrentColor"));
        nameBox.getChildren().add(heightField);
        nameBox.getChildren().add(widthLabel);
        nameBox.getChildren().add(widthField);

        // END HBOX
        endBox = new HBox();

        // NOW ORGANIZE OUR BUTTONS
        HBox buttonBox = new HBox();
        buttonBox.getChildren().add(yesButton);
        buttonBox.getChildren().add(noButton);

        // WE'LL PUT EVERYTHING HERE
        messagePane = new VBox();
        messagePane.setAlignment(Pos.CENTER);
        messagePane.getChildren().add(nameBox);
        buttonBox.setAlignment(Pos.CENTER);

        messagePane.getChildren().add(buttonBox);

        // MAKE IT LOOK NICE
        messagePane.setPadding(new Insets(10, 20, 20, 20));
        messagePane.setSpacing(10);

        // AND PUT IT IN THE WINDOW
        messageScene = new Scene(messagePane);
        this.setWidth(500);
        this.setHeight(150);
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

    public String getResizeHeight() {
        return heightField.getText();
    }

    public String getResizeWidth() {
        return widthField.getText();
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
