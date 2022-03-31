package models.dcrGraph;

import java.util.HashSet;

public class DCRMarking{
    public HashSet<String> executed = new HashSet<>();
    public HashSet<String> included = new HashSet<>();
    public HashSet<String> pending = new HashSet<>();
}
