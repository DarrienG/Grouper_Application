package com.darrienglasser.grouper;

import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.List;

/**
 * Adapter for {@link NameCreatorActivityAdapter}.
 */
public class NameCreatorActivityAdapter
        extends RecyclerView.Adapter<NameCreatorActivityAdapter.ViewHolder> {

    private static List<String> mNameList;

    private static OnItemClickListener mItemClickListener;

    interface OnItemClickListener {
        void onDestroyRowClickListener(View v, int position);
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, TextWatcher {

        EditText mNameEntryText;
        ImageButton mRemoveButton;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            mNameEntryText = (EditText) itemLayoutView.findViewById(R.id.entry_view);
            mNameEntryText.addTextChangedListener(this);
            mRemoveButton = (ImageButton) itemLayoutView.findViewById(R.id.remove_button);
            mRemoveButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mItemClickListener != null) {
                mItemClickListener.onDestroyRowClickListener(view, getAdapterPosition());
            }
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            // no op
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            // no op
        }

        @Override
        public void afterTextChanged(Editable editable) {
            mNameList.set(getAdapterPosition(), editable.toString());
        }
    }

    /**
     * Public constructor.
     *
     * @param nameList List of names to put in Adapter.
     */
    public NameCreatorActivityAdapter(List<String> nameList) {
        mNameList = nameList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemLayoutView =
                LayoutInflater.from(
                        parent.getContext()).inflate(R.layout.new_name_row, parent, false);
        return new ViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mNameEntryText.setText(mNameList.get(position));
    }

    @Override
    public int getItemCount() {
        return mNameList.size();
    }

    /**
     * Helper method. Used for quickly adding a new entry to RecyclerView.
     */
    public void addItem() {
        mNameList.add("");
        notifyItemInserted(mNameList.size() - 1);
    }


    /**
     * Helper method. Returns list of names.
     *
     * @return List of names.
     */
    public List<String> getNameList() {
        return mNameList;
    }

    /**
     * Helper method. Quickly removes item in row, and updates the list.
     *
     * @param position Position to remove.
     */
    public void removeRow(int position) {
        // If the user interacts with the last item before the layout stops animating, the adapter
        // will pass -1 and kill the app. >_>
        if (position == -1) {
            position = 0;
        }
        mNameList.remove(position);
        notifyItemRemoved(position);
    }
}
