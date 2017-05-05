Belief Revision on OWL ontologies.

To run, go to reasoner/target/
download the jar file and both .n3 files.
the jar file is a package and contains all the dependencies.
To run,
$ java -jar reasoner.jar test.n3 update.n3

The script updates the ontology in test.n3 with the axioms in update.n3 and outputs a revised ontology revised.n3
It can only handle clashes between Abox class assertions and subclass axioms in the TBox for now. 
test.n3 has to be consistent to begin with. Inconsistency causing axioms can be put in update.n3
