package application;

import java.util.Collection;

/**
 * Graph
 * 
 * @author Zhaoyi
 */
public interface Graph {
	/**
	 * Add vertex
	 * 
	 * @param str - vertex name
	 * @return true if the vertex was not in graph
	 */
	public boolean addVertex(String str);

	/**
	 * Remove vertex
	 * 
	 * @param str - vertex name
	 * @return true if the vertex was in graph
	 */
	public boolean removeVertex(String str);

	/**
	 * Add edge
	 * 
	 * @param str1 - vertex name
	 * @param str2 - vertex name
	 * @return true if the edge was not in graph
	 */
	public boolean addEdge(String str1, String str2);

	/**
	 * Remove edge
	 * 
	 * @param str1 - vertex name
	 * @param str2 - vertex name
	 * @return true if the edge was in graph
	 */
	public boolean removeEdge(String str1, String str2);

	/**
	 * Clear the graph
	 */
	public void clear();

	/**
	 * Return all vertices
	 * 
	 * @return a collection of all vertices names
	 */
	public Collection<String> getAllVertices();

	/**
	 * Return adjacent vertices of a given vertex
	 * 
	 * @param str - vertex name
	 * @return a collection of adjacent vertices of a given vertex
	 */
	public Collection<String> getAdjacent(String str);

	/**
	 * Return number of edges
	 * 
	 * @return number of edges
	 */
	public int size();

	/**
	 * Return number of vertices
	 * 
	 * @return number of vertices
	 */
	public int order();
}