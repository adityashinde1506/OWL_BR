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
    private OWLOntology KB;
    private OWLOntology updateOntology;
    private ReasoningModule reasoningModule;

    private void addAxiomToKB(OWLAxiom axiom){
        // Adds axiom to the knowledge base. Then checks for inconsistencies and calls the resolve methods if inconsistency is found. Very slow. :( :(

        AddAxiom addAxiom=new AddAxiom(this.ontology,axiom);
        this.ontologyManager.applyChange(addAxiom);
        if(!this.reasoningModule.getModelConsistency()){
            System.out.println("---------------Inconsistency on last addition to KB ----------------");
            System.out.println("Resolving...");
            this.reasoningModule.populateRevisionModule();
            this.reasoningModule.reviseOntology();
            System.out.println("---------------Inconsistency Resolved ----------------\r\n");
        }
    }

    public ModelHandler(String filename){
        File file=new File(filename);
        this.ontologyManager=OWLManager.createOWLOntologyManager();
        this.dataFactory=OWLManager.getOWLDataFactory();
        try {
                this.ontology=ontologyManager.loadOntologyFromOntologyDocument(file);
                this.KB=ontologyManager.createOntology();
                this.reasoningModule=new ReasoningModule(this.ontology,this.ontologyManager);
                System.out.println("Ontology loaded.");
                System.out.println("------------------");
        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println("Failed to load ontology.");
            System.exit(1);
        }
    }

    public void updateOntology(String filename){
        System.out.println("Updating Ontology...");
        File file=new File(filename);
        try{
                this.updateOntology=this.ontologyManager.loadOntologyFromOntologyDocument(file);
        }
        catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        for(OWLAxiom axiom:this.updateOntology.getAxioms()){
            System.out.println("Adding axiom "+axiom.toString());
            this.addAxiomToKB(axiom);
        }
    }

    public void createKB(){
        /* Adds axioms to the knowledge base one at a time. Better to merge two ontologies using OWLMerger instead of iterating like this. Use only for testing. */
       for (OWLAxiom axiom : this.ontology.getAxioms()){
           this.addAxiomToKB(axiom);
       }
    }

    public void printClasses(){
        /*Just for debugging*/
        for (OWLEntity _class : this.ontology.getSignature()){
            System.out.println(_class.toStringID());
        }
    }

    public OWLOntology getOntology(){
        /* In case some other class wants to directly mess with the knowledge base. # Need to flush the reasoner buffer for changes to reflect in the reasoner.*/
        return this.ontology;
    }

    public void printOntology(){
        /* Just for debugging */
        for(OWLAxiom axiom: this.ontology.getAxioms()){
            System.out.println(axiom.toString());
        }
    }

    public OWLClass getThing(){
        return this.dataFactory.getOWLThing();
    }

    public void writeRevisedOntology(){
        try{
                this.ontologyManager.saveOntology(this.ontology,new FileOutputStream("revised.n3"));
        }
        catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }

    public ReasoningModule getReasoningModule(){
        /* To provide direct access to the reasoning module.*/
        return this.reasoningModule;
    }
}

