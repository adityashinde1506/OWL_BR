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
    private OWLOntology KB;
    private HashSet<OWLAxiom> unSatAxioms = new HashSet<OWLAxiom>();

    public RevisionModule(OWLReasoner reasoner,OWLOntology kb){
        this.reasoner=reasoner;
        this.KB=kb;
        System.out.println("Revision module initialised.");
    }

    public void addUnSatAxiom(OWLAxiom axiom){
        /* Add a single unsatisfaible axiom to the list. */
        if(!this.unSatAxioms.contains(axiom)){
            this.unSatAxioms.add(axiom);
        }
    }

    public boolean discardInconsistentAxioms(OWLOntologyManager manager){
        List<OWLOntologyChange> changes=manager.removeAxioms(this.KB,this.unSatAxioms);
        return true;
    }


    private void printAxioms(){
        /* Just for debugging. */
        System.out.println("Printing unsatisfiable axioms.");
        for(OWLAxiom ax:this.unSatAxioms){
            System.out.println(ax.toString());
        }
        System.out.println("==============================");
    }
}
