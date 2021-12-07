import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.FileWriter;
import java.io.IOException;

public class Ex1 {



    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
        BuildGraphFromTxt build= new BuildGraphFromTxt("input.txt");
        //  BayesianNetGraph network = parser.readNetworkFromFile("input.txt");
        BayesianNetGraph graph=build.buildGraph();
        graph.getAllResults();
        //  network.print(System.out);

        FileWriter writer = new FileWriter("output.txt");
        for(String str: graph.finalReusltOfInput) {
            writer.write(str + System.lineSeparator());//for pretty output
        }
        writer.close();

    }

}


