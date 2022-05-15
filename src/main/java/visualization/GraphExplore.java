package visualization;

import java.util.Iterator;
import java.util.Scanner;

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.graphstream.ui.spriteManager.Sprite;
import org.graphstream.ui.spriteManager.SpriteManager;

public class GraphExplore {
    public static void main(String args[]) throws InterruptedException {
        new GraphExplore();
    }

    public GraphExplore() throws InterruptedException {
        System.setProperty("org.graphstream.ui", "swing");
        Graph graph = new SingleGraph("tutorial 1");
        graph.setAttribute("ui.title", "Title");

        MultiGraph multiGraph = new MultiGraph("aaa");
        SpriteManager sman = new SpriteManager(multiGraph);

        Graph merge = Graphs.merge(graph, multiGraph);
//        merge.display();

        multiGraph.addNode("a");
        multiGraph.addNode("b");
        multiGraph.getNode("a").setAttribute("ui.style", "size: 20px, 10px; padding: 6px;");
        multiGraph.addEdge("ab", "a", "b", true);
        multiGraph.getEdge("ab").setAttribute("ui.label", "fasdfsafs;");
        multiGraph.getEdge("ab").setAttribute("ui.style",
                "fill-color: purple; arrow-shape: diamond; arrow-size: 10px, 5px; size: 3px;" +
                        " text-color: rgb(255,204,0); text-alignment: above; text-size: 15;"
                        );
//        multiGraph.getEdge("ab").setAttribute("ui.label", "fasdfsafas");
//        multiGraph.display();
        merge.display();

        Scanner sc = new Scanner(System.in);
        multiGraph.getNode("a").setAttribute("ui.label", "bbbbbbb");
        sc.nextLine();
        multiGraph.getNode("a").setAttribute("ui.label", "aaaaaaaaa");
        sc.nextLine();

//        Sprite s = sman.addSprite("S1");
//        s.attachToEdge("ab");
//        s.setPosition(0.5);
//        s.setAttribute("ui.label", "abccc");

//        graph.setAttribute("ui.stylesheet", styleSheet);
//        graph.setAutoCreate(true);
//        graph.setStrict(false);
//        graph.display();
//
//        graph.addEdge("AB", "A", "B");
//        graph.addEdge("BC", "B", "C");
//        graph.addEdge("CA", "C", "A");
//        graph.addEdge("AD", "A", "D");
//        graph.addEdge("DE", "D", "E");
//        graph.addEdge("DF", "D", "F");
//        graph.addEdge("EF", "E", "F");
//
//        for (Node node : graph) {
//            node.setAttribute("ui.label", node.getId());
//            node.setAttribute("ui.style", "size: 10px, 15px;\n" +
//                    "\tshape: circle; fill-color: rgb(0,100,255);");
//        }
//
//        explore(graph.getNode("A"));
//        Thread.sleep(2000);
//        graph.addEdge("AF", "A", "F");
        merge.display();
    }

    public void explore(Node source) {
        Iterator<? extends Node> k = source.getBreadthFirstIterator();

        while (k.hasNext()) {
            Node next = k.next();
            next.setAttribute("ui.class", "marked");
            sleep();
        }
    }

    protected void sleep() {
        try { Thread.sleep(1000); } catch (Exception e) {}
    }

    protected String styleSheet =
            "node {" +
                    "	fill-color: black;" +
                    "}" +
                    "node.marked {" +
                    "	fill-color: red;" +
                    "}";
}