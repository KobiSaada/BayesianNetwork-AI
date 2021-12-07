import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

public class helpCpt {

    /**
     * Parent class of entries in a CPT.
     */
    abstract public class Entry {
        abstract public void print(PrintWriter out, String prefix);
    }

    /**
     * Each dimension of a CPT corresponds to a RandomVariable and
     * contains a set of Entries, one for each value of the variable.
     */
    public class Dimension extends Entry {
        public NodeBayes variable;
       public String[] nameOfN;//the name
       public ArrayList<Entry> cpt;
        public Map<Object,Entry> entries;
        public Dimension(NodeBayes variable) {
            this.variable = variable;
            nameOfN = new String[variable.getValues().size() + 2];
            this.cpt = new ArrayList<>();
           // Domain domain = variable.get_name();
            this.entries = new LinkedHashMap<Object,Entry>(variable.getValues().size());
        }
        /**
         * Print this CPT Dimension to the given PrintWriter.
         */
        public void print(PrintWriter out, String prefix) {
            String newprefix="";
            for (Map.Entry<Object,Entry> entry : entries.entrySet()) {
                newprefix =prefix+entry.getKey()+ "\t";
                entry.getValue().print(out, newprefix);
            }

        }

    }

    /**
     * The terminal Entries in a CPT contain the probability value
     * corresponding to the values on the path from the root to the
     * Entry.
     */
    public class ProbabilityValue extends Entry {
        public String value;
        public ProbabilityValue(String value) {
            this.value = value;
        }
        /**
         * Print this CPT ProbabilityValue to the given PrintWriter.
         */
        public void print(PrintWriter out, String prefix) {
            out.print(prefix);
            out.println(value);
        }
    }

    /**
     * The Entry at the root of this CPT.
     */
    protected Entry root;

    /**
     * Construct and return a new CPT representing the possible combinations
     * of values of the given query and <q>given</q> RandomVariables.
     * This CPT is <q>empty</q> in the sense of having no probability
     * values at the leaves.
     */
    public helpCpt(NodeBayes query, List<NodeBayes> givens) {
        root = init(query, givens, 0);
    }

    /**
     * Returns an Entry for the index'th given variable (if index &lt;
     * number of givens, in which case the sub-entries are created
     * recursively), and otherwise are ProbabilityValues storing the
     * values of the query variable. The result is a <q>tree</q> whose
     * levels enumerate the values of the given variables in order,
     * followed by values of the query variables, as described in the
     * comment for the CPT class.
     */
    protected Entry init(NodeBayes query, List<NodeBayes> givens, int index) {
        if (index < givens.size()) {
            // Add a Dimension for the current given variable
            NodeBayes given = givens.get(index);
            Dimension dimen = new Dimension(given);
            for (Object value : given.getValues()) {
                Entry entry = init(query, givens, index+1);
                dimen.entries.put(value, entry);
                dimen.cpt.add(entry);

            }
            return dimen;
        } else {
            // Done givens; add ProbabilityValues for the query var's values
            Dimension dimen = new Dimension(query);
            for (Object value : query.getValues()) {
                ProbabilityValue p = new ProbabilityValue("0.0");
                dimen.entries.put(value, p);
               // dimen.cpt.add(value);
            }
            return dimen;
        }
    }

    /**
     * Return the descendent of the given Entry identified by the given
     * sequence of values, starting from the given index.
     * @throws NoSuchElementException if the values don't represent a path to a Entry.
     */
    protected ProbabilityValue find(Entry entry, helper.Assignment assignment) throws NoSuchElementException {
        if (entry == null) {
            throw new NoSuchElementException();
        } else if (entry instanceof ProbabilityValue) {
            return (ProbabilityValue)entry;
        } else {
            Dimension dimen = (Dimension)entry;
            NodeBayes var = dimen.variable;
            Object value = assignment.get(var);
            if (value == null) {
                throw new NoSuchElementException();
            } else {
                entry = dimen.entries.get(value);
                if (entry == null) {
                    throw new NoSuchElementException();
                } else {
                    return find(entry, assignment);
                }
            }
        }
    }

    /**
     * Stores the given probability in this CPT for the combination
     * values for variables repesented by the given Assignment.
     * @throws NoSuchElementException if the values don't represent a path to a ProbabilityValue.
     */
    public void set(helper.Assignment e, String p) {
        ProbabilityValue pv = find(root, e);
        if (pv != null) {
            pv.value = p;
        } else {
            throw new NoSuchElementException();
        }
    }

    /**
     * Returns the probability stored in this CPT for the combination
     * of values for variables repesented by the given Assignment.
     * @throws NoSuchElementException if the values don't represent a path to a ProbabilityValue.
     * @return
     */
    public String get(helper.Assignment e) throws NoSuchElementException {
        ProbabilityValue pv = find(root, e);
        if (pv != null) {
            return pv.value;
        } else {
            throw new NoSuchElementException();
        }
    }

    /**
     * Returns a depth-first Iterator over the ProbabilityValues of this CPT
     * (ie., over the terminal entries that contain the probability values).
     * <p>
     * This routine is entirely for populating a CPT from an XMLBIF
     * representation of a Bayesian network. Specifically, it enumerates
     * the given (conditioning) variables first, in the order they were
     * added to the variable's domain (i.e., the order of the {@code outcome}
     * elements in the XMLBIF {@code variable} element). It finishes by
     * enumerating the possible values of the query variable. For example,
     * suppose variable A has three values A1, A2, and A3, variable B has values
     * B1 and B2, and variable C has four values C1, C2, C3, C4.
     * The CPT for the conditional probability distribution P(A|B,C) would
     * be enumerated as follows:
     * <pre>
     B1                                        B2
     C1        C2        C3        C4          C1        C2        C3        C4
     A1 A2 A3  A1 A2 A3  A1 A2 A3  A1 A2 A3    A1 A2 A3  A1 A2 A3  A1 A2 A3  A1 A2 A3
     * </pre>
     * This matches the ordering used in the XMLBIF {@code table} element
     * inside a {@code definition} element.
     */
    public Iterator<ProbabilityValue> valueIterator() {
        final Stack<Entry> stack = new Stack<Entry>();
        stack.push(root);
        return new Iterator<ProbabilityValue>() {
            public boolean hasNext() {
                return !stack.isEmpty();
            }
            public ProbabilityValue next() throws NoSuchElementException {
                while (true) {
                    Entry entry = stack.pop();
                    if (entry instanceof ProbabilityValue) {
                        return (ProbabilityValue)entry;
                    } else {
                        Dimension dimen = (Dimension)entry;
                        Collection<Entry> entries = dimen.entries.values();
                        // Push in reverse order (to preserve DFS ordering)
                        reversePush(stack, entries.iterator());
                    }
                }
            }
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    /**
     * Push the elements returned by the given Iterator onto the given
     * Stack in reverse order. We just store the elements on the runtime
     * stack and then push them onto the given stack as the recursion
     * unwinds. How Lispilicious!
     */
    protected void reversePush(Stack<Entry> stack, Iterator<Entry> elements) {
        if (elements.hasNext()) {
            Entry element = elements.next();
            reversePush(stack, elements);
            stack.push(element);
        }
    }

    // Printable

    /**
     * Print this CPT to the given PrintWriter.
     */
    public void print(PrintWriter out) {
        root.print(out, "");
    }

    /**
     * Print this CPT to the given PrintStream.
     */
    public void print(PrintStream out) {
        PrintWriter writer = new PrintWriter(out, true);
        print(writer);
        writer.flush();
    }

    /**
     * Print this CPT to System.out.
     */
    public void print() {
        print(System.out);
    }

    /**
     * Return the string representation of this CPT.
     */
    public String toString() {
        StringWriter writer = new StringWriter();
        PrintWriter out = new PrintWriter(writer);
        print(out);
        out.flush();
        return writer.toString();
    }

    // Testing

    protected void trace(String msg) {
        //System.err.println(msg);
    }
}
