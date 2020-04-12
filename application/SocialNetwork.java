package application;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * Social network using a graph
 *
 * @author Zhaoyi
 */
public class SocialNetwork implements Graph {
	// people associated with their names
	private Map<String, Person> network;

	// number of edges
	private int size;

	/**
	 * Construct a social network
	 */
	public SocialNetwork() {
		super();
		network = new HashMap<String, Person>();
		size = 0;
	}

	/**
	 * Add vertex
	 * 
	 * @param str - vertex name
	 * @return true if the vertex was not in graph
	 */
	@Override
	public boolean addVertex(String str) {
		Person p = getVertex(str);
		if (p != null)
			return false;
		network.put(str, new Person(str));
		return true;
	}

	/**
	 * Remove vertex
	 * 
	 * @param str - vertex name
	 * @return true if the vertex was in graph
	 */
	@Override
	public boolean removeVertex(String str) {
		if (str == null)
			return false;
		Person p = network.remove(str);
		if (p == null)
			return false;

		// remove related edges
		size -= p.friends.size();
		Person person = null;
		for (String name : network.keySet()) {
			person = network.get(name);
			person.friends.remove(str);
		}
		return true;
	}

	/**
	 * Add edge
	 * 
	 * @param str1 - vertex name
	 * @param str2 - vertex name
	 * @return true if the edge was not in graph
	 */
	@Override
	public boolean addEdge(String str1, String str2) {
		if (str1 != null && str1.equals(str2))
			return false;
		Person p1 = getVertex(str1);
		Person p2 = getVertex(str2);

		// add vertices if them do not exist
		if (p1 == null)
			network.put(str1, p1 = new Person(str1));
		if (p2 == null)
			network.put(str2, p2 = new Person(str2));

		// add edge
		if (p1.friends.add(str2) && p2.friends.add(str1)) {
			size++;
			return true;
		}
		return false;
	}

	/**
	 * Remove edge
	 * 
	 * @param str1 - vertex name
	 * @param str2 - vertex name
	 * @return true if the edge was in graph
	 */
	@Override
	public boolean removeEdge(String str1, String str2) {
		Person p1 = getVertex(str1);
		Person p2 = getVertex(str2);

		// if any vertices do not exist
		if (p1 == null || p2 == null)
			return false;

		if (p1.friends.remove(str2) && p2.friends.remove(str1)) {
			size--;
			return true;
		}
		return false;
	}

	/**
	 * Clear the graph
	 */
	@Override
	public void clear() {
		network.clear();
		size = 0;
	}

	/**
	 * Return all vertices
	 * 
	 * @return a collection of all vertices names
	 */
	@Override
	public Set<String> getAllVertices() {
		return network.keySet();
	}

	/**
	 * Return adjacent vertices of a given vertex
	 * 
	 * @param str - vertex name
	 * @return a collection of adjacent vertices of a given vertex
	 */
	@Override
	public Set<String> getAdjacent(String str) {
		Person p = network.get(str);
		return p == null ? Collections.emptySet() : p.friends;
	}

	/**
	 * Return number of edges
	 * 
	 * @return number of edges
	 */
	@Override
	public int size() {
		return size;
	}

	/**
	 * Return number of vertices
	 * 
	 * @return number of vertices
	 */
	@Override
	public int order() {
		return network.size();
	}

	/**
	 * Find the number of connected components
	 * 
	 * @return the number of connected components
	 */
	public int components() {
		HashSet<String> visited = new HashSet<String>();
		int components = 0;
		for (String name : network.keySet())
			if (!visited.contains(name)) { // if unvisited, mark all
				markAllVisited(name, visited);
				components++;
			}
		return components;
	}

	/**
	 * Mark all vertices connected to the given vertex as visited
	 * 
	 * @param name    - vertex
	 * @param visited - record of visited vertices
	 */
	private void markAllVisited(String name, Set<String> visited) {
		visited.add(name);
		for (String neighbour : getAdjacent(name))
			if (!visited.contains(neighbour)) // if unvisited, mark all
				markAllVisited(neighbour, visited);
	}

	/**
	 * Find the mutual adjacent vertices of two given vertices
	 * 
	 * @param str1 - vertex name
	 * @param str2 - vertex name
	 * @return a collection of mutual adjacent vertices
	 */
	public Set<String> mutual(String str1, String str2) {
		Set<String> s1 = getAdjacent(str1);
		Set<String> s2 = getAdjacent(str2);
		s1.retainAll(s2); // intersection
		return s1;
	}

	/**
	 * Find the shortest path between two given vertices
	 * 
	 * @param str1 - vertex name
	 * @param str2 - vertex name
	 * @return a shortest path between two vertices
	 */
	public List<String> connection(String str1, String str2) {
		LinkedList<String> connection = new LinkedList<String>();
		HashSet<String> visited = new HashSet<String>();
		HashMap<String, Integer> dis = new HashMap<String, Integer>();
		HashMap<String, String> pred = new HashMap<String, String>();
		Queue<String> q = new LinkedList<String>();

		q.offer(str1);
		dis.put(str1, 0);
		pred.put(str1, null);
		String vtx;

		while (!q.isEmpty()) {
			vtx = q.poll();
			visited.add(vtx); // mark visited
			for (String succ : getAdjacent(vtx)) {
				if (visited.add(succ)) // mark visited
					q.offer(succ);
				int distance = dis.get(vtx) + 1;
				// shorter distance
				if (distance < dis.getOrDefault(succ, Integer.MAX_VALUE)) {
					dis.put(succ, distance);
					pred.put(succ, vtx);
				}
			}
		}

		if (visited.contains(str2)) // in the same connected component
			for (vtx = str2; vtx != null; vtx = pred.get(vtx))
				connection.add(0, vtx);
		return connection;
	}

	/**
	 * Find a vertex
	 * 
	 * @param str - vertex name
	 * @return vertex or null if not found
	 */
	private Person getVertex(String str) {
		if (str == null)
			return null;
		return network.get(str);
	}
}
