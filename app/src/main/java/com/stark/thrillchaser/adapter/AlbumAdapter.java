package com.stark.thrillchaser.adapter;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.stark.thrillchaser.R;
import com.stark.thrillchaser.activity.AlbumDetailActivity;
import com.stark.thrillchaser.model.MusicFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {

    private static final String TAG = "AlbumAdapter";

    private Context mcontext;
    private ArrayList<MusicFile> albumMusicFiles;
    private HashMap<String,String> albumTracker;
    private ArrayList<MusicFile> albumList;

    public AlbumAdapter(Context context, ArrayList<MusicFile> albumMusicFiles) {
        this.mcontext = context;
        this.albumMusicFiles = albumMusicFiles;
        albumTracker=new HashMap<>() ;
        albumList=new ArrayList<>();

        filterAlbum();
    }

    private void filterAlbum() {

        for (int i = 0; i <albumMusicFiles.size() ; i++) {
            albumTracker.put(albumMusicFiles.get(i).getAlbum(),albumMusicFiles.get(i).getPath());
        }
        for (String albumName: albumTracker.keySet()) {
            albumList.add(new MusicFile(albumName,albumTracker.get(albumName)));
        }


    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mcontext).inflate(R.layout.album_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        Log.i(TAG, "onBindViewHolder: "+albumTracker.size());

          holder.albumTitle.setText(albumList.get(position).getAlbum());

           final byte[] image = getSongImage(albumList.get(position).getPath());


            if (image != null) {
                Glide.with(mcontext)
                        .asBitmap()
                        .load(image)
                        .into(holder.albumImage);
            } else {
                Glide.with(mcontext)
                        .load(R.drawable.music)
                        .into(holder.albumImage);
            }


        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(mcontext,AlbumDetailActivity.class);
                intent.putExtra("albumName",albumList.get(position).getAlbum());
                mcontext.startActivity(intent);
            }
        });
}


    @Override
    public int getItemCount() {
        return albumTracker.size();
    }

    public static byte[] getSongImage(String path) {

        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(path);
        byte[] image = mediaMetadataRetriever.getEmbeddedPicture();
        mediaMetadataRetriever.release();

        return image;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CardView cardView;
        private ImageView albumImage;
        private TextView albumTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.album_cardview);
            albumImage = itemView.findViewById(R.id.album_image);
            albumTitle = itemView.findViewById(R.id.album_title);

        }


    }
}
