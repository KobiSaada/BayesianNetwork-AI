import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class helper {
    public class Assignment extends LinkedHashMap<NodeBayes,Object> {

        public static final long serialVersionUID = 1L;

        //added constructor for making new assignment off of existing with new memory allocated
        public Assignment(Assignment a){
            super();
            for(NodeBayes rv : a.keySet()){
                this.set(rv, a.get(rv));
            }
        }

        public Assignment() {
            super();
        }


        public void set(NodeBayes var, Object val) {
            put(var, val);
        }


        public Set<NodeBayes> variableSet() {
            return keySet();
        }


        public Assignment copy() {
            return (Assignment)this.clone();
        }

        public String toString() {
            StringBuilder buf = new StringBuilder();
            for (Map.Entry<NodeBayes,Object> entry : entrySet()) {
                if (buf.length() > 0) {
                    buf.append(",");
                }
                buf.append(entry.getKey());
                buf.append("=");
                buf.append(entry.getValue());
            }
            return buf.toString();
        }
    }

}
