package visualization;

import org.graphstream.graph.Element;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.ui.layout.springbox.implementations.SpringBox;
import org.graphstream.ui.swing.SwingGraphRenderer;
import org.graphstream.ui.swing_viewer.DefaultView;
import org.graphstream.ui.swing_viewer.SwingViewer;
import org.graphstream.ui.swing_viewer.ViewPanel;
import org.graphstream.ui.view.Viewer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {


    public static void main(String[] args) {
        Main main = new Main();
        SwingUtilities.invokeLater(main::display);
    }


    private void display(){
        System.setProperty("org.graphstream.ui", "swing");

        Graph sourceGraph = prepareGraph();

        Viewer viewer1 = new SwingViewer(sourceGraph, Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        viewer1.enableAutoLayout(new SpringBox());
        Viewer viewer2 = new SwingViewer(sourceGraph, Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        viewer2.enableAutoLayout(new SpringBox());
        ViewPanel viewPanel1 = new DefaultView(viewer1,"panel1",new SwingGraphRenderer());
        viewPanel1.setPreferredSize(new Dimension(750,350));
        ViewPanel viewPanel2 = new DefaultView(viewer2,"panel2",new SwingGraphRenderer());
        viewPanel2.setPreferredSize(new Dimension(750,350));


        JPanel panel1 = new JPanel();
        panel1.setBackground(Color.gray);
        panel1.setLayout(new BorderLayout());
        panel1.setPreferredSize(new Dimension(750,350));
        panel1.add(viewPanel1,BorderLayout.CENTER);

        JPanel panel2 = new JPanel();
        panel1.setBackground(Color.blue);
        panel2.setLayout(new BorderLayout());
        panel2.setPreferredSize(new Dimension(750,350));
        panel2.add(viewPanel2, BorderLayout.CENTER);

        JPanel graphPanel = new JPanel();
        graphPanel.setPreferredSize(new Dimension(1600, 600));
        graphPanel.setLayout(new BorderLayout(10,10));
        graphPanel.add(panel1,BorderLayout.WEST);
        graphPanel.add(panel2, BorderLayout.EAST);

        JComboBox<String> lCCommunitiesNames = new JComboBox<>();
        lCCommunitiesNames.setPreferredSize(new Dimension(795,30));

        JPanel lCComboPanel = new JPanel();
        lCComboPanel.setLayout(new BorderLayout());
        lCComboPanel.add(lCCommunitiesNames,BorderLayout.CENTER);


        JComboBox<String> minoltaCommunitiesNames = new JComboBox<>();
        minoltaCommunitiesNames.setPreferredSize(new Dimension(795,30));
        JPanel minoltaComboPanel = new JPanel();
        minoltaComboPanel.setLayout(new BorderLayout());
        minoltaComboPanel.add(minoltaCommunitiesNames, BorderLayout.CENTER);

        JPanel selectorPanel = new JPanel();
        selectorPanel.setLayout(new BorderLayout(10,10));
        graphPanel.setPreferredSize(new Dimension(1600, 100));
        selectorPanel.add(lCComboPanel,BorderLayout.WEST);
        selectorPanel.add(minoltaCommunitiesNames, BorderLayout.EAST);

        JFrame f=new JFrame();
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        f.setSize(new Dimension(1600,900));
        f.setLayout(new BorderLayout(200,10));
        f.add(graphPanel,BorderLayout.WEST);
        f.add(selectorPanel,BorderLayout.SOUTH);
        f.setVisible(true);

    }

    private Graph prepareGraph(){
        Graph graph = new DefaultGraph("sampleGraph");
        graph.setAutoCreate(true);
        graph.setStrict(false);
        graph.addNode("A");
        graph.addNode("B");
        graph.addNode("C");
        graph.addNode("D");
        graph.addNode("E");
        graph.addEdge("1","A","B");
        graph.addEdge("2","A","C");
        graph.addEdge("3","C","B");
        graph.addEdge("4","C","D");
        graph.addEdge("5","C","E");
        return graph;
    }
}
