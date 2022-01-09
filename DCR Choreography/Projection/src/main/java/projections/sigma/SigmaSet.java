package projections.sigma;

import model.DCRGraph;
import model.entities.Participant;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class SigmaSet {
    public static HashSet<String> getSigmaSet(DCRGraph dcrGraph, String role){
        List<Participant> participants = dcrGraph.getARolesInteractions(role);
        HashSet<String> res = new HashSet<>();
        for (Participant participant: participants){
            res.add(participant.getIdentity());
        }
        return res;
    }
}
