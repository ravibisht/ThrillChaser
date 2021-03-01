package com.stark.thrillchaser.model;

public class MusicFile {

  private   String name;
  private   String album;
  private   String duration;
  private   String artist;
  private   String path;
  private   boolean isFavorite;
  public String favorite;
  private   String id;


    public MusicFile(String favorite) {

        this.favorite=favorite;
    }

    public MusicFile(String id,String name, String album, String duration, String artist, String path) {
        this.id=id;

        this.name = name;
        this.album = album;
        this.duration = duration;
        this.artist = artist;
        this.path = path;
    }

    public MusicFile(String name, String album, String duration, String artist, String path, boolean isFavorite) {

        this.name = name;
        this.album = album;
        this.duration = duration;
        this.artist = artist;
        this.path = path;
        this.isFavorite = isFavorite;
    }

    public MusicFile(String albumName, String s) {
        this.album=albumName;
        this.path=s;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
