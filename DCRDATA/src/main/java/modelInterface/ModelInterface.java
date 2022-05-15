package modelInterface;

import models.dcrGraph.DCRGraph;
import models.jsonDCR.JsonDCR;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public interface ModelInterface {
    JsonDCR parseJsonToObject(String filename)
            throws IOException;
    DCRGraph transferToDCRGraph(JsonDCR jsonDCR)
            throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException;
    boolean projectable(DCRGraph dcrGraph, String role)
            throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException;
}
