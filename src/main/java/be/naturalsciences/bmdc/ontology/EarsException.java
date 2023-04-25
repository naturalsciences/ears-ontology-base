/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.naturalsciences.bmdc.ontology;

/**
 * A checked exception class to indicate general problems with the EARS
 * application.
 *
 * @author thomas
 */
public class EarsException extends Exception {

    public EarsException(String string) {
        super(string);
    }

    public EarsException(String string, Throwable thrwbl) {
        super(string, thrwbl);
    }

}
