package visualization;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.MultiGraph;

public class LeHavre {
    public static void main(String args[]) {
        System.setProperty("org.graphstream.ui", "swing");
        new LeHavre();
    }

    public LeHavre() {
        Graph graph = new MultiGraph("Le Havre");

        try {
            graph.read("LeHavre.dgs");
        } catch(Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        graph.display(false);   // No auto-layout.
    }
}
