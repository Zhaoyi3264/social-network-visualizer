package application;

import static application.CtrlFactory.createButton;
import static application.CtrlFactory.createLabel;
import static java.lang.String.format;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Pane that contains buttons to interact with the social network
 * 
 * @author Zhaoyi
 */
/**
 * @author Patron
 *
 */
public class ControlPane extends GridPane {
	// regex for strings that only contains alphanumeric, apostrophe, underscore
	// private final String INPUT_FORMAT = "[a-zA-Z_0-9'_]+";
	private final String INPUT_FORMAT = "\\S+";

	private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyy-MM-dd_");
	// number of files saved
	private static int fileNum = 0;

	// parent container
	private Stage parent;

	// related controls
	private NetworkPane network;
	private Pane resultPane;
	private Pane statusPane;
	private FileChooser explorer;

	// inputs fields
	private TextField input1;
	private TextField input2;

	// data structures
	private SocialNetwork sn;
	private List<String> log;

	// parser
	private Parser p;

	/**
	 * Construct a control pane
	 * 
	 * @param parent   - parent container
	 * @param network  - network pane
	 * @param status   - status pane
	 * @param result   - result pane
	 * @param explorer - file explorer window
	 * @param sn       - social network
	 */
	public ControlPane(Stage parent, NetworkPane network, Pane status,
			Pane result, FileChooser explorer, SocialNetwork sn) {
		super();
		this.parent = parent;
		this.network = network;
		this.resultPane = result;
		this.statusPane = status;
		this.explorer = explorer;
		this.sn = sn;
		log = new LinkedList<String>();
		p = new Parser(sn, network);
		createControls();
		setHgap(10);
		setVgap(5);
		setAlignment(Pos.CENTER);
	}

	/**
	 * Create and arrange labels, input fields, buttons for the pane
	 */
	private void createControls() {
		Label input = createLabel("Input:");
		input1 = new TextField();
		input2 = new TextField();
		input1.setTooltip(new Tooltip("Input field"));
		input2.setTooltip(new Tooltip("Input field"));

		Label modify = createLabel("Modify:");
		Button add = createButton("Add", "Add a person or relation", this::add);
		Button remove = createButton("Remove", "Remove a person or relation",
				this::remove);
		Button clear = createButton("Clear", "Clear all people", this::clear);

		Label query = createLabel("Query:");
		Button search = createButton("Search",
				"Search and display a person at the center", this::search);
		Button mutual = createButton("Mutual friends",
				"Display mutual friends of two people", this::mutual);
		Button connection = createButton("Connection",
				"Display the fewest people that connect two people",
				this::connection);

		Label io = createLabel("I/O:");
		Button load = createButton("Load",
				"Load a external data file into the network", this::load);
		Button save = createButton("Save",
				"Save the current network as a data file", this::save);

		// row 1
		setConstraints(input, 0, 0);
		setConstraints(input1, 1, 0, 3, 1);
		setConstraints(input2, 5, 0, 3, 1);

		// row 2
		setConstraints(modify, 0, 1);
		setConstraints(add, 1, 1);
		setConstraints(remove, 2, 1);
		setConstraints(clear, 3, 1);

		setConstraints(query, 4, 1);
		setConstraints(search, 5, 1);
		setConstraints(mutual, 6, 1);
		setConstraints(connection, 7, 1);

		setConstraints(io, 8, 1);
		setConstraints(load, 9, 1);
		setConstraints(save, 10, 1);

		// add all controls
		getChildren().addAll(input, input1, input2, modify, add, remove, clear,
				query, search, mutual, connection, io, load, save);
	}

	/**
	 * Handler for add
	 * 
	 * @param e - unused
	 */
	private void add(ActionEvent e) {
		String s1 = input1.getText();
		String s2 = input2.getText();
		boolean i1 = validateInput(s1);
		boolean i2 = validateInput(s2);
		if (i1 && i2) { // add edge
			changeHistory(format("Add relation\n%s-%s", s1, s2),
					sn.addEdge(s1, s2) ? "Success" : "Fail");
			log.add(format("a %s %s", s1, s2)); // log

			// determine if repaint is needed
			if (s1.equals(network.central))
				network.repaint(sn.getAdjacent(s1));
			else if (s2.equals(network.central))
				network.repaint(sn.getAdjacent(s2));
		} else if (!i1 && !i2) { // invalid input
			error("Invalid inputs");
		} else { // add vertex
			String name = i1 ? s1 : s2;
			changeHistory(format("Add person\n%s", name),
					sn.addVertex(name) ? "Success" : "Fail");
			log.add(format("a %s", name)); // log
		}
		changeStatus();
		clearInput();
	}

	/**
	 * Handler for remove
	 * 
	 * @param e - unused
	 */
	private void remove(ActionEvent e) {
		String s1 = input1.getText();
		String s2 = input2.getText();
		boolean i1 = validateInput(s1);
		boolean i2 = validateInput(s2);
		if (i1 && i2) { // remove edge
			changeHistory(format("Remove relation\n%s-%s", s1, s2),
					sn.removeEdge(s1, s2) ? "Success" : "Fail");

			// determine if repaint is needed
			if (s1.equals(network.central))
				network.repaint(sn.getAdjacent(s1));
			else if (s2.equals(network.central))
				network.repaint(sn.getAdjacent(s2));
			log.add(format("r %s %s", s1, s2)); // log
		} else if (!i1 && !i2) { // invalid input
			error("Invalid inputs");
		} else { // remove vertex
			String name = i1 ? s1 : s2;
			boolean repaint = sn.getAdjacent(network.central).contains(name);
			changeHistory(format("Remove person\n%s", name),
					sn.removeVertex(name) ? "Success" : "Fail");

			// repaint the network if necessary
			if (repaint)
				network.repaint(sn.getAdjacent(network.central));

			// clear if the central is removed
			if (name.equals(network.central)) {
				network.clear();
				network.central = "";
			}
			log.add(format("r %s", name)); // log
		}
		changeStatus();
		clearInput();
	}

	/**
	 * Handler for clear
	 * 
	 * @param e - unused
	 */
	private void clear(ActionEvent e) {
		network.clear();
		sn.clear();
		changeHistory("Clear network", "Success");
		changeStatus();
		clearInput();
	}

	/**
	 * Handler for search
	 * 
	 * @param e - unused
	 */
	private void search(ActionEvent e) {
		String s1 = input1.getText();
		String s2 = input2.getText();
		boolean i1 = validateInput(s1);
		boolean i2 = validateInput(s2);
		if (i1 && i2) { // invalid
			error("Please enter only one name");
		} else if (!i1 && !i2) { // invalid input
			error("Invalid inputs");
		} else { // search
			String name = i1 ? s1 : s2;
			if (sn.getAllVertices().contains(name)) {
				network.paint(name, sn.getAdjacent(name));
				changeHistory(format("Search %s", name), "Found");
			} else {
				changeHistory(format("Search %s", name), "Not found");
			}
			log.add(format("s %s", name)); // log
		}
		clearInput();
	}

	/**
	 * Handler for find mutual friends
	 * 
	 * @param e - unused
	 */
	private void mutual(ActionEvent e) {
		String s1 = input1.getText();
		String s2 = input2.getText();
		if (validateInput(s1) && validateInput(s2)) { // valid
			Set<String> mutual = sn.mutual(s1, s2);
			StringBuilder sb = new StringBuilder();
			if (mutual.size() == 0)
				sb.append("No mutual friends");
			else
				for (String name : mutual)
					sb.append(name + "\n");
			changeHistory(format("Mutual friends\nof %s and %s", s1, s2),
					sb.toString());
		} else { // error
			error("Please enter two names");
		}
		clearInput();
	}

	/**
	 * Handler for find connection
	 * 
	 * @param e - unused
	 */
	private void connection(ActionEvent e) {
		String s1 = input1.getText();
		String s2 = input2.getText();
		if (validateInput(s1) && validateInput(s2)) { // valid
			List<String> connection = sn.connection(s1, s2);
			StringBuilder sb = new StringBuilder();
			if (connection.size() == 2)
				sb.append("They are already friends");
			else if (connection.size() == 0)
				sb.append("No such connection");
			else
				for (String name : connection)
					sb.append(name + "\n");
			changeHistory(
					format("Fewest people\nthat connects\n%s and %s", s1, s2),
					sb.toString());
		} else { // error
			error("Please enter two names");
		}
		clearInput();
	}

	/**
	 * Handler for load
	 * 
	 * @param e - unused
	 */
	private void load(ActionEvent e) {
		explorer.setTitle("Load");
		File load = explorer.showOpenDialog(parent);
		if (load == null)
			error("No file is chosen");
		else {
			changeHistory("Load\n" + load.getName(),
					p.load(load) ? "Success" : "Error occured");
			String line = null;
			try (BufferedReader br = new BufferedReader(new FileReader(load))) {
				while ((line = br.readLine()) != null)
					log.add(line);
			} catch (Exception ex) {
				error("Cannot read the file " + load.getName());
			}
		}
		changeStatus();
	}

	/**
	 * Handler for save
	 * 
	 * @param e - unused
	 */
	private void save(ActionEvent e) {
		explorer.setTitle("Save");
		explorer.setInitialFileName(DATE_FORMAT.format(new Date()) + fileNum++);
		File save = explorer.showSaveDialog(parent);
		if (save == null)
			error("No file is chosen");
		else
			changeHistory("Save\n" + save.getName(),
					logTo(save) ? "Success" : "Error occured");
	}

	/**
	 * Validate the input
	 * 
	 * @param str - input
	 * @return true if the input is valid
	 */
	private boolean validateInput(String str) {
		return Pattern.matches(INPUT_FORMAT, str);
	}

	/**
	 * Clear two input fields
	 */
	private void clearInput() {
		input1.clear();
		input2.clear();
	}

	/**
	 * Show an error message
	 * 
	 * @param msg - message
	 */
	private void error(String msg) {
		new Alert(AlertType.ERROR, msg).show();
	}

	/**
	 * Show the query and its result
	 * 
	 * @param query  - query
	 * @param result - result
	 */
	private void changeHistory(String query, String result) {
		ObservableList<Node> children = resultPane.getChildren();
		((Label) children.get(1)).setText(query);
		((Label) children.get(2)).setText(result);
	}

	/**
	 * Show the status of the social network
	 */
	private void changeStatus() {
		ObservableList<Node> status = statusPane.getChildren();
		((Label) status.get(1)).setText("Number of\npeople: " + sn.order());
		((Label) status.get(2)).setText("Number of\nrelations: " + sn.size());
		((Label) status.get(3))
				.setText("Connected\nComponents: " + sn.components());
	}

	/**
	 * Write logs
	 * 
	 * @return true if no exception occurred
	 */
	public boolean log() {
		Path out = Paths.get("log.txt");
		try {
			Files.write(out, log, Charset.defaultCharset());
		} catch (Exception e) {
			return false;
		}
		log.clear();
		return true;
	}

	/**
	 * 
	 * Save logs to the specified file
	 * 
	 * @param file - file to save to
	 * @return true if no exception occurred
	 */
	public boolean logTo(File file) {
		Path out = Paths.get(file.getName());
		try {
			Files.write(out, log, Charset.defaultCharset());
		} catch (Exception e) {
			return false;
		}
		return true;
	}
}
