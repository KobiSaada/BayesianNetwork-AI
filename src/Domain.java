import java.util.ArrayList;
import java.util.Collection;
public class Domain extends ArrayList<Object> {

    public static final long serialVersionUID = 1L;

    public Domain() {
        super();
    }

    public Domain(int size) {
        super(size);
    }

    public Domain(Object... elements) {
        this();
        for (Object o : elements) {
            add(o);
        }
    }

    public Domain(Collection<Object> collection) {
        this();
        for (Object o : collection) {
            add(o);
        }
    }
}