package visualization;

import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.stream.thread.ThreadProxyPipe;
import org.graphstream.ui.swing.SwingGraphRenderer;
import org.graphstream.ui.swing_viewer.DefaultView;
import org.graphstream.ui.swing_viewer.SwingViewer;
import org.graphstream.ui.view.Viewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.Scanner;

import javax.swing.*;

public class MultipleViews implements Runnable {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new MultipleViews());
    }

    @Override
    public void run() {
        JFrame frame = new JFrame("Multiple Views");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        MultiGraph graph1 = new MultiGraph("g1");
        ThreadProxyPipe pipe1 = new ThreadProxyPipe() ;
        pipe1.init(graph1);
        Viewer viewer1 = new SwingViewer(pipe1);
        graph1.addNode("a");
        graph1.setAttribute("ui.title", "Title");
        DefaultView view1 = new DefaultView(viewer1, "view1", new SwingGraphRenderer());
        viewer1.addView(view1);
        viewer1.enableAutoLayout();

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(view1, BorderLayout.CENTER);
        panel.setBackground(Color.white);
        panel.setPreferredSize(new Dimension(750, 350));
        panel.add(new JLabel("Inputs panel"), BorderLayout.BEFORE_FIRST_LINE);

        JPanel panel1 = new JPanel(new BorderLayout(5, 5));

        panel1.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panel1.add(panel, BorderLayout.CENTER);

        frame.add(panel1, BorderLayout.BEFORE_LINE_BEGINS);
        frame.add(createPanel2(), BorderLayout.AFTER_LINE_ENDS);

        frame.add(createPanel2(), BorderLayout.AFTER_LAST_LINE);

        frame.pack();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);

//        Scanner scanner = new Scanner(System.in);
//        scanner.nextLine();
//        graph1.getNode("a").setAttribute("ui.style", "fill-color: red;");
    }

    private JPanel createPanel1() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panel.add(createView1(), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createView1() {
        MultiGraph graph1 = new MultiGraph("g1");
        ThreadProxyPipe pipe1 = new ThreadProxyPipe() ;
        pipe1.init(graph1);
        Viewer viewer1 = new SwingViewer(pipe1);
        graph1.addNode("a");
        graph1.setAttribute("ui.title", "Title");
        DefaultView view1 = new DefaultView(viewer1, "view1", new SwingGraphRenderer());
        viewer1.addView(view1);
        viewer1.enableAutoLayout();

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(view1, BorderLayout.CENTER);
        panel.setBackground(Color.white);
        panel.setPreferredSize(new Dimension(750, 350));
        panel.add(new JLabel("Inputs panel"), BorderLayout.BEFORE_FIRST_LINE);
        return panel;
    }

    private JPanel createPanel2() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        panel.add(createView2(), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createView2() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.white);
        panel.setPreferredSize(new Dimension(750, 350));
        return panel;
    }

}
