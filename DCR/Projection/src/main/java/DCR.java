import models.dcrGraph.DCRGraph;
import models.jsonDCR.JsonDCR;
import modelInterface.ModelImp;
import projectionInterface.ProjectionImp;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;

public class DCR {
    public static void main(String[] args) throws Exception {
        ModelImp modelImp = new ModelImp();
        ProjectionImp projectionImp = new ProjectionImp();
        JsonDCR jsonDCR = modelImp.parseJsonToObject("/Projection/src/main/resources/DCR.json");
        DCRGraph dcrGraph = modelImp.transferToDCRGraph(jsonDCR);
        boolean seller2Projectable = modelImp.projectable(dcrGraph, "Seller2");
        HashSet<String> buyerSigma = projectionImp.getARolesSigma(dcrGraph, "Buyer");
        DCRGraph buyerSigmaProjection = projectionImp.sigmaProjection(dcrGraph, buyerSigma);


        System.out.println("finish...");
    }
}
