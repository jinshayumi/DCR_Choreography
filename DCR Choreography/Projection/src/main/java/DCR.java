import checker.ProjectableChecker;
import model.DCRGraph;
import model.DCRLoader;
import model.entities.JSONDCR;

import java.io.IOException;

public class DCR {
    static final String filePath = "/Projection/src/main/resources/DCR.json";
    public static void main(String[] args) throws IOException {
        // load the DCR Choreography from file.
        DCRGraph dcrGraph = new DCRGraph(filePath);
        // check Projectability.
        System.out.println(ProjectableChecker.isProjectable(dcrGraph,"Seller2"));


    }
}
