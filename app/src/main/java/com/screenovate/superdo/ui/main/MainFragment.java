package com.screenovate.superdo.ui.main;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.screenovate.superdo.R;
import com.screenovate.superdo.data.Bag;
import com.screenovate.superdo.data.SuperDoModel;

import java.util.Objects;

import okhttp3.OkHttpClient;

public class MainFragment extends Fragment {

    private Button startBtn;
    private Button stopBtn;
    private SuperDoModel mDataModel;
    private RecyclerView mBagList;
    private BagAdapter mAdapter;
    private EditText mFilterEdit;
    public static MainFragment newInstance() {
        return new MainFragment();
    }
    private final Object insertLock = new Object();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View self = inflater.inflate(R.layout.main_fragment, container, false);
        startBtn = self.findViewById(R.id.start_button);
        stopBtn = self.findViewById(R.id.stop_button);
        mBagList = self.findViewById(R.id.bag_list);
        mFilterEdit = self.findViewById(R.id.filter_edit);
        return self;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mDataModel = new SuperDoModel(getContext(), new BagListener() {
            @Override
            public void onNewBag(final Bag bag) {
                Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        synchronized (insertLock) {
                            if(mAdapter != null)
                                mAdapter.addBag(bag);
                        }
                    }
                });
            }
        });
        mAdapter = new BagAdapter(new DiffUtil.ItemCallback<Bag>() {

            @Override
            public boolean areItemsTheSame(@NonNull Bag oldItem, @NonNull Bag newItem) {
                return  oldItem.getBagColor().equals(newItem.getBagColor()) &&
                        oldItem.getName().equals(newItem.getName()) &&
                        oldItem.getWeight().equals(newItem.getWeight());
            }

            @Override
            public boolean areContentsTheSame(@NonNull Bag oldItem, @NonNull Bag newItem) {
                return  oldItem.getBagColor().equals(newItem.getBagColor()) &&
                        oldItem.getName().equals(newItem.getName()) &&
                        oldItem.getWeight().equals(newItem.getWeight());
            }
        });
        mBagList.setAdapter(mAdapter);
        if (startBtn != null) {
            startBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDataModel.startLoading();
                }
            });
        }
        mBagList.setLayoutManager(new LinearLayoutManager(getActivity()));

        if(stopBtn != null) {
            stopBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDataModel.stopLoading();
                }
            });
        }

        mFilterEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                synchronized (insertLock) {
                    mAdapter.getFilter().filter(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

}
