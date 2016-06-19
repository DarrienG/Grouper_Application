package com.darrienglasser.grouper;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class ActionAdapter extends RecyclerView.Adapter<ActionAdapter.ViewHolder> {

    /**
     * Contains the randomized list of groups.
     */
    private ArrayList<ArrayList<String>> mGroupContainer;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        /**
         * The container that holds a portion of the groups.
         */
        private TextView mGroupText;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            mGroupText = (TextView) itemLayoutView.findViewById(R.id.groupList);
        }
    }

    /**
     * Public constructor.
     */
    public ActionAdapter(ArrayList<ArrayList<String>> groupContainer) {
        mGroupContainer = groupContainer;
    }

    @Override
    public ActionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int ViewType) {
        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.number_action_row, parent, false);

        return new ViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String catString = "";

        for (int i = 0; i < mGroupContainer.get(position).size(); ++i) {
            catString += mGroupContainer.get(position).get(i) + "  ";
        }

        holder.mGroupText.setText(catString.trim());
    }

    @Override
    public int getItemCount() {
        return mGroupContainer.size();
    }

    /**
     * Removes single item from RecyclerView and list of data.
     * @param removedItem Item to remove.
     */
    public void updateDataSet(int removedItem) {
        mGroupContainer.remove(removedItem);
        notifyItemRemoved(removedItem);
    }
}
