package assignment7;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class ClientMain extends Application {
	
	private GridPane clientGrid;
	private Stage clientStage;
	private Scene clientScene;
	private TextArea incoming;
	private TextField outgoing;
	private TextField sending;
	private BufferedReader reader; 
	private PrintWriter writer;
	private String name;

	public void run() throws Exception {
		setLogin(); //sets the name
		setUpNetworking();

	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		run();
	}

	public static void main(String[] args) {
		launch(args);
	}
	
	/**
	 * setLogin() creates a window to allow user to set name/ID
	 * @throws Exception
	 */
	public void setLogin() throws Exception{
		GridPane loginGrid = new GridPane();
		Stage loginStage = new Stage();
		Scene loginScene = new Scene(loginGrid, 400, 200);
		loginStage.setTitle("Login Window");
		loginGrid.setPadding(new Insets(10, 10, 10, 10));
		loginGrid.setVgap(20);
		loginGrid.setHgap(10);
		
		Button loginBtn = new Button();
		loginBtn.setText("LOGIN");
		GridPane.setConstraints(loginBtn, 1, 2);
		
		TextField loginName = new TextField();
		loginName.setPrefWidth(400);
		loginName.setPromptText("Enter name (alphanumeric only)");
		GridPane.setConstraints(loginName, 1, 1);
		
		loginGrid.getChildren().addAll(loginBtn, loginName);
		loginStage.setScene(loginScene);
		loginStage.show();
		
		loginBtn.setOnAction(e -> {
			if (!loginName.getText().matches("[a-zA-Z0-9]+") || (loginName.getText().length() == 0)){
				loginName.setText("");
			}
			else{
				this.name = loginName.getText();
				loginStage.close();
				writer.println("?" + name + "-" + name + " joined the chat.");
				writer.flush();
				initView();
			}
		});
		
		
	}
	
	/**
	 * initView() creates the interface for the user to interface with the server
	 */
	public void initView() {
		this.clientGrid = new GridPane();
		this.clientStage = new Stage();
		this.clientScene = new Scene(clientGrid, 1000, 500);
		HBox messageInput = new HBox();
		
		clientStage.setTitle("Chat window for: " + this.name);
		clientGrid.setPadding(new Insets(10, 10, 10, 10));
		clientGrid.setVgap(20);
		clientGrid.setHgap(10);
		
		Button leaveBtn = new Button();
		leaveBtn.setText("LEAVE");
		GridPane.setConstraints(leaveBtn, 0, 5);
		
		Button sendBtn = new Button();
		sendBtn.setText("SEND");
		GridPane.setConstraints(sendBtn, 2, 2);
		
		incoming = new TextArea();
		incoming.setPrefSize(750, 400);
		incoming.setEditable(false);
		ScrollPane scroller = new ScrollPane(incoming);
		scroller.setPrefSize(750, 400);
		GridPane.setConstraints(incoming, 0, 0);
		
		outgoing = new TextField();
		outgoing.setPrefWidth(400);
		outgoing.setPromptText("Enter message");
		GridPane.setConstraints(outgoing, 0, 2);
		
		sending = new TextField();
		sending.setPrefWidth(300);
		sending.setPromptText("Enter receivers");
		GridPane.setConstraints(sending, 1, 2);
		
		messageInput.setSpacing(15);
		GridPane.setConstraints(messageInput, 0, 2);
		messageInput.getChildren().addAll(outgoing, sending, sendBtn);
		
		
		clientGrid.getChildren().addAll(leaveBtn, incoming, messageInput);
		clientStage.setScene(clientScene);
		clientStage.show();
		
		leaveBtn.setOnAction(e -> {
			writer.println("*" + name + "-" + name + " left the chat.");
			writer.flush();
			clientStage.close();
		});
		
		sendBtn.setOnAction(e -> { //error-check for non-client names
			if (sending.getText().length() == 0){
				writer.println(name + "-" + outgoing.getText());
			}
			else{
				writer.println(name + ", " + sending.getText() + "-" + outgoing.getText());
			}
			writer.flush();
			outgoing.setText("");
			sending.setText("");
			outgoing.requestFocus();
			sending.requestFocus();
		});
	}
	
	/**
	 * setUpNetworking() establishes connections from the client side
	 * @throws Exception
	 */
	private void setUpNetworking() throws Exception {
		@SuppressWarnings("resource")
		Socket sock = new Socket("127.0.0.1", 4242);
		InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
		reader = new BufferedReader(streamReader);
		writer = new PrintWriter(sock.getOutputStream());
		System.out.println("networking established");
		Thread readerThread = new Thread(new IncomingReader());
		readerThread.start();
	}

	class IncomingReader implements Runnable {
		
		/**
		 * run() interfaces messages from server to client
		 */
		public void run() { //interface availableClients and incoming
			String message, filtered;
			try {
				while ((message = reader.readLine()) != null) {
					ClientProtocol cp = new ClientProtocol(message, name);
					filtered = cp.formatIncoming();
					if (filtered != null){
						incoming.appendText(filtered);
					}
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
}
