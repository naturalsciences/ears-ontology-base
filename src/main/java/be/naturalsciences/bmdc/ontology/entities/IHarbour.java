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
public interface IHarbour<E extends IEarsTerm> extends AsConcept<E>, Serializable {

    public String getCountry();

    public Double getLatDec();

    public Double getLonDec();

    public void setCountry(String country);

    public void setLatDec(Double latDec);

    public void setLonDec(Double lonDec);
}
