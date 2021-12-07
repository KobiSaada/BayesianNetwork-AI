
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.*;

public class BuildGraphFromTxt {
    private String fileName;
    private ArrayList<String> lp = new ArrayList<>();
    BayesianNetGraph network = new BayesianNetGraph();
    private  String[] linesFromTxt;
    private int numOfVertex;

    public ArrayList<String> getLp() {
        return lp;
    }


    public void setLp(ArrayList<String> lp) {
        this.lp = lp;
    }


    //constructor that get the file name.
    public BuildGraphFromTxt(String fileName) {
        this.fileName = fileName;
    }
    public BuildGraphFromTxt(){};
    public String[] getContextFromInput(String path) {

        InputStream inputS = null;
        try {
            inputS = new FileInputStream(path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader buf = new BufferedReader(new InputStreamReader(inputS));
        String line = null;
        try {
            line = buf.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        StringBuilder sBuild = new StringBuilder();
        while (line != null) {
            sBuild.append(line).append("\n");
            try {
                line = buf.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String fileAsString = sBuild.toString();
        String[] result = fileAsString.split("\n", 2);
        return result;
    }
    public BayesianNetGraph buildGraph() throws ParserConfigurationException, IOException, SAXException {
      this.network= readNetworkFromFile(this.fileName);

        ArrayList<String> p = new ArrayList<>();

        return this.network;
    }


    public BayesianNetGraph readNetworkFromFile(String fileName) throws IOException, ParserConfigurationException, SAXException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();

        linesFromTxt = getContextFromInput(fileName);
        String lastParagraph = linesFromTxt[linesFromTxt.length - 1];
        String[] lastParagraphAfterParsing = lastParagraph.split("\n");
        for (int i = 0; i < lastParagraphAfterParsing.length ; i++) {
            network.getLastParagraph().add(lastParagraphAfterParsing[i]);
        }
        ArrayList<String> p = new ArrayList<>();
        for (String s : lastParagraphAfterParsing) {
            if (s.charAt(0) == 'P') {
                p.add(s);
            }
        }
        this.network.setCheckPropebilties(p);
        Document doc = db.parse(new File(linesFromTxt[0]));
        return processDocument(doc);
    }

    protected BayesianNetGraph processDocument(Document doc) {
        Element networkElt = doc.getDocumentElement();

        // First do the variables
        doForEachElement(doc, "VARIABLE", new ElementTaker1() {
            public void element(Element e) {
                processVariableElement(e, network);
            }
        });
        // Then do the defintions (a.k.a, links and CPTs)
        doForEachElement(doc, "DEFINITION", new ElementTaker1() {
            public void element(Element e) {
                processDefinitionElement(e, network);
            }
        });
        return network;
    }

    protected void doForEachElement(Document doc, String tagname, ElementTaker1 taker) {
        NodeList nodes = doc.getElementsByTagName(tagname);
        if (nodes != null && nodes.getLength() > 0) {
            for (int i=0; i < nodes.getLength(); i++) {
                Node node = nodes.item(i);
                taker.element((Element)node);
            }
        }
    }

    protected void processVariableElement(Element e, BayesianNetGraph network) {
        Element nameElt = getChildWithTagName(e, "NAME");
        String name = getChildText(nameElt);
        //trace("creating variable: " + name);
        // Node k=new Node("s");
        NodeBayes var = new NodeBayes(name);
        //NodeBayes domain = var.get_name();
        ArrayList<String> s=new ArrayList<>();
        doForEachChild(e, "OUTCOME", new ElementTaker1() {
            public void element(Element e) {
                String value = getChildText(e);
                ArrayList<String> s=new ArrayList<>();
                s.add(value);
                var.getValues().add(value);


                //trace("adding value: " + value);


            }

        });


        network.add(var);
    }

    protected void processDefinitionElement(Element e, final BayesianNetGraph network) {
        Element forElt = getChildWithTagName(e, "FOR");
        String forName = getChildText(forElt);
        //trace("creating links to variable: " + forName);
        NodeBayes forVar = network.getNodeByName(forName);
        final List<NodeBayes> givens = new ArrayList<NodeBayes>();
        doForEachChild(e, "GIVEN", new ElementTaker1() {
            public void element(Element e) {
                String value = getChildText(e);
                //trace("adding parent: " + value);
                givens.add(network.getNodeByName(value));
            }
        });
        helpCpt cpt = new helpCpt(forVar, givens);
        Element tableElt = getChildWithTagName(e, "TABLE");
        String tableStr = getChildText(tableElt);
        initCPTFromString(cpt, tableStr,forVar,givens);
        network.connect(forVar, givens, cpt);
    }


    public void initCPTFromString(helpCpt cpt, String str, NodeBayes n, List<NodeBayes> givens) throws NumberFormatException {
        //trace("initCPTFromString: " + str);
        StringTokenizer tokens = new StringTokenizer(str);
        Iterator<helpCpt.ProbabilityValue> values = cpt.valueIterator();
        while (tokens.hasMoreTokens()) {
            String token = tokens.nextToken();
            //trace("probability: " + token);
            helpCpt.ProbabilityValue pv = values.next();
            pv.value =""+Double.parseDouble(token);

        }
        String[]ko=new String[2];
        String s=""+cpt;
        s.replaceAll("\\\t","");
        String[] s1=s.split("\n");


        String ss="";

        for (int i=0; i<s1.length;i++) {
            ss += s1[i]+", ";
            if (i%2!=0)
                ss+="\n";

        }
        String []split=ss.split("\n");
        String q1 ="";
       // for (int p =0 ; p<split.length;p++){

       //     q1+= split[p].split(":")[1]+" ";

     //   }
      //  String []split=ss.split(":");
        String k="";
        ss.replaceAll("\\r|\\n", "");
        ss.replaceAll(",", "");
        String []s2=ss.split("\n");

        ArrayList<String[]> arr =new ArrayList<String[]>();

        for (int j=0 ;j<s1.length;j++){

            arr.add(s1[j].split("\\t"));


        }
        int sizeOfEntreyArray = givens.size();
        String[] firstLine = new String[sizeOfEntreyArray + 2];
        int runner = 0;
        if (!givens.isEmpty()) {

            for (int k1 = 0; k1 <givens.size(); k1++) {
                firstLine[runner] = givens.get(k1).get_name();
                runner++;
            }
        }
        firstLine[runner] = n.get_name();
        firstLine[firstLine.length-1] ="P=" ;
        n.getCpt().getCpt().addAll(arr);
        n.getCpt().setFirstline(firstLine);

    }

    protected Element getChildWithTagName(Element elt, String tagname) {
        NodeList children = elt.getChildNodes();
        if (children != null && children.getLength() > 0) {
            for (int i=0; i < children.getLength(); i++) {
                Node node = children.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element childElt = (Element)node;
                    if (childElt.getTagName().equals(tagname)) {
                        return childElt;
                    }
                }
            }
        }
        throw new NoSuchElementException(tagname);
    }

    protected void doForEachChild(Element elt, String tagname, ElementTaker1 taker) {
        NodeList children = elt.getChildNodes();
        if (children != null && children.getLength() > 0) {
            for (int i=0; i < children.getLength(); i++) {
                Node node = children.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element childElt = (Element)node;
                    if (childElt.getTagName().equals(tagname)) {
                        taker.element(childElt);
                    }
                }
            }
        }
    }


    /**
     * Returns the concatenated child text of the specified node.
     * This method only looks at the immediate children of type
     * Node.TEXT_NODE or the children of any child node that is of
     * type Node.CDATA_SECTION_NODE for the concatenation.
     */
    public String getChildText(Node node) {
        if (node == null) {
            return null;
        }
        StringBuilder buf = new StringBuilder();
        Node child = node.getFirstChild();
        while (child != null) {
            short type = child.getNodeType();
            if (type == Node.TEXT_NODE) {
                buf.append(child.getNodeValue());
            }
            else if (type == Node.CDATA_SECTION_NODE) {
                buf.append(getChildText(child));
            }
            child = child.getNextSibling();
        }
        return buf.toString();
    }


    protected void trace(String msg) {
        System.err.println(msg);
    }



}


interface ElementTaker1 {
    public void element(Element e);
}



