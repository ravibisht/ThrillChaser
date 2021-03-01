package com.stark.thrillchaser.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.stark.thrillchaser.R;
import com.stark.thrillchaser.adapter.SongAdapter;
import com.stark.thrillchaser.fragment.AlbumFragment;
import com.stark.thrillchaser.fragment.FavoriteSongFrag;
import com.stark.thrillchaser.fragment.SongFragment;
import com.stark.thrillchaser.model.MusicFile;

import java.security.PublicKey;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private static final String TAG = "MainActivity";
    private static final String MY_SORT_PREF = "my_sort_pref";
    public static final String SENDER_FLOATING_BTN="SENDER_FLOATING_BTN";

    private static final int STORAGE_PERMISSION_CODE = 1;
    public static ArrayList<MusicFile> musicFiles;
    public static boolean shuffleBoolean, repeatBoolean;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private FloatingActionButton openMusicPlayer;

    public ArrayList<MusicFile> getAllMusicFiles(Context context) {

        SharedPreferences sharedPreferences = getSharedPreferences(MY_SORT_PREF, MODE_PRIVATE);
        String sortOrder = sharedPreferences.getString("sortBy", "sortByName");

        String selectedSortOrder=null;
        switch (sortOrder){
            case "sortByName":
                selectedSortOrder=MediaStore.MediaColumns.DISPLAY_NAME+" ASC";
                break;
            case "sortByDate":
                selectedSortOrder=MediaStore.MediaColumns.DATE_ADDED+" ASC";
                break;
            case "sortBySize":
                selectedSortOrder=MediaStore.MediaColumns.SIZE+" DESC";
                break;

        }

        Log.e(TAG, "getAllMusicFiles: "+selectedSortOrder);
        ArrayList<MusicFile> tempMusicFiles = new ArrayList<>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media._ID};

        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, selectedSortOrder);

        if (cursor != null) {

            while (cursor.moveToNext()) {
                String album = cursor.getString(0);
                String artist = cursor.getString(1);
                String duration = cursor.getString(2);
                String path = cursor.getString(3);
                String title = cursor.getString(4);
                String id = cursor.getString(5);

                MusicFile musicFile = new MusicFile(id, title, album, duration, artist, path);
             //   Log.e(TAG, "Path : " + path + "  - name : " + title);
                tempMusicFiles.add(musicFile);
            }

            cursor.close();


        }
        return tempMusicFiles;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setPermission();

        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.tab_gradient));
        actionBar.setLogo(getResources().getDrawable(R.drawable.music));
        initViewPager();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        MenuItem menuItem = menu.findItem(R.id.search_btn);

        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        String userInput = newText.toLowerCase();

        ArrayList<MusicFile> filteredMusicFiles = new ArrayList<>();

        for (MusicFile m : musicFiles) {
            if (m.getName().toLowerCase().contains(userInput)) {
                filteredMusicFiles.add(m);
            }

        }

        SongFragment.songAdapter.updateMusicList(filteredMusicFiles);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        SharedPreferences.Editor editor = getSharedPreferences(MY_SORT_PREF, MODE_PRIVATE).edit();

        switch (item.getItemId()) {
            case R.id.sortByName:
                editor.putString("sortBy", "sortByName");
                editor.apply();
                this.recreate();
                break;
            case R.id.sortByDate:
                editor.putString("sortBy", "sortByDate");
                editor.apply();
                this.recreate();
                break;
            case R.id.sortBySize:
                editor.putString("sortBy", "sortBySize");
                editor.apply();
                this.recreate();
                break;
            default:
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    private boolean setPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED) {
            musicFiles = getAllMusicFiles(this);
            return true;
        } else {
            requestStoragePermission();
        }
        return false;
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                this.recreate();

            } else {
                requestStoragePermission();
            }
        }
    }

    private void initViewPager() {
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tab_layout);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new SongFragment(), "Songs");
        viewPagerAdapter.addFragment(new AlbumFragment(), "Album");
        viewPagerAdapter.addFragment(new FavoriteSongFrag(), "Favorite");
        viewPager.setAdapter(viewPagerAdapter);
        openMusicPlayer=findViewById(R.id.open_music);
        tabLayout.setupWithViewPager(viewPager);

        openMusicPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,PlayerActivity.class);
                intent.putExtra(SongAdapter.SENDER,SENDER_FLOATING_BTN);
                startActivity(intent);
            }
        });
    }


    public static class ViewPagerAdapter extends FragmentPagerAdapter {


        ArrayList<Fragment> allFrag;
        ArrayList<String> titles;

        public ViewPagerAdapter(@NonNull FragmentManager fm) {
            super(fm);
            allFrag = new ArrayList<>();
            titles = new ArrayList<>();
        }

        void addFragment(Fragment fragment, String title) {
            allFrag.add(fragment);
            titles.add(title);

        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return allFrag.get(position);
        }

        @Override
        public int getCount() {
            return allFrag.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {


            return titles.get(position);
        }
    }

}
