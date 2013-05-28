/*Copyright (C) 2010-2012 Institute for Software Integrated Systems (ISIS)
This software was developed by the Institute for Software Integrated
Systems (ISIS) at Vanderbilt University, Tennessee, USA for the 
Transformative Apps program under DARPA, Contract # HR011-10-C-0175.
The United States Government has unlimited rights to this software. 
The US government has the right to use, modify, reproduce, release, 
perform, display, or disclose computer software or computer software 
documentation in whole or in part, in any manner and for any 
purpose whatsoever, and to have or authorize others to do so.
 */

package edu.vu.isis.ammo.api.type;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * The form is used to convey some selection of objects.
 */

public class Form extends AmmoType implements Map<String, String> {
    static final Logger logger = LoggerFactory.getLogger("type.form");

    static final public Form RESET = null;

    private final Map<String, String> backing;

    // *********************************
    // Parcelable Support
    // *********************************

    public static final Parcelable.Creator<Form> CREATOR =
            new Parcelable.Creator<Form>() {

                @Override
                public Form createFromParcel(Parcel source) {
                    return new Form(source);
                }

                @Override
                public Form[] newArray(int size) {
                    return new Form[size];
                }
            };

    public static Form readFromParcel(Parcel source) {
        if (AmmoType.isNull(source))
            return null;
        return new Form(source);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        plogger.trace("marshall form {}", this);
        // dest.writeMap(this);
    }

    private Form(Parcel in) {
        this.backing = new HashMap<String, String>();
        // in.readMap(this, loader)
        plogger.trace("unmarshall form {}", this);
    }

    // *********************************
    // Standard Methods
    // *********************************
    @Override
    public String toString() {
        return this.backing.toString();
    }

    // *********************************
    // IAmmoReques Support
    // *********************************

    public Form() {
        this.backing = new HashMap<String, String>();
    }

    // *********************************
    // Map Methods
    // *********************************

    @Override
    public void clear() {
        this.backing.clear();
    }

    @Override
    public boolean containsKey(Object key) {
        return this.backing.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.backing.containsValue(value);
    }

    @Override
    public Set<java.util.Map.Entry<String, String>> entrySet() {
        return this.backing.entrySet();
    }

    @Override
    public String get(Object key) {
        return this.backing.get(key);
    }

    @Override
    public boolean isEmpty() {
        return this.backing.isEmpty();
    }

    @Override
    public Set<String> keySet() {
        return this.backing.keySet();
    }

    @Override
    public String put(String key, String value) {
        return this.backing.put(key, value);
    }

    @Override
    public void putAll(Map<? extends String, ? extends String> arg0) {
        this.backing.putAll(arg0);
    }

    @Override
    public String remove(Object key) {
        this.dirtyHashcode.set(true);
        return this.backing.remove(key);
    }

    @Override
    public int size() {
        return this.backing.size();
    }

    @Override
    public Collection<String> values() {
        return this.backing.values();
    }

    /**
     * check that the two objects are logically equal.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Form))
            return false;
        final Form that = (Form) obj;
        if (AmmoType.differ(this.backing, that.backing))
            return false;
        return true;
    }

    @Override
    public synchronized int hashCode() {
        if (!this.dirtyHashcode.getAndSet(false))
            return this.hashcode;
        this.hashcode = AmmoType.HashBuilder.newBuilder()
                .increment(this.backing)
                .hashCode();
        return this.hashcode;
    }

    @Override
    public String asString() {
        logger.error("asString() not implemented");
        return null;
    }

}