package owlreasoner;

import org.semanticweb.owlapi.apibinding.*;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.*;
import org.semanticweb.HermiT.*;
import java.io.*;
import java.util.*;

public class ModelHandler {
    public OWLOntology getOntology(){
        OWLOntologyManager manager=OWLManager.createOWLOntologyManager();
        File file=new File("/home/adityas/Projects/Test_Reasoner/Reasoner/data/test.n3");
        System.out.println(manager);
        OWLOntology ontology=null;
        try {
                ontology=manager.loadOntologyFromOntologyDocument(file);
                System.out.println(ontology);
                //assertNotNull(ontology);
        }
        catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        return ontology;
    }

    public void printAxioms(OWLOntology ontology){
        OWLDataFactory data_factory=OWLManager.getOWLDataFactory();
        OWLClass owl_class=data_factory.getOWLThing();
        for (OWLEntity _class : ontology.getSignature()){
            System.out.println(_class.toStringID());
        }
        System.out.println(owl_class);
    }

    public void getInference(OWLOntology ontology){
        OWLReasoner reasoner=new Reasoner(ontology);
        System.out.println(reasoner.isConsistent());
    }
}

