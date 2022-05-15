package projectionInterface;

import models.dcrGraph.DCRGraph;

import java.util.HashSet;

public interface IProjection {
    // Get a role's sigma sets.
    HashSet<String> getARolesSigma(DCRGraph dcrGraph, String role);
    // Generate sigma projection for some set.
    DCRGraph sigmaProjection(DCRGraph dcrGraph, HashSet<String> sigmaSet) throws Exception;
    // Generate end-up projection for some role.
    DCRGraph endUpProjection(DCRGraph choreography, DCRGraph sigmaProjection, String role);
    // Combine previous three functions together.
    DCRGraph Process(DCRGraph choreography, String role) throws Exception;
}
