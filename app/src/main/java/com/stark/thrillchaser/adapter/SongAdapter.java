package com.stark.thrillchaser.adapter;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.stark.thrillchaser.activity.PlayerActivity;
import com.stark.thrillchaser.R;
import com.stark.thrillchaser.model.MusicFile;

import java.io.File;
import java.util.ArrayList;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {
    public static ArrayList<MusicFile> musicFilesSongAdapter;
    private Context context;
    public String sender = "sender";
    public static final String SENDER = "sender";


    public SongAdapter(Context context, ArrayList<MusicFile> musicFiles, String sender) {
        this.context = context;
        musicFilesSongAdapter = musicFiles;
        this.sender = sender;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.music_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {


        holder.musicFileName.setText(musicFilesSongAdapter.get(position).getName());

        final byte[] image = getSongImage(musicFilesSongAdapter.get(position).getPath());


        if (image != null) {
            Glide.with(context)
                    .asBitmap()
                    .load(image)
                    .into(holder.musicImage);
        } else {
            Glide.with(context)
                    .load(R.drawable.music)
                    .into(holder.musicImage);
        }

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PlayerActivity.class);
                intent.putExtra("position", position);
                intent.putExtra(SENDER, sender);
                context.startActivity(intent);
            }
        });


        holder.relativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                deleteSong(position);
                return false;
            }
        });

    }

    private void deleteSong(final int position) {


        final AlertDialog.Builder builder = new AlertDialog.Builder(context);


        builder.setMessage("Do you really want to delete ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                        Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                                Long.parseLong(musicFilesSongAdapter.get(position).getId()));

                        File file = new File(musicFilesSongAdapter.get(position).getPath());
                        boolean deleted = file.delete();

                        if (deleted) {
                            context.getContentResolver().delete(uri, null, null);
                            musicFilesSongAdapter.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, musicFilesSongAdapter.size());
                            Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("No", null);


        final AlertDialog alertDialog = builder.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.GREEN);
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);

            }
        });

        alertDialog.setTitle("Deleting the song");
        alertDialog.show();

    }


    @Override
    public int getItemCount() {
        return musicFilesSongAdapter.size();
    }

    private byte[] getSongImage(String path) {

        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(path);
        byte[] image = mediaMetadataRetriever.getEmbeddedPicture();
        mediaMetadataRetriever.release();

        return image;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView musicFileName, testing;
        ImageView musicImage;
        TextView favoriteSong;
        RelativeLayout relativeLayout, favorite;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            musicImage = itemView.findViewById(R.id.music_img);
            musicFileName = itemView.findViewById(R.id.music_file_name);
            favoriteSong = itemView.findViewById(R.id.testing);
            relativeLayout = itemView.findViewById(R.id.card_layout);
            favorite = itemView.findViewById(R.id.favorite_layout);
            testing = itemView.findViewById(R.id.testing);

            testing.setOnClickListener(new View.OnClickListener() {
                int select = getAdapterPosition();

                @Override
                public void onClick(View view) {
                    Toast.makeText(context, "select : " + getAdapterPosition() + " and get : " + getAdapterPosition(), Toast.LENGTH_SHORT).show();
                    testing.setText("hi");
                    notifyDataSetChanged();
                }
            });


        }
    }


    public void updateMusicList(ArrayList<MusicFile> updatedMusicFiles) {

        musicFilesSongAdapter = new ArrayList<>();
        musicFilesSongAdapter.addAll(updatedMusicFiles);
        notifyDataSetChanged();

    }


}
