package com.topqal.tarik.moviesbox.model;

public class Cast {

    private String profilePath;
    private String actorName;
    private String movieCharacter;

    public Cast(String profilePath, String actorName, String movieCharacter) {
        this.profilePath = profilePath;
        this.actorName = actorName;
        this.movieCharacter = movieCharacter;
    }

    public String getProfilePath() {
        return profilePath;
    }

    public String getActorName() {
        return actorName;
    }

    public String getMovieCharacter() {
        return movieCharacter;
    }
}
