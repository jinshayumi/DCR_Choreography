package dcrChoreography.endPointProjection;
import modelInterface.ModelImp;
import models.dcrGraph.DCRGraph;
import models.jsonDCR.JsonDCR;
import org.junit.Test;
import projectionInterface.ProjectionImp;

import java.util.HashSet;

import static org.junit.Assert.*;

public class TestEndPointProjection {
    private static final String testProjectionPath = "/src/main/resources/test/dcrChoreography/endPointProjection/";

    @Test
    public void testEndPointProjection10_0() throws Exception{
        ModelImp modelImp = new ModelImp();
        ProjectionImp projectionImp = new ProjectionImp();
        JsonDCR jsonDCR = modelImp.parseJsonToObject
                (testProjectionPath + "projection10_0.json");
        DCRGraph dcrGraph = modelImp.transferToDCRGraph(jsonDCR);

        assertEquals(projectionImp.getARolesSigma(dcrGraph, "role2").size(), 3);
    }

    @Test
    public void testEndPointProjection10_1() throws Exception{
        ModelImp modelImp = new ModelImp();
        ProjectionImp projectionImp = new ProjectionImp();
        JsonDCR jsonDCR = modelImp.parseJsonToObject
                (testProjectionPath + "projection10_1.json");
        DCRGraph dcrGraph = modelImp.transferToDCRGraph(jsonDCR);

        assertEquals(projectionImp.getARolesSigma(dcrGraph, "role1").size(), 2);
    }

    @Test
    public void testEndPointProjection11_0() throws Exception{
        ModelImp modelImp = new ModelImp();
        ProjectionImp projectionImp = new ProjectionImp();
        JsonDCR jsonDCR = modelImp.parseJsonToObject
                (testProjectionPath + "projection11_0.json");
        DCRGraph dcrGraph = modelImp.transferToDCRGraph(jsonDCR);

        HashSet<String> rolesSigma = projectionImp.getARolesSigma(dcrGraph, "Buyer");
        DCRGraph sigmaProjection = projectionImp.sigmaProjection(dcrGraph, rolesSigma);

        assertTrue(sigmaProjection.getDcrMarking().included.contains("Reject"));
        assertTrue(sigmaProjection.getDcrMarking().included.contains("Accept1"));
        assertTrue(sigmaProjection.getDcrMarking().included.contains("Accept2"));
        assertTrue(sigmaProjection.getDcrMarking().included.contains("Quote1"));

    }

    @Test
    public void testEndPointProjection12_0() throws Exception{
        ModelImp modelImp = new ModelImp();
        ProjectionImp projectionImp = new ProjectionImp();
        JsonDCR jsonDCR = modelImp.parseJsonToObject
                (testProjectionPath + "projection12_0.json");
        DCRGraph dcrGraph = modelImp.transferToDCRGraph(jsonDCR);

        HashSet<String> rolesSigma = projectionImp.getARolesSigma(dcrGraph, "Buyer");
        DCRGraph sigmaProjection = projectionImp.sigmaProjection(dcrGraph, rolesSigma);

        System.out.println(sigmaProjection.getOneMap("TimeResponse"));

        assertTrue(sigmaProjection.getOneMap("TimeResponse").get("interactionAsk").contains("Quote1"));
        assertTrue(sigmaProjection.getOneMap("TimeResponse").get("interactionAsk").contains("Quote2"));
        assertTrue(sigmaProjection.getOneMap("TimeResponse").get("Quote1").contains("Accept1"));
        assertTrue(sigmaProjection.getOneMap("TimeResponse").get("Quote2").contains("Accept2"));

    }

    @Test
    public void testEndPointProjection13_0() throws Exception{
        ModelImp modelImp = new ModelImp();
        ProjectionImp projectionImp = new ProjectionImp();
        JsonDCR jsonDCR = modelImp.parseJsonToObject
                (testProjectionPath + "projection13_0.json");
        DCRGraph dcrGraph = modelImp.transferToDCRGraph(jsonDCR);

        HashSet<String> rolesSigma = projectionImp.getARolesSigma(dcrGraph, "Seller1");
        DCRGraph sigmaProjection = projectionImp.sigmaProjection(dcrGraph, rolesSigma);

        assertTrue(sigmaProjection.getOneMap("TimeInclusion").get("Accept1").contains("Order1"));
    }

    @Test
    public void testEndPointProjection14_0() throws Exception{
        ModelImp modelImp = new ModelImp();
        ProjectionImp projectionImp = new ProjectionImp();
        JsonDCR jsonDCR = modelImp.parseJsonToObject
                (testProjectionPath + "projection14_0.json");
        DCRGraph dcrGraph = modelImp.transferToDCRGraph(jsonDCR);

        HashSet<String> rolesSigma = projectionImp.getARolesSigma(dcrGraph, "Buyer");
        DCRGraph sigmaProjection = projectionImp.sigmaProjection(dcrGraph, rolesSigma);

        System.out.println(sigmaProjection.getOneMap("TimeExclusion"));

        assertEquals(sigmaProjection.getOneMap("TimeExclusion").get("Accept1").size(),6);
        assertEquals(sigmaProjection.getOneMap("TimeExclusion").get("Reject").size(),6);

    }

    @Test
    public void testEndPointProjection15_0() throws Exception{
        ModelImp modelImp = new ModelImp();
        ProjectionImp projectionImp = new ProjectionImp();
        JsonDCR jsonDCR = modelImp.parseJsonToObject
                (testProjectionPath + "projection12_0.json");
        DCRGraph dcrGraph = modelImp.transferToDCRGraph(jsonDCR);

        HashSet<String> rolesSigma = projectionImp.getARolesSigma(dcrGraph, "Buyer");
        DCRGraph sigmaProjection = projectionImp.sigmaProjection(dcrGraph, rolesSigma);
        DCRGraph endPointProjection = projectionImp.endUpProjection(dcrGraph, sigmaProjection, "Buyer");
        assertTrue(endPointProjection.getDcrMarking().included.contains("ShipDetails"));

        HashSet<String> rolesSigma2 = projectionImp.getARolesSigma(dcrGraph, "Seller1");
        DCRGraph sigmaProjection2 = projectionImp.sigmaProjection(dcrGraph, rolesSigma2);
        DCRGraph endPointProjection2 = projectionImp.endUpProjection(dcrGraph, sigmaProjection2, "Seller1");
        assertFalse(endPointProjection2.getDcrMarking().included.contains("Order1"));

        HashSet<String> rolesSigma3 = projectionImp.getARolesSigma(dcrGraph, "Shipper");
        DCRGraph sigmaProjection3 = projectionImp.sigmaProjection(dcrGraph, rolesSigma3);
        DCRGraph endPointProjection3 = projectionImp.endUpProjection(dcrGraph, sigmaProjection3, "Shipper");
        assertFalse(endPointProjection3.getDcrMarking().included.contains("ShipDetails"));
    }
}
