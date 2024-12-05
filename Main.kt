import java.io.File  // file reading import

// Data classe
data class City(val name: String)// for cities
data class Edge(val city1: City, val city2: City, val distance: Int)//the distance between cities - the connection

class Graph {               // Graph
    private val Cities = mutableSetOf<City>()//this is a set of all the cties while the one below is the is all of the edges
    private val edges = mutableListOf<Edge>()
    fun addEdge(edge: Edge) {
        edges.add(edge) //this adds the edge to the list
        Cities.add(edge.city1)// this ensures the cities are also added
        Cities.add(edge.city2)
    }
    fun getCities(): Set<City> = Cities// gets cities and edges on the grpah
    fun getEdges(): List<Edge> = edges
}

fun Loadfile(graph: Graph, filePath: String) { // File loader
    val file = File(filePath)// this opens the file
    file.forEachLine { line ->// this goes over each line ine the file a for loop of sort
        val parts = line.split(",")// then splits each of the lines up by a coma
        val city1 = City(parts[0].trim())// the folowing lines split the string into three sections and adds it to the grpahs
        val city2 = City(parts[1].trim())
        val distance = parts[2].trim().toInt()
        graph.addEdge(Edge(city1, city2, distance))
    }
}
fun calculateMST(graph: Graph): List<Edge> {                // MST calculation
    val MST = mutableListOf<Edge>()                 // List to store the MST edges
    val visitedCities = mutableSetOf<City>()         // Set of Cities already included in the MST
    val edgeList = mutableListOf<Edge>()        // List to store edges for manual selection
    val startCity = graph.getCities().firstOrNull()    // Start from an arbitrary city (e.g., the first city in the graph)
        ?: return MST                               // Return empty MST if no Cities in the graph
    visitedCities.add(startCity)
    edgeList.addAll(graph.getEdges().filter { it.city1 == startCity || it.city2 == startCity })    // Add all edges of the starting city to the edge list
    while (MST.size < graph.getCities().size - 1 && edgeList.isNotEmpty()) {
        val smallestEdge = edgeList.minByOrNull { it.distance } ?: break        // Find the smallest edge in the edge list
        edgeList.remove(smallestEdge)
        val unvisitedCity = when {                                  // Determine the unvisited city connected by this edge
            !visitedCities.contains(smallestEdge.city1) -> smallestEdge.city1
            !visitedCities.contains(smallestEdge.city2) -> smallestEdge.city2
            else -> null
        }
        if (unvisitedCity != null) {
            MST.add(smallestEdge) // Add the edge to the MST
            visitedCities.add(unvisitedCity) // Mark the city as visited

            // Add all edges of the newly visited city to the edge list
            graph.getEdges()
                .filter {
                    (it.city1 == unvisitedCity && !visitedCities.contains(it.city2)) ||
                            (it.city2 == unvisitedCity && !visitedCities.contains(it.city1))
                }
                .forEach { edgeList.add(it) }
        }
    }

    return MST
}
fun main() {// the  Main function
    val graph = Graph()
    //menu of the appliaction
    while (true) {
        println("\n--- Telecomunication Application ---")
        println("1.Load File and process it ")
        println("2.View Cities and Connections")
        println("3.Minimum Spanning Tree")
        println("4.Close")
        print("Enter your choice(choose from 1/2/3/4): ")

        when (readLine()?.toIntOrNull()) { //.toIntOrNull() is a string that checks if a value is a integer, this statement loops until option 4
            1 -> {// for each of the option the task that gets completed
                print("Load preexisting file? (y/n): ")
                val response = readLine()?.lowercase() // this Store response to avoid multiple readLine() calls

                if (response == "y") {
                    try {
                        Loadfile(graph,"src/Capitals.txt")// we use the load file function to open our dataset
                        println("\nFile loaded successfully.")// this outputted if it is successful, exception handling techniques are used to stop error from popping up
                    } catch (e: Exception) {
                        println("Error: ${e.message}")// prints the error
                    }
                } else if (response == "n") {
                    print("Enter file path: ")
                    val filePath = readLine()!!
                    try {
                        Loadfile(graph, filePath)
                        println("\nFile loaded successfully.")
                    } catch (e: Exception) {
                        println("Error: ${e.message}")
                    }
                } else {
                    println("Invalid input. Please enter 'y' or 'n'.")
                }
            }


            2 -> {
                if (graph.getCities().isEmpty()) {
                    println("No data loaded. Please load a file first.")
                } else {
                    println("\nCities and Connections:")
                    graph.getEdges().forEach { edge ->
                        println("${edge.city1.name} <-> ${edge.city2.name} : ${edge.distance} km")
                    }
                }
            }
            3 -> {
                if (graph.getCities().isEmpty()) {
                    println("No data loaded. Please load a file first.")
                } else {
                    val mstEdges = calculateMST(graph)
                    val totalDistance = mstEdges.sumOf { it.distance }
                    println("\nMinimum Spanning Tree:")
                    mstEdges.forEach { edge ->
                        println("${edge.city1.name} <-> ${edge.city2.name} : ${edge.distance} km")
                    }
                    println("Total Cable Length: $totalDistance km")
                }
            }
            4 -> {
                println("Closing application")
                break// breaks the loop
            }
            else -> println("\nInvalid choice. Please try again.")
        }
    }
}
