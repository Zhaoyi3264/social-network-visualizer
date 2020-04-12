package application;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

/**
 * Utility class that contains methods for creating control components
 *
 * @author Zhaoyi
 */
public class CtrlFactory {
	public static final Font TITLE_FONT = new Font("arial", 15);
	public static final Font TEXT_FONT = new Font("arial", 13);

	/**
	 * Create label with title font
	 * 
	 * @param text - text
	 * @return label
	 */
	public static Label createTitle(String text) {
		Label label = new Label(text);
		label.setFont(TITLE_FONT);
		return label;
	}

	/**
	 * Create label with text font
	 * 
	 * @param text - text
	 * @return label
	 */
	public static Label createLabel(String text) {
		Label label = new Label(text);
		label.setFont(TEXT_FONT);
		return label;
	}

	/**
	 * Create button
	 * 
	 * @param text    - text
	 * @param tooltip - tool tip
	 * @param eh      - event handler
	 * @return button
	 */
	public static Button createButton(String text, String tooltip,
			EventHandler<ActionEvent> eh) {
		Button b = new Button(text);
		b.setFont(TEXT_FONT);
		b.setOnAction(eh);
		b.setMinSize(75, 25);
		b.setTooltip(new Tooltip(tooltip));
		b.setStyle("-fx-background-color: #e2d1c3;\n"
				+ "-fx-effect: dropshadow(gaussian, black, 8, 0.0 , 0, 1);");
		b.setStyle("");
		return b;
	}

	/**
	 * Create text
	 * 
	 * @param x   - x coordinate
	 * @param y   - y coordinate
	 * @param txt - text
	 * @return text
	 */
	public static Text createText(double x, double y, String txt) {
		// pad and center align the text
		Text text = new Text(x - 35, y, String.format("%-10s", txt));
		text.setTextAlignment(TextAlignment.JUSTIFY);
		text.setTextOrigin(VPos.CENTER);
		text.setFont(TITLE_FONT);
		return text;
	}
}
