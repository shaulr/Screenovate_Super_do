package com.screenovate.superdo.ui.main;

import android.graphics.Color;

import android.util.Log;
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

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;

public class BagAdapter extends ListAdapter<Bag, BagViewHolder> {
    private final static String TAG = BagAdapter.class.getSimpleName();
    private List<Bag> items = new ArrayList<>();
    private List<Bag> filteredItems = new ArrayList<>();
    private CountDownLatch filterLock = new CountDownLatch(0);
    private BagFilter filter;
    private RecyclerView mRecyclerView;

    BagAdapter(@NonNull DiffUtil.ItemCallback<Bag> diffCallback) {
        super(diffCallback);
        filter = new BagFilter(this);
    }
    BagFilter getFilter() {
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
    public void onAttachedToRecyclerView(@NotNull RecyclerView recyclerView) {
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

    void addBag(Bag bag) {
        if((getFilter().filterString.length() > 0 &&
                bag.getWeight().startsWith(getFilter().filterString) )) {
            try {
                filterLock.await();
            } catch (InterruptedException e) {
                Log.e(TAG, Log.getStackTraceString(e));
            }
            filteredItems.add(bag);
            submitList(filteredItems);
            notifyItemInserted(filteredItems.size() - 1);
            mRecyclerView.scrollToPosition(filteredItems.size() - 1);
            items.add(bag);
            filterLock.countDown();
            return;
        }

        items.add(bag);
        if(getFilter().filterString.length() == 0) {
            submitList(items);
            notifyItemInserted(items.size() - 1);
            mRecyclerView.scrollToPosition(items.size() - 1);
        }
    }


    public class BagFilter extends Filter {

        private BagAdapter adapter;
        String filterString = "";
        BagFilter(BagAdapter adapter) {
            this.adapter = adapter;
        }


        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            filterString = constraint.toString();

            if(constraint.length() == 0) {
                results.values = items;
                results.count = items.size();
                return results;
            }

            filterLock = new CountDownLatch(1);
            int count = items.size();
            List<Bag> tempFiltered = new ArrayList<>(count);

            Bag filterableString;

            for (int i = 0; i < count; i++) {
                if (items.get(i).getWeight().startsWith(filterString)) {
                    tempFiltered.add(items.get(i));
                }
            }

            results.values = tempFiltered;
            results.count = tempFiltered.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if(filterString.length() > 0) {
                filteredItems.clear();
                filteredItems.addAll((List<Bag>) results.values);
                adapter.submitList(filteredItems);
                mRecyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            } else {
                adapter.submitList(items);
                mRecyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            }
            filterLock.countDown();

        }

    }

}
