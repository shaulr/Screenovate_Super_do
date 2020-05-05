package com.screenovate.superdo.ui.main;

import android.graphics.Color;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.screenovate.superdo.R;
import com.screenovate.superdo.data.Bag;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class BagAdapter extends ListAdapter<Bag, BagViewHolder> {
    List<Bag> mOrigList = new ArrayList<>();
    List<Bag> mFilteredList = new ArrayList<>();

    private BagFilter filter;
    private RecyclerView mRecyclerView;

    protected BagAdapter(@NonNull DiffUtil.ItemCallback<Bag> diffCallback) {
        super(diffCallback);
        filter = new BagFilter(this);
    }
    public BagFilter getFilter() {
        return filter;
    }

    @NonNull
    @Override
    public BagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bag_view_holder, parent, false);
        return new BagViewHolder(itemView);
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        mRecyclerView = recyclerView;
    }
    @Override
    public void onBindViewHolder(@NonNull BagViewHolder holder, int position) {
        Bag bag = getItem(position);
        holder.color.setBackgroundColor( Color.parseColor(bag.getBagColor()));

        holder.name.setText(bag.getName());
        holder.weight.setText(bag.getWeight());
    }

    public void addBag(Bag bag) {
        mOrigList.add(bag);
        getFilter().filter(filter.filterString);
    }


    public class BagFilter extends Filter {

        private BagAdapter adapter;
        public boolean isFiltering = false;
        public String filterString = "";
        public BagFilter(BagAdapter adapter) {
            this.adapter = adapter;
        }


        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if(constraint.length() == 0) {
                results.values = mOrigList;
                results.count = mOrigList.size();
                return results;
            }
            isFiltering = true;
            filterString = constraint.toString();

            final List<Bag> list = getCurrentList();

            int count = list.size();
            List<Bag> tempFiltered = new ArrayList<Bag>(count);

            Bag filterableString;

            for (int i = 0; i < count; i++) {
                if (list.get(i).getWeight().startsWith(filterString)) {
                    tempFiltered.add(list.get(i));
                }
            }

            results.values = tempFiltered;
            results.count = tempFiltered.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            List<Bag> currentList = (List<Bag>) results.values;
            adapter.submitList(currentList);
            adapter.notifyItemInserted(currentList.size() - 1);
            mRecyclerView.scrollToPosition(currentList.size() - 1);
            mFilteredList.add(currentList.get(currentList.size() - 1));
        }

    }

}
