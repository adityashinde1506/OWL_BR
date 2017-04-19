package owlreasoner;

import com.clarkparsia.owlapi.explanation.*;
import org.semanticweb.owlapi.apibinding.*;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.*;
import org.semanticweb.HermiT.*;
import java.io.*;
import java.util.*;

public class ModelHandler {

    private OWLOntology ontology;
    private OWLOntologyManager ontologyManager;
    private OWLDataFactory dataFactory;

    public ModelHandler(String filename){
        File file=new File(filename);
        this.ontologyManager=OWLManager.createOWLOntologyManager();
        this.dataFactory=OWLManager.getOWLDataFactory();
        try {
                this.ontology=ontologyManager.loadOntologyFromOntologyDocument(file);
                System.out.println("Ontology loaded.");
        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println("Failed to load ontology.");
            System.exit(1);
        }
    }

    public void printClasses(){
        for (OWLEntity _class : this.ontology.getSignature()){
            System.out.println(_class.toStringID());
        }
    }

    public OWLOntology getOntology(){
        return this.ontology;
    }

    public void printOntology(){
        try {
                this.ontologyManager.saveOntology(this.ontology,System.out);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}

