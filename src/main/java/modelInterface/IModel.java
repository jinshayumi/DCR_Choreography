package modelInterface;

import models.dcrGraph.DCRGraph;
import models.jsonDCR.JsonDCR;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public interface IModel {
    // Deserialize JSON file into Object of JsonDCR.
    JsonDCR parseJsonToObject(String filename)
            throws IOException;
    // Transform the deserialized object to DCR choreography.
    DCRGraph transferToDCRGraph(JsonDCR jsonDCR)
            throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException;
    // To determine if a DCR choreography is projectable for some role.
    boolean projectable(DCRGraph dcrGraph, String role)
            throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException;
}
