package com.stark.thrillchaser.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.stark.thrillchaser.R;
import com.stark.thrillchaser.adapter.SongAdapter;
import com.stark.thrillchaser.fragment.SongFragment;
import com.stark.thrillchaser.model.MusicFile;

import java.util.ArrayList;
import java.util.Random;

import static com.stark.thrillchaser.activity.AlbumDetailActivity.albumMusicFilesList;
import static com.stark.thrillchaser.activity.MainActivity.musicFiles;
import static com.stark.thrillchaser.activity.MainActivity.repeatBoolean;
import static com.stark.thrillchaser.activity.MainActivity.shuffleBoolean;
import static com.stark.thrillchaser.adapter.SongAdapter.musicFilesSongAdapter;

public class PlayerActivity extends AppCompatActivity  implements MediaPlayer.OnCompletionListener {

    public static final String MUSIC_SHARE_PREF="music_share_pref";
    public static final String MUSIC_POSITION="music_position";
    public static final String MUSIC_CURRENT_TIME="music_current_time";

    private static int musicCurrentTimePosition=-1;

    private TextView songName,durationPlayed,totalDuration;
    private ImageView shuffleOnOff,playPrev,playNext,repeatOnOff,menuBtn,backBtn,songImg;
    private FloatingActionButton playPause;
    private SeekBar songSeekBar;

    private int position=-1;
    private String sender;

    static ArrayList<MusicFile> listMusicFiles=new ArrayList<>();
    static Uri uri;
    static MediaPlayer mediaPlayer;

    Handler handler=new Handler();

    Thread playPauseBtn,playNextBtn,playPrevBtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        getSupportActionBar().hide();
        initViews();
        getIntentMethod();
        setCoverPicture(uri);
        songSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                if(mediaPlayer!=null && b)
                    mediaPlayer.seekTo(musicCurrentTimePosition ==-1 ? i*1000:musicCurrentTimePosition);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


         if(musicCurrentTimePosition>-1)
             changeTotalTimePlayed(musicCurrentTimePosition,"FloatingBtn");
         else
          changeTotalTimePlayed(0,null);

          mediaPlayer.setOnCompletionListener(this);

        shuffleOnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(shuffleBoolean){
                    shuffleBoolean=false;
                    shuffleOnOff.setImageResource(R.drawable.ic_shuffle_off);
                }
                else{
                    shuffleBoolean=true;
                    shuffleOnOff.setImageResource(R.drawable.ic_shuffle_on);
                }
            }
        });

        repeatOnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(repeatBoolean){
                    repeatBoolean=false;
                    repeatOnOff.setImageResource(R.drawable.ic_repeat_off);
                }
                else{
                    repeatBoolean=true;
                    repeatOnOff.setImageResource(R.drawable.ic_repeat_on);
                }

            }
        });
    }

    private void changeTotalTimePlayed(final int musicCurrentTimePosition, final String sender) {
        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(sender!=null && sender.equals("FloatingBtn")){

                    int mCurrentPosition = musicCurrentTimePosition / 1000;
                    songSeekBar.setProgress(mCurrentPosition);
                    durationPlayed.setText(formattedTime(mCurrentPosition));

                }
                else if(sender==null) {
                    if (mediaPlayer != null) {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        songSeekBar.setProgress(mCurrentPosition);
                        durationPlayed.setText(formattedTime(mCurrentPosition));
                    }
                }
                handler.postDelayed(this,1000);

            }
        });
    }

    @Override
    protected void onResume() {
        playPauseBtn();
        playNextBtn();
        playPrevBtn();
        
        super.onResume();
    }

    private void playNextBtn() {
        musicCurrentTimePosition=-1;

        playNextBtn=new Thread( ){
            @Override
            public void run() {
                playNext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        playNextBtnClicked();
                    }
                });
            }
        };
        playNextBtn.start();
    }

    private void playPrevBtn() {

        playPrevBtn=new Thread( ){
            @Override
            public void run() {
                playPrev.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        playPrevBtnClicked();
                    }
                });
            }
        };
        playPrevBtn.start();
    }

    private void playPauseBtn() {
        playPauseBtn=new Thread( ){
            @Override
            public void run() {
                playPause.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        playPauseBtnClicked();
                    }
                });
            }
        };

        playPauseBtn.start();

    }

    private void playPauseBtnClicked() {

        if(mediaPlayer.isPlaying()){
            playPause.setImageResource(R.drawable.music_icon);
            mediaPlayer.pause();
        }
        else {
            playPause.setImageResource(R.drawable.playing);
            mediaPlayer.start();

        }
        songSeekBar.setMax(mediaPlayer.getDuration()/1000);
        musicCurrentTimePosition=-1;
        changeTotalTimePlayed(0,null);
    }

    private void playNextBtnClicked() {
        musicCurrentTimePosition=-1;

        if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.release();

            if(shuffleBoolean && !repeatBoolean)
                position=getRandom(listMusicFiles.size()-1);

            else if(!shuffleBoolean && !repeatBoolean)
                   position=((position+1)%listMusicFiles.size());



            uri=Uri.parse(listMusicFiles.get(position).getPath());

            mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
            setCoverPicture(uri);
            songName.setText(listMusicFiles.get(position).getName());
            songName.setSelected(true);
            totalDuration.setText(formattedTime(mediaPlayer.getDuration() / 1000));


            changeTotalTimePlayed(0,null);

            playPause.setImageResource(R.drawable.playing);
            mediaPlayer.start();

        }

        else{
            musicCurrentTimePosition=-1;

            mediaPlayer.stop();
                mediaPlayer.release();

            if(shuffleBoolean && !repeatBoolean)
                position=getRandom(listMusicFiles.size()-1);

            else if(!shuffleBoolean && !repeatBoolean)
                position=((position+1)%listMusicFiles.size());

                uri=Uri.parse(listMusicFiles.get(position).getPath());
                mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
                setCoverPicture(uri);
                songName.setText(listMusicFiles.get(position).getName());
                songName.setSelected(true);
                totalDuration.setText(formattedTime(mediaPlayer.getDuration() / 1000));
                changeTotalTimePlayed(0,null);
                playPause.setImageResource(R.drawable.music_icon);
        }

        mediaPlayer.setOnCompletionListener(this);
    }

    private int getRandom(int bound) {

        return  new Random().nextInt(bound+1);
    }

    private void playPrevBtnClicked() {

        musicCurrentTimePosition=-1;

        if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.release();

            if(shuffleBoolean && !repeatBoolean)
                position=getRandom(listMusicFiles.size()-1);

            else if(!shuffleBoolean && !repeatBoolean)
                position=((position - 1) < 0 ? (listMusicFiles.size()-1): (position-1));

            uri=Uri.parse(listMusicFiles.get(position).getPath());

            mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
            setCoverPicture(uri);
            songName.setText(listMusicFiles.get(position).getName());
            songName.setSelected(true);
            totalDuration.setText(formattedTime(mediaPlayer.getDuration() / 1000));
            changeTotalTimePlayed(0,null);

            playPause.setImageResource(R.drawable.playing);
            mediaPlayer.start();

        }
        else{
            mediaPlayer.stop();
            mediaPlayer.release();

            if(shuffleBoolean && !repeatBoolean)
                position=getRandom(listMusicFiles.size()-1);

            else if(!shuffleBoolean && !repeatBoolean)
                position=((position - 1) < 0 ? (listMusicFiles.size()-1): (position-1));

            uri=Uri.parse(listMusicFiles.get(position).getPath());
            mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
            setCoverPicture(uri);
            songName.setText(listMusicFiles.get(position).getName());
            songName.setSelected(true);
            totalDuration.setText(formattedTime(mediaPlayer.getDuration() / 1000));
            changeTotalTimePlayed(0,null);
            playPause.setImageResource(R.drawable.music_icon);
        }
        mediaPlayer.setOnCompletionListener(this);

    }

    private  void imageAnimation(final Context context, final Bitmap bitmap, final ImageView imageView){

        Animation animationOut= AnimationUtils.loadAnimation(context,android.R.anim.fade_out);
        final Animation animationIn= AnimationUtils.loadAnimation(context,android.R.anim.fade_in);

        animationOut.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                if(bitmap!=null)
                     Glide.with(context).load(bitmap).into(imageView);
                else
                    Glide.with(context).asBitmap().load(R.drawable.music).into(imageView);

                animationIn.setAnimationListener(new Animation.AnimationListener() {

                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                imageView.startAnimation(animationIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        imageView.startAnimation(animationOut);
    }

    private void initViews() {
        songName=findViewById(R.id.song_name);

        durationPlayed=findViewById(R.id.duration_played);
        totalDuration=findViewById(R.id.duration_total);
        shuffleOnOff=findViewById(R.id.shuffle_on);
        playPrev=findViewById(R.id.play_prev);
        playNext=findViewById(R.id.play_next);
        repeatOnOff=findViewById(R.id.repeat);
        menuBtn=findViewById(R.id.menu_btn);
        backBtn=findViewById(R.id.back_btn);
        playPause=findViewById(R.id.play_pause);
        songSeekBar=findViewById(R.id.seekbar);
        songImg=findViewById(R.id.music_art);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
    private void getIntentMethod() {

        position=getIntent().getIntExtra("position",-1);
        sender=getIntent().getStringExtra(SongAdapter.SENDER);


        if(sender!=null && sender.equals(SongFragment.SENDER_SONG_LIST))
                 listMusicFiles=musicFiles;
        else if (sender!=null && sender.equals(AlbumDetailActivity.SENDER_ALBUM_LIST))
                 listMusicFiles=albumMusicFilesList;
        else if (sender!=null && sender.equals(MainActivity.SENDER_FLOATING_BTN)) {
                 SharedPreferences sharedPreferences = getSharedPreferences(MUSIC_SHARE_PREF, MODE_PRIVATE);
                 position=sharedPreferences.getInt(MUSIC_POSITION,0);
                 musicCurrentTimePosition=sharedPreferences.getInt(MUSIC_CURRENT_TIME,-1);
                 listMusicFiles=musicFiles;

        }
        else
            listMusicFiles=musicFilesSongAdapter;

        if(listMusicFiles!=null){
            playPause.setImageResource(R.drawable.playing);
            uri=Uri.parse(listMusicFiles.get(position).getPath());
        }
        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }



        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);

        if(musicCurrentTimePosition>-1)
            mediaPlayer.seekTo(musicCurrentTimePosition);

        musicCurrentTimePosition=-1;
        songName.setText(listMusicFiles.get(position).getName());
        songName.setSelected(true);
        totalDuration.setText(formattedTime(mediaPlayer.getDuration() / 1000));
        songSeekBar.setMax(mediaPlayer.getDuration() / 1000);
        mediaPlayer.start();



    }


    private void changeTotalTimePlayedFromFloatingBTN(final int mCurrentPositionFromFloatBtn) {
        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer!=null){
                    int mCurrentPosition=mCurrentPositionFromFloatBtn/1000;
                    songSeekBar.setProgress(mCurrentPosition);
                    durationPlayed.setText(formattedTime(mCurrentPosition));
                }
                handler.postDelayed(this,1000);

            }
        });
    }
    public  void setCoverPicture(Uri uri){

        MediaMetadataRetriever mediaMetadataRetriever=new MediaMetadataRetriever();
        mediaMetadataRetriever.setDataSource(uri.toString());

        byte [] imageOfMeta=mediaMetadataRetriever.getEmbeddedPicture();

        mediaMetadataRetriever.release();

        Bitmap bitmap;

        if(imageOfMeta!=null) {
            bitmap= BitmapFactory.decodeByteArray(imageOfMeta,0,imageOfMeta.length);

            imageAnimation(this,bitmap,songImg);
        }
        else {
            imageAnimation(this,null,songImg);
        }

    }
    private String formattedTime(int mCurrentPosition) {

        String totalOut="";
        String totalNew="";

        String seconds=String.valueOf(mCurrentPosition % 60);
        String minutes=String.valueOf(mCurrentPosition / 60);

        totalOut=minutes+":"+seconds;



        if(seconds.length()==1){
            return totalNew;
        }

        return totalOut;



    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        playNextBtnClicked();
        this.mediaPlayer.start();
        playPause.setImageResource(R.drawable.playing);

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {

        SharedPreferences.Editor sharedPreferences = getSharedPreferences(MUSIC_SHARE_PREF, MODE_PRIVATE).edit();
        sharedPreferences.putInt(MUSIC_POSITION,position);
        sharedPreferences.putInt(MUSIC_CURRENT_TIME,mediaPlayer.getCurrentPosition());
        sharedPreferences.apply();
        super.onDestroy();
    }


}