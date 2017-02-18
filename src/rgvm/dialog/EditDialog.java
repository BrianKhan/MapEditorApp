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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import properties_manager.PropertiesManager;
import static saf.components.AppStyleArbiter.CLASS_SUBHEADING_LABEL;
import rgvm.PropertyType;
import rgvm.data.RegionItem;
/**
 * This class is heavily based on AppYesNoCancelDialogSingleton 
 * with changes made to it to allow for a custom add function
 * 
 * @author Richard McKenna
 * @author Brian Khaneyan
 * @version 1.0
 */
public class EditDialog extends Stage {
    // HERE'S THE SINGLETON
    static EditDialog singleton;
    
    // GUI CONTROLS FOR OUR DIALOG
    VBox messagePane;
    
    HBox nameBox;
    HBox leaderBox;
    HBox capitalBox;
    HBox endBox;
    HBox imageBox;
    
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
    PropertiesManager props;
    String YES;
    String NO;
    String LEFT;
    String RIGHT;

    /**
     *
     */
    

    /**
     *
     */
    
    
    /**
     * Note that the constructor is private since it follows
     * the singleton design pattern.
     * 
     * @param primaryStage The owner of this modal dialog.
     */
    private EditDialog() {
        props = PropertiesManager.getPropertiesManager();
        YES = props.getProperty(PropertyType.YES);
        NO = props.getProperty(PropertyType.NO);
        LEFT = props.getProperty(PropertyType.LEFT);
        RIGHT = props.getProperty(PropertyType.RIGHT);
    }
    
    /**
     * The static accessor method for this singleton.
     * 
     * @return The singleton object for this type.
     */
    public static EditDialog getSingleton() {
	if (singleton == null)
	    singleton = new EditDialog();
	return singleton;
    }
	
    /**
     * This method initializes the singleton for use.
     * 
     * @param primaryStage The window above which this
     * dialog will be centered.
     */
    public void init(Stage primaryStage, RegionItem item, String regionPath) {
        // MAKE THIS DIALOG MODAL, MEANING OTHERS WILL WAIT
        // FOR IT WHEN IT IS DISPLAYED
        // CHANGED SO THAT WE ONLY SET THIS WINDOW MODAL ONCE, AS WE 
        // CAN ONLY INITIALIZE IN OUR WORKSPACE CLASS EACH TIME WE HIT THE BUTTON
        if(primaryStage.getModality().toString() != "NONE") {
           
        initModality(Modality.WINDOW_MODAL);
        
        initOwner(primaryStage);
        
        }
        Stage myStage = primaryStage;
        this.setOnCloseRequest ( e-> { 
            selection = "no";
            e.consume();
            this.hide();
        });
        // LABELS AND TEXT FIELDS
        nameLabel = new Label(props.getProperty(PropertyType.NAME));        
        leaderLabel = new Label(props.getProperty(PropertyType.LEADER));
        capitalLabel = new Label(props.getProperty(PropertyType.CAPITAL));

        //css
        leaderLabel.getStyleClass().add(CLASS_SUBHEADING_LABEL);
        capitalLabel.getStyleClass().add(CLASS_SUBHEADING_LABEL);
        
        nameField = new TextField(item.getName());
        capitalField = new TextField(item.getCapital());
        leaderField = new TextField(item.getLeader());

        
        // YES, NO, AND CANCEL BUTTONS
        yesButton = new Button(YES);
        noButton = new Button(NO);
        leftButton = new Button(LEFT);
        rightButton = new Button(RIGHT);
        //cancelButton = new Button(CANCEL);
	
	// MAKE THE EVENT HANDLER FOR THESE BUTTONS
        EventHandler yesNoCancelHandler = (EventHandler<ActionEvent>) (ActionEvent ae) -> {
            Button sourceButton = (Button)ae.getSource();
            EditDialog.this.selection = sourceButton.getText();
            EditDialog.this.hide();
        };
        myStage.setOnCloseRequest(e-> {
            selection = "no";
            EditDialog.this.hide();
        });
        
	// AND THEN REGISTER THEM TO RESPOND TO INTERACTIONS
        yesButton.setOnAction(yesNoCancelHandler);
        noButton.setOnAction(yesNoCancelHandler);
        leftButton.setOnAction(yesNoCancelHandler);
        rightButton.setOnAction(yesNoCancelHandler);
      //  cancelButton.setOnAction(yesNoCancelHandler);
        
        // CATEGORY HBOX
        nameBox = new HBox();
        nameBox.getChildren().add(nameLabel);
        nameBox.getChildren().add(nameField = new TextField(item.getName()));
        
        // DESCRIPTION HBOX
        leaderBox = new HBox();
        leaderBox.getChildren().add(leaderLabel);
        leaderBox.getChildren().add(leaderField);
        
        // START HBOX
        capitalBox = new HBox();
        capitalBox.getChildren().add(capitalLabel);
        capitalBox.getChildren().add(capitalField);
        
        // END HBOX
        
        imageBox = new HBox();
        
        File leaderImage = new File(item.getLeaderPath());
        
                if (leaderImage.exists()) {
                    
                    System.out.println("Found leader image in: " +leaderImage.toString());    
                    Image img = new Image("file:"+item.getLeaderPath());
                    
                     ImageView holder = new ImageView();
                     holder.setFitHeight(50);
                     holder.setFitWidth(100);
                     holder.setImage(img);
                     imageBox.getChildren().add(holder);
        
                }
                
                else {
                    
                    System.out.println("Did not find leader image in: "+leaderImage.toString());
                }
                File flagImage = new File(item.getFlagPath());
                if(flagImage.exists()) {
                      System.out.println("Found flag image in: " +flagImage.toString());
                      Image imgs = new Image("file:"+item.getFlagPath());
                      ImageView holders = new ImageView();
                      holders.setPreserveRatio(true);
                      holders.setFitHeight(50);
                      holders.setFitWidth(100);
                      holders.setImage(imgs);
                      imageBox.getChildren().add(holders);
                }
                else {
                    System.out.println("Did not find flag image in: "+flagImage.toString());
                }
                
        
        
        
        
        // NOW ORGANIZE OUR BUTTONS
        HBox buttonBox = new HBox();
        buttonBox.getChildren().add(leftButton);
        buttonBox.getChildren().add(yesButton);
        buttonBox.getChildren().add(noButton);
        buttonBox.getChildren().add(rightButton);
       // buttonBox.getChildren().add(cancelButton);
        
        // WE'LL PUT EVERYTHING HERE
        messagePane = new VBox();
        messagePane.setAlignment(Pos.CENTER);
        messagePane.getChildren().add(nameBox);
        messagePane.getChildren().add(leaderBox);
        messagePane.getChildren().add(capitalBox);
        messagePane.getChildren().add(imageBox);
        buttonBox.setAlignment(Pos.CENTER);
        
        messagePane.getChildren().add(buttonBox);
        
        // MAKE IT LOOK NICE
        messagePane.setPadding(new Insets(10, 20, 20, 20));
        messagePane.setSpacing(10);
        
        

        // AND PUT IT IN THE WINDOW
        messageScene = new Scene(messagePane);
        this.setWidth(350);
        this.setHeight(250);
        this.setScene(messageScene);
    }

    /**
     * Accessor method for getting the selection the user made.
     * 
     * @return Either YES, NO, or CANCEL, depending on which
     * button the user selected when this dialog was presented.
     */
    public String getSelection() {
        return selection;
    }
    public String getName() {
        return nameField.getText();
    }
    public String getLeader() {
        return leaderField.getText();
    }
    public String getCapital() {
        return capitalField.getText();
    }
    
 
    /**
     * This method loads a custom message into the label
     * then pops open the dialog.
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