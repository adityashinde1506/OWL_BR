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
    private OWLOntologyManager manager;
    private Configuration configuration;
    private SatisfiabilityConverter satConverter=new SatisfiabilityConverter(OWLManager.getOWLDataFactory());
    private RevisionModule revisionModule;
        
    public ReasoningModule(OWLOntology o,OWLOntologyManager m){
        /* Constructor*/
        this.ontology=o;
        this.manager=m;
        this.configuration=new Configuration();
        this.configuration.throwInconsistentOntologyException=false;
        this.reasoner=this.factory.createReasoner(this.ontology,this.configuration);
        //this.reasoner=new Reasoner(o);
        //System.out.println(this.reasoner.isPrecomputed());
        //this.revisionModule=new RevisionModule(this.reasoner,this.ontology);
        System.out.println("Reasoning Module Initialised.");
    }

    public RevisionModule getRevisionModule(){
        return this.revisionModule;
    }

    public void getClasses(OWLAxiom axiom){
        System.out.println("Getting classes in axiom.");
        for (OWLClass _class: axiom.getClassesInSignature()){
            System.out.println(_class.toString());
            //this.revisionModule.getUnsatAxiom(_class);
        }
    }

    public boolean getModelConsistency(){
        /* Tell if the KB is consistent*/
        this.reasoner.flush();
        return this.reasoner.isConsistent();
    }

    public void printModel(){
        /* Just for debugging*/
        for (OWLAxiom axiom:this.ontology.getAxioms()){
            System.out.println(axiom.toString());
        }
    }

    public boolean checkSatisfiability(OWLAxiom axiom){
        /* Check if KB satisfies a particular axiom. Throws an exception sometimes. Not yet able to figure out why.*/
        this.reasoner.flush();
        return this.reasoner.isSatisfiable(this.satConverter.convert(axiom));
    }

    public void populateRevisionModule(){
        /* Populate revision module with unsatisfiable classes.*/
        this.revisionModule=new RevisionModule(this.reasoner);
        Node<OWLClass> unSatNode=this.reasoner.getUnsatisfiableClasses();
        for(OWLClass _class:unSatNode.getEntities()){
            //System.out.println(_class.toString());
            this.getExplanation(_class);
        }
    }

    public void reviseOntology(){
        /* Gets the subclass axiom which is causing the inconsistency and deletes it.
         * Actual implementation was supposed to weaken the subclass axiom.
         * But could not figure out how to write weakened statement in OWL syntax.
         */
        for(OWLAxiom axiom: this.revisionModule.getInconsistencyCreators()){
            RemoveAxiom raxiom=new RemoveAxiom(this.ontology,axiom);
            System.out.println("Removing axiom "+axiom.toString());
            this.manager.applyChange(raxiom);
        }
    }

/*
    public void resolveInconsistency(){
        // Get a list of unsatisfiable classes. Then provide explanations for each one.
        this.populateRevisionModule();
        if(this.revisionModule.discardInconsistentAxioms(this.manager)){
            this.printModel();
            System.out.println(this.getModelConsistency());
        }
        else{
            System.out.println("Could not remove inconsistencies.");
        }
    }
*/
    public void computeInferences(){
        /* Supposed to do initial reasoning over the KB. Does not work. No idea why.*/
        Reasoner reasoner=new Reasoner(this.ontology);
        reasoner.precomputeInferences(InferenceType.CLASS_ASSERTIONS);
        reasoner.flush();
    }

    public void getExplanation(OWLClass _axiom){
        /* Get axioms which explain why the class is unsatisfiable. Then add each axiom to the Revision module to revise the KB.*/
        factory=new Reasoner.ReasonerFactory() {
            protected OWLReasoner createHermiTOWLReasoner(Configuration configuration,OWLOntology ontology){
                configuration.throwInconsistentOntologyException=false;
                return new Reasoner(configuration,ontology);
            }
        };

        BlackBoxExplanation bbExp=new BlackBoxExplanation(this.ontology,factory,this.reasoner);
        HSTExplanationGenerator expGenerator= new HSTExplanationGenerator(bbExp);
        Set<Set<OWLAxiom>> explanations=expGenerator.getExplanations(_axiom);
        for (Set<OWLAxiom> axiomSet : explanations){
            for (OWLAxiom axiom: axiomSet){
                this.revisionModule.addUnSatAxiom(axiom);
               // System.out.println(axiom.getAxiomType().toString());
            }
        }
    }
}

