package com.stark.thrillchaser.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.stark.thrillchaser.R;
import com.stark.thrillchaser.adapter.SongAdapter;
import com.stark.thrillchaser.model.MusicFile;

import java.util.ArrayList;

import static com.stark.thrillchaser.activity.MainActivity.musicFiles;

public class AlbumDetailActivity extends AppCompatActivity {

    public static final String SENDER_ALBUM_LIST="ALBUM_LIST";
    private static final String TAG = "AlbumDetailActivity";
    static ArrayList<MusicFile> albumMusicFilesList;
    private ImageView albumImage;
    private TextView tvAlbumName;
    private RecyclerView recyclerView;
    private SongAdapter songAdapter;
    private String albumName;

    public static byte[] getSongImage(String path) {

        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(path);
        byte[] image = mediaMetadataRetriever.getEmbeddedPicture();
        mediaMetadataRetriever.release();

        return image;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_detail);
        albumImage = findViewById(R.id.album_cover_image);
        recyclerView = findViewById(R.id.rec_album_list);
        tvAlbumName=findViewById(R.id.album_name);

        albumMusicFilesList = new ArrayList<>();
        albumName = getIntent().getStringExtra("albumName");
        tvAlbumName.setText(albumName);

        getSupportActionBar().hide();


        int j = 0;
        for (int i = 0; i < musicFiles.size(); i++) {

            if (albumName.equals(musicFiles.get(i).getAlbum())) {
                System.out.println("Album " + i + " : " + musicFiles.get(i).getAlbum());
                albumMusicFilesList.add(j, musicFiles.get(i));
                Log.i(TAG, "onCreate:  " + musicFiles.get(i).getAlbum());
                j++;
            }

        }

        songAdapter = new SongAdapter(this, albumMusicFilesList,SENDER_ALBUM_LIST);
        recyclerView.setAdapter(songAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // final byte[] image = getSongImage(musicFilesList.get(0).getPath());


        if (false) {
            Glide.with(this)
                    .asBitmap()
                    .load(false)
                    .into(albumImage);
        } else {
            Glide.with(this)
                    .load(R.drawable.music)
                    .into(albumImage);
        }

    }


}