import org.bytedeco.javacv.FrameFilter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class BaconGame {
    static AdjacencyMapGraph<String, Set<String>> graph = new AdjacencyMapGraph<String,Set<String>>();      //graph of all actors connected by movie edges
    Map<String, String> actorID = new HashMap<String, String>();                                        //maps actor IDs to actors
    Map<String, String> movieID = new HashMap<String, String>();                                        //maps movie IDS to movies
    Map<String, ArrayList<String>> movieToActor = new HashMap<String, ArrayList<String>>();                        //maps movies IDs to list of actor IDs in them
    BufferedReader input;                                                                  //reading from files
    static String center = "Kevin Bacon";                                                  //current center of the universe
    static Graph<String, Set<String>> BFStree;                                             //same as "graph" but after running BFS
    Map<String, Double> actorToAvgSeparation = new HashMap<>();                            //maps actors to their avg separations
    List<String> centers = new ArrayList<>();                                              //sorted list of best centers by avg. sep.
    String actorFile = "/Users/caroline/Documents/IdeaProjects/cs10/ps4/actors.txt";
    String movieFile = "/Users/caroline/Documents/IdeaProjects/cs10/ps4/movies.txt";
    String movieActorFile = "/Users/caroline/Documents/IdeaProjects/cs10/ps4/movie-actors.txt";


    /*
     * Makes a map of each Actor ID to the actor it represents
     */
    public void makeActorID () {
        //opening actorID file
        try {
            input = new BufferedReader(new FileReader(actorFile));
        }
        catch (Exception e) {
            System.err.println(e);
            System.err.println("18 file not found");
        }

        //reading input, adding to map of actorIDs to actors
        String s;
        try {
            while ((s = input.readLine()) != null) {
                String[] ids = s.split("\\|");
                actorID.put(ids[0], ids[1]);
            }
        }
        catch (Exception e) {
            System.err.println(e);
            System.err.println("35 can't read line");
        }

        //closing input
        try {
            input.close();
        }
        catch (Exception e) {
            System.err.println(e);
            System.err.println("46 can't close file");
        }
    }


    /*
     * Makes a map of each Movie ID to the movie it represents
     */
    public void makeMovieID() {
        //opening movieID file
        try {
            input = new BufferedReader(new FileReader(movieFile));
        }
        catch (Exception e) {
            System.err.println(e);
            System.err.println("62 file not found");
        }

        //reading input, adding to map of movieIDs to movies
        String x;
        try {
            while ((x = input.readLine()) != null) {
                String[] ids = x.split("\\|");
                movieID.put(ids[0], ids[1]);
            }
        }
        catch (Exception e) {
            System.err.println(e);
            System.err.println("74 can't read line");
        }

        //closing input
        try {
            input.close();
        }
        catch (Exception e) {
            System.err.println(e);
            System.err.println("82 can't close file");
        }
    }

    /*
     * Makes a map of each Movie ID to an array of the Actor IDs that appear in it
     */
    public void makeMovieToActor () {
        //opening movie-actors file
        try {
            input = new BufferedReader(new FileReader(movieActorFile));
        }
        catch (Exception e) {
            System.err.println(e);
            System.err.println("95 file not found");
        }

        //reading input, making map of movieIDs to actorsIDs
        String z;
        try {
            //while there are more movie/actor pairs
            while ((z = input.readLine()) != null) {
                //empty arrayList to add something in first if statement
                ArrayList<String> empty = new ArrayList<String>();

                //turning array w/ movieID|actorID into ArrayList so can put into map
                String[] IDS = z.split("\\|");
                ArrayList<String> ids = new ArrayList<>();
                ids.add(IDS[0]);
                ids.add(IDS[1]);

                //if the movie isn't in the map already, create new node w key movieID and add actorID to its array
                if (!movieToActor.containsKey(ids.get(0))) {
                    movieToActor.put(ids.get(0), empty);
                    movieToActor.get(ids.get(0)).add(ids.get(1));

                }
                //if the movie is in the map already, add actorID to its array
                else if (movieToActor.containsKey(ids.get(0))) {
                    movieToActor.get(ids.get(0)).add(ids.get(1));
                }
            }
        }
        catch (Exception e) {
            System.err.println(e);
            System.err.println("101 can't read line");
        }

        //closing input
        try {
            input.close();
        }
        catch (Exception e) {
            System.err.println(e);
            System.err.println("109 can't close file");
        }
    }


    /*
     * Makes a graph with every actor connected by movie edges to every other actor that they costar with
     */
    public void makeGraph(){

        //making graph of actors to costars w/ movies as edges. is a map of maps

        //inserting every actor into the map as a vertex
        Set<String> actors = actorID.keySet();
        for (String actor: actors) {
            graph.insertVertex(actorID.get(actor));
        }

        //making a graph (map of maps) that has each actor, their costars and the edges as the movies they're in
        Set<String> movies = movieToActor.keySet();
        for (String movie: movies){
            int numActors = movieToActor.get(movie).size();
            ArrayList<String> actorsInMovie = movieToActor.get(movie);      //all the actors in this particular movie

            //for each actor in the movie
            for (int i = 0; i<numActors; i++) {
                String star = actorsInMovie.get(i);                         //name of actor are working with now

                //loop over every other actor
                for (int j = 0; j<numActors; j++) {
                    //if there's already an edge, then add the current movie to the edge
                    if (graph.hasEdge(actorID.get(star), actorID.get(actorsInMovie.get(j)))) {
                        graph.getLabel(actorID.get(star), actorID.get(actorsInMovie.get(j))).add(movieID.get(movie));
                    }
                    //if there's not already an edge there, then create a new edge w new set. add this movie
                    else {
                        //if actor not self, then add undirected edge between the two actors w/ edge movie title
                        if (!star.equals(actorsInMovie.get(j))) {
                            graph.insertUndirected(actorID.get(star), actorID.get(actorsInMovie.get(j)),  new HashSet<String>());

                            graph.getLabel(actorID.get(star), actorID.get(actorsInMovie.get(j))).add(movieID.get(movie));
                        }
                    }
                }
            }
        }
    }

    /*
     * Method to run bfs from GraphLib on "center," which is the center of our current universe.
     */
    public Graph<String, Set<String>> runBFS (String root) {
        return GraphLib.bfs(graph, root);
    }

    /*
     * Method to run GraphLib's "get path"
     * Also sets up print statements using the path you get
     */
    public int runGetPath(String name) {
        if (BFStree == null) {
            return -1;
        }

        //making new arrayList of the path
        List<String> path = new ArrayList<String>();
        path = GraphLib.getPath(BFStree, name);

        //print statements to tell the user what the path is
        if (path.size() > 0) {
            int separation = path.size();
            System.out.println(path.get(separation - 1) + "'s number is " + (separation - 1));

            //printing each item in the path
            for (int i = separation - 1; i > 0; i--) {
                System.out.println(path.get(i) + " appeared in " + BFStree.getLabel(path.get(i), path.get(i - 1))
                        + " with " + path.get(i - 1));
            }
        }
        return 1;
    }

    /*
     * Method to run GraphLib's "missing vertices"
     * Also sets up print statements using the set it returns
     */
    public void runMissingVertices () {
        Set<String> missing = GraphLib.missingVertices(graph, BFStree);
        System.out.println("The missing vertices are: ");
        Iterator<String> itr = missing.iterator();
        while (itr.hasNext()){
            System.out.println(itr.next());
        }
    }


    /*
     * Updates the center of the graph, runs bfs on the new center
     * Also prints out statement telling user new center, # of connections and avg separation
     */
    public void updateCenter (String newCenter) {
        center = newCenter;

        //run bfs on new center
        BFStree = GraphLib.bfs(graph, center);

        System.out.printf(" %s is now the center of the universe connected to %d/11568 actors with average separation %.2f.\n", center, (BFStree.numVertices()-1), runAvgSep(BFStree, center));

    }


    /*
     * Method to run GraphLib's "average separation"
     * prints out the separation that it returns
     */
    public double runAvgSep (Graph<String, Set<String>> tree, String root) {
        double avgSep = GraphLib.averageSeparation(tree, root);
        return avgSep;
    }

    /*
     * Runs GraphLib's "vertices by in degree" method
     * takes user input to determine which vertices to print
     */
    public void runVertsByIn (int low, int high) {
        List<String> vertsByInDegree = new ArrayList<>();
        Map<String,Integer> vertToDegree = new HashMap<>();

        //making array that has all the vertices in graph sorted by indegree
        vertsByInDegree = GraphLib.verticesByInDegree(graph);

        //making map that maps each vertex to its in degree
        for(String s: graph.vertices()) {
            vertToDegree.put(s, graph.inDegree(s));
        }

        System.out.println("The actors with degree between " +low+ " and " +high+ " are:");

        //if the number of degrees is between low and high (from user input) then print them in descending order
        for (String x: vertsByInDegree) {
            if (vertToDegree.get(x) >= low && vertToDegree.get(x) <= high) {
                System.out.println(x + " ("+ vertToDegree.get(x)+ ")");
            }
        }
    }


    /*
     * Finds the best centers of the universe by average separation using a comparator
     */
    public void bestCenter (int range) {

        if (actorToAvgSeparation.size() == 0 && centers.size() == 0) {
            for (String actor : BFStree.vertices()) {
                Graph<String, Set<String>> newBFStree = runBFS(actor);
                double avSep = runAvgSep(newBFStree, actor);
                actorToAvgSeparation.put(actor, avSep);
            }


            //making comparator
            class centerCompare implements Comparator<String> {
                int i = 1;

                public int compare(String node1, String node2) {
                    double diff = (actorToAvgSeparation.get(node1) - actorToAvgSeparation.get(node2));
                    i++;
                    return Double.compare(diff, 0);
                }
            }

            Comparator<String> compareMe = new centerCompare();                 //new comparator

            //adding all the vertices to an ArrayList
            for (String s : BFStree.vertices()) {
                centers.add(s);
            }

            centers.sort(compareMe);                                            //sorting by avg separation
        }

        //printing out specified range
        if (range>0) {
            System.out.println("The top " + range + " centers of the universe are: ");
            for (int i=0; i < Math.min(centers.size(), range); i++) {
                System.out.printf("%s (%.2f) \n", centers.get(i), actorToAvgSeparation.get(centers.get(i)));
            }
        }
        else if (range<0) {
            System.out.println("The bottom " + (range*-1)+ " centers of the universe are: ");
            for (int i=1; i <= Math.min(centers.size()-1, (range*-1)); i++) {
                System.out.printf("%s (%.2f) \n", centers.get(centers.size()-i), actorToAvgSeparation.get(centers.get(centers.size()-i)));
            }
        }
    }


    public static void main(String[] args) {
        BaconGame game = new BaconGame();
        center = "Kevin Bacon";

        //running methods needed to make the graph and bfs tree
        game.makeActorID();
        game.makeMovieID();
        game.makeMovieToActor();
        game.makeGraph();
        BFStree = game.runBFS(center);


        //writing out the instructions and setting the first center to kevin bacon
        System.out.println("'u' to update the center of the universe \n'p [name]' for path \n" +
                "'i' for missing vertices \n'd' for sort by number of connections \n'c' for best centers of the universe " +
                "(by avg. separation, takes ~40sec the first time) \n'q' for quit game");

        game.updateCenter("Kevin Bacon");

        //making new scanner, reading the first line
        Scanner in = new Scanner(System.in);
        String s = in.nextLine();

        //checking to make sure the user is typing one of the commands listed
        while (s.charAt(0) != 'u' && s.charAt(0) != 'p' && s.charAt(0) != 'i' &&
                s.charAt(0) != 'd' && s.charAt(0) != 's' && s.charAt(0) != 'q' && s.charAt(0) != 'c') {
            System.out.println("Please type one of the commands listed");
            s = in.nextLine();
        }

        //while the user doesn't want to quit
        while (s.charAt(0) != 'q') {
            //printing the intro section
            System.out.println("\n" +center + " game > \n" + s);

            if (s.charAt(0) == 'p') {
                //getting the name as a substring of what the user has typed
                String name = s.substring(2, s.length());

                //making sure the name they input is in the graph
                if (graph.hasVertex(name)) {
                    game.runGetPath(name);
                }
                else {
                    System.out.println("name not found! please try again");
                }
            }
            else if (s.charAt(0) == 'i') {
                game.runMissingVertices();
            }
            else if (s.charAt(0) == 'u') {
                //getting the name they want as a substring
                String name = s.substring(2, s.length());

                //making sure the graph has the name they want
                if (graph.hasVertex(name)) {
                    game.updateCenter(name);
                }
                else {
                    System.out.println("name not found! please try again.");
                }
            }
            else if (s.charAt(0) == 'd') {
                int low =  0, high = 0;

                //making sure the lower bound they type is an integer
                System.out.println("what lower bound do you want?");
                try {
                    low = in.nextInt();
                }
                catch (Exception e) {
                    System.out.println("not an integer! try again");
                    String dontWant = in.nextLine(); //getting rid of the enter character
                    low = in.nextInt();
                }

                //making sure the upper bound they type is an integer
                System.out.println("what upper bound do you want?");
                try {
                    high = in.nextInt();
                }
                catch (Exception e) {
                    System.out.println("not an integer! try again");
                    String dontWant = in.nextLine(); //getting rid of the enter character
                    high = in.nextInt();
                }

                //making sure the bounds are valid
                if (high < low) {
                    System.out.println("invalid bounds! try again");
                }
                else {
                    game.runVertsByIn(low, high);
                }
            }
            else if (s.charAt(0) == 'c') {
                System.out.println("What range would you like? (positive for top, negative for bottom)");
                int num;
                try {
                    num = in.nextInt();
                }
                catch (Exception e) {
                    System.out.println("that's not an integer! please try again");
                    num = in.nextInt();
                }
                game.bestCenter(num);
            }


            //taking care of the "enter" character that is read as the next string sometimes
            if (s.charAt(0) == 'd' || s.charAt(0) == 's' || s.charAt(0) == 'c') {
                String dontNeed = in.nextLine();
            }

            //reading in the next line
            s = in.nextLine();

            //checking to make sure the user is typing one of the commands listed
            while (s.charAt(0) != 'u' && s.charAt(0) != 'p' && s.charAt(0) != 'i' &&
                    s.charAt(0) != 'd' && s.charAt(0) != 's' && s.charAt(0) != 'q' && s.charAt(0) != 'c') {
                System.out.println("wrong input! try again.");
                s = in.nextLine();
            }
        }
    }
}
