import java.util.*;

/**
 * Library for graph analysis.
 * 
 * @author Caroline Tornquist. Fall 2019, Updated June 2020
 */

public class GraphLib<V,E> {

	/**
	 * Breadth First Search
	 *
	 * @param g      -- graph to search
	 * @param source -- node to start the search from
	 * @return graph with only those nodes reachable from source. Each node has directed edge to its parent, with the
	 * "center" of the graph as source.
	 */
	public static <V, E> Graph<V, E> bfs(Graph<V, E> g, V source) {
		if (g == null || source == null) {
			return null;
		}

		AdjacencyMapGraph<V, E> backTrack = new AdjacencyMapGraph<V, E>();//will store our graph
		Queue<V> queue = new LinkedList<V>();                            //queue to implement BFS

		queue.add(source);
		backTrack.insertVertex(source);

		while (!queue.isEmpty()) {                                        //loop until no more vertices
			V u = queue.remove();                                        //dequeue
			for (V v : g.outNeighbors(u)) {                            //loop over out neighbors
				if (!backTrack.hasVertex(v)) {                            //if neighbor not visited, neighbor is discovered from this vertex
					queue.add(v);                                        //enqueue neighbor
					backTrack.insertVertex(v);
					backTrack.insertDirected(v, u, g.getLabel(u, v));    //save that this vertex was discovered from parent
				}
			}
		}
		return backTrack;
	}

	/**
	 * Find a path from "center" of graph to end vertex. Assumes method bfs() has already been run.
	 *
	 * @param tree -- graph returned by method bfs()
	 * @param v    -- end vertex
	 */
	public static <V, E> List<V> getPath(Graph<V, E> tree, V v) {

		//make sure end vertex is in the graph
		if (!tree.hasVertex(v)) {
			System.err.println("\tNo path found.\n");
			return new ArrayList<V>();
		}

		ArrayList<V> path = new ArrayList<V>(); //this will hold the path from start to end vertex

		//start from end vertex and work backward to start vertex
		while (tree.outDegree(v) != 0) {            //while not at root
			path.add(0, v);                //add current node

			//set current node equal to its parent (each node only has one out neighbor, its parent)
			for (V x : tree.outNeighbors(v)) {
				v = x;
			}

			//adding the root node to the beginning of the array
			if (tree.outDegree(v) == 0) {
				path.add(0, v);
			}

		}
		return path;
	}

	/**
	 * Given a graph and a subgraph, will return a set with the vertices in the graph, but not the subgraph.
	 * Will return an empty set if subgraph is not actually a subgraph of graph.
	 *
	 * @param graph    -- the parent graph
	 * @param subgraph -- the subgraph, likely created by the bfs method
	 * @return a set of the missing vertices
	 */
	public static <V, E> Set<V> missingVertices(Graph<V, E> graph, Graph<V, E> subgraph) {
		Set<V> missing = new HashSet<V>();

		//looping through each vertex in graph
		for (V v : graph.vertices()) {

			if (!subgraph.hasVertex(v)) { //if it doesn't have the vertex, add it
				missing.add(v);
			}
		}

		return missing;
	}

	/**
	 * Calculates the average separation of the tree from the root by dividing the total separation (distance from
	 * each node to the root) by the number of vertices.
	 *
	 * @param tree -- graph to get avg separation of
	 * @param root -- starting point in graph
	 * @return average separation as floating point number
	 */
	public static <V, E> double averageSeparation(Graph<V, E> tree, V root) {
		int numVerts = tree.numVertices() - 1;

		int totalSeparation = getSeparation(tree, root, 0);

		return (double) totalSeparation / numVerts;
	}

	/**
	 * Helper method for averageSeparation that calculates the total amount of separation in the tree recursively.
	 */
	public static <V, E> int getSeparation(Graph<V, E> g, V start, int depth) {
		int num = depth;

		//if it has in neighbors
		if (g.inDegree(start) != 0) {

			//for every in neighbor, recursively call it and add one to the depth
			for (V in : g.inNeighbors(start)) {
				num += getSeparation(g, in, depth + 1);
			}
		}

		return num;
	}


	/**
	 * Orders vertices in decreasing order by their in-degree
	 *
	 * @param g graph
	 * @return list of vertices sorted by in-degree, decreasing (i.e., largest at index 0)
	 */
	public static <V, E> List<V> verticesByInDegree(Graph<V, E> g) {

		//making comparator
		class inCompare implements Comparator<V> {
			Graph<V, E> graph;

			public inCompare(Graph<V, E> graph) {
				this.graph = graph;
			}

			public int compare(V node1, V node2) {
				return graph.inDegree(node2) - graph.inDegree(node1);
			}
		}

		//add nodes to array
		List<V> nodesByInDegree = new ArrayList<>();
		for (V node : g.vertices()) {
			nodesByInDegree.add(node);
		}

		//new comparator
		Comparator<V> compareMe = new inCompare(g);

		//pass comparator and let sort
		nodesByInDegree.sort(compareMe);
		return nodesByInDegree;
	}

}