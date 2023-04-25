/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package be.naturalsciences.bmdc.ontology.writer;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author Thomas Vandenberghe
 * @param <Scope>
 * @param <String>
 */
public class ScopeMap extends LinkedHashMap {

    public static enum Scope {

        BASE, STATIC, VESSEL, PROGRAM, TEST
    };

    private static final int MAX_ENTRIES = 1;

    public static final ScopeMap BASE_SCOPE;
    public static final ScopeMap STATIC_SCOPE;
    public static final ScopeMap VESSEL_SCOPE;
    public static final ScopeMap PROGRAM_SCOPE;
    public static final ScopeMap TEST_SCOPE;

    static {
        BASE_SCOPE = new ScopeMap(Scope.BASE, "");
        //BASE_SCOPE.put(Scope.BASE, null);

        STATIC_SCOPE = new ScopeMap(Scope.STATIC, "");
        //STATIC_SCOPE.put(Scope.STATIC, null);

        VESSEL_SCOPE = new ScopeMap(Scope.VESSEL, null);
        //VESSEL_SCOPE.put(Scope.VESSEL, null);

        PROGRAM_SCOPE = new ScopeMap(Scope.PROGRAM, null);
        //PROGRAM_SCOPE.put(Scope.PROGRAM, null);

        TEST_SCOPE = new ScopeMap(Scope.TEST, "");
    }

    public ScopeMap(Scope scope, String scopedTo) {
        this.put(scope, scopedTo);
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry eldest) {
        return size() > MAX_ENTRIES;
    }

    public String getScopedTo() {
        if (this.values() != null && this.values().size() > 0) {
            return (String) this.values().toArray()[0];
        }
        return null;
    }

    public Scope getScope() {
        if (this.keySet() != null && this.keySet().size() > 0) {
            return (Scope) this.keySet().toArray()[0];
        }
        return null;
    }

    public java.lang.String getScopeString() {
        return getScope().name();
    }

    public boolean sameScope(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ScopeMap)) {
            return false;
        }
        ScopeMap s = (ScopeMap) o;
        return s.getScope() == this.getScope();
    }

    @Override
    public int hashCode() {
        return this.getScope().hashCode();
    }

    /**
     *
     * @param scope
     * @return
     */
    public static Scope getScope(String scoped) {
        for (Scope scopemap : Scope.values()) {
            if (scoped.equals(scopemap.toString())) {
                return scopemap;
            }
        }
        return null;
    }
}
