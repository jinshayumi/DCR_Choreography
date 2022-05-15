import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        HashSet<List<String>> c = new HashSet<>();
        List<String> a = new ArrayList<>();
        a.add("a");
        a.add("b");
        c.add(a);

        HashSet<List<String>> d = new HashSet<>();
        List<String> b = new ArrayList<>();
        b.add("a");
        b.add("b");
        d.add(b);
        System.out.println(a.equals(b));
        System.out.println(c.equals(d));
    }
}
