/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.naturalsciences.bmdc.ontology.entities;

import java.io.Serializable;

/**
 *
 * @author Thomas Vandenberghe
 */
public interface IMetaTerm<E extends IEarsTerm> extends AsConcept<E>, Serializable {

    public String getAcronym();

    public void setAcronym(String acronym);
}
