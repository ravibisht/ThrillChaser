package com.stark.thrillchaser.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.stark.thrillchaser.R;
import com.stark.thrillchaser.adapter.AlbumAdapter;


import static com.stark.thrillchaser.activity.MainActivity.musicFiles;


public class AlbumFragment extends Fragment {


    RecyclerView recyclerView;
    AlbumAdapter albumAdapter;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);

        if ( musicFiles!=null && !(musicFiles.size() < 1)) {
            albumAdapter = new AlbumAdapter(getContext(), musicFiles);
            recyclerView.setAdapter(albumAdapter);
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        }

        return view;

    }
}