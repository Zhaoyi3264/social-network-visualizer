
package application;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SocialNetworkTest {
	private SocialNetwork sn;
	private String[] people = { "A", "B", "C", "D", "E", "F", "G", "H" };

	private void addAll() {
		for (String str : people)
			sn.addVertex(str);
	}

	@BeforeEach
	public void setup() {
		sn = new SocialNetwork();
	}

	@AfterEach
	public void clean() {
		sn = null;
	}

	@Test
	public void test01_add_people_size() {
		assertEquals(0, sn.order());
		assertEquals(0, sn.size());
		addAll();
		assertEquals(people.length, sn.order());
		assertEquals(0, sn.size());
	}

	@Test
	public void test02_add_duplicate_people_size() {
		assertEquals(0, sn.order());
		assertEquals(0, sn.size());
		addAll();
		addAll();
		assertEquals(people.length, sn.order());
		assertEquals(0, sn.size());
	}

	@Test
	public void test03_remove_people_size() {
		addAll();
		assertEquals(people.length, sn.order());
		for (int i = 0; i < people.length; i += 2)
			sn.removeVertex(people[i]);
		assertEquals(people.length / 2, sn.order());
	}

	@Test
	public void test04_remove_people_twice_size() {
		addAll();
		assertEquals(people.length, sn.order());
		for (int i = 0; i < people.length; i += 2)
			sn.removeVertex(people[i]);
		for (int i = 0; i < people.length; i += 2)
			sn.removeVertex(people[i]);
		sn.removeVertex("does_not_exist");
		sn.removeVertex("what is this");
		assertEquals(people.length / 2, sn.order());
	}

	@Test
	public void test05_add_complete_relation_size() {
		addAll();
		for (int i = 0; i < people.length; i++)
			for (int j = i + 1; j < people.length; j++)
				sn.addEdge(people[i], people[j]);
		assertEquals(people.length * (people.length - 1) / 2, sn.size());
	}

	@Test
	public void test06_add_remove_complete_relation_size() {
		addAll();
		for (int i = 0; i < people.length; i++)
			for (int j = i + 1; j < people.length; j++)
				sn.addEdge(people[i], people[j]);
		sn.removeVertex(people[0]);
		assertEquals((people.length - 1) * (people.length - 2) / 2, sn.size());
		sn.removeVertex(people[1]);
		assertEquals((people.length - 2) * (people.length - 3) / 2, sn.size());
		assertEquals(people.length - 2, sn.order());
	}

	@Test
	public void test07_add_remove_relation_size() {
		addAll();
		for (int i = 0; i < people.length; i++)
			sn.addEdge(people[0], people[i]);
		assertEquals(people.length - 1, sn.size());
		sn.removeEdge(people[0], people[people.length - 1]);
		assertEquals(people.length - 2, sn.size());
		sn.removeVertex(people[0]);
		assertEquals(0, sn.size());
	}

	@Test
	public void test08_remove_not_present_relation() {
		addAll();
		for (int i = 0; i < 3; i++) {
			sn.addEdge("A", "B");
			sn.addEdge("A", "F");
			sn.addEdge("B", "E");
			sn.addEdge("E", "F");
			sn.addEdge("A", "H");
			sn.addEdge("B", "C");
			sn.addEdge("H", "C");
		}
		assertEquals(7, sn.size());
		sn.removeEdge("H", "F");
		sn.removeEdge("D", "G");
		assertEquals(7, sn.size());
	}

	@Test
	public void test09_adjacent_set() {
		addAll();
		sn.addEdge("A", "B");
		sn.addEdge("A", "F");
		sn.addEdge("B", "E");
		sn.addEdge("E", "F");
		sn.addEdge("A", "H");
		sn.addEdge("B", "C");
		sn.addEdge("H", "C");
		assertEquals(
				new HashSet<String>(Set.of(new String[] { "A", "E", "C" })),
				sn.getAdjacent("B"));
		assertEquals(new HashSet<String>(Set.of(new String[] { "H", "B" })),
				sn.getAdjacent("C"));
		assertEquals(new HashSet<String>(Set.of(new String[] {})),
				sn.getAdjacent("D"));
	}

	@Test
	public void test10_all_vertices() {
		addAll();
		sn.addEdge("A", "B");
		sn.addEdge("A", "F");
		sn.addEdge("B", "E");
		sn.addEdge("E", "F");
		sn.addEdge("A", "H");
		sn.addEdge("B", "C");
		sn.addEdge("H", "C");
		sn.removeVertex("F");
		sn.removeVertex("D");
		assertEquals(
				new HashSet<String>(
						Set.of(new String[] { "A", "B", "C", "E", "G", "H" })),
				sn.getAllVertices());
	}

	@Test
	public void test11_mis() {
		addAll();
		sn.addEdge("A", "B");
		sn.addEdge("A", "F");
		sn.addEdge("B", "E");
		sn.addEdge("E", "F");
		sn.addEdge("A", "H");
		sn.addEdge("B", "C");
		sn.addEdge("H", "C");
		sn.removeVertex("F");
		sn.removeVertex("D");
		assertEquals(people.length - 2, sn.order());
		assertEquals(5, sn.size());
	}

	@Test
	public void test12_components() {
		addAll();
		assertEquals(people.length, sn.components());
		sn.addEdge(people[0], people[1]);
		sn.addEdge(people[1], people[2]);
		sn.addEdge(people[4], people[5]);
		assertEquals(people.length - 3, sn.components());
		sn.addEdge(people[1], people[4]);
		assertEquals(people.length - 4, sn.components());
		sn.removeVertex(people[1]);
		assertEquals(people.length - 2, sn.components());
		sn.removeVertex(people[4]);
		assertEquals(people.length - 2, sn.components());
	}

	@Test
	public void test13_mutual() {
		addAll();
		sn.addEdge(people[0], people[1]);
		sn.addEdge(people[0], people[2]);
		sn.addEdge(people[0], people[3]);
		sn.addEdge(people[0], people[6]);
		sn.addEdge(people[0], people[7]);

		sn.addEdge(people[4], people[0]);
		sn.addEdge(people[4], people[1]);
		sn.addEdge(people[4], people[3]);
		sn.addEdge(people[4], people[5]);
		sn.addEdge(people[4], people[7]);
		assertEquals(
				new HashSet<String>(Set
						.of(new String[] { people[1], people[3], people[7] })),
				sn.mutual(people[0], people[4]));
	}

	@Test
	public void test14_connection() {
		addAll();
		sn.addEdge(people[0], people[1]);
		sn.addEdge(people[0], people[4]);
		sn.addEdge(people[0], people[6]);

		sn.addEdge(people[1], people[2]);
		sn.addEdge(people[2], people[3]);
		sn.addEdge(people[2], people[5]);
		sn.addEdge(people[3], people[4]);

		sn.addEdge(people[6], people[5]);
		sn.addEdge(people[6], people[7]);
		assertEquals(Arrays.asList(new String[] { "A", "E" }),
				sn.connection(people[0], people[4]));
		assertEquals(Arrays.asList(new String[] { "A", "G", "F" }),
				sn.connection(people[0], people[5]));
		assertEquals(Arrays.asList(new String[] { "C", "F", "G" }),
				sn.connection(people[2], people[6]));
		assertEquals(Arrays.asList(new String[] { "G", "F", "C" }),
				sn.connection(people[6], people[2]));
	}

	@Test
	public void test15_complete_connection() {
		addAll();
		for (int i = 0; i < people.length; i++)
			for (int j = i + 1; j < people.length; j++)
				sn.addEdge(people[i], people[j]);
		for (int i = 0; i < people.length; i++)
			for (int j = 0; j < people.length; j++)
				if (i != j)
					assertEquals(2, sn.connection(people[i], people[j]).size());
	}

	@Test
	public void test16_not_same_component_connection() {
		addAll();
		sn.addEdge(people[0], people[1]);
		sn.addEdge(people[1], people[2]);
		sn.addEdge(people[2], people[3]);
		sn.addEdge(people[1], people[3]);
		sn.addEdge(people[4], people[5]);
		sn.addEdge(people[5], people[6]);
		sn.addEdge(people[6], people[7]);
		assertEquals(Arrays.asList(new String[] { "D", "B", "A" }),
				sn.connection(people[3], people[0]));
		assertEquals(0, sn.connection(people[0], people[7]).size());
		assertEquals(0, sn.connection(people[1], people[4]).size());
		assertEquals(0, sn.connection(people[2], people[6]).size());
		assertEquals(0, sn.connection(people[3], people[5]).size());
	}
}
