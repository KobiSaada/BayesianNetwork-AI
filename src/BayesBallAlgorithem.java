import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class BayesBallAlgorithem {
   // BayesianNetGraph g=new BayesianNetGraph();

    BayesBallAlgorithem(){

    }


    public String BayesBallAlgorithm(BayesianNetGraph g,String start, String end, ArrayList<String> colored) {

        for (NodeBayes n : g.get_nodes()) {
            n.setColor("white");
            n.setDiscovered(false);
            n.setColored(false);
        }
        boolean areIndependence = true;
        Queue<NodeBayes> queue = new LinkedList<>();

        if(!colored.isEmpty()) {
            for (String node : colored) {
             NodeBayes nodeColored= g.getNodeByName(node + " ");
             nodeColored.setColored(true);
            }
        }
        //Sign all the decencies that given

        //store the start and end Node for checking.
        NodeBayes startNode = g.getNodeByName(start + " ");
        NodeBayes endNode = g.getNodeByName(end + " ");

        for (NodeBayes c : startNode.getChildrens()) {
            c.setCameFrom("P");//p parent
            queue.add(c);
        }
        for (NodeBayes p : startNode.getParents()) {//c chilldren
            p.setCameFrom("C");
            queue.add(p);
        }

//travel with bfs algo
        while (!queue.isEmpty()) {
            NodeBayes v = queue.poll();
            if (v.get_name().equals(endNode.get_name())) areIndependence = false;

            if (!v.isDiscovered()) {
                if (!v.isColored() && v.getCameFrom().equals("C")) {

                    for (NodeBayes c : v.getChildrens()) {
                        c.setCameFrom("P");
                        queue.add(c);
                    }
                    for (NodeBayes p : v.getParents()) {
                        p.setCameFrom("C");
                        queue.add(p);
                    }
                } else if (!v.isColored() && v.getCameFrom().equals("P")) {

                    for (NodeBayes c : v.getChildrens()) {
                        c.setCameFrom("P");
                        c.setDiscovered(false);
                        queue.add(c);

                    }
                } else if (v.isColored() && v.getCameFrom().equals("P")) {

                    for (NodeBayes p : v.getParents()) {
                        p.setCameFrom("C");
                        p.setDiscovered(false);
                        queue.add(p);
                    }
                }
            }
            v.setDiscovered(true);
        }
        if (areIndependence) return "yes";
        else return "no";
    }
}
