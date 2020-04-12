package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * Parser for social network visualizer
 *
 * @author Zhaoyi
 */
public class Parser {
	private SocialNetwork sn;
	private NetworkPane network;

	/**
	 * Create the parser
	 * 
	 * @param sn      - social network
	 * @param network - network pane
	 */
	public Parser(SocialNetwork sn, NetworkPane network) {
		super();
		this.sn = sn;
		this.network = network;
	}

	/**
	 * Parse the given file and change the social network and redraw it
	 * 
	 * @param file - file to load
	 * @return true if no exception occurred
	 */
	public boolean load(File file) {
		boolean flag = true; // false if error occurs
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line = null;
			String[] commands = null;
			int argc = 0;
			while ((line = br.readLine()) != null) {
				if (line.isEmpty()) // ignore empty line
					continue;
				commands = line.trim().split("\\s+");
				argc = commands.length;
				if (argc > 3 || argc < 2) {
					flag = false;
					continue;
				}
				switch (commands[0]) {
					case "a": // add
						if (argc == 2) {
							if (!sn.addVertex(commands[1]))
								flag = false;
						} else {
							if (!sn.addEdge(commands[1], commands[2]))
								flag = false;
						}
						break;
					case "r": // remove
						if (argc == 2) {
							if (!sn.removeVertex(commands[1]))
								flag = false;
						} else {
							if (!sn.removeEdge(commands[1], commands[2]))
								flag = false;
						}
						break;
					case "s": // search
						String name = commands[1];
						if (argc == 2 && sn.getAllVertices().contains(name)) {
							network.paint(name, sn.getAdjacent(name));
						} else {
							flag = false;
						}
						break;
					default: // error
						flag = false;
						break;
				}
			}
		} catch (Exception e) {
			flag = false;
		}
		return flag;
	}
}
