package de.leipzig.imise.bioportal;

import org.semanticweb.owlapi.model.OWLClass;

import de.leipzig.imise.bioportal.bean.concept.ClassBean;

public interface NcboConcept {
    String getName();
    ClassBean getBean();
    OWLClass getOwlClass();
}
