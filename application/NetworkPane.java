package application;

import static application.CtrlFactory.createText;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

/**
 * Pane for visualizing social network
 *
 * @author Zhaoyi
 */
public class NetworkPane extends Pane {
	private final int MARGIN = 20; // margin of the canvas
	private final int OFFSET = 40; // horizontal offset of text

	// central user
	String central = "";

	// dimension of the canvas
	private double w;
	private double h;

	// circle parameters
	private Point2D center;
	private double radius;

	private Canvas cvs;
	private GraphicsContext gc;

	// data
	private SocialNetwork sn;
	private HashMap<String, Point2D> coordinates; // coordinate of vertices

	/**
	 * Construct a network pane
	 * 
	 * @param sn - social network
	 */
	public NetworkPane(SocialNetwork sn) {
		super();
		this.sn = sn;
		coordinates = new HashMap<String, Point2D>();
		cvs = new Canvas();
		gc = cvs.getGraphicsContext2D();
		gc.setLineWidth(2);
		gc.setStroke(Color.ROSYBROWN);

		setOnMouseClicked(this::onClick);
	}

	/**
	 * Set the size of the canvas
	 * 
	 * @param w - width
	 * @param h - height
	 */
	public void setSize(double w, double h) {
		this.w = w;
		this.h = h;
		center = new Point2D(w / 2, h / 2);
		radius = Math.min(w, h) / 2 - MARGIN;
		cvs.setWidth(w);
		cvs.setHeight(h);
	}

	/**
	 * Clear all vertices and visualization
	 */
	public void clear() {
		coordinates.clear();
		ObservableList<Node> children = getChildren();
		children.clear();
		children.add(cvs);
		gc.clearRect(0, 0, w, h);
	}

	/**
	 * Paint the visualization
	 * 
	 * @param central - central user
	 * @param friends - his friends
	 */
	public void paint(String central, Collection<String> friends) {
		this.central = central;
		// clear children and canvas
		clear();
		ObservableList<Node> children = getChildren();
		if (friends.size() == 0) {
			// center
			children.add(createText(center.getX(), center.getY(), central));
			drawNode(center.getX(), center.getY());
			return;
		}

		double wordX = 0;
		double wordY = 0;
		double drg = 360.0 / friends.size();
		double rad = 0;
		Iterator<String> itr = friends.iterator();
		String str = null;

		// circle
		for (double degrees = 0; itr.hasNext(); degrees += drg) {
			// calculate position
			rad = Math.toRadians(degrees);
			wordX = center.getX() + radius * Math.cos(rad);
			wordY = center.getY() + radius * Math.sin(rad);

			// display the text and draw a rectangle around it
			children.add(createText(wordX, wordY, str = itr.next()));
			gc.strokeLine(wordX, wordY, center.getX(), center.getY());
			drawNode(wordX, wordY);
			coordinates.put(str, new Point2D(wordX, wordY));
		}

		// center
		children.add(createText(center.getX(), center.getY(), central));
		drawNode(center.getX(), center.getY());
	}

	/**
	 * Repaint the visualization
	 * 
	 * @param friends - new friends of the central user
	 */
	public void repaint(Collection<String> friends) {
		paint(central, friends);
	}

	/**
	 * Handler for mouse click
	 * 
	 * @param e - unused
	 */
	private void onClick(MouseEvent e) {
		if (e.getButton() != MouseButton.PRIMARY)
			return;

		// only response to left click
		double x = e.getX();
		double y = e.getY();

		// only response if the click is on the circle
		if (!isOnCircle(x, y))
			return;

		// check if the click is on any node
		for (String s : coordinates.keySet()) {
			Point2D point = coordinates.get(s);
			if (isOver(point.getX(), point.getY(), x, y)) {
				paint(s, sn.getAdjacent(s));
				break;
			}
		}
	}

	/**
	 * Check if the click is on the visualization
	 * 
	 * @param x - mouse x coordinate
	 * @param y - mouse y coordinate
	 * @return true if the click is on the visualization
	 */
	private boolean isOnCircle(double x, double y) {
		return Math.abs(radius - center.distance(x, y)) < 25;
	}

	/**
	 * Check if the mouse click is over specific vertex
	 * 
	 * @param x0 - vertex x coordinate
	 * @param y0 - vertex y coordinate
	 * @param x  - mouse x coordinate
	 * @param y  - mouse y coordinate
	 * @return true if the click is over this vertex
	 */
	private boolean isOver(double x0, double y0, double x, double y) {
		boolean xRange = x0 - OFFSET <= x && x <= (x0 + 2 * OFFSET);
		boolean yRange = y0 - 15 <= y && y <= (y0 + 30);
		return xRange && yRange;
	}

	/**
	 * Draw the visualization
	 * 
	 * @param x - vertex x coordinate
	 * @param y - vertex y coordinate
	 */
	private void drawNode(double x, double y) {
		gc.clearRect(x - OFFSET, y - 15, 2 * OFFSET, 30);
		gc.strokeRoundRect(x - OFFSET, y - 15, 2 * OFFSET, 30, 25, 25);
	}
}
