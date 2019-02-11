package com.topqal.tarik.moviesbox;

public class Urls {

    private Urls() {
    }

    public static final String POSTER_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/";
    public static final String POSTER_IMAGE_SIZE= "w780";

    public static final String BASE_URL = "https://api.themoviedb.org/3";
    public static final String PARAM_DISCOVER = "/discover";
    public static final String PARAM_MOVIE = "/movie";
    public static final String PARAM_TRAILERS = "/videos";
    public static final String PARAM_CREDITS = "/credits";
    public static final String PARAM_REVIEWS= "/reviews";
    public static final String PARAM_QUESTION_MARK = "?";
    public static final String PARAM_PAGE_NO = "page=";
    public static final String PAGE = "1";
    public static final String PARAM_SORT = "&sort_by=";
    public static final String PARAM_ADULT = "&include_adult=false";
    public static final String PARAM_API = "&api_key=";
    public static final String TRAILERS_PARAM_API = "api_key=";
    public static final String API_KEY = BuildConfig.ApiKey;
    public static final String GOOGLE_API_KEY = BuildConfig.GoogleApiKey;
    public static final String PARAM_LANGUAGE = "&language=";
    public static final String LANGUAGE = "en-US";

    // Youtube base URL
    public static final String YOUTUBE_BASE_URL = "https://www.youtube.com/watch?v=";
}
