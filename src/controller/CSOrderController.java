package controller;

import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import application.CSTable;
import application.Main;
import database.DBManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Menu;
import model.Order;
import util.ImageFactory;
import util.ScreenController;

/**
 * OrderController(customer) contains method for handling all event
 * receive from the UserInterface. Contains method for viewing and ordering
 * customer orders. (With help form TA for spacing nodes)
 * 
 * @author Piyawat & Vichaphol
 *
 */
public class CSOrderController implements Observer {
	@FXML
	private Button order;
	@FXML
	private Button clear;
	@FXML
	private Button remove;
	@FXML
	private Button back;
	@FXML
	private Button exit;
	@FXML
	private Button checkBill;
	@FXML
	private TextField total;
	@FXML
	private FlowPane foodpane;
	@FXML
	private FlowPane drinkpane;
	@FXML
	private TextArea display;
	@FXML
	private TextArea display2;

	@FXML
	private VBox root;

	private static String tablenumber;
	private Alert alert;

	private static Order o = Order.getInstance();
	private static DBManager dbm = DBManager.getInstance();
	private static ImageFactory instance = ImageFactory.getInstance();

	private int tmpTotal;
	
	@FXML
	public void initialize() {
		instance.getFoodButton().forEach(x -> foodpane.getChildren().add(x));
		instance.getDrinkButton().forEach(x -> drinkpane.getChildren().add(x));
		o.addObserver(this);
		setDisplayProp();
		display.setPrefHeight(400);
		display2.setPrefHeight(400);
		display.setStyle("-fx-font-family: monospace");
		display2.setStyle("-fx-font-family: monospace");

	}

	/**
	 * Handler for order button. When event receive orders are sent out to the
	 * database.
	 * 
	 * @param event
	 */
	public void orderButtonHandler(MouseEvent event) {
		if (o.getOrders().isEmpty()) {
			alert = new Alert(AlertType.ERROR, "Must order atleast one item!", ButtonType.OK);
			alert.show();
		}
		else {
			alert = new Alert(AlertType.CONFIRMATION, "Are you sure to order?", ButtonType.YES, ButtonType.NO);
			alert.showAndWait().ifPresent(response -> {
				if (response == ButtonType.YES) {
					dbm.orderToDB(tablenumber, o.getOrders());
					setDisplay2();
					o.clearOrders();
				}
			});
		}
	}

	/**
	 * Handler for clear button. When event receive current orders from
	 * Map<Menu,QTY> is removed.
	 * 
	 * @param event
	 */
	public void clearButtonHandler(MouseEvent event) {
		o.clearOrders();
	}

	/**
	 * Handler for back button. When event receive the CS table scene is shown.
	 * 
	 */
	public void backButtonHandler(ActionEvent event) {
		ScreenController.switchWindow((Stage) back.getScene().getWindow(), new CSTable());
	}

	/**
	 * Handler for logout button. When event receive the Start up scene is shown.
	 * 
	 */
	public void exitButtonHandler(ActionEvent event) {
		ScreenController.switchWindow((Stage) exit.getScene().getWindow(), new Main());
	}

	/**
	 * Static method for scene before opening this scene to get the button text and
	 * set as table number.
	 * 
	 * @param buttonText
	 */
	public static void setTable(String buttonText) {
		tablenumber = buttonText;
	}

	// Set the current total
	private void setTotal() {
		String temp = "" + (o.getTotal() + tmpTotal);
		total.setText(temp);

	}

	/*
	 * Set the temporary total attribute which is use to display the current total
	 */
	private void setTempTotal(Map<Menu, Integer> map) {
		tmpTotal = o.getTotal(map);
	}

	// set the display properties
	private void setDisplayProp() {
		display.setDisable(true);
		display.setText(tablenumber);
		display2.setDisable(true);
		setDisplay2();
		setTotal();
	}

	// Set the top display in the UI
	private void setDisplay() {
		String text = o.orderToText(o.getOrders());
		display.setText(text);
		setTotal();
	}

	// Set the lower display in the UI
	private void setDisplay2() {
		Map<Menu, Integer> temp = dbm.getDBOrders(tablenumber);
		String text = o.orderToText(temp);
		display2.setText(text);
		setTempTotal(temp);
		o.clearOrders();
	}

	/**
	 * Overridden method from java.util.Observer to set the display everytime a menu
	 * button is pressed.
	 */
	@Override
	public void update(Observable observable, Object arg) {
		setDisplay();
	}
}
