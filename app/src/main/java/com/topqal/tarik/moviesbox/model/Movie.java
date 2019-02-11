package com.topqal.tarik.moviesbox.model;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

@Entity(tableName = "movie")
public class Movie implements Parcelable {
    private String title;
   private String poster;
   private String backdrop;
    @ColumnInfo(name = "movie_info")
   private String movieInfo;
   private String date;
   private float rating;
    @PrimaryKey
   private int id;


    public Movie(String title, String poster, String backdrop, String movieInfo, String date, float rating, int id) {
        this.title = title;
        this.poster = poster;
        this.backdrop = backdrop;
        this.movieInfo = movieInfo;
        this.date = date;
        this.rating = rating;
        this.id = id;
    }

    private Movie(Parcel in) {
        title = in.readString();
        poster = in.readString();
        backdrop = in.readString();
        movieInfo = in.readString();
        date = in.readString();
        rating = in.readFloat();
        id = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(poster);
        dest.writeString(backdrop);
        dest.writeString(movieInfo);
        dest.writeString(date);
        dest.writeFloat(rating);
        dest.writeInt(id);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getBackdrop() {
        return backdrop;
    }

    public void setBackdrop(String backdrop) {
        this.backdrop = backdrop;
    }

    public String getMovieInfo() {
        return movieInfo;
    }

    public void setMovieInfo(String movieInfo) {
        this.movieInfo = movieInfo;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
