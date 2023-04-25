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
public interface IEarsTerm<A extends IAction, E extends IEarsTerm, IS extends IItemStatus, LDe extends IEarsTermLabelDe, LEn extends IEarsTermLabelEn, LEs extends IEarsTermLabelEs, LFr extends IEarsTermLabelFr, LIt extends IEarsTermLabelIt, LNl extends IEarsTermLabelNl, SEV extends ISpecificEventDefinition, M extends IMetaTerm, P extends IProcess, PM extends IParameter, PR extends IProperty, S extends ISubject, T extends ITool, TC extends IToolCategory> extends Serializable, Term<E, IS> {

    public static enum Language {

        de, en, es, fr, it, nl
    }
}
