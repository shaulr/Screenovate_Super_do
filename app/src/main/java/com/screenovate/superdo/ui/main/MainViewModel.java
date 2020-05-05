package com.screenovate.superdo.ui.main;

import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.screenovate.superdo.data.Bag;

import java.util.LinkedList;
import java.util.List;

public class MainViewModel extends ViewModel {
    private List<Bag> bags = new LinkedList<Bag>();
    private BagListener listener;

    public void addBag(Bag bag) {
        bags.add(bag);
        listener.onNewBag(bag);
    }

    void setBagListener(BagListener listener) {
        this.listener = listener;
    }

    public List<Bag> getList() {
        return bags;
    }
}
