package owlreasoner;

import com.clarkparsia.owlapi.explanation.*;
import org.semanticweb.owlapi.apibinding.*;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.*;
import org.semanticweb.HermiT.*;
import java.io.*;
import java.util.*;

public class ReasoningModule {

    private OWLReasoner reasoner;
    private Reasoner.ReasonerFactory factory;
    private OWLOntology ontology;

    public ReasoningModule(OWLOntology o){
        this.ontology=o;
        this.factory=new Reasoner.ReasonerFactory();
        this.reasoner=this.factory.createReasoner(this.ontology);
        //this.reasoner=new Reasoner(o);
        //System.out.println("Reasoning Module Initialised.");
    }

    public boolean getModelConsistency(){
        return this.reasoner.isConsistent();
    }

    public void explain(){
        BlackBoxExplanation bb_exp=new BlackBoxExplanation(this.ontology,this.factory,this.reasoner);

    }
}

