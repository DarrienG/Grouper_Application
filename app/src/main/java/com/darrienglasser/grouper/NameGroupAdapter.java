package com.darrienglasser.grouper;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.realm.Realm;
import io.realm.RealmResults;

public class NameGroupAdapter extends RecyclerView.Adapter<NameGroupAdapter.ViewHolder> {

    /**
     * Contains list of names to display.
     */
    private RealmResults<NameGroupData> mNameContainer;

    /**
     * Android context.
     */
    private Context mContext;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        /**
         * Main card container.
         */
        View mCardView;

        /**
         * Custom name view.
         */
        TextView mCustNameView;

        /**
         * View containing ID of group.
         */
        TextView mIdView;

        /**
         * View containing number of groups.
         */
        TextView mNumGroupsView;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            mCardView = itemLayoutView.findViewById(R.id.card_view);
            mCustNameView = (TextView) itemLayoutView.findViewById(R.id.custName);
            mIdView = (TextView) itemLayoutView.findViewById(R.id.group_id);
            mNumGroupsView = (TextView) itemLayoutView.findViewById(R.id.numSGroups);
            mCardView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(v.getContext(), NameListActionActivity.class);
            intent.putExtra("position", getAdapterPosition());
            v.getContext().startActivity(intent);
        }
    }

    /**
     * Public constructor.
     *
     * @param nameContainer Data to pass into adapter.
     * @param context Android context.
     */
    public NameGroupAdapter(RealmResults<NameGroupData> nameContainer, Context context) {
        mNameContainer = nameContainer;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView =
                LayoutInflater.from(
                        parent.getContext()).inflate(R.layout.name_group_row, parent, false);

        return new ViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mCustNameView.setText(mNameContainer.get(position).mGroupName);
        holder.mIdView.setText(
                String.format(
                        mContext.getString(R.string.dyn_id),
                        mNameContainer.get(position).getId()));

        holder.mNumGroupsView.setText(String.format(
                mContext.getString(R.string.subg_amt),
                mNameContainer.get(position).getSubGroupSize()));

    }

    @Override
    public int getItemCount() {
        return mNameContainer.size();
    }

    /**
     * Helper method to quickly remove item from list and Realm database.
     * @param position Row to remove.
     */
    public void removeRow(final int position, Realm realm) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(NameGroupData.class).findAll().deleteFromRealm(position);
            }
        });
        notifyItemRemoved(position);
    }

}
