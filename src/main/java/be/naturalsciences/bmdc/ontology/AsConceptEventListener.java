/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.naturalsciences.bmdc.ontology;

import java.beans.PropertyChangeListener;

/**
 *
 * @author thomas
 */
public interface AsConceptEventListener extends PropertyChangeListener {

    public void nodeAdded(AsConceptEvent nme);

    public void nodeDestroyed(AsConceptEvent ne);

    public void nodeRenamed(AsConceptEvent ne);

}
