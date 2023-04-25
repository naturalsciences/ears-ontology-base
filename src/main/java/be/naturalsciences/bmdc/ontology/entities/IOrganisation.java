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
public interface IOrganisation<E extends IEarsTerm, O extends IOrganisation> extends AsConcept<E>, Serializable {

    public String getAdministrativeArea();

    public String getCity();

    public IOrganisation getCollateCentre();

    public String getCountry();

    public String getDeliveryPoint();

    public String getElectronicMailAddress();

    public String getFacsimile();

    public Double getLatDec();

    public Double getLonDec();

    public String getOnlineResource();

    public String getPostalCode();

    public String getVoice();

    public void setAdministrativeArea(String administrativeArea);

    public void setCity(String city);

    public void setCollateCentre(O collateCentre);

    public void setCountry(String country);

    public void setDeliveryPoint(String deliveryPoint);

    public void setElectronicMailAddress(String electronicMailAddress);

    public void setFacsimile(String facsimile);

    public void setLatDec(Double latDec);

    public void setLonDec(Double lonDec);

    public void setOnlineResource(String onlineResource);

    public void setPostalCode(String postalCode);

    public void setVoice(String voice);
}
