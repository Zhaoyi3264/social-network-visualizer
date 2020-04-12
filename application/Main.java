package application;

import java.io.File;
import java.util.Optional;

import javafx.application.Application;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

/**
 * Driver for social network visualizer program
 *
 * @author Zhaoyi
 */
public class Main extends Application {
	// insets
	private final Insets INSETS = new Insets(5, 5, 5, 5);

	// border
	private final Border BORDER = new Border(
			new BorderStroke(Color.BURLYWOOD, BorderStrokeStyle.SOLID,
					CornerRadii.EMPTY, BorderWidths.DEFAULT, INSETS));

	// file explorer window
	private FileChooser explorer;

	/**
	 * Initialization before showing the window
	 */
	@Override
	public void init() throws Exception {
		super.init();
		explorer = new FileChooser();
		explorer.getExtensionFilters()
				.add(new ExtensionFilter("Text Files", "*.txt"));
		File log = new File("log.txt");
		if (log.exists())
			log.delete();
	}

	/**
	 * Setup for the window
	 */
	@Override
	public void start(Stage stage) throws Exception {
		SocialNetwork sn = new SocialNetwork();

		// left and right
		VBox status = createLeftPane();
		VBox result = createRightPane();

		// center
		NetworkPane network = new NetworkPane(sn);
		network.setBorder(BORDER);

		// bottom
		ControlPane control = new ControlPane(stage, network, status, result,
				explorer, sn);

		// setup stage
		BorderPane root = new BorderPane(network, null, result, control,
				status);
		root.setPadding(INSETS);

		// prompt for saving file when exiting
		stage.setOnCloseRequest(e -> {
			Alert alert = new Alert(AlertType.CONFIRMATION,
					"Do you want to save the social network before exit?",
					new ButtonType("Save", ButtonData.YES),
					new ButtonType("Exit without Save", ButtonData.NO));
			Optional<ButtonType> choice = alert.showAndWait();
			if (choice.get().getButtonData() == ButtonData.YES) { // save
				explorer.setTitle("Save");
				explorer.setInitialFileName("save.txt");
				File save = explorer.showSaveDialog(stage);
				if (save == null) // no file is chosen
					new Alert(AlertType.ERROR, "No file is chosen.\nGoodbye!")
							.showAndWait();
				else { // save the file
					String msg = String.format("%s save file %s.\nGoodbye!",
							control.logTo(save) ? "Successfully" : "Fail to",
							save.getName());
					new Alert(AlertType.INFORMATION, msg).showAndWait();
				}
			} else { // not save
				new Alert(AlertType.INFORMATION, "Goodbye!").showAndWait();
			}
			control.log();
		});
		Paint gradient = new LinearGradient(0, 0, 0, 1, true,
				CycleMethod.NO_CYCLE,
				new Stop(0,
						new Color(226 / 255.0, 209 / 255.0, 195 / 255.0, 1)),
				new Stop(1,
						new Color(253 / 255.0, 252 / 255.0, 251 / 255.0, 1)));

		root.setBackground(
				new Background(new BackgroundFill(gradient, null, null)));
		stage.setScene(new Scene(root, 960, 540));
		stage.setTitle("Social Network Visualizer");
		stage.setResizable(false);
		stage.show();

		// determine canvas size and paint the network
		Bounds bounds = root.getCenter().getBoundsInParent();
		network.setSize(bounds.getWidth(), bounds.getHeight());
	}

	/**
	 * Create right pane
	 * 
	 * @return query result pane
	 */
	private VBox createRightPane() {
		Label title = CtrlFactory.createTitle("Query Result");
		Label query = CtrlFactory.createLabel("No queries");
		Label result = CtrlFactory.createLabel("No results");
		VBox queryResult = new VBox(10, title, query, result);
		for (Node node : queryResult.getChildren())
			VBox.setMargin(node, INSETS);
		queryResult.setAlignment(Pos.CENTER);
		return queryResult;
	}

	/**
	 * Create left pane
	 * 
	 * @return network status pane
	 */
	private VBox createLeftPane() {
		Label title = CtrlFactory.createTitle("Network Status");
		Label numV = CtrlFactory.createLabel("Number of\npeople: 0");
		Label numE = CtrlFactory.createLabel("Number of\nrelations: 0");
		Label component = CtrlFactory.createLabel("Connected\nComponents: 0");
		VBox status = new VBox(10, title, numV, numE, component);
		for (Node node : status.getChildren())
			VBox.setMargin(node, INSETS);
		status.setAlignment(Pos.CENTER);
		return status;
	}

	/**
	 * Launch the JavaFX
	 * 
	 * @param args - unused
	 */
	public static void main(String[] args) {
		launch(args);
	}
}
