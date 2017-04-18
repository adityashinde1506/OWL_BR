package owlreasoner;

import org.semanticweb.owlapi.apibinding.*;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.*;

public class Main {

    public static void main(String args[]) {
        System.out.println("Reasoner started!");
        ModelHandler handler=new ModelHandler();
        OWLOntology onto=handler.getOntology();
        handler.printAxioms(onto);
        handler.getInference(onto);
    }
}
