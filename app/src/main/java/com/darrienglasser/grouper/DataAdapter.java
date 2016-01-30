package com.darrienglasser.grouper;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmResults;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {

    /**
     * Data bound to views.
     */
    private static RealmResults<GroupData> sDataSet;

    /**
     * Android context.
     */
    private Context mContext;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public CardView mExpansiveView;
        public TextView mNameHolder;
        public TextView mNumGroupsHolder;
        public TextView mNumSGroupsHolder;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            mNameHolder = (TextView) itemLayoutView.findViewById(R.id.custName);
            mNumGroupsHolder = (TextView) itemLayoutView.findViewById(R.id.numGroups);
            mNumSGroupsHolder = (TextView) itemLayoutView.findViewById(R.id.numSGroups);
            mExpansiveView = (CardView) itemLayoutView.findViewById(R.id.card_view);
            mExpansiveView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Intent intent = new Intent(v.getContext(), ActionActivity.class);

            intent.putExtra("DataPos", position);
            v.getContext().startActivity(intent);
        }
     }

    /**
     * Public constructor to initialize DataAdapter.
     *
     * @param dataset Data pushed to adapter.
     */
    public DataAdapter(RealmResults<GroupData> dataset, Context context){
        sDataSet = dataset;
        mContext = context;
    }

    @Override
    public DataAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int ViewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.main_row, parent, false);

        return new ViewHolder(itemLayoutView);
    }

    // Provide data to each of the positions in the RecyclerView
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.mNameHolder.setText(String.format(
                mContext.getString(R.string.dyn_string), sDataSet.get(position).getName()));

        holder.mNumGroupsHolder.setText(String.format(
                mContext.getString(R.string.num_groups), sDataSet.get(position).getNumGroups()));

        holder.mNumSGroupsHolder.setText(String.format(
                mContext.getString(R.string.subg_amt), sDataSet.get(position).getSgSize()));

    }

    // Get size of data set
    @Override
    public int getItemCount() {
        if (sDataSet == null){
            return 0;
        }
        return sDataSet.size();
    }

    public void removeItem (final int position, Realm realm) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                sDataSet.deleteFromRealm(position);
            }
        });
        notifyItemRemoved(position);
    }

    /**
     * Convenience method for destroying all items in recyclerview with a nice animation.
     */
    public void destroyAll(Realm realm) {
        notifyItemRangeRemoved(0, getItemCount());
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.deleteAll();
            }
        });
    }
}
