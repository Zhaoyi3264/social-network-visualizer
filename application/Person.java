package application;

import java.util.HashSet;
import java.util.Set;

/**
 * Vertex in the social network
 *
 * @author Zhaoyi
 */
public class Person {
	String name;
	Set<String> friends;

	/**
	 * Construct a person
	 * 
	 * @param name - name
	 */
	Person(String name) {
		super();
		this.name = name;
		friends = new HashSet<String>();
	}
}