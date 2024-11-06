import java.io.File //import the File Class so that the program can read the Capitals.txt file

// Add class to represent the capitals in the .txt file
data class Capital(val name: String)

// The class named "Edges" is loading and representing the values from a capital to another and the distance between each of them
data class Edges(val from: Capital, val to: Capital, val distance: Int)

// Create a new class "Graph" which will calculate the minimum spanning tree using Kruskal's algorithm.
// The Graph class also manages the edges (nodes) and the capitals in the .txt file given

class Graph {
    val capitals = mutableSetOf<Capital>()  // This val holds the capital names
    val edges = mutableListOf<Edges>()   // This val holds the edges for the capitals which represent the distance between the capitals

    // The addEdge function will add a connection between capital A and B for example Amsterdam - Bucharest
    // The connection is represented by the integer values in KM at the end of each line of the .txt file
    fun addEdge(from: String, to: String, distance: Int) {
        val capitalA = capitals.find { it.name == from } ?: Capital(from).also { capitals.add(it) }
        val capitalB = capitals.find { it.name == to } ?: Capital(to).also { capitals.add(it) }
        // The values above (capital A and Capital B) represent the start and finish capitals using the from: and to: properties
        edges.add(Edges(capitalA, capitalB, distance)) // after which I created the connection/edge of the 2 capitals represented by the integer values at the end of each line in the .txt file
    }

    // the next function calculates the minimum spanning tree (mst) of the graph using Kruskal’s Algorithm which was taught in Advanced Algorithms and Data Structures
    // Kruskal’s Algorithm is also the algorithm that I used for that coursework last year.
    fun mst(): Pair<List<Edges>, Int> { //returns the list of all the edges in the txt file provided
        val sortedEdges = edges.sortedBy { it.distance }
        // Kruskal’s Algorithm requires that all edges are sorted for it to work properly so the sortedEdges val above ensures the edges are sorted by distance ascending
        val mainMap = capitals.associateWith { it }.toMutableMap() // mainMap will track the "parent" of each of the capitals in the file.
        //this can be considered similar to a key:value pair in a dictionary where parent is the key and capital is the value, so each capital has its own parent (parent:capital)

        // findRoot function finds the root of the capital using path compression required for the union algorithm in Kruskal's algorithm
        fun findRoot(capital: Capital): Capital {
            if (mainMap[capital] == capital) return capital
            val root = findRoot(mainMap[capital]!!)
            mainMap[capital] = root
            return root //returns the root of the capitals
        }

        // the connection function sets the root of capitalA to capitalB which merges them
        fun connection(capitalA: Capital, capitalB: Capital) {
            val rootA = findRoot(capitalA)
            val rootB = findRoot(capitalB)
            if (rootA != rootB) mainMap[rootA] = rootB //

        }

        val mstEdges = mutableListOf<Edges>() // this is a mutable list which holds the edges values from the MST itself
        var totalDistance = 0 // the totalDistance variable is initially set to 0 (integer), it's purpose is to add up the total distance from the minimum spanning tree

        //the following for-loop will loop through each individual edge in the txt file
        for (edge in sortedEdges) {
            if (findRoot(edge.from) != findRoot(edge.to)) {
                //if the  edges of capitalA and capitalB denoted by from: and to: don;t share the same root, the edge is then added to the MST
                mstEdges.add(edge)
                totalDistance += edge.distance //the total distance is incremented by the edge's distance
                connection(edge.from, edge.to) // the connection function (also if easier, the union function) is used to merge the 2 subsets
            }
        }
        return Pair(mstEdges, totalDistance) //returns a pair of the total cable length and the edges of the MST
    }
}

// Function to load edges from Capitals.txt from the same folder as the main application
fun loadGraph(graph: Graph) {
    val capitalsFile = "Capitals.txt" // this should read the file itself, if it cannot be found, the path can be pasted between the quotation marks too
    File(capitalsFile).forEachLine { line ->
        val parts = line.split(",")
        //the values between the commas are separated
        if (parts.size == 3) {
            val from = parts[0].trim()
            val to = parts[1].trim()
            //if each line in the file has 3 elements, mainly the CapitalA, CapitalB and Edges/distance, it trims them and converts the distance to an integer
            val distance = parts[2].trim().toIntOrNull() ?: return@forEachLine
            graph.addEdge(from, to, distance) //adds the edges between the capitals using the addEdge function created above
        }
    }
}

// This function uses a CLI (Command Line Interface). I chose to do a CLI because it's faster to load and easier to understand
fun main() {
    val graph = Graph()

    println("Data from the Capitals.txt file") //this prints each line of the txt file in the terminal console, in my case IntelliJ

    // Load data from graph
    loadGraph(graph)

    println("Graph loaded. With ${graph.capitals.size} capitals and ${graph.edges.size} connections.") // prints out the number of capitals and connections found in the file

    // Calculate the Minimum spanning tree
    val (mstEdges, totalDistance) = graph.mst()

    // Display the MST result
    println("\nMinimum Spanning Tree (MST):")
    mstEdges.forEach { edge ->
        println("${edge.from.name} TO ${edge.to.name} : ${edge.distance} km") // Prints the capitals from A to B along with the distance. Example: Athens TO Skopje : 482 KM
    }
    println("\nTotal cable length required: $totalDistance km")
}