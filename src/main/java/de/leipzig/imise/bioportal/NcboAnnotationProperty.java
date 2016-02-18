package de.leipzig.imise.bioportal;

import org.ncbo.stanford.bean.concept.ClassBean;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import java.util.List;

public class NcboAnnotationProperty {
    private OWLOntologyManager manager;
    private OWLOntology  ontology;
    private String lookupString;
    
    private OWLAnnotationProperty annotationProperty;
    
    public NcboAnnotationProperty(OWLOntologyManager manager, OWLOntology ontology,
                                  IRI id,  String label, String lookupString) throws OWLOntologyChangeException {
        this.manager = manager;
        this.ontology = ontology;
        this.lookupString   = lookupString;
        annotationProperty   = manager.getOWLDataFactory().getOWLAnnotationProperty(id);
        OWLDataFactory factory = manager.getOWLDataFactory();
        OWLAxiom decl = factory.getOWLDeclarationAxiom(annotationProperty);
        manager.addAxiom(ontology, decl);
        OWLAxiom axiom = factory.getOWLAnnotationAssertionAxiom(factory.getOWLAnnotationProperty(OWLRDFVocabulary.RDFS_LABEL.getIRI()),
                                                                id, factory.getOWLLiteral(label));
        manager.addAxiom(ontology, axiom);
    }
    
    @SuppressWarnings("unchecked")
    public void transferFromBioportal(NcboConcept c) throws OWLOntologyChangeException {
        OWLDataFactory factory  = manager.getOWLDataFactory();
        Object annotationValues = c.getBean().getRelations().get(lookupString);
        if (!(annotationValues instanceof List)) {
            return;
        }
        for (Object o2 : (List) annotationValues) {
            if (o2 instanceof String) {
                OWLLiteral  annotationValue = factory.getOWLLiteral((String) o2);
                OWLAxiom axiom = factory.getOWLAnnotationAssertionAxiom(annotationProperty,
                                                                        c.getOwlClass().getIRI(), 
                                                                        annotationValue);
                manager.addAxiom(ontology, axiom);
            } else if (o2 instanceof ClassBean) {
            	String id = ((ClassBean)o2).getId();
            	String label = ((ClassBean)o2).getLabel();
            	OWLLiteral annotationValue = factory.getOWLLiteral(label + " (Id: " + id + ")");
                OWLAxiom axiom = factory.getOWLAnnotationAssertionAxiom(annotationProperty,
                                                                        c.getOwlClass().getIRI(), 
                                                                        annotationValue);
                manager.addAxiom(ontology, axiom);
            }
        }
    }
}
