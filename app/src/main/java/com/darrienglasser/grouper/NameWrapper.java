package com.darrienglasser.grouper;

import io.realm.RealmObject;
import io.realm.annotations.Index;

/**
 * POJO data wrapper. Used to store data for list of names.
 */
public class NameWrapper extends RealmObject {

    /**
     * Name to store.
     */
    private String mName;

    /**
     * Group to store id in.
     */
    @Index
    private String id;

    /**
     * Default constructor. Required for Realm.
     */
    public NameWrapper() {}

    public NameWrapper(String mName, String id) {
        this.mName = mName;
        this.id = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getId() {
        return id;
    }
}
