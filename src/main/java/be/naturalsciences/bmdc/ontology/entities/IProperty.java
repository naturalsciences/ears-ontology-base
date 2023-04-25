/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.naturalsciences.bmdc.ontology.entities;

import java.io.Serializable;
import java.util.Collection;

/**
 *
 * @author Thomas Vandenberghe
 */
public interface IProperty<E extends IEarsTerm, EV extends IEventDefinition, OV extends IObjectValue> extends AsConcept<E>, Serializable {

    /**
     * Adds the given event definition to this property. Note that this method
     * does not add this property to the event definition as well!
     *
     */
    public void addEventDefinition(EV ev);

    /**
     * Adds the given object value to this property. Note that this method does
     * not add this property to the object value as well!
     *
     */
    public void addObjectValue(OV ov);

    public String getDimension();

    public Collection<? extends IEventDefinition> getEventDefinitionCollection();

    public String getFormatPattern();

    public Collection<? extends IObjectValue> getObjectValueCollection();

    public String getUnit();

    public boolean isMandatory();

    public boolean isMultiple();

    public void setDimension(String dimension);

    public void setEventDefinitionCollection(Collection<EV> eventDefinitionCollection);

    public void setFormatPattern(String formatPattern);

    public void setMandatory(boolean mandatory);

    public void setMultiple(boolean multiple);

    public void setObjectValueCollection(Collection<OV> objectValueCollection);

    public void setUnit(String unit);

    public String getValueClass();

    public void setValueClass(String valueClass);
}
