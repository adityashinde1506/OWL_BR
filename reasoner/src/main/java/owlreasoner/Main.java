package owlreasoner;

import org.semanticweb.owlapi.apibinding.*;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.*;

public class Main {

    public static void main(String args[]) {
        ModelHandler handler=new ModelHandler(args[0]);
        //ModelHandler handler=new ModelHandler("/home/adityas/Downloads/individuals.owl");
        //handler.createKB();
        handler.updateOntology(args[1]);
        ReasoningModule rm=handler.getReasoningModule();
        //handler.printOntology();
        //System.out.println("Resolving...");
        //rm.resolveInconsistency();
        System.out.println("Is Consistent?");
        System.out.println(rm.getModelConsistency());
        handler.writeRevisedOntology();
        //rm.explain(handler.getThing());
    }
}
