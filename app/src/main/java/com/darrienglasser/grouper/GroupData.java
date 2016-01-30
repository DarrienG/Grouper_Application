package com.darrienglasser.grouper;

import io.realm.RealmObject;

/**
 * Basic POJO data wrapper to hold all attributes for grouping application.
 */
public class GroupData extends RealmObject {
    /** Custom name for group. */
    private String mName;

    /** Number of total groups. */
    private int mNumGroups;

    /** Subgroup size. */
    private int mSgSize;

    /**
     * Default constructor. Required for Realm.
     */
    public GroupData() {}

    /**
     * Public constructor used to initialize group data.
     * @param name Name of the desired group.
     * @param numGroups Number of desired groups.
     * @param sgSize Number of desired subgroups.
     */
    public GroupData(String name, int numGroups, int sgSize) {
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

      public void setNumGroups(int mNumGroups) {
        this.mNumGroups = mNumGroups;
    }

    public void setSgSize(int sgSize) {
        mSgSize = sgSize;
    }

    @Override
    public String toString() {
        return "GroupData{" +
                ", mName='" + mName + '\'' +
                ", mNumGroups=" + mNumGroups +
                ", mSgSize=" + mSgSize +
                '}';
    }
}
