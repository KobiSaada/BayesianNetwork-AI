import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.*;
import java.util.List;

import static java.math.RoundingMode.HALF_UP;
import static java.util.Arrays.*;

public class BayesianNetGraph {

    ArrayList<String> finalReusltOfInput = new ArrayList<>();
    static int numOfmultply = 0;
    static int numOfSum = 0;
    private List<String> lastParagraph = new ArrayList<>();


    private ArrayList<NodeBayes> _nodes;
    private int _edge_count;//for extend
    private ArrayList<String[]> checkDependcies;
    private ArrayList<String> checkPropebilties;
    private PriorityQueue<CPT> PqCpt;
    private BayesBallAlgorithem b=new BayesBallAlgorithem();



    ArrayList<String[]> resultOf;
    ArrayList<String> hidden;
    ArrayList<String> evidance1;
    ArrayList<String[]> evidance;

    public List<String> getLastParagraph() {
        return lastParagraph;
    }

    public void setLastParagraph(List<String> lastParagraph) {
        lastParagraph = lastParagraph;
    }
    protected NodeBayes getNodeForVariable(NodeBayes var){
        for (NodeBayes node : this.get_nodes()) {
            if (node == var) {
                return node;
            }
        }
        throw new NoSuchElementException();
    }
    public void connect(NodeBayes var, List<NodeBayes> parents, helpCpt cpt) {
        NodeBayes node = getNodeForVariable(var);
       // node.getParents()= new ArrayList<NodeBayes>(parents.size());
        Collections.reverse(parents);
        for (NodeBayes pvar : parents) {
           NodeBayes pnode = getNodeForVariable(pvar);
            node.getParents().add(pnode);
            pnode.getChildrens().add(node);
        }
        node.setCpt1(cpt);
    }

    public BayesianNetGraph() {
        _nodes = new ArrayList();
        _edge_count = 0;
        checkDependcies = new ArrayList<>();
        checkPropebilties = new ArrayList<>();
        resultOf = new ArrayList<String[]>();
        hidden = new ArrayList<String>();
        evidance = new ArrayList<String[]>();
    }

    public boolean add(NodeBayes d) {

        boolean ans = false;
        d.set_id(_nodes.size());
        this._nodes.add(d);
        ans = true;


        return ans;
    }

    public static int getIndex(String[] firstline, String s) {
        int index = -1;
        for (int i = 0; i < firstline.length; i++) {
            if (firstline[i].equals(s) || firstline[i].equals(s + " ")) index = i;
        }
        return index;
    }

    public int size() {
        return this._nodes.size();
    }

    public int getNodeIndexByName(String s) {
        int ans = -1;
        for (int i = 0; ans == -1 && i < this.size(); ++i) {
            NodeBayes cr = (NodeBayes) this._nodes.get(i);
            String name = cr.get_name();
            s.replace("\\s", "");
            s.replace(" ","");
            removeSpace(s);
            String k=removeSpace(s);
            String k1=removeSpace(name);
            if (k.equals(k1) || k.equals(" " + k1) || k.equals(k1 + " ")) {
                ans = i;
            }
        }
        return ans;
    }
    public static String removeSpace(String s) {
        String withoutspaces = "";
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) != ' ')
                withoutspaces += s.charAt(i);

        }
        return withoutspaces;

    }
    public ArrayList<String> getVariablesNames(){
        ArrayList<String> names = new ArrayList<String>();
        for (NodeBayes n : this.get_nodes())
            names.add(n.get_name());
        return names;
    }
    public NodeBayes getNodeByName(String s) {
        NodeBayes ans = null;
        int ind = this.getNodeIndexByName(s);
        if (ind != -1) {
            ans = this._nodes.get(ind);
        }
        return ans;
    }

    public ArrayList<NodeBayes> get_nodes() {
        return _nodes;
    }

    public void prepareForBayesballAlgorithem(String[] dependcies) {

        String start = "";
        String end = "";
        ArrayList<String> depend = new ArrayList<>();
        String[] startAndEnd = dependcies[0].split("-");
        start = startAndEnd[0];
        end = startAndEnd[1];
        if (dependcies[1].length()!=0) {
            String[] depGiven = dependcies[1].split(",");

            for (int i =0;i<depGiven.length;i++){
            String[] depGiv2 = depGiven[i].split("=");

            depend.add(depGiv2[0] + " ");
        }
            }
        String result = b.BayesBallAlgorithm(this,start, end, depend);
        finalReusltOfInput.add(result);

    }

    public void getAllResults() {
        for (String line : lastParagraph) {
            if (line.charAt(0) != 'P') {
                int index = 0;
                while (line.charAt(index) != '|') {
                    index++;
                }
                String left = line.substring(0, index);
                String right = line.substring(index + 1, line.length());
                String[] toAdd = new String[2];
                toAdd[0] = left;
                toAdd[1] = right;
                prepareForBayesballAlgorithem(toAdd);

            }
            if (line.charAt(0) == 'P') {
            caluclatePropebiltyVE(line);
            }
        }
    }



   public String caluclatePropebiltyVE(String s) {
        numOfSum = 0;
        numOfmultply = 0;
        //Getting all the information from the query
        int indexForLeft = 0;
        int indexForRight = 0;
        while (s.charAt(indexForLeft) != '|') {
            indexForLeft++;
        }
        while (s.charAt(indexForRight) != ')') {
            indexForRight++;
        }
        String left = s.substring(2, indexForLeft);
        String middle = s.substring(indexForLeft + 1, indexForRight);
        String right = s.substring(indexForRight + 2, s.length());


        String[] left2 = left.split(",");
        for (int i = 0; i < left2.length; i++) {
            String[] current = left2[i].split("=");
            resultOf.add(current);
        }

        String[] middle2 = middle.split(",");
        for (int i = 0; i < middle2.length; i++) {
            String[] current = middle2[i].split("=");
            evidance.add(current);
        }
        String[] right2 = right.split("-");
        for (int i = 0; i < right2.length; i++) {
            String[] current = right2[i].split("-");
            for (int j = current.length-1; j >=0; j--) {
                hidden.add(current[j]);
            }
        }


        String result = joinAlgorithm(this.getResultOf(), this.getEvidance(), this.getHidden());
        finalReusltOfInput.add(result);
        hidden.clear();
        resultOf.clear();
        evidance.clear();
        this.getHidden().clear();
        this.getResultOf().clear();
        this.getEvidance().clear();

        return result;

    }
    public void del(NodeBayes n,ArrayList<String> s){
        int i=0;
        for(String s1:s){
            NodeBayes k= getNodeByName(s1);
            for (i=0;i<k.getCpt().getFirstline().length;i++) {
                if (k.getCpt().getFirstline()[i].equals(n.get_name())) {
                    s.remove(s1);

                }
            }
            }
    }


    public String joinAlgorithm(ArrayList<String[]> resultOf, ArrayList<String[]> evidance, ArrayList<String> hidden) {

        //ArrayList<String> relevantNodes = getRelevantNodes();
        ArrayList<String> relevantNodes= getRelevant();

        for (int i = 0; i < hidden.size(); i++) {
            if (!relevantNodes.contains(hidden.get(i))) {

                hidden.remove(i);
                i--;
            }
        }
        for (int i = 0; i < hidden.size(); i++) {
      //      del(getNodeByName(hidden.get(i)), relevantNodes);
        }
        ArrayList<CPT> copyCpt = getCopyOfCpt(relevantNodes);
        //for(CPT c :copyCpt){
          //  c.Replace();
       // }

        String finalResult = checkIfDirectResult(copyCpt);


        for (String[] e : evidance) {

           // System.out.println();
            for (CPT cpt : copyCpt) {
              //  System.out.println("----entered loop-----"+Arrays.deepToString(cpt.getCpt().get(0)));
                if ((asList(cpt.nameOfN).contains(e[0] + " ")||asList(cpt.nameOfN).contains(e[0]))&&cpt.getCpt().size()!=0) {
                    String nodeName = e[0];
                    String value = e[1];
                    int index = 0;
                    for (int j = 0; j < cpt.nameOfN.length; j++) {
                        if (cpt.nameOfN[j].equals(nodeName + " ")||cpt.nameOfN[j].equals(nodeName)) {
                            index = j;
                        }
                    }
                    for (int j = 0; j < cpt.getCpt().size(); j++) {
                        if (!cpt.getCpt().get(j)[index].equals(value)) {
                            cpt.getCpt().remove(j);

                            j--;
                        }
                    }
                    //remove the the entry that contains
                    for (int j = 0; j < cpt.getCpt().size(); j++) {
                        List<String> current = new ArrayList<String>(asList(cpt.getCpt().get(j)));
                        current.remove(index);
                        current.toArray(cpt.getCpt().get(j));
                    }
                    //remove the index column

                    String[] newFirstLine = new String[cpt.getFirstline().length - 1];
                    int runner = 0;
                    for (int i = 0; i < cpt.getFirstline().length; i++) {
                        if (i != index) {
                            newFirstLine[runner] = cpt.getFirstline()[i];
                            runner++;

                        }
                    }
                    newFirstLine[newFirstLine.length - 1] = "P=";
                    cpt.setFirstline(newFirstLine);
                }
            }
        }

        for (int i = 0; i < copyCpt.size(); i++) {
            if (copyCpt.get(i).getFirstline().length == 1) {
                copyCpt.remove(i);
                i--;
            }
        }

        if (!finalResult.isEmpty()) {
            BigDecimal result1 = new BigDecimal(finalResult);
            String result = result1.setScale(5, HALF_UP).toString() + "," + numOfSum + "," + numOfmultply;
            return result;
        } else {

            for (int i = 0; i < hidden.size(); i++) {
                PqCpt = new PriorityQueue<>();
                String currentHidden = hidden.get(i);
                for (int h = 0; h < copyCpt.size(); h++) {
                    for (int j = 0; j < copyCpt.get(h).nameOfN.length - 1; j++) {

                        if (copyCpt.get(h).nameOfN[j].equals(currentHidden)) {
                            PqCpt.add(copyCpt.get(h));
                            copyCpt.remove(h);
                            h--;
                            break;
                        }
                    }
                }
                while (PqCpt.size() > 1) {

                    CPT cpt1 = PqCpt.poll();
                    CPT cpt2 = PqCpt.poll();
                    CPT cptToAdd=join(cpt1,cpt2);
                 //  cptToAdd.Replace();
                    PqCpt.add(cptToAdd);

                }
                CPT catBeforeElimination = PqCpt.poll();
              //  catBeforeElimination.Replace();
                CPT cptAfterElimination = elimination(catBeforeElimination, currentHidden);

           if (catBeforeElimination!=null)
                copyCpt.add(cptAfterElimination);
            }
            PriorityQueue<CPT> pqAfterElimination = new PriorityQueue<>();
            for (CPT cpt : copyCpt) {
                if (hidden.contains(cpt.getFirstline()[0])||cpt.getFirstline()[0].equals(resultOf.get(0)[0])) {
                    pqAfterElimination.add(cpt);
                }
                else {numOfSum++;}
            }
            while (pqAfterElimination.size() > 1) {
                //pqAfterElimination.remove();
                CPT cpt1 = pqAfterElimination.poll();
                CPT cpt2 = pqAfterElimination.poll();
             //  if ((cpt1.getCpt().size()!=0&&cpt2.getCpt().size()!=0)&&cpt1.getFirstline().length>1&&cpt2.getFirstline().length>1){
                CPT cptToAdd = join(cpt1, cpt2);
                pqAfterElimination.add(cptToAdd);//}
           //    else if (cpt1.getCpt().size()!=0&&cpt1.getFirstline().length<=1){

                //   pqAfterElimination.add(cpt1);
              //  }
            //   else if (cpt2.getCpt().size()!=0&&cpt2.getFirstline().length<=1){
           //         pqAfterElimination.add(cpt2);
          //      }

          }
            BigDecimal result3 = new BigDecimal("0");

    CPT cptFinal = pqAfterElimination.poll();

    //  for (String last[] : cptFinal.getCpt()) {
    // System.out.println(Arrays.toString(last));
    // }

    BigDecimal mone = new BigDecimal("0");
    BigDecimal mechane = new BigDecimal("0");
    String resultOf2 = resultOf.get(0)[0];
    String valueOf = resultOf.get(0)[1];
    for (String[] s : cptFinal.getCpt()) {
        if (getIndex(cptFinal.getFirstline(), resultOf2)!=-1&&s[getIndex(cptFinal.getFirstline(), resultOf2)].equals(valueOf)) {
            BigDecimal current = new BigDecimal(s[getIndex(cptFinal.getFirstline(), "P=")]);
            mone = mone.add(current);
           numOfSum++;
        } else {
            BigDecimal down = new BigDecimal(s[getIndex(cptFinal.getFirstline(), "P=")]);
            mechane = mechane.add(down);
        }
    }

    mechane = mechane.add(mone);
    MathContext canDivde = new MathContext(5, HALF_UP);


    result3 = mone.divide(mechane, canDivde);

    String result = result3.setScale(5, HALF_UP).toString() + "," + numOfSum + "," + numOfmultply;

                return result;


        }


    }

    private String checkIfDirectResult(ArrayList<CPT> copyCpt) {
        String answer = "";
        ArrayList<CPT> containsTheResult = new ArrayList<>();
        for (int i = 0; i < copyCpt.size(); i++) {
            for (String[] resultOf : resultOf) {
                if (copyCpt.get(i).nameOfN[copyCpt.get(i).nameOfN.length - 2].equals(resultOf[0])) {
                    containsTheResult.add(copyCpt.get(i));
                }
            }
        }
        boolean isFound = false;
        int indexOfStrightAnswer = -1;
        for (int i = 0; i < containsTheResult.size(); i++) {
            for (String[] e : evidance) {
                for (int j = 0; j < containsTheResult.get(i).nameOfN.length - 2; j++) {
                    if (e[0].equals(containsTheResult.get(i).nameOfN[j])) {
                        isFound = true;
                        break;
                    } else {
                        isFound = false;
                    }
                }
            }
            indexOfStrightAnswer = i;
        }
        CPT theTableContainResult = containsTheResult.get(indexOfStrightAnswer);
        for (String[] entry : theTableContainResult.getCpt()) {
            boolean theCorrectLine = false;
            String valueOfQuery = resultOf.get(0)[1];
            if (entry[entry.length - 2].equals(valueOfQuery)&&!evidance.get(0)[0].equals("")) {
                for (String[] e : evidance) {
                    String name = e[0];
                    String value = e[1];
                    if (getIndex(theTableContainResult.nameOfN, name) != -1) {
                        if (entry[getIndex(theTableContainResult.nameOfN, name)].equals(value)) {
                            theCorrectLine = true;
                        }
                    } else {
                        theCorrectLine = false;
                    }
                }
            }
            if (theCorrectLine) {
                answer = entry[entry.length - 1];
                break;
            }

        }
        return answer;

    }
    private boolean isAncestor(String varName){
        Queue<NodeBayes> nextNodes = new LinkedList<>();
        NodeBayes firstNode = (NodeBayes) this.getNodeByName(varName);
        nextNodes.add(firstNode);
        while (!nextNodes.isEmpty()){
            NodeBayes curr_node = nextNodes.remove();
            String name = curr_node.get_name();
            ArrayList<String> evidenceVars=new ArrayList<>();
            if(this.evidance != null) {
                for (int i =0 ;i<evidance.size();i++) {
                    evidenceVars.add(evidance.get(i)[0]);
                }
            }
            if(name.equals(resultOf.get(0)[0]) || evidenceVars.contains(name)){return true;}
            else if(!curr_node.getChildrens().isEmpty()){
                for(NodeBayes kid: curr_node.getChildrens()){nextNodes.add(kid);}
            }
        }
        return false;
    }
    private ArrayList<String> getRelevant(){

        // start with all the variables
        ArrayList<String> variables = this.getVariablesNames();
        ArrayList<String> not_relevant_variables = new ArrayList<>();
        for (String varName : variables){


            if (varName.equals(this.resultOf.get(0)[0])){continue;}


            ArrayList<String> evidenceVars = new ArrayList<>();
            if(this.evidance!= null&&evidance.get(0)[0]!=""){
                for (int i =0 ;i<evidance.size();i++) {
                    evidenceVars.add(evidance.get(i)[0]);
                }
                if (evidenceVars.contains(varName)){continue;}
            }

            if (isAncestor(varName) == false){
                not_relevant_variables.add(varName);
                continue;
            }

            // ancestor variables which is not dependant with the query variable not remains in the list
            //BayesBallAlgorithem b = new BayesBallAlgorithem(this, varName, resultOf.get(0), evidenceVars);

            if (b.BayesBallAlgorithm(this, varName, resultOf.get(0)[0], evidenceVars)== "yes"){
                not_relevant_variables.add(varName);
                //variables.remove(varName);
            }
        }

        // remove all the not relevant vars from the list
        if(!not_relevant_variables.isEmpty()){
            for(String not_relevant_var : not_relevant_variables){
                variables.remove(not_relevant_var);
            }
        }

        // return the relevant variables
        String[] relevant = variables.toArray(new String[0]);
        ArrayList<String> r=new ArrayList<String>();
        r.addAll(Arrays.asList(relevant));

        return r;
    }


    private ArrayList<String> getRelevantNodes() {//not good enough
        ArrayList<NodeBayes> whoRelevant = new ArrayList<>();
        for (String[] s : resultOf) {
            whoRelevant.add(getNodeByName(s[0]));
        }

        for (String[] s : evidance) {
            whoRelevant.add(getNodeByName(s[0]));

        }

        ArrayList<String> relevent = new ArrayList<>();



        Queue<NodeBayes> queue = new LinkedList<>();

        for (NodeBayes n : whoRelevant) {
            if (!relevent.contains(n.get_name())) {
                queue.add(n);
                relevent.add(n.get_name());
                while (!queue.isEmpty()) {
                    NodeBayes current = queue.poll();
                    for (NodeBayes p : current.getParents()) {
                        if (!relevent.contains(p.get_name())) {
                            (queue).add(p);
                            relevent.add(p.get_name());
                        }
                    }
                }
            }
        }
//Collections.reverse(relevent);
        return relevent;
    }

    public CPT join(CPT cpt1, CPT cpt2) {

        String[] newFirstLine = CreateNewName(cpt1.nameOfN, cpt2.getFirstline());
        CPT newCpt = joinCpt(newFirstLine, cpt1, cpt2);
        newCpt.setFirstline(newFirstLine);

        return newCpt;
    }

    private ArrayList<CPT> getCopyOfCpt(ArrayList<String> relevent) {
        ArrayList<CPT> copyCpt = new ArrayList<>();

        for (NodeBayes n : this.get_nodes()) {
            if (relevent.contains(n.get_name())) {
                CPT newCpt = new CPT(n.getCpt().nameOfN.length);
                newCpt.setFirstline(n.getCpt().getFirstline());
                for (String[] s : n.getCpt().getCpt()) {
                    String[] newEntry = new String[s.length];
                    System.arraycopy(s, 0, newEntry, 0, s.length);
                    newCpt.getCpt().add(newEntry);
                }
                copyCpt.add(newCpt);
            }
        }
        return copyCpt;
    }

    public static String[] CreateNewName(String[] firstline1, String[] firstline2) {
        int sizeOfFirstLine1 = firstline1.length;
        int sizeOfFirstLine2 = firstline2.length;
        String[] newFirstLine = new String[1];

        if (sizeOfFirstLine1 > sizeOfFirstLine2) {

            newFirstLine = combineFirstLine(firstline1, firstline2);

        } else if (sizeOfFirstLine2 > sizeOfFirstLine1) {

            newFirstLine = combineFirstLine(firstline1, firstline2);

        } else {
            newFirstLine = combineFirstLine(firstline1, firstline2);

        }
        //  System.out.println(Arrays.toString(newFirstLine)+" the new first line");
        return newFirstLine;
    }

    private static String[] combineFirstLine(String[] firstLine1, String[] firstLine2) {

        ArrayList<String> common = new ArrayList<>();
        common.addAll(Arrays.asList(firstLine1).subList(0, firstLine1.length - 1));
        for (int i = 0; i < firstLine2.length; i++) {
            if (!common.contains(firstLine2[i]) && !firstLine2[i].equals("P=")) {
                common.add(firstLine2[i]);
            }
        }
        String[] newFirstLine = new String[common.size() + 1];
        newFirstLine[newFirstLine.length - 1] = "P=";
        int runner = 0;
        for (String curent : common) {
            newFirstLine[runner] = curent;
            runner++;
        }
        return newFirstLine;
    }

    //get 2 cpt table and join them
    public static CPT joinCpt(String[] newFirstLine, CPT cpt1, CPT cpt2) {

        CPT newCpt = new CPT(newFirstLine.length);
        ArrayList<String> commonNames = getCommonVaribles(cpt1.getFirstline(), cpt2.getFirstline());
      //  cpt1.Replace();
      //  cpt2.Replace();
        ArrayList<String[]> cptTable1 = cpt1.getCpt();
        ArrayList<String[]> cptTable2 = cpt2.getCpt();

        for (int i = 0; i < cptTable1.size(); i++) {
            for (int j = 0; j < cptTable2.size(); j++) {
                boolean sameValue = false;
                for (String common : commonNames) {
                    if (cptTable1.get(i)[getIndex(cpt1.nameOfN, common)].equals(cptTable2.get(j)[getIndex(cpt2.getFirstline(), common)])) {
                        sameValue = true;
                    } else sameValue = false;
                }
                if (sameValue == true) {
                    String[] newLine = new String[newFirstLine.length];
                    for (int k = 0; k < newLine.length - 1; k++) {
                        String current = newFirstLine[k];
                        int index = getIndex(cpt1.getFirstline(), current);
                        if (index != -1) {
                            newLine[k] = cpt1.getCpt().get(i)[index];
                        } else {
                            int index2 = getIndex(cpt2.getFirstline(), current);
                            newLine[k] = cpt2.getCpt().get(j)[index2];
                        }
                    }
                    BigDecimal cpt1P = new BigDecimal(cpt1.getCpt().get(i)[cpt1.getFirstline().length - 1]);
                    BigDecimal cpt2P = new BigDecimal(cpt2.getCpt().get(j)[cpt2.getFirstline().length - 1]);
                    BigDecimal result = cpt1P.multiply(cpt2P);
                    numOfmultply++;


                    newLine[newLine.length - 1] = result.toString();
                    newCpt.getCpt().add(newLine);
                    newCpt.setFirstline(newFirstLine);


                }
            }
        }
        return newCpt;
    }

    //Check the common names in the first line
    private static ArrayList<String> getCommonVaribles(String[] f1, String[] f2) {
        ArrayList<String> commonNames = new ArrayList<>();
        for (int i = 0; i < f1.length - 1; i++) {
            String currentFirstLine1 = f1[i];
            for (int j = 0; j < f2.length - 1; j++) {
                if (f2[j].equals(currentFirstLine1)) {
                    commonNames.add(currentFirstLine1);
                }
            }
        }

        return commonNames;
    }

    public CPT elimination(CPT cptBeforElimination, String variable) {
        //change the firstLine of the cpt
        //cptBeforElimination.Replace();

        String[] newFirstLine = new String[cptBeforElimination.getFirstline().length - 1];
        int runner = 0;
        for (int i = 0; i < cptBeforElimination.getFirstline().length; i++) {
            if (!cptBeforElimination.getFirstline()[i].equals(variable)) {
                newFirstLine[runner] = cptBeforElimination.getFirstline()[i];
                runner++;
            }
        }
        CPT cptAfterElimination = new CPT(newFirstLine.length);
        cptAfterElimination.setFirstline(newFirstLine);

        ArrayList<Integer> sumOf = getWhoToSum(cptBeforElimination.getFirstline(), variable);
        ArrayList<Integer> linesThatEdded = new ArrayList<>();

        for (int i = 0; i < cptBeforElimination.getCpt().size(); i++) {
            ArrayList<String[]> toCheckIfEquals = new ArrayList<String[]>();
            boolean equalsLines = true;
            BigDecimal sum = new BigDecimal(cptBeforElimination.getCpt().get(i)[cptBeforElimination.getFirstline().length-1]);
            if (!linesThatEdded.contains(i)) {
                linesThatEdded.add(i);

                for (int index : sumOf) {
                    String[] current = new String[2];
                    current[0] = Integer.toString(index);
                    current[1] = cptBeforElimination.getCpt().get(i)[index];
                    toCheckIfEquals.add(current);
                }
                for (int j = 1; j < cptBeforElimination.getCpt().size(); j++) {
                    if (!linesThatEdded.contains(j) && i != j) {
                        equalsLines = true;
                        for (String[] values : toCheckIfEquals) {
                            if (!cptBeforElimination.getCpt().get(j)[Integer.parseInt(values[0])].equals(values[1]))
                                equalsLines = false;
                        }
                        if (equalsLines) {
                            linesThatEdded.add(j);

                            BigDecimal propToAdd = new BigDecimal(cptBeforElimination.getCpt().get(j)[cptBeforElimination.getFirstline().length-1]);
                            sum = sum.add(propToAdd);
                            numOfSum++;
                        }

                    }
                }
                String[] newEntry = new String[newFirstLine.length];
                int runner2 = 0;
                for (String[] toAdd : toCheckIfEquals) {
                    newEntry[runner2] = toAdd[1];
                    runner2++;
                }
                newEntry[newEntry.length - 1] = sum.toString();
                cptAfterElimination.getCpt().add(newEntry);
            }
        }

                return cptAfterElimination;

    }

    private ArrayList<Integer> getWhoToSum(String[] firstLine, String variable) {
        ArrayList<Integer> whoToSuom = new ArrayList<>();
        for (int i = 0; i < firstLine.length - 1; i++) {
            if (!firstLine[i].equals(variable)) {
                String needToBeAdded = firstLine[i];
                int index = getIndex(firstLine, needToBeAdded);
                whoToSuom.add(index);
            }
        }
        return whoToSuom;
    }

    public ArrayList<String[]> getCheckDependcies() {
        return checkDependcies;
    }

    public ArrayList<String> getCheckPropebilties() {
        return this.checkPropebilties;
    }

    public void setCheckDependcies(ArrayList<String[]> checkDependcies) {
        this.checkDependcies = checkDependcies;
    }

    public void setCheckPropebilties(ArrayList<String> checkPropebilties) {
        this.checkPropebilties.addAll(checkPropebilties);
    }

    public String toString() {
        String ans = "";
        ans = ans + this.size() + "\n" + this._edge_count + "\n";
        for (int i = 0; i < this.size(); ++i) {
            NodeBayes cr = (NodeBayes) this._nodes.get(i);
            ans = ans + cr + "\n";
        }
        return ans;
    }

    public ArrayList<String[]> getResultOf() {
        return resultOf;
    }

    public ArrayList<String> getHidden() {
        return hidden;
    }

    public ArrayList<String[]> getEvidance() {
        return evidance;
    }

    public void setResultOf(ArrayList<String[]> resultOf) {
        this.resultOf.addAll(resultOf);
    }

    public void setHidden(ArrayList<String> hidden) {
        this.hidden.addAll(hidden);
    }

    public void setEvidance(ArrayList<String[]> evidance) {
        this.evidance.addAll(evidance);
    }
    public void print(PrintWriter out) {
        print(out, 0);
    }

    /**
     * Print this Node to the given PrintWriter at the given
     * indent level.
     */
    protected void print(PrintWriter out, int indent) {
        for (int i=0; i < indent; i++) {
            out.print(" ");
        }
        out.print("[");
        //out.print(NodeBayes.toString());

        for (int i=0; i < indent; i++) {
            out.print(" ");
        }
        out.print("]");
    }

    /**
     * Print this Node to the given PrintStream.
     */
    public void print(PrintStream out) {
        PrintWriter writer = new PrintWriter(out, true);
        print(writer);
        writer.flush();
    }

    /**
     * Print this Node to System.out.
     */
    public void print() {
        print(System.out);
    }

    /**
     * Return the string representation of this Node.
     */
    public String toString1() {
        StringWriter writer = new StringWriter();
        PrintWriter out = new PrintWriter(writer);
        print(out);
        out.flush();
        return writer.toString();
    }

}

