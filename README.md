# BayesianNetwork-AI
Project in Algorithms-AI on a Bayesian network with 2 algorithms Bayesball and Variable elimination.

# Prerequisites: :bulb:
* Java  <code><a href = "https://www.jetbrains.com/idea/"><img height="40" src="https://upload.wikimedia.org/wikipedia/commons/thumb/9/9c/IntelliJ_IDEA_Icon.svg/96px-IntelliJ_IDEA_Icon.svg.png" alt="IntelliJ IDEA"></a></code>

# INFO ::mag_right:
In this project we were required to get information from the xml network and enter a graph into our Bayesian network with all the information for each variable and then answer two types of queries one using a basyeball algorithm and the other VariableElimination and output the solutions to an output file.

# BayseBall Algo::basketball:
Bayes ball is an efficient algorithm for computing d-separation by passing simple messages between nodes of the graph. The name "Bayes Ball" stems from the idea of balls bouncing around a directed graph, where if a ball cannot bounce between two nodes then they are [conditionally] independent.
<img width="506" alt="Screen Shot 2021-12-07 at 21 45 13" src="https://user-images.githubusercontent.com/73976733/145096498-b9927271-f9ab-4569-a29e-886f8ebacb61.png">

# VariableElimination ::bomb: 
Variable elimination (VE) is a simple and general exact inference algorithm in probabilistic graphical models, such as Bayesian networks and Markov random fields. It can be used for inference of maximum a posteriori (MAP) state or estimation of conditional or marginal distributions over a subset of variables.
<img width="1192" alt="Screen Shot 2021-12-07 at 21 55 59" src="https://user-images.githubusercontent.com/73976733/145097228-59ee020c-94a3-47c6-be61-26bd1d95197f.png">
# Example: :page_facing_up:

 after you clone this project there is a class call Ex3 main run it in check(0):
 
        g = DiGraph()  # creates an empty directed graph
    for n in range(4):
        g.add_node(n)
    g.add_edge(0, 1, 1)
    g.add_edge(1, 0, 1.1)

    g.add_edge(1, 2, 1.3)
    g.add_edge(2, 3, 1.1)
    g.add_edge(1, 3, 1.9)
    g.remove_edge(1, 3)
    g.add_edge(1, 3, 10)
    print(g)  # prints the __repr__ (func output)
    print(g.get_all_v())  # prints a dict with all the graph's vertices.
    print(g.all_in_edges_of_node(1))
    print(g.all_out_edges_of_node(1))
    g_algo = GraphAlgo(g)
    print(g_algo.shortest_path(0, 3))
    g_algo.plot_graph()
 
