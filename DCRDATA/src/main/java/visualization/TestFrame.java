package visualization;

import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.MultiGraph;

import javax.swing.*;
import java.awt.*;

public class TestFrame extends JFrame {

    public TestFrame() {
        super("Test frame");
        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        init();
    }

    private void init() {
        JPanel panelInputs = new JPanel();
        JPanel panelSide = new JPanel();
//        PanelGraph panelGraph = new PanelGraph(); // The graph is in this panel
        MultiGraph graph1 = new MultiGraph("g1");
        panelInputs.add(new JLabel("Inputs panel"));
        panelSide.add(new JLabel("Side panel"));

        this.getContentPane().setLayout(new GridBagLayout());
        this.getContentPane().add(panelInputs, new GridBagConstraints(0, 0, 3, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        this.getContentPane().add(panelSide, new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
//        this.getContentPane().add(graph1, new GridBagConstraints(1, 1, 2, 1, 2, 2, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

        this.setPreferredSize(new Dimension(1600, 900));
        this.pack();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        TestFrame frame = new TestFrame();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
