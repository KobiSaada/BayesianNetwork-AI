
import java.util.ArrayList;
import java.util.HashMap;

public class NodeBayes
{
    
    private static int _counter = 0;
    private int _id;
    private String color = "white";
    private ArrayList<NodeBayes> _ni;
    private ArrayList<NodeBayes> parents;
    private ArrayList<NodeBayes> childrens;
    private String _name;
    private boolean colored;
    private boolean discovered;
    private CPT cpt;
    private helpCpt helpCpt;
    private ArrayList<String> values;
    private ArrayList<String> copYvalues;
    private String cameFrom;
    private HashMap<String, Integer> mCachedIndex;
    public NodeBayes(){
        mCachedIndex = new HashMap<String, Integer>();
        this.set_id(_counter++);
        this.set_ni(new ArrayList());
       // this.set_name(s);
        parents = new ArrayList<>();
        childrens = new ArrayList<>();
        colored = false;
        discovered = false;
        color = "white";
        this.copYvalues=new ArrayList<>();
        cameFrom ="";
        values = new ArrayList<>();
        this.cpt = new CPT(parents.size());
        this.helpCpt =new helpCpt(this,this.parents);



    }
    public NodeBayes(NodeBayes s){
        this.values=s.getValues();
        mCachedIndex = new HashMap<String, Integer>();
        this._id=s._id++;
        this._name=s._name;
        parents = new ArrayList<>();
        childrens = s.childrens;
        colored = s.colored;
        discovered = s.discovered;
        color = s.color;
        this.copYvalues=s.copYvalues;
        cameFrom =s.cameFrom;
        cpt=new CPT(s.cpt.getCpt().size());
        this.helpCpt =s.helpCpt;


    }
    public NodeBayes(String s)
    {
        mCachedIndex = new HashMap<String, Integer>();
        this.set_id(_counter++);
        this.set_ni(new ArrayList());
        this.set_name(s);
        parents = new ArrayList<>();
        childrens = new ArrayList<>();
        colored = false;
        discovered = false;
        color = "white";
        this.copYvalues=new ArrayList<>();
        cameFrom ="";
        values = new ArrayList<>();
        this.cpt = new CPT(parents.size());
        this.helpCpt =new helpCpt(this,this.parents);


    }
    public int indexOf(String var)
    {
        Integer result = mCachedIndex.get(var);
        if (result != null)
            return mCachedIndex.get(var);

        if (var.equals(this._name))
            result = this.parents.size();
        else
            result = helpFunc().indexOf(var);

        mCachedIndex.put(var, result);
        return result;
    }
public ArrayList<String> helpFunc(){
        ArrayList<String>s=new ArrayList<>();
        for (int i =0 ;i<this.parents.size();i++){
            s.add(this.parents.get(i).get_name());}
        return s;

}
    public void fillRow(String value, int col)
    {
        this.cpt.cpt.get(this.cpt.cpt.size() - 1)[col] = value;
    }

    public void addEmptyRow()
    {
        this.cpt.cpt.add(new String[this.parents.size() + 2]);
    }

    public char get_id()
    {
        return (char)this._id;
    }

    public void set_id(int _id)
    {
        this._id = _id;
    }


    public void addParents(NodeBayes parent)
    {
        this.parents.add(parent);
    }

    public ArrayList<NodeBayes> get_ni()
    {
        return this._ni;
    }

    public void set_ni(ArrayList<NodeBayes> _ni)
    {
        this._ni = _ni;
    }


    public void add(NodeBayes n)
    {
            this._ni.add(n);

     
    }
    public boolean hasEdge(String dest){
        boolean ans = false;
        for (int i = 0; i < this._ni.size(); ++i) {
            if (dest.equals(" "+this._ni.get(i)._name));{
                ans = true;
            }
        }
        return ans;
    }


    public int degree() {
        return this._ni.size();
    }


    public String get_name() {
        return this._name;
    }

    private void set_name(String name) {this._name = name;
    }




    public String toString() {
        String ans = "Node: " + this._id + ", name, " + this._name + " ,|ni|, " + this._ni.size() ;
        return ans;
    }


    public ArrayList<NodeBayes> getParents() { return parents; }
    public ArrayList<NodeBayes> getChildrens() { return childrens; }
    public void setColored(boolean colored) { this.colored = colored; }
    public boolean isColored() { return colored; }
    public void setDiscovered(boolean discovered) { this.discovered = discovered; }
    public boolean isDiscovered() { return discovered; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public String getCameFrom() { return cameFrom; }
    public void setCameFrom(String cameFrom) { this.cameFrom = cameFrom; }
    public ArrayList<String> getValues() { return this.values; }
    public void setValues(ArrayList<String> values) { this.values = values; }
    public CPT getCpt() { return cpt; }
    public void setCpt1(helpCpt cpt) { this.helpCpt = cpt; }
    public void setCpt(CPT cpt) { this.cpt = cpt; }
    public ArrayList<String> getcopyValues(){
        return this.copYvalues;
    }
    public helpCpt getCpt1() { return helpCpt; }
}