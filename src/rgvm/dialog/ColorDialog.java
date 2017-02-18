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
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import properties_manager.PropertiesManager;
import static saf.components.AppStyleArbiter.CLASS_SUBHEADING_LABEL;
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
public class ColorDialog extends Stage {

    // HERE'S THE SINGLETON
    static ColorDialog singleton;

    // GUI CONTROLS FOR OUR DIALOG
    VBox messagePane;

    HBox nameBox;
    HBox leaderBox;
    HBox capitalBox;
    HBox endBox;

    CheckBox checkField;

    TextField nameField;
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
    String color;
    PropertiesManager props;
    String YES;
    String NO;

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
    private ColorDialog() {
        props = PropertiesManager.getPropertiesManager();
        YES = props.getProperty(PropertyType.YES);
        NO = props.getProperty(PropertyType.NO);
    }

    /**
     * The static accessor method for this singleton.
     *
     * @return The singleton object for this type.
     */
    public static ColorDialog getSingleton() {
        if (singleton == null) {
            singleton = new ColorDialog();
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
        if (primaryStage.getModality().toString() != "NONE") {

            initModality(Modality.WINDOW_MODAL);

            initOwner(primaryStage);
        }
        // LABELS AND TEXT FIELDS
        nameLabel = new Label(props.getProperty(PropertyType.COLOR));
        ColorPicker cp = new ColorPicker();
        cp.setValue(Color.valueOf("#000000"));
        leaderLabel = new Label(props.getProperty(PropertyType.LEADER));
        capitalLabel = new Label(props.getProperty(PropertyType.CAPITAL));

        //css
        leaderLabel.getStyleClass().add(CLASS_SUBHEADING_LABEL);
        capitalLabel.getStyleClass().add(CLASS_SUBHEADING_LABEL);

        nameField = new TextField();
        capitalField = new TextField();
        leaderField = new TextField();

        // YES, NO, AND CANCEL BUTTONS
        yesButton = new Button(YES);
        noButton = new Button(NO);

        // MAKE THE EVENT HANDLER FOR THESE BUTTONS
        EventHandler yesNoCancelHandler = (EventHandler<ActionEvent>) (ActionEvent ae) -> {
            Button sourceButton = (Button) ae.getSource();
            ColorDialog.this.selection = sourceButton.getText();
            if (selection.equalsIgnoreCase("yes")) {
                color = cp.getValue().toString();
            }
            ColorDialog.this.hide();
        };

        // AND THEN REGISTER THEM TO RESPOND TO INTERACTIONS
        yesButton.setOnAction(yesNoCancelHandler);
        noButton.setOnAction(yesNoCancelHandler);

        // CATEGORY HBOX
        nameBox = new HBox();
        nameBox.setAlignment(Pos.CENTER);
        nameBox.getChildren().add(nameLabel);
        //nameBox.getChildren().add(nameField = new TextField("CurrentColor"));
        nameBox.getChildren().add(cp);

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
        this.setWidth(225);
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

    public String getColor() {
        return color;
    }

    public void setColor(String col) {
        color = col;
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
        showAndWait();
    }
}
