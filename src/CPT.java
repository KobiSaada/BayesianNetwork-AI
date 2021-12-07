import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class CPT implements Comparable<CPT> {

    String[] nameOfN;//the name
    ArrayList<String[]> cpt;

    public CPT(int size) {
        nameOfN = new String[size + 2];
        this.cpt = new ArrayList<>();
    }
    public void Replac(){

    if (this.cpt.size()==8) {
        for (int j = 2; j < this.cpt.size() - 1; j += 3) {
            int y = this.cpt.get(j).length - 1;
            if (j % 2 == 0) {
                // Collections.swap(Arrays.asList(this.cpt.get(j).clone()), y, this.cpt.get(j+1)..clone());


                String e = this.cpt.get(j)[y];
                this.cpt.get(j)[y] = this.cpt.get(j + 2)[y];
                this.cpt.get(j + 2)[y] = e;
            }

            //      System.out.println((s.get(j-1).toString()));
            //    System.out.println((s.get(j).toString()));


        }

        int k = this.cpt.size() - 3;
        int y = this.getCpt().get(k).length - 1;
        String e1 = this.cpt.get(k)[y];
        this.cpt.get(k)[y] = this.cpt.get(k - 2)[y];
        this.cpt.get(k - 2)[y] = e1;
    }

    }
    public void printCpt(){
       // System.out.println(this.cpt);
        for (int i=0;i<this.cpt.size();i++){
            System.out.println(Arrays.deepToString(this.cpt.get(i)));
            
            
        }
    }
    public ArrayList<String[]> getCpt() { return cpt; }
    public void setCpt(ArrayList<String[]> cpt) {
        this.cpt.addAll(cpt); }
    public String[] getFirstline() { return nameOfN; }

    public void setFirstline(String[] firstline) { this.nameOfN = firstline; }
    public void removCpt( ){
        this.cpt.remove(this);

    }


    @Override
    public int compareTo(CPT o) {
        return ((Integer)this.getCpt().size()).compareTo(o.getCpt().size());
    }
}
