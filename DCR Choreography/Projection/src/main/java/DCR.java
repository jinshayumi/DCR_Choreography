import checker.ProjectableChecker;
import model.DCRGraph;
import model.DCRLoader;
import model.entities.JSONDCR;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DCR {
    static final String filePath = "/Projection/src/main/resources/DCR.json";
    public static void main(String[] args) throws IOException {
        // load the DCR Choreography from file.
        DCRGraph dcrGraph = new DCRGraph(filePath);
        // check Projectability.
        System.out.println(ProjectableChecker.isProjectable(dcrGraph,"Seller2"));
        List<StringBuffer> resList = new ArrayList<>();
        StringBuffer a = new StringBuffer();
        StringBuffer a1 = a.append("a");
        a1.toString();


    }
}
