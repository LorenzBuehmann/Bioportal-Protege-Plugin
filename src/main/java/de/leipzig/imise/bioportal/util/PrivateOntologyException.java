package de.leipzig.imise.bioportal.util;

public class PrivateOntologyException extends Exception{
	
	public PrivateOntologyException() {
		super("This ontology is either private or licensed.");
	}

}
