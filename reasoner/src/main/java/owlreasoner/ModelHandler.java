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
    private ReasoningModule reasoningModule;

    private void addAxiomToKB(OWLAxiom axiom){
        System.out.println("Adding "+axiom.toString()+" to KB.");
        AddAxiom addAxiom=new AddAxiom(this.KB,axiom);
        this.ontologyManager.applyChange(addAxiom);
        if(!this.reasoningModule.getModelConsistency()){
            this.reasoningModule.printUnsatisfiableClasses();
            //this.reasoningModule.explain(axiom);
        }
    }

    public ModelHandler(String filename){
        File file=new File(filename);
        this.ontologyManager=OWLManager.createOWLOntologyManager();
        this.dataFactory=OWLManager.getOWLDataFactory();
        try {
                this.ontology=ontologyManager.loadOntologyFromOntologyDocument(file);
                this.KB=ontologyManager.createOntology();
                this.reasoningModule=new ReasoningModule(this.KB);
                System.out.println("Ontology loaded.");
        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println("Failed to load ontology.");
            System.exit(1);
        }
    }

    public void createKB(){
       for (OWLAxiom axiom : this.ontology.getAxioms()){
           this.addAxiomToKB(axiom);
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
        for(OWLAxiom axiom: this.ontology.getAxioms()){
            System.out.println(axiom.toString());
        }
    }

    public OWLClass getThing(){
        return this.dataFactory.getOWLThing();
    }
}

