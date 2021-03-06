package owlreasoner;

import com.clarkparsia.owlapi.explanation.*;
import org.semanticweb.owlapi.apibinding.*;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.*;
import org.semanticweb.owlapi.model.parameters.*;
import org.semanticweb.HermiT.*;
import java.io.*;
import java.util.*;

public class RevisionModule {

    private OWLReasoner reasoner;
    private OWLOntology unSatOntology;
    private OWLOntologyManager manager=OWLManager.createOWLOntologyManager();
    private HashSet<OWLAxiom> unSatAxioms = new HashSet<OWLAxiom>();
    private HashSet<OWLAxiom> toRemove=new HashSet<OWLAxiom>();

    public RevisionModule(OWLReasoner reasoner){
        this.reasoner=reasoner;
        try{
                this.unSatOntology=manager.createOntology();
        }
        catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("Revision module initialised.");
    }

    public void addUnSatAxiom(OWLAxiom axiom){
        /* Add a single unsatisfaible axiom to the list and to the ontology. */
        AddAxiom addAxiom=new AddAxiom(this.unSatOntology,axiom);
        if(!this.unSatAxioms.contains(axiom)){
            this.unSatAxioms.add(axiom);
            this.manager.applyChange(addAxiom);
        }
    }

    public Set<OWLNamedIndividual> getIndividuals(){
       //Gets a list of Individuals from unsatisfiable Class Assertion axioms.
       return this.unSatOntology.getIndividualsInSignature();
    }
/*
    public boolean discardInconsistentAxioms(OWLOntologyManager manager){
        List<OWLOntologyChange> changes=manager.removeAxioms(this.KB,this.unSatAxioms);
        return true;
    }
*/
    public Set<OWLClassAssertionAxiom> getClassAssertionAxiomForIndividual(OWLNamedIndividual individual){
        return this.unSatOntology.getClassAssertionAxioms(individual);
    }

    private Set<OWLSubClassOfAxiom> getSubClassAssertion(OWLClassExpression cExp){
        return this.unSatOntology.getSubClassAxiomsForSubClass(cExp.asOWLClass());
    }

    private void addException(OWLSubClassOfAxiom axiom,OWLNamedIndividual individual){
        //Supposed to add exception ofr individual to GCI. But could not do it. so simply remove the subclassof assertion.
        System.out.println("Inconsistency causing axiom is "+axiom.toString());
        this.toRemove.add(axiom);
    }

    private void weakenAxiom(Set<OWLSubClassOfAxiom> axioms,OWLNamedIndividual individual){
        //Supposed to implement weakening algorithm. But could not do it.
        for(OWLSubClassOfAxiom axiom:axioms){
            this.addException(axiom,individual);
        }
    }

    private void weakenRelatedSubClassAxiom(OWLClassAssertionAxiom caAxiom,OWLNamedIndividual individual){
        //Weakens the subclassof axiom related to the class expression asserted in the unsatisfiable class assertion.
        OWLClassExpression cExp=caAxiom.getClassExpression();
        Set<OWLSubClassOfAxiom> axioms=this.getSubClassAssertion(cExp);
        if(!axioms.isEmpty()){
            this.weakenAxiom(axioms,individual);
        }
    }

    public void locateInconsistency(){
        System.out.println("Locating Inconsistency...");
        for(OWLNamedIndividual individual:this.getIndividuals()){
            for(OWLClassAssertionAxiom caAxiom:this.getClassAssertionAxiomForIndividual(individual)){
                this.weakenRelatedSubClassAxiom(caAxiom,individual);
            }
        }
    }

    public void reviseKB(){
        //Searches the KB of all unsatisfiable classes and adds the one causing inconsistency.
        this.printAxioms();
        this.locateInconsistency();
    }

    public HashSet<OWLAxiom> getInconsistencyCreators(){
        //Returns the set of subclass axioms to remove.
        this.reviseKB();
        return this.toRemove;
    }

    private void printDeletions(){
        System.out.println(this.toRemove);
    }

    public void printAxioms(){
        /* Just for debugging. */
        System.out.println("Printing unsatisfiable axioms.");
        for(OWLAxiom ax:this.unSatOntology.getAxioms()){
            System.out.println(ax.toString());
        }
        System.out.println("==============================");
    }
}
