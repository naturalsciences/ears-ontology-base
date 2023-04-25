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
public interface IObjectValue<E extends IEarsTerm, CE extends ICompoundEarsTerm, PR extends IProperty> extends AsConcept<CE>, Serializable {

    public void addCompoundEarsTerm();

    public String getLabel();

    public void setLabel();

    public String getPrefLabel();

    public void setProperty(PR property);

    public IProperty getProperty();

    public Term getValuedTermRef();

    public void setValuedTermRef(E valuedTermRef);

}
