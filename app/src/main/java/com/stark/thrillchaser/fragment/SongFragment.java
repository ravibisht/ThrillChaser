package com.stark.thrillchaser.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.stark.thrillchaser.R;
import com.stark.thrillchaser.adapter.SongAdapter;

import static com.stark.thrillchaser.activity.MainActivity.musicFiles;


public class SongFragment extends Fragment {

    RecyclerView recyclerView;
    public static SongAdapter songAdapter;
    TextView favoriteSong;

    public static final String SENDER_SONG_LIST = "SONG_LIST";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_song, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        favoriteSong = view.findViewById(R.id.testing);


        if (musicFiles !=null && !(musicFiles.size() < 1)) {
            songAdapter = new SongAdapter(getContext(), musicFiles, SENDER_SONG_LIST);
            recyclerView.setAdapter(songAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        }


        return view;

    }


}