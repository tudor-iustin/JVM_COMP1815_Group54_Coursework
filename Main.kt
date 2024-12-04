import java.io.File

// Data classes
data class City(val name: String)

data class Edge(val city1: City, val city2: City, val distance: Int)

// Graph class
class Graph {
    private val Cities = mutableSetOf<City>()
    private val edges = mutableListOf<Edge>()

    fun addCity(city: City) {
        Cities.add(city)
    }

    fun addEdge(edge: Edge) {
        edges.add(edge)
        Cities.add(edge.city1)
        Cities.add(edge.city2)
    }

    fun getCities(): Set<City> = Cities
    fun getEdges(): List<Edge> = edges
}

// File loader
fun loadFile(graph: Graph, filePath: String) {
    val file = File(filePath)
    file.forEachLine { line ->
        val parts = line.split(",")
        if (parts.size == 3) {
            val city1 = City(parts[0].trim())
            val city2 = City(parts[1].trim())
            val distance = parts[2].trim().toInt()
            graph.addEdge(Edge(city1, city2, distance))
        } else {
            throw IllegalArgumentException("Invalid file format. Each line must have 'city1,city2,distance'")
        }
    }
}


// MST calculation
fun calculateMST(graph: Graph): List<Edge> {
    val mst = mutableListOf<Edge>() // List to store the MST edges
    val visitedCities = mutableSetOf<City>() // Set of Cities already included in the MST
    val edgeList = mutableListOf<Edge>() // List to store edges for manual selection

    // Start from an arbitrary city (e.g., the first city in the graph)
    val startCity = graph.getCities().firstOrNull()
        ?: return mst // Return empty MST if no Cities in the graph

    visitedCities.add(startCity)

    // Add all edges of the starting city to the edge list
    edgeList.addAll(graph.getEdges().filter { it.city1 == startCity || it.city2 == startCity })

    while (mst.size < graph.getCities().size - 1 && edgeList.isNotEmpty()) {
        // Find the smallest edge in the edge list
        val smallestEdge = edgeList.minByOrNull { it.distance } ?: break
        edgeList.remove(smallestEdge)

        // Determine the unvisited city connected by this edge
        val unvisitedCity = when {
            !visitedCities.contains(smallestEdge.city1) -> smallestEdge.city1
            !visitedCities.contains(smallestEdge.city2) -> smallestEdge.city2
            else -> null
        }

        if (unvisitedCity != null) {
            mst.add(smallestEdge) // Add the edge to the MST
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

    return mst
}


// Main function
fun main() {
    val graph = Graph()

    while (true) {
        println("\n--- Telecoms Application ---")
        println("1. Load File")
        println("2. View Cities and Connections")
        println("3. Compute Minimum Spanning Tree")
        println("4. Exit")
        print("Enter your choice: ")

        when (readLine()?.toIntOrNull()) {
            1 -> {
                print("Enter file path(choose from 1/2/3/4): ")
                val filePath = readLine()!!
                try {
                    loadFile(graph, filePath)
                    println("\nFile loaded successfully.")
                } catch (e: Exception) {
                    println("Error: ${e.message}")
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
                println("Exiting application. Goodbye!")
                break
            }
            else -> println("Invalid choice. Please try again.")
        }
    }
}
