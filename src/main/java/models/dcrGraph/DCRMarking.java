package models.dcrGraph;

import java.io.Serializable;
import java.util.HashSet;

public class DCRMarking implements Serializable {
    public HashSet<String> executed = new HashSet<>();
    public HashSet<String> included = new HashSet<>();
    public HashSet<String> pending = new HashSet<>();
}
