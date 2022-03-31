package projectionInterface;

import models.dcrGraph.DCRGraph;

import java.util.HashSet;

public interface IProjection {
    HashSet<String> getARolesSigma(DCRGraph dcrGraph, String role);
    DCRGraph sigmaProjection(DCRGraph dcrGraph, HashSet<String> sigmaSet) throws Exception;
    DCRGraph endUpProjection(DCRGraph choreography, DCRGraph sigmaProjection, String role);
    DCRGraph Process(DCRGraph choreography, String role) throws Exception;
}
