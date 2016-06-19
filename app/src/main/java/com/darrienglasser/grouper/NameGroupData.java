package com.darrienglasser.grouper;

import io.realm.RealmObject;
import io.realm.annotations.Index;

/**
 * Class used to save data for groups of lists of names.
 */
public class NameGroupData extends RealmObject {
    /**
     * id of names to get.
     */
    @Index
    String id;

    /**
     * Name of group.
     */
    String mGroupName;

    /**
     * Size of subgroups of names.
     */
    int mSubGroupSize;

    /**
     * Public constructor. Required for Realm.
     */
    public NameGroupData() {}

    /**
     * Constructor.
     *
     * @param name Name of group.
     * @param id id of names to pull from database.
     * @param subGroupSize Size of groups of names.
     */
    public NameGroupData(String name, String id, int subGroupSize) {
        mGroupName = name;
        this.id = id;
        mSubGroupSize = subGroupSize;
    }

    public String getId() {
        return id;
    }

    public int getSubGroupSize() {
        return mSubGroupSize;
    }

    public String getGroupName() {
        return mGroupName;
    }
}
