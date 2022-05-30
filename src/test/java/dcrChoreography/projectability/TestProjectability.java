package dcrChoreography.projectability;

import modelInterface.ModelImp;
import models.dcrGraph.DCRGraph;
import models.jsonDCR.JsonDCR;
import org.junit.Test;
import projectionInterface.ProjectionImp;
import static org.junit.Assert.*;

public class TestProjectability {
    private static final String testProjectabilityPath = "/src/main/resources/test/dcrChoreography/projectability/";

    // condition.
    @Test
    public void testProjectability0_0() throws Exception{
        ModelImp modelImp = new ModelImp();
        JsonDCR jsonDCR = modelImp.parseJsonToObject(testProjectabilityPath + "projectability0_0.json");
        DCRGraph dcrGraph = modelImp.transferToDCRGraph(jsonDCR);

        assertTrue(modelImp.projectable(dcrGraph, "role2"));
    }

    @Test
    public void testProjectability0_1() throws Exception{
        ModelImp modelImp = new ModelImp();
        JsonDCR jsonDCR = modelImp.parseJsonToObject(testProjectabilityPath + "projectability0_1.json");
        DCRGraph dcrGraph = modelImp.transferToDCRGraph(jsonDCR);

        assertTrue(modelImp.projectable(dcrGraph, "role2"));
    }

    @Test
    public void testProjectability0_2() throws Exception{
        ModelImp modelImp = new ModelImp();
        JsonDCR jsonDCR = modelImp.parseJsonToObject(testProjectabilityPath + "projectability0_2.json");
        DCRGraph dcrGraph = modelImp.transferToDCRGraph(jsonDCR);

        assertFalse(modelImp.projectable(dcrGraph, "role2"));
    }

    // response.
    @Test
    public void testProjectability1_0() throws Exception{
        ModelImp modelImp = new ModelImp();
        JsonDCR jsonDCR = modelImp.parseJsonToObject(testProjectabilityPath + "projectability1_0.json");
        DCRGraph dcrGraph = modelImp.transferToDCRGraph(jsonDCR);

        assertTrue(modelImp.projectable(dcrGraph, "role2"));
    }

    @Test
    public void testProjectability1_1() throws Exception{
        ModelImp modelImp = new ModelImp();
        JsonDCR jsonDCR = modelImp.parseJsonToObject(testProjectabilityPath + "projectability1_1.json");
        DCRGraph dcrGraph = modelImp.transferToDCRGraph(jsonDCR);

        assertTrue(modelImp.projectable(dcrGraph, "role2"));
    }

    @Test
    public void testProjectability1_2() throws Exception{
        ModelImp modelImp = new ModelImp();
        JsonDCR jsonDCR = modelImp.parseJsonToObject(testProjectabilityPath + "projectability1_2.json");
        DCRGraph dcrGraph = modelImp.transferToDCRGraph(jsonDCR);

        assertFalse(modelImp.projectable(dcrGraph, "role2"));
    }

    // milestone.
    @Test
    public void testProjectability2_0() throws Exception{
        ModelImp modelImp = new ModelImp();
        JsonDCR jsonDCR = modelImp.parseJsonToObject(testProjectabilityPath + "projectability2_0.json");
        DCRGraph dcrGraph = modelImp.transferToDCRGraph(jsonDCR);

        assertTrue(modelImp.projectable(dcrGraph, "role2"));
    }

    @Test
    public void testProjectability2_1() throws Exception{
        ModelImp modelImp = new ModelImp();
        JsonDCR jsonDCR = modelImp.parseJsonToObject(testProjectabilityPath + "projectability2_1.json");
        DCRGraph dcrGraph = modelImp.transferToDCRGraph(jsonDCR);

        assertTrue(modelImp.projectable(dcrGraph, "role2"));
    }

    @Test
    public void testProjectability2_2() throws Exception{
        ModelImp modelImp = new ModelImp();
        JsonDCR jsonDCR = modelImp.parseJsonToObject(testProjectabilityPath + "projectability2_2.json");
        DCRGraph dcrGraph = modelImp.transferToDCRGraph(jsonDCR);

        assertFalse(modelImp.projectable(dcrGraph, "role2"));
    }

    // inclusion.
    @Test
    public void testProjectability3_0() throws Exception{
        ModelImp modelImp = new ModelImp();
        JsonDCR jsonDCR = modelImp.parseJsonToObject(testProjectabilityPath + "projectability3_0.json");
        DCRGraph dcrGraph = modelImp.transferToDCRGraph(jsonDCR);

        assertTrue(modelImp.projectable(dcrGraph, "role2"));
    }

    @Test
    public void testProjectability3_1() throws Exception{
        ModelImp modelImp = new ModelImp();
        JsonDCR jsonDCR = modelImp.parseJsonToObject(testProjectabilityPath + "projectability3_1.json");
        DCRGraph dcrGraph = modelImp.transferToDCRGraph(jsonDCR);

        assertTrue(modelImp.projectable(dcrGraph, "role2"));
    }

    @Test
    public void testProjectability3_2() throws Exception{
        ModelImp modelImp = new ModelImp();
        JsonDCR jsonDCR = modelImp.parseJsonToObject(testProjectabilityPath + "projectability3_2.json");
        DCRGraph dcrGraph = modelImp.transferToDCRGraph(jsonDCR);

        assertFalse(modelImp.projectable(dcrGraph, "role2"));
    }

    // exclusion.
    @Test
    public void testProjectability4_0() throws Exception{
        ModelImp modelImp = new ModelImp();
        JsonDCR jsonDCR = modelImp.parseJsonToObject(testProjectabilityPath + "projectability4_0.json");
        DCRGraph dcrGraph = modelImp.transferToDCRGraph(jsonDCR);

        assertTrue(modelImp.projectable(dcrGraph, "role2"));
    }

    @Test
    public void testProjectability4_1() throws Exception{
        ModelImp modelImp = new ModelImp();
        JsonDCR jsonDCR = modelImp.parseJsonToObject(testProjectabilityPath + "projectability4_1.json");
        DCRGraph dcrGraph = modelImp.transferToDCRGraph(jsonDCR);

        assertTrue(modelImp.projectable(dcrGraph, "role2"));
    }

    @Test
    public void testProjectability4_2() throws Exception{
        ModelImp modelImp = new ModelImp();
        JsonDCR jsonDCR = modelImp.parseJsonToObject(testProjectabilityPath + "projectability4_2.json");
        DCRGraph dcrGraph = modelImp.transferToDCRGraph(jsonDCR);

        assertFalse(modelImp.projectable(dcrGraph, "role2"));
    }

    // e' inclusion e'' condition e'
    @Test
    public void testProjectability5_0() throws Exception{
        ModelImp modelImp = new ModelImp();
        JsonDCR jsonDCR = modelImp.parseJsonToObject(testProjectabilityPath + "projectability5_0.json");
        DCRGraph dcrGraph = modelImp.transferToDCRGraph(jsonDCR);

        assertTrue(modelImp.projectable(dcrGraph, "role2"));
    }

    @Test
    public void testProjectability5_1() throws Exception{
        ModelImp modelImp = new ModelImp();
        JsonDCR jsonDCR = modelImp.parseJsonToObject(testProjectabilityPath + "projectability5_1.json");
        DCRGraph dcrGraph = modelImp.transferToDCRGraph(jsonDCR);

        assertTrue(modelImp.projectable(dcrGraph, "role2"));
    }

    @Test
    public void testProjectability5_2() throws Exception{
        ModelImp modelImp = new ModelImp();
        JsonDCR jsonDCR = modelImp.parseJsonToObject(testProjectabilityPath + "projectability5_2.json");
        DCRGraph dcrGraph = modelImp.transferToDCRGraph(jsonDCR);

        assertFalse(modelImp.projectable(dcrGraph, "role2"));
    }

    // e' inclusion e'' milestone e'
    @Test
    public void testProjectability6_0() throws Exception{
        ModelImp modelImp = new ModelImp();
        JsonDCR jsonDCR = modelImp.parseJsonToObject(testProjectabilityPath + "projectability6_0.json");
        DCRGraph dcrGraph = modelImp.transferToDCRGraph(jsonDCR);

        assertTrue(modelImp.projectable(dcrGraph, "role2"));
    }

    @Test
    public void testProjectability6_1() throws Exception{
        ModelImp modelImp = new ModelImp();
        JsonDCR jsonDCR = modelImp.parseJsonToObject(testProjectabilityPath + "projectability6_1.json");
        DCRGraph dcrGraph = modelImp.transferToDCRGraph(jsonDCR);

        assertTrue(modelImp.projectable(dcrGraph, "role2"));
    }

    @Test
    public void testProjectability6_2() throws Exception{
        ModelImp modelImp = new ModelImp();
        JsonDCR jsonDCR = modelImp.parseJsonToObject(testProjectabilityPath + "projectability6_2.json");
        DCRGraph dcrGraph = modelImp.transferToDCRGraph(jsonDCR);

        assertFalse(modelImp.projectable(dcrGraph, "role2"));
    }

    // e' exclusion e'' condition e'
    @Test
    public void testProjectability7_0() throws Exception{
        ModelImp modelImp = new ModelImp();
        JsonDCR jsonDCR = modelImp.parseJsonToObject(testProjectabilityPath + "projectability7_0.json");
        DCRGraph dcrGraph = modelImp.transferToDCRGraph(jsonDCR);

        assertTrue(modelImp.projectable(dcrGraph, "role2"));
    }

    @Test
    public void testProjectability7_1() throws Exception{
        ModelImp modelImp = new ModelImp();
        JsonDCR jsonDCR = modelImp.parseJsonToObject(testProjectabilityPath + "projectability7_1.json");
        DCRGraph dcrGraph = modelImp.transferToDCRGraph(jsonDCR);

        assertTrue(modelImp.projectable(dcrGraph, "role2"));
    }

    @Test
    public void testProjectability7_2() throws Exception{
        ModelImp modelImp = new ModelImp();
        JsonDCR jsonDCR = modelImp.parseJsonToObject(testProjectabilityPath + "projectability7_2.json");
        DCRGraph dcrGraph = modelImp.transferToDCRGraph(jsonDCR);

        assertFalse(modelImp.projectable(dcrGraph, "role2"));
    }

    // e' exclusion e'' milestone e'
    @Test
    public void testProjectability8_0() throws Exception{
        ModelImp modelImp = new ModelImp();
        JsonDCR jsonDCR = modelImp.parseJsonToObject(testProjectabilityPath + "projectability8_0.json");
        DCRGraph dcrGraph = modelImp.transferToDCRGraph(jsonDCR);

        assertTrue(modelImp.projectable(dcrGraph, "role2"));
    }

    @Test
    public void testProjectability8_1() throws Exception{
        ModelImp modelImp = new ModelImp();
        JsonDCR jsonDCR = modelImp.parseJsonToObject(testProjectabilityPath + "projectability8_1.json");
        DCRGraph dcrGraph = modelImp.transferToDCRGraph(jsonDCR);

        assertTrue(modelImp.projectable(dcrGraph, "role2"));
    }

    @Test
    public void testProjectability8_2() throws Exception{
        ModelImp modelImp = new ModelImp();
        JsonDCR jsonDCR = modelImp.parseJsonToObject(testProjectabilityPath + "projectability8_2.json");
        DCRGraph dcrGraph = modelImp.transferToDCRGraph(jsonDCR);

        assertFalse(modelImp.projectable(dcrGraph, "role2"));
    }

    // e' response e'' condition e'
    @Test
    public void testProjectability9_0() throws Exception{
        ModelImp modelImp = new ModelImp();
        JsonDCR jsonDCR = modelImp.parseJsonToObject(testProjectabilityPath + "projectability9_0.json");
        DCRGraph dcrGraph = modelImp.transferToDCRGraph(jsonDCR);

        assertTrue(modelImp.projectable(dcrGraph, "role2"));
    }

    @Test
    public void testProjectability9_1() throws Exception{
        ModelImp modelImp = new ModelImp();
        JsonDCR jsonDCR = modelImp.parseJsonToObject(testProjectabilityPath + "projectability9_1.json");
        DCRGraph dcrGraph = modelImp.transferToDCRGraph(jsonDCR);

        assertTrue(modelImp.projectable(dcrGraph, "role2"));
    }

    @Test
    public void testProjectability9_2() throws Exception{
        ModelImp modelImp = new ModelImp();
        JsonDCR jsonDCR = modelImp.parseJsonToObject(testProjectabilityPath + "projectability9_2.json");
        DCRGraph dcrGraph = modelImp.transferToDCRGraph(jsonDCR);

        assertFalse(modelImp.projectable(dcrGraph, "role2"));
    }
}
