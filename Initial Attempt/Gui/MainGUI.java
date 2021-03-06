/*************************************************************
 * ProjectGUI.java
 * Demo Lab Inventory Application
 * 
 * Eliza Gazda
 * 4 December 2015
 * 
 * Variable List:
 * 
 *	private String FileNameItem, FileNameCheckedOutItem; //fileNames
 * 	private TableView<CheckedOutItems> checkOutTable;//A new TableView is created to display the checked-out item table in the GUI
 *  private table: TableView<Item>; //A new TableView is created to display the item table in the GUI
 *	private TableColumn<Item, String> itemNumColumn, itemNameColumn, quantityColumn, manufacturerColumn, shelfNumColumn;//Columns that are added to the item table in the GUI
 *	private TableColumn<CheckedOutItems, String> checkedOutItemNameColumn,personNameColumn,dateOutColumn,dateInColumn;//Columns that are added to the checkout  table in the GUI
 *	//all panes
 *	private HBox search, buttons, labelHBox, checkOutHbox, checkOutButtons, checkOutTitlePane;
 *	private VBox tableSetUp, searchSetUp, checkOutTableSetUp, MainPaneTab2;
 *  private BorderPane MainPaneTab1, MainPaneTab3;
 *	
 *	//declare tab pane and tabs
	private TabPane tabPane;
	private Tab inventoryTab, checkInOutTab, checkInOutHistoryTab;
 *
 *  //GUI components
 *	private Buttons:searchButton, displayAll, editButton, endEditButton, saveButton, addItemsButton, deleteItemsButton, saveCheckOutHistoryButton, deleteCheckOut, editCheckOut, stopEditCheckOut;
 *	private TextField nameOutTextField, dateInTextField, dateOutTextField, itemOutTextField;	
 *  private Label tableTitle, checkOutTableTitle, searchTitle, searchNameLabel;
	private TextField searchName;
 *	
 *	//Declare two Observable Lists for the Items displayed in the table
 *	private ObservableList<Item> Inventory;
 *	private ObservableList<CheckedOutItems> checkOutHistory;	
 *
 * Methods List:
 *  main(String[]):void : launches the GUI
 *  ProjectGUI(): displays all GUI components and objects, uses event handlers for buttons and editable fields
 *  start(Stage):void : displays the scene, makes GUI editable
 *  searchName(String):void: a method for the search option in the GUI, allows for the display of the items searched by searching the name of the item
 *  errorBox():void:displays the error box that explains when a search doesen't match anything on the table
 *	showAddRow():void: new pane that allows the addittion of more items to the table
 *	textFieldAddRow(): HBox:TextBoxes for the input data to the inventory
 *	HBox textFieldCheckOutRow():HBox: TextBoxes to be displayed on the second tab, check-out tab
 *	submitcheckOutItems():void: allows the submution of items form the check-out tab to the check-out table
 *  deleteRow():void: allows for the user to delete a row from Inventory table and from the list
 *  deleteCheckOutRow():void:allows for the user to delete a row from Checked-Out Items table and from the list
 *  save():void:writes inventory data to a file using comma separated values
 *  load():void:reads inventory data from a file\
 *  savecheckOutHistory():void: writes checked-out items data to a file using comma separated values
 *  loadcheckOutHistory():void: reads checked-out items data from a file
 *
 *************************************************************/


//all libraries imported
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.control.TableColumn.CellEditEvent;



public class MainGUI extends Application {
	
	//Declare File Names
	private String FileNameItem, FileNameCheckedOutItem;

	// Declare panes, table, and columns
	private TableView<Item> table;
	private TableView<CheckedOutItems> checkOutTable;
	private TableColumn<Item, String> itemNumColumn, itemNameColumn, quantityColumn, manufacturerColumn, shelfNumColumn;
	private TableColumn<CheckedOutItems, String> checkedOutItemNameColumn,personNameColumn,dateOutColumn,dateInColumn;
	private HBox search, buttons, labelHBox, checkOutHbox, checkOutButtons, checkOutTitlePane;
	private VBox tableSetUp, searchSetUp, checkOutTableSetUp, MainPaneTab2;
	private BorderPane MainPaneTab1, MainPaneTab3;
	
	
	//declare tab pane and tabs
	protected TabPane tabPane;
	private Tab inventoryTab, checkInOutTab, checkInOutHistoryTab;
	
	//Declare GUI objects
	private Label tableTitle, checkOutTableTitle, searchTitle, searchNameLabel;
	private TextField searchName;
	private Button searchButton, displayAll, editButton, endEditButton, saveButton, addItemsButton, deleteItemsButton, saveCheckOutHistoryButton, deleteCheckOut, editCheckOut, stopEditCheckOut;
	private TextField nameOutTextField, dateInTextField, dateOutTextField, itemOutTextField;
	
	//Declare two Observable Lists for the Items displayed in the table
	private ObservableList<Item> Inventory;
	private ObservableList<CheckedOutItems> checkOutHistory;

	//launches the GUI
	public static void main(String[] args) { launch(args); }

	//constructor for the GUI, displays all GUI components and objects, uses event handlers for buttons and editable fields
	public ProjectGUI() {
		
		//declare new file names for file IO
		FileNameItem = new String("InventoryDB.csv");
		FileNameCheckedOutItem = new String("checkOutHistoryDB.csv");

		
		//instantiate tabs and tab pane
	    inventoryTab = new Tab();
	    checkInOutTab = new Tab();
	    checkInOutHistoryTab = new Tab();
	    tabPane = new TabPane();
		
	    
	    
	    //For the inventoryTab
		//Instantiate Pane for the center of the MainPane:THE TABLE
		table = new TableView<Item>();
		tableTitle = new Label("Inventory");
		tableTitle.setFont(new Font("Arial", 20));
		
		//items are added to the list called Inventory
		Inventory = FXCollections.observableArrayList();
		load();	//import file to get table data
		table.setItems(Inventory);	//Items added to the table as rows
		table.setTableMenuButtonVisible(true);
		
		//columns with column names created 
		itemNumColumn =new TableColumn<>("Item #");
		itemNumColumn.setCellValueFactory(new PropertyValueFactory<>("itemNum"));
		itemNumColumn.setCellFactory(TextFieldTableCell.<Item>forTableColumn());//set column cells editable by adding text field
		//add an event handler to allow for column editing when a certain cell is clicked on
		//user must type in desired input and press enter to display the new value
		itemNumColumn.setOnEditCommit(
			    (CellEditEvent<Item, String> t) -> {
			        ((Item) t.getTableView().getItems().get(
			            t.getTablePosition().getRow())
			            ).setitemNum(t.getNewValue());
			    });
		itemNameColumn = new TableColumn<>("Name");
		itemNameColumn.setCellValueFactory(new PropertyValueFactory<>("itemName"));
		itemNameColumn.setCellFactory(TextFieldTableCell.<Item>forTableColumn());//set column cells editable by adding text field
		//add an event handler to allow for column editing when a certain cell is clicked on
		//user must type in desired input and press enter to display the new value
		itemNameColumn.setOnEditCommit(
			    (CellEditEvent<Item, String> t) -> {
			        ((Item) t.getTableView().getItems().get(
			            t.getTablePosition().getRow())
			            ).setitemName(t.getNewValue());
			    });
		quantityColumn = new TableColumn<>("Quantity");
		quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
		quantityColumn.setCellFactory(TextFieldTableCell.<Item>forTableColumn());//set column cells editable by adding text field
		//add an event handler to allow for column editing when a certain cell is clicked on
		//user must type in desired input and press enter to display the new value
		quantityColumn.setOnEditCommit(
			    (CellEditEvent<Item, String> t) -> {
			        ((Item) t.getTableView().getItems().get(
			            t.getTablePosition().getRow())
			            ).setquantity(t.getNewValue());
			    });
		manufacturerColumn = new TableColumn<>("Manufacturer");
		manufacturerColumn.setCellValueFactory(new PropertyValueFactory<>("manufacturer"));
		manufacturerColumn.setCellFactory(TextFieldTableCell.<Item>forTableColumn());//set column cells editable by adding text field
		//add an event handler to allow for column editing when a certain cell is clicked on
		//user must type in desired input and press enter to display the new value
		manufacturerColumn.setOnEditCommit(
			    (CellEditEvent<Item, String> t) -> {
			        ((Item) t.getTableView().getItems().get(
			            t.getTablePosition().getRow())
			            ).setmanufacturer(t.getNewValue());
			    });
		
		shelfNumColumn = new TableColumn<>("Shelf Number");
		shelfNumColumn.setCellValueFactory(new PropertyValueFactory<>("shelfNum"));
		shelfNumColumn.setCellFactory(TextFieldTableCell.<Item>forTableColumn());	//set column cells editable by adding text field
		//add an event handler to allow for column editing when a certain cell is clicked on
		//user must type in desired input and press enter to display the new value
		shelfNumColumn.setOnEditCommit(
			    (CellEditEvent<Item, String> t) -> {
			        ((Item) t.getTableView().getItems().get(
			            t.getTablePosition().getRow())
			            ).setshelfNum(t.getNewValue());
			    });
		
		//add all columns to the table, and set preferences for style
		table.getColumns().setAll(itemNumColumn, itemNameColumn, quantityColumn, manufacturerColumn, shelfNumColumn);
		itemNumColumn.prefWidthProperty().bind(table.widthProperty().multiply(0.05));
		itemNameColumn.prefWidthProperty().bind(table.widthProperty().multiply(0.3));
		quantityColumn.prefWidthProperty().bind(table.widthProperty().multiply(0.1));
		manufacturerColumn.prefWidthProperty().bind(table.widthProperty().multiply(0.1));
		shelfNumColumn.prefWidthProperty().bind(table.widthProperty().multiply(0.1));
        itemNumColumn.setResizable(false);
        itemNameColumn.setResizable(false);
        quantityColumn.setResizable(false);
        manufacturerColumn.setResizable(false);
        shelfNumColumn.setResizable(false);
		
		//set table style
		tableSetUp = new VBox();
	    tableSetUp.setSpacing(5);
	    tableSetUp.setPadding(new Insets(10, 0, 0, 10));
	    tableSetUp.getChildren().addAll(tableTitle, table);
		
		   
	    //top pane, search boxes, set style for top pane
		search = new HBox();
		searchNameLabel = new Label("Name of Item:");
		searchName = new TextField();
		search.getChildren().addAll(searchNameLabel, searchName);
		search.setSpacing(10);
		
		searchTitle = new Label("Search Inventory:");
		searchTitle.setFont(new Font("Arial", 40));
		searchButton = new Button("Search");
		searchButton.setStyle("-fx-font-size: 16; -fx-effect: dropshadow( gaussian  , black , 3 , 0.0 , 0 , 0 )");
		//event handler to search the name of the item when the button is pressed
		searchButton.setOnAction(e -> {searchName(searchName.getText()); searchName.clear();});
		displayAll = new Button("Display All Inventory");
		displayAll.setOnAction(e-> table.setItems(Inventory));	//display all items on the table
		displayAll.setStyle("-fx-effect: dropshadow( gaussian  , black , 3 , 0.0 , 0 , 0 )");

		searchSetUp = new VBox();
		searchSetUp.getChildren().addAll(searchTitle, search, searchButton, displayAll);
		searchSetUp.setAlignment(Pos.TOP_CENTER);
		searchSetUp.setSpacing(20);

		
		//bottom pane, edit and save buttons, set style for bottom pane
		buttons = new HBox();
		editButton = new Button("Edit On");
		editButton.setStyle("-fx-background-color: #003366; -fx-font-size: 16; -fx-text-fill:#fff8dc; -fx-effect: dropshadow( gaussian  , black , 6 , 0.0 , 0 , 0 )");
		editButton.setOnAction(e->{table.setEditable(true);});
		endEditButton = new Button("Edit Off");
		endEditButton.setStyle("-fx-background-color: #003366; -fx-font-size: 16; -fx-text-fill:#fff8dc; -fx-effect: dropshadow( gaussian  , black , 6 , 0.0 , 0 , 0 )");
		endEditButton.setOnAction(e->{table.setEditable(false);});
		saveButton = new Button("Save");
		saveButton.setStyle("-fx-background-color: #33CC00; -fx-font-size: 16; -fx-text-fill:#fff8dc; -fx-effect: dropshadow( gaussian  , black , 6 , 0.0 , 0 , 0 )");
		saveButton.setOnAction(e->{save();});
		addItemsButton = new Button("Add Items");
		addItemsButton.setStyle("-fx-background-color: #33CC00; -fx-font-size: 16; -fx-text-fill:#fff8dc; -fx-effect: dropshadow( gaussian  , black , 6 , 0.0 , 0 , 0 )");
		addItemsButton.setOnAction(e->{showAddRow();});
		deleteItemsButton = new Button("Delete Items");
		deleteItemsButton.setStyle("-fx-background-color: #CC0000; -fx-font-size: 16; -fx-text-fill:#fff8dc; -fx-effect: dropshadow( gaussian  , black , 6 , 0.0 , 0 , 0 )");
		deleteItemsButton.setOnAction(e->{deleteRow();});
		
		buttons.getChildren().addAll(editButton, endEditButton, saveButton, addItemsButton, deleteItemsButton);
		buttons.setAlignment(Pos.CENTER);
		buttons.setSpacing(20);
		buttons.setMinHeight(60);
		
		//define and enter all components to the MainPane
		MainPaneTab1 = new BorderPane();
		
		MainPaneTab1.setCenter(tableSetUp);
		MainPaneTab1.setTop(searchSetUp);
		MainPaneTab1.setBottom(buttons);
		
		
		
		//For the second tab, the checked-out items tab
		//decalare new labels
		Label checkOutLabel = new Label("Check Out Items:");
		checkOutLabel.setStyle("-fx-font-size:40;");
		Label itemOutLabel = new Label("Items:");
		Label nameOutLabel = new Label("Name:");
		Label dateInLabel = new Label("Date In:");
		Label dateOutLabel = new Label("Date Out:");
		//submit button
		Button submitCheckOut = new Button("Submit");
		submitCheckOut.setStyle("-fx-font-size: 16; -fx-effect: dropshadow( gaussian  , black , 3 , 0.0 , 0 , 0)");
		submitCheckOut.setOnAction(e->{
			submitcheckOutItems();
			itemOutTextField.clear();
			nameOutTextField.clear();
			dateOutTextField.clear();
			dateInTextField.clear();
		});
		//setup of the middle tab
		checkOutHbox = textFieldCheckOutRow();
		labelHBox = new HBox(120);
		labelHBox.getChildren().addAll(itemOutLabel, nameOutLabel, dateOutLabel, dateInLabel);
		MainPaneTab2 = new VBox(20);
		MainPaneTab2.getChildren().addAll(checkOutLabel, labelHBox, checkOutHbox, submitCheckOut);
		MainPaneTab2.setAlignment(Pos.TOP_CENTER);
		
		
		
		//For the third tab, the Checked-out items table
		//declare new table
		checkOutTable = new TableView<>();
		checkOutHistory = FXCollections.observableArrayList();
		loadcheckOutHistory(); //load the data for the table
		checkOutTable.setItems(checkOutHistory);
				
		//columns with column names created 
		checkedOutItemNameColumn =new TableColumn<>("Item:");
		checkedOutItemNameColumn.setCellValueFactory(new PropertyValueFactory<>("itemName"));
		checkedOutItemNameColumn.setCellFactory(TextFieldTableCell.<CheckedOutItems>forTableColumn());//set column cells editable by adding text field
		//add an event handler to allow for column editing when a certain cell is clicked on
		//user must type in desired input and press enter to display the new value
		checkedOutItemNameColumn.setOnEditCommit(
			    (CellEditEvent<CheckedOutItems, String> t) -> {
			        ((CheckedOutItems) t.getTableView().getItems().get(
			            t.getTablePosition().getRow())
			            ).setitemName(t.getNewValue());
			    });
		personNameColumn = new TableColumn<>("Name:");
		personNameColumn.setCellValueFactory(new PropertyValueFactory<>("personName"));
		personNameColumn.setCellFactory(TextFieldTableCell.<CheckedOutItems>forTableColumn());//set column cells editable by adding text field
		//add an event handler to allow for column editing when a certain cell is clicked on
		//user must type in desired input and press enter to display the new value
		personNameColumn.setOnEditCommit(
			    (CellEditEvent<CheckedOutItems, String> t) -> {
			        ((CheckedOutItems) t.getTableView().getItems().get(
			            t.getTablePosition().getRow())
			            ).setpersonName(t.getNewValue());
			    });
		dateOutColumn = new TableColumn<>("Date-Out");
		dateOutColumn.setCellValueFactory(new PropertyValueFactory<>("dateOut"));				
		dateOutColumn.setCellFactory(TextFieldTableCell.<CheckedOutItems>forTableColumn());//set column cells editable by adding text field
		//add an event handler to allow for column editing when a certain cell is clicked on
		//user must type in desired input and press enter to display the new value
		dateOutColumn.setOnEditCommit(
			    (CellEditEvent<CheckedOutItems, String> t) -> {
			        ((CheckedOutItems) t.getTableView().getItems().get(
			            t.getTablePosition().getRow())
			            ).setdateOut(t.getNewValue());
			    });
		dateInColumn = new TableColumn<>("Date-In");
		dateInColumn.setCellValueFactory(new PropertyValueFactory<>("dateIn"));
		dateInColumn.setCellFactory(TextFieldTableCell.<CheckedOutItems>forTableColumn());//set column cells editable by adding text field
		//add an event handler to allow for column editing when a certain cell is clicked on
		//user must type in desired input and press enter to display the new value
		dateInColumn.setOnEditCommit(
			    (CellEditEvent<CheckedOutItems, String> t) -> {
			        ((CheckedOutItems) t.getTableView().getItems().get(
			            t.getTablePosition().getRow())
			            ).setdateIn(t.getNewValue());
			    });
		
		//add all columns to the table, and set preferences for style
		checkOutTable.getColumns().setAll(checkedOutItemNameColumn, personNameColumn, dateOutColumn, dateInColumn);
		checkedOutItemNameColumn.prefWidthProperty().bind(checkOutTable.widthProperty().multiply(0.5));
		personNameColumn.prefWidthProperty().bind(checkOutTable.widthProperty().multiply(0.3));
		dateOutColumn.prefWidthProperty().bind(checkOutTable.widthProperty().multiply(0.1));
		dateInColumn.prefWidthProperty().bind(checkOutTable.widthProperty().multiply(0.1));
		checkedOutItemNameColumn.setResizable(false);
		personNameColumn.setResizable(false);
		dateOutColumn.setResizable(false);
		dateInColumn.setResizable(false);
		
		//set table style
		checkOutTableSetUp = new VBox();
		checkOutTableSetUp.getChildren().add(checkOutTable);
		
		checkOutTableTitle = new Label("Checked Out Items:");
		checkOutTableTitle.setFont(new Font("Arial", 40));
		checkOutTitlePane = new HBox();
		checkOutTitlePane.getChildren().addAll(checkOutTableTitle);
		checkOutTitlePane.setAlignment(Pos.CENTER);
		checkOutTitlePane.setMinHeight(100);
		
		//all buttons declared and their events
		saveCheckOutHistoryButton = new Button("Save");
		saveCheckOutHistoryButton.setStyle("-fx-background-color: #33CC00; -fx-font-size: 16; -fx-text-fill:#fff8dc; -fx-effect: dropshadow( one-pass-box  , black , 6 , 0.0 , 0 , 0 )");
		saveCheckOutHistoryButton.setOnAction(e->{savecheckOutHistory();});
		deleteCheckOut = new Button("Delete");
		deleteCheckOut.setStyle("-fx-background-color: #CC0000;-fx-font-size: 16; -fx-text-fill:#fff8dc; -fx-effect: dropshadow( one-pass-box  , black , 6 , 0.0 , 0 , 0 )");
		deleteCheckOut.setOnAction(e->{deleteCheckOutRow();});
		editCheckOut = new Button("Edit On");
		editCheckOut.setStyle("-fx-background-color: #003366; -fx-font-size: 16; -fx-text-fill:#fff8dc; -fx-effect: dropshadow( gaussian  , black , 6 , 0.0 , 0 , 0 )");
		editCheckOut.setOnAction(e->{checkOutTable.setEditable(true);});
		stopEditCheckOut = new Button("Edit Off");
		stopEditCheckOut.setStyle("-fx-background-color: #003366; -fx-font-size: 16; -fx-text-fill:#fff8dc; -fx-effect: dropshadow( gaussian  , black , 6 , 0.0 , 0 , 0 )");
		stopEditCheckOut.setOnAction(e->{checkOutTable.setEditable(false);});
		//enter buttons in the pane
		checkOutButtons = new HBox();
		checkOutButtons.getChildren().addAll(saveCheckOutHistoryButton, deleteCheckOut, editCheckOut, stopEditCheckOut);
		checkOutButtons.setAlignment(Pos.CENTER);
		checkOutButtons.setSpacing(20);
		checkOutButtons.setMinHeight(60);
		
		MainPaneTab3 = new BorderPane();
		MainPaneTab3.setTop(checkOutTitlePane);
		MainPaneTab3.setCenter(checkOutTable);
		MainPaneTab3.setBottom(checkOutButtons);
		
		
		
		// Add all the panes to the TabPane
		inventoryTab.setText("Search Inventory");
		inventoryTab.setContent(MainPaneTab1);
		MainPaneTab1.setStyle("-fx-background-color: #b3d8ff");
		tabPane.getTabs().add(inventoryTab);
		
		checkInOutTab.setText("Check-Out");
		checkInOutTab.setContent(MainPaneTab2);
		MainPaneTab2.setStyle("-fx-background-color: #ffffcc");
		tabPane.getTabs().add(checkInOutTab);
		
		checkInOutHistoryTab.setText("Checked-Out Items");
		checkInOutHistoryTab.setContent(MainPaneTab3);
		MainPaneTab3.setStyle("-fx-background-color: #ffff99");
		tabPane.getTabs().add(checkInOutHistoryTab);
		
		
	}
		
	//displays the scene, makes GUI editable
	@Override
	public void start(Stage stage) throws Exception {
		
		// Set the scene and the stage
		Scene scene = new Scene(tabPane, 1000, 600);
		stage.setScene(scene);
		stage.setTitle("Demo Lab Inventory");
		stage.setResizable(false);
		
		// Display the GUI
		
		stage.show();
	}

	//a method for the search option in the GUI, allows for the display of the items searched by searching the name of the item
	public void searchName(String searchNameText){
		
		//make new List that will be displayed when the object is searched		
		ObservableList<Item> SearchList = FXCollections.observableArrayList();
		Item[] inventoryArray = Inventory.toArray(new Item[0]); //convert list to array
		
		//loop through every Item in the Inventory list
		for(int i=0; i < Inventory.size(); i++){
			//compare is the user input matches the name in the Inventory List
			if(inventoryArray[i].getitemName().toLowerCase().contains(searchNameText.toLowerCase()) && !searchNameText.equals("")){
				SearchList.add(inventoryArray[i]);	//add items to the new list
			}
		}
		//searchNameText.
		if(SearchList.size() == 0){
			table.setItems(Inventory);
			errorBox();
		}
		else{
			table.setItems(SearchList);	//display the new list in the table
		}
	}
	
	//displays the error box that explains when a search doesen't match anything on the table
	public void errorBox(){ 
		//create new pane 
		BorderPane errorPane = new BorderPane();
		//error text to be displayed
		Label errorText = new Label("Item not found, please search another item or look up an item by scrolling through the table.");
		errorText.setStyle("-fx-text-fill: red");
		//new button
		Button okButton = new Button("OK");
		//the setup
		errorPane.setCenter(errorText);
		errorPane.setBottom(okButton);
		errorPane.setAlignment(okButton, Pos.TOP_CENTER);
		
		//new scene is created
		Scene scene = new Scene(errorPane, 550, 100);
		Stage stage = new Stage();
		stage.setScene(scene);
		stage.setTitle("ERROR");
		stage.setResizable(false);
		stage.show();
		okButton.setOnAction(e->{stage.close();});

	}
	
	//new pane that allows the addittion of more items to the table
	public void showAddRow(){
		//set up for the pane
		VBox textFieldSetUp = new VBox();
		VBox newItemInputsVBox = new VBox();
		Button addToList = new Button("Add To Inventory");
		HBox textFieldLabelPane = new HBox(100); 
		
		// Create the text box labels
		Label itemNumLabel = new Label("Item #      ");
		Label itemNameLabel = new Label("Item Name   ");
		Label manufLabel = new Label("Manufacturer");
		Label quantLabel = new Label("Quantity");
		Label shelfNumLabel = new Label("Shelf #     ");
		Button addRowButton = new Button("Add More Items");
		addRowButton.setOnAction(e->{newItemInputsVBox.getChildren().add(textFieldAddRow());});
		textFieldLabelPane.getChildren().addAll(itemNumLabel, itemNameLabel, quantLabel, manufLabel, shelfNumLabel, addRowButton);	
		textFieldSetUp.getChildren().addAll(addToList, textFieldLabelPane);
		newItemInputsVBox.getChildren().addAll(textFieldAddRow());
		 
		//new stage
		Stage stage = new Stage();
		//items are added to list along with all of their attributes, columns
		addToList.setOnAction(e->{
			String[] inputData = new String[5]; //an array of new input data
			//each item(row) is put into a new hBox, HBoxes are converted to an array of HBoxes
			HBox[] addItemsRowsArray = newItemInputsVBox.getChildren().toArray(new HBox[0]);
			//for each row 
			for(int i=0; i < addItemsRowsArray.length; i++ ){
				//each textflied is converted to  a textfiedld array
				TextField[] addItemRow = addItemsRowsArray[i].getChildren().toArray(new TextField[0]);
				//for each textfield array
				for(int j=0; j < addItemRow.length; j++ ){
					//new inputdata is created by collecting the inputs from the textBox
					inputData[j] = addItemRow[j].getText();
				}
				Inventory.add(new Item(inputData)); //nputu data added to the inventory
			}
			stage.close();
			
		});
		 //new pane created
		BorderPane pane = new BorderPane();
		pane.setCenter(newItemInputsVBox);
		pane.setTop(textFieldSetUp);
		pane.setAlignment(addToList, Pos.TOP_CENTER);
		// Create and display said the aforementioned pane in a new stage 	
		ScrollPane s1 = new ScrollPane(pane);
		s1.setPrefSize(900, 300);
		Scene scene = new Scene(s1);
		stage.setScene(scene);
		stage.setTitle("Add Items");
		stage.setResizable(true);
		stage.show();

	}
	
	//TextBoxes for the input data to the inventory
	public HBox textFieldAddRow(){

		//Create text field for user input
		TextField enterItemNum = new TextField();
		TextField enterItemName = new TextField();
		TextField enterQuant = new TextField();
		TextField enterManuf = new TextField();
		TextField enterShelfNum = new TextField();
		
		//textfield are put in an HBox
		HBox textFieldPane = new HBox();
		textFieldPane.getChildren().addAll(enterItemNum, enterItemName, enterQuant, enterManuf, enterShelfNum);
		
		return textFieldPane;
	}
	
	//TextBoxes to be displayed on the second tab, check-out tab
	public HBox textFieldCheckOutRow(){
		
		//Create text field for user input
		nameOutTextField = new TextField();
		itemOutTextField = new TextField();
		dateInTextField = new TextField();
		dateOutTextField = new TextField();
		//textBoxes are in an HBox
		HBox textFieldPane = new HBox();
		textFieldPane.getChildren().addAll(itemOutTextField, nameOutTextField, dateOutTextField, dateInTextField);
		
		return textFieldPane;
	}
	
	//allows the submution of items form the check-out tab to the check-out table
	public void submitcheckOutItems(){
		String[] checkOutInputData = new String[4]; //new string array that stores the input from textboxes
			//new textField array for the textboxes 
			TextField[] addCheckedOutItem = checkOutHbox.getChildren().toArray(new TextField[0]);
			
			//for each textfield
			for(int i=0; i < addCheckedOutItem.length; i++ ){
				//new input is collected from the input in the textBoxes
				checkOutInputData[i] = addCheckedOutItem[i].getText();
			}
			checkOutHistory.add(new CheckedOutItems(checkOutInputData)); //input os added to the checkOutHstory list
	}
	
	//allows for the user to delete a row from Inventory table and from the list
	public void deleteRow(){
		//inventory list is converted to an array of Items
		Item[] inventoryArray = Inventory.toArray(new Item[0]);
		if(table.getSelectionModel() != null){
			//when the desired row/item is selected the item and row are removed
    		Inventory.remove(inventoryArray[table.getSelectionModel().getSelectedIndex()]);
        }
	}
	
	//allows for the user to delete a row from Checked-Out Items table and from the list
	public void deleteCheckOutRow(){
		//checkedOutHsitory list is converted to an array of Items
		CheckedOutItems[] checkedOutHistoryArray = checkOutHistory.toArray(new CheckedOutItems[0]);
		if(checkOutTable.getSelectionModel() != null){
			//when the desired row/item is selected the item and row are removed
			checkOutHistory.remove(checkedOutHistoryArray[checkOutTable.getSelectionModel().getSelectedIndex()]);
        }
	}
	
	//writes inventory data to a file using comma separated values
	public void save(){
		CsvFileWriter.writeCsvFile(FileNameItem, Inventory);
	}
	
	//reads inventory data from a file
	public void load(){
		//if file does not exist
		if(!(new File(FileNameItem).exists())){
			try {
				(new PrintWriter(FileNameItem)).close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		this.Inventory = CsvFileReader.readCsvFile(FileNameItem);
	}
	
	//writes checked-out items data to a file using comma separated values
	public void savecheckOutHistory(){
		CsvFileWriter1.writeCsvFile(FileNameCheckedOutItem, checkOutHistory);
	}
	
	//reads checked-out items data from a file
	public void loadcheckOutHistory(){
		//if file does not exist
		if(!(new File(FileNameCheckedOutItem).exists())){
			try {
				(new PrintWriter(FileNameCheckedOutItem)).close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		this.checkOutHistory = CsvFileReader1.readCsvFile(FileNameCheckedOutItem);
	}
}
	