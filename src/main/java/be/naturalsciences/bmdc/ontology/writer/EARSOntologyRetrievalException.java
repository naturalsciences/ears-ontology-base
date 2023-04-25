/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.naturalsciences.bmdc.ontology.writer;

/**
 * An exception class used to indicate problems with parsing any response from the EARS ontology server (the base ontology or the axioms underlying it). 
 * Does NOT include connection errors, these are IOExceptions.
 * 
 * @author Thomas Vandenberghe
 */
public class EARSOntologyRetrievalException extends Exception {

    public EARSOntologyRetrievalException(String msg, Exception ex) {
        super(msg, ex);
    }
}
