package com.darrienglasser.grouper;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Adapter for {@link NameCreatorFragment}.
 */
public class NameCreatorMainAdapter
        extends RecyclerView.Adapter<NameCreatorMainAdapter.ViewHolder> {

    /**
     * Data used to populate rows.
     */
    RealmResults<NameWrapper> mListData;

    /**
     * Easy access to database.
     */
    Realm mRealm;

    /**
     * Easy callback for item clicking.
     */
    private static OnItemClickListener mItemClickListener;

    /**
     * Generic ClickListener for row in view.
     */
    interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    /**
     * Setter for ClickListener.
     *
     * @param itemClickListener Item click listener.
     */
    public void setOnClickListener(OnItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private View mCardView;
        private TextView mCustNameView;
        private TextView mShortNameList;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            mCardView = itemLayoutView.findViewById(R.id.card_view);
            mCustNameView = (TextView) itemLayoutView.findViewById(R.id.custName);
            mShortNameList = (TextView) itemLayoutView.findViewById(R.id.nameShortList);
            mCardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(view, getAdapterPosition());
            }
        }
    }

    /**
     * Constructor.
     *
     * @param listData List of id's.
     * @param realm Database accessor.
     */
    public NameCreatorMainAdapter(RealmResults<NameWrapper> listData, Realm realm) {
        mListData = listData;
        mRealm = realm;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView = LayoutInflater.from(
                parent.getContext()).inflate(R.layout.name_creator_main_row, parent, false);

        return new ViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mCustNameView.setText(mListData.get(position).getId());
        RealmResults<NameWrapper> nameWrappers =
                mRealm.where(
                        NameWrapper.class)
                        .equalTo("id", mListData.get(position).getId()).findAll();

        String shortNames = "";

        for (int i = 0; i < nameWrappers.size() && i < 5; ++i) {
            shortNames += nameWrappers.get(i).getName() + ", ";
        }

        shortNames = shortNames.substring(0, shortNames.length() - 2);
        if (nameWrappers.size() > 5) {
            shortNames += "...";
        }
        holder.mShortNameList.setText(shortNames);
    }

    @Override
    public int getItemCount() {
        return mListData.size();
    }

    /**
     * Helper method. Quickly removes item in row, and updates the list.
     *getId
     * @param position Position to remove.
     */
    public void removeRow(final int position) {
        final String id = mListData.get(position).getId();
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                mRealm.where(NameWrapper.class)
                        .equalTo("id", id)
                        .findAll().deleteAllFromRealm();

                mRealm.where(NameGroupData.class)
                        .equalTo("id", id)
                        .findAll().deleteAllFromRealm();

            }
        });
        notifyItemRemoved(position);
    }
}
