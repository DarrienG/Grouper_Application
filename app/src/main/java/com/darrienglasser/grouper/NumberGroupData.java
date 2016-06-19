package com.darrienglasser.grouper;

import io.realm.RealmObject;

/**
 * Basic POJO data wrapper to hold all attributes for numbered lists.
 */
public class NumberGroupData extends RealmObject {
    /** Custom name for group. */
    private String mName;

    /** Number of total groups. */
    private int mNumGroups;

    /** Subgroup size. */
    private int mSgSize;

    /**
     * Default constructor. Required for Realm.
     */
    public NumberGroupData() {}

    /**
     * Public constructor used to initialize group data.
     * @param name Name of the desired group.
     * @param numGroups Number of desired groups.
     * @param sgSize Number of desired subgroups.
     */
    public NumberGroupData(String name, int numGroups, int sgSize) {
        mName = name;
        mNumGroups = numGroups;
        mSgSize = sgSize;
    }

    public String getName() {
        return mName;
    }

    public int getNumGroups() {
        return mNumGroups;
    }

    public int getSgSize() {
        return mSgSize;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    @Override
    public String toString() {
        return "NumberGroupData{" +
                ", mName='" + mName + '\'' +
                ", mNumGroups=" + mNumGroups +
                ", mSgSize=" + mSgSize +
                '}';
    }
}
