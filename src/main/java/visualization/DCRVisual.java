package visualization;

import models.dcrGraph.DCRGraph;
import models.jsonDCR.timeRelationship.*;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;

import java.lang.reflect.InvocationTargetException;

public class DCRVisual {
    String title;
    public DCRGraph dcrGraph;
    public MultiGraph visualGraph;

    public DCRVisual(DCRGraph dcrGraph, String title) throws
            ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        this.dcrGraph = dcrGraph;
        this.title = title;
        init();
    }

    private void init()
            throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        // init the graph and set the title.
        System.setProperty("org.graphstream.ui", "swing");
        visualGraph = new MultiGraph(title);
        visualGraph.setAttribute("ui.quality");
        visualGraph.setAttribute("ui.title", title);

        // add all the interactions as nodes.
        for (String interaction : dcrGraph.getEvents()) {
            visualGraph.addNode(interaction);
            Node node = visualGraph.getNode(interaction);

//            System.out.println(title + " before:" + node.getAttribute("ui.style"));
//            node.setAttribute("ui.style",
//                    "size-mode: fit;" +
//                            "shape: box;" +
//                            // "fill-color: white;" +
//                            "text-color: black;" +
//                            "text-size: 15;" +
//                            "text-background-mode: none;" +
//                            "fill-mode: none;");
            // markings.
            setAnInteractionsLabel(interaction);

        }

        // add all relationships as edges.

        // condition edges.
        for (String key : dcrGraph.getTimeConditions().keySet()) {
            for (TimeCondition timeCondition : dcrGraph.getTimeConditions().get(key)) {
                addConditionEdge(key, timeCondition);

            }
        }

        for (String key : dcrGraph.getTimeResponses().keySet()) {
            for (TimeResponse timeResponse : dcrGraph.getTimeResponses().get(key)) {
                addResponseEdge(key, timeResponse);

            }
        }
        for (String key : dcrGraph.getTimeMilestones().keySet()) {
            for (TimeMilestone timeMilestone : dcrGraph.getTimeMilestones().get(key)) {
                addMilestoneEdge(key, timeMilestone);

            }
        }
        for (String key : dcrGraph.getTimeInclusions().keySet()) {
            for (TimeInclusion timeInclusion : dcrGraph.getTimeInclusions().get(key)) {
                addInclusionEdge(key, timeInclusion);

            }
        }
        for (String key : dcrGraph.getTimeExclusions().keySet()) {
            for (TimeExclusion timeExclusion : dcrGraph.getTimeExclusions().get(key)) {
                addExclusionEdge(key, timeExclusion);

            }
        }
    }
    public void display(){
        visualGraph.display();
    }

    public void updateMarkings()
            throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        for (String interaction : dcrGraph.getEvents()) {
            // markings.
            setAnInteractionsLabel(interaction);
        }
    }

    private void setAnInteractionsLabel(String interaction)
            throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Node node = visualGraph.getNode(interaction);
        String property = "size-mode: fit;" +
                "shape: box;" +
                // "fill-color: white;" +
                "text-color: black;" +
                "text-size: 15;" +
                "text-background-mode: none;" +
                "text-style: bold;"
                ;
        String interactionWithMarking = " ";
        // sender or receiver.
        if (dcrGraph.getEventsReceivers().get(interaction).contains(title)) {
            interactionWithMarking += "?";
        } else
            interactionWithMarking += "!";
//        interactionWithMarking += interaction + "(";
        interactionWithMarking += interaction;
        if (dcrGraph.getDcrMarking().executed.contains(interaction)) {
//            interactionWithMarking += "x:1,";
            property += "fill-mode: plain; fill-color: yellow;";
//            node.setAttribute("ui.style", "fill-mode: plain; fill-color: yellow;");
        } else
//            interactionWithMarking += "x:0,";
        if (dcrGraph.getDcrMarking().pending.contains(interaction)) {
//            interactionWithMarking += "p:1,";
//            interactionWithMarking += "PENDING";
            property += "text-color: red;";
        } else{
        }
//            interactionWithMarking += "p:0,";
        if (dcrGraph.getDcrMarking().included.contains(interaction)) {
//            interactionWithMarking += "i:1";
            property += "stroke-mode: plain; stroke-color: black; stroke-width: 2;";
//            node.setAttribute("ui.style", "stroke-mode: dashes; stroke-color: black; stroke-width: 2;");
        } else{
//            interactionWithMarking += "i:0";
//            node.setAttribute("ui.style", "stroke-mode: plain; stroke-color: black; stroke-width: 2;");
            property += "stroke-mode: dashes; stroke-color: black; stroke-width: 2;";
        }
//        interactionWithMarking += ")";
        if (!property.contains("fill-mode")){
            property += "fill-mode: none;";
        }
        if (dcrGraph.enabled(interaction)){
            interactionWithMarking += "(enabled)";
        }
        node.setAttribute("ui.label", interactionWithMarking);
        node.setAttribute("ui.style", property);
    }

    private void addConditionEdge(String key, TimeCondition timeCondition) {
        String edgeIdentity = key + "$" + timeCondition.getTo() + "$" + "Condition";
        visualGraph.addEdge(edgeIdentity, key, timeCondition.getTo(), true);
        Edge edge = visualGraph.getEdge(edgeIdentity);
        // edge.setAttribute("ui.label", timeCondition.getCondition());
        edge.setAttribute("ui.style",
                "shape: angle;" +
                        "fill-color: rgb(255,204,0);" + // deep yellow.
                        "arrow-shape: circle; " + // arrow shape is a circle.
                        "arrow-size: 10px, 10px; " + // arrow size.
                        "size: 2px;" +
                        "text-color: rgb(255,204,0);" +
                        "text-alignment: along;" +
                        "text-size: 15;"); // edge width.
    }

    private void addResponseEdge(String key, TimeResponse timeResponse) {
        String edgeIdentity = key + "$" + timeResponse.getTo() + "$" + "Response";
        visualGraph.addEdge(edgeIdentity, key, timeResponse.getTo(), true);
        Edge edge = visualGraph.getEdge(edgeIdentity);
        // edge.setAttribute("ui.label", timeResponse.getCondition());
        edge.setAttribute("ui.style",
                "fill-color: rgb(135,206,235);" + // deep yellow.
                        "arrow-shape: circle; " + // arrow shape is a circle.
                        "arrow-size: 10px, 10px; " + // arrow size.
                        "size: 2px;" +
                        "text-color: blue;" +
                        "text-alignment: along;" +
                        "text-size: 15;"); // edge width.
    }

    private void addMilestoneEdge(String key, TimeMilestone timeMilestone) {
        String edgeIdentity = key + "$" + timeMilestone.getTo() + "$" + "Milestone";
        visualGraph.addEdge(edgeIdentity, key, timeMilestone.getTo(), true);
        Edge edge = visualGraph.getEdge(edgeIdentity);
        // edge.setAttribute("ui.label", timeMilestone.getCondition());
        edge.setAttribute("ui.style",
                "fill-color: rgb(153,51,250);" + // deep yellow.
                        "arrow-shape: diamond; " + // arrow shape is a circle.
                        "arrow-size: 10px, 5px; " + // arrow size.
                        "size: 2px;" +
                        "text-color: purple;" +
                        "text-alignment: along;" +
                        "text-size: 15;"); // edge width.
    }

    private void addInclusionEdge(String key, TimeInclusion timeInclusion) {
        String edgeIdentity = key + "$" + timeInclusion.getTo() + "$" + "Inclusion";
        visualGraph.addEdge(edgeIdentity, key, timeInclusion.getTo(), true);
        Edge edge = visualGraph.getEdge(edgeIdentity);
        // edge.setAttribute("ui.label", timeInclusion.getCondition());
        edge.setAttribute("ui.style",
                "fill-color: rgb(0,201,87);" + // deep yellow.
                        "arrow-shape: arrow; " + // arrow shape is a circle.
                        "arrow-size: 10px, 5px; " + // arrow size.
                        "size: 2px;" +
                        "text-color: green;" +
                        "text-alignment: along;" +
                        "text-size: 15;"); // edge width.
    }

    private void addExclusionEdge(String key, TimeExclusion timeExclusion) {
        String edgeIdentity = key + "$" + timeExclusion.getTo() + "$" + "Exclusion";
        visualGraph.addEdge(edgeIdentity, key, timeExclusion.getTo(), true);
        Edge edge = visualGraph.getEdge(edgeIdentity);
        // edge.setAttribute("ui.label", timeExclusion.getCondition());
        edge.setAttribute("ui.style",
                "fill-color: red;" + // deep yellow.
                        "arrow-shape: arrow; " + // arrow shape is a circle.
                        "arrow-size: 10px, 5px; " + // arrow size.
                        "size: 2px;" +
                        "text-color: red;" +
                        "text-alignment: along;" +
                        "text-size: 15;"); // edge width.
    }

}
