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
    private Reasoner.ReasonerFactory factory=new Reasoner.ReasonerFactory();
    public OWLOntology ontology;
    private Configuration configuration;
    private SatisfiabilityConverter satConverter=new SatisfiabilityConverter(OWLManager.getOWLDataFactory());

    public ReasoningModule(OWLOntology o){
        this.ontology=o;
        this.configuration=new Configuration();
        this.configuration.throwInconsistentOntologyException=false;
        this.reasoner=this.factory.createReasoner(this.ontology,this.configuration);
        //this.reasoner=new Reasoner(o);
        //System.out.println(this.reasoner.isPrecomputed());
        System.out.println("Reasoning Module Initialised.");
    }

    public boolean getModelConsistency(){
        this.reasoner.flush();
        return this.reasoner.isConsistent();
    }

    public void printModel(){
        for (OWLAxiom axiom:this.ontology.getAxioms()){
            System.out.println(axiom.toString());
        }
    }

    public boolean checkSatisfiability(OWLAxiom axiom){
        this.reasoner.flush();
        return this.reasoner.isSatisfiable(this.satConverter.convert(axiom));
    }

    public void printUnsatisfiableClasses(){
        Node<OWLClass> unSatNode=this.reasoner.getUnsatisfiableClasses();
        for(OWLClass _class:unSatNode.getEntities()){
            /*if(_class.isOWLThing() || _class.isOWLNothing()){
                continue;
            }*/
            System.out.println(_class.toString());
            this.explain(_class);
        }
    }

    public void explain(OWLClass _axiom){
        factory=new Reasoner.ReasonerFactory() {
            protected OWLReasoner createHermiTOWLReasoner(Configuration configuration,OWLOntology ontology){
                configuration.throwInconsistentOntologyException=false;
                return new Reasoner(configuration,ontology);
            }
        };
        BlackBoxExplanation bbExp=new BlackBoxExplanation(this.ontology,factory,this.reasoner);
        HSTExplanationGenerator expGenerator= new HSTExplanationGenerator(bbExp);
        Set<Set<OWLAxiom>> explanations=expGenerator.getExplanations(_axiom);
        System.out.println("Explanations fetched");
        for (Set<OWLAxiom> axiomSet : explanations){
            System.out.println("Getting Axioms.");
            for (OWLAxiom axiom: axiomSet){
                System.out.println(axiom.toString());
            }
            System.out.println("--------------------------");
        }
    }
}

