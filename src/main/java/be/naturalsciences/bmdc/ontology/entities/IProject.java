/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.naturalsciences.bmdc.ontology.entities;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Thomas Vandenberghe
 */
public interface IProject<E extends IEarsTerm> extends AsConcept<E>, Serializable {

    public Date getStartDate();

    public void setStartDate(Date startDate);

    public Date getEndDate();

    public void setEndDate(Date endDate);

    public String getCountry();

    public void setCountry(String country);

    public String getAuthor();

    public void setAuthor(String author);

    public String getOriginator();

    public void setOriginator(String originator);

    public String getPointOfContact();

    public void setPointOfContact(String pointOfContact);

}
