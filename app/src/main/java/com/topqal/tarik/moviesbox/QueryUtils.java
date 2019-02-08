package com.topqal.tarik.moviesbox;

import android.text.TextUtils;
import android.util.Log;

import com.topqal.tarik.moviesbox.model.Cast;
import com.topqal.tarik.moviesbox.model.Movie;
import com.topqal.tarik.moviesbox.model.Review;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class QueryUtils {

    /**
     * Private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    private static final String TAG = QueryUtils.class.getSimpleName();

    public static List<Movie> fetchMovies(String queryUrl) {

        URL url = createUrl(queryUrl);

        String jsonResults = null;
        try {
            jsonResults = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(TAG, "Problem making the Http request", e);
        }

        return extractJsonString(jsonResults);
    }

    private static URL createUrl(String stringUrl) {

        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(TAG, "problem with building the url", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(TAG, "Problem retrieving the JSON result", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder builder = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                builder.append(line);
                line = reader.readLine();
            }
        }
        return builder.toString();
    }

    private static List<Movie> extractJsonString(String jsonResponse) {

        //Return early if the JSON result string is empty
        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }
        //Create an empty ArrayList to start adding movies
        List<Movie> movies = new ArrayList<>();

        try {
            // Create JSONObject from the json response string
            JSONObject root = new JSONObject(jsonResponse);

            //Extract JSONArray with the key name "results"
            JSONArray resultsArray = root.getJSONArray("results");

            // Create an object for each movie in the ArrayList
            for (int i = 0; i < resultsArray.length(); i++) {

                // Get a single movie at position i within the list of movies
                JSONObject currentMovie = resultsArray.getJSONObject(i);
                // Extract the value for the title
                String title = currentMovie.getString("title");
                // Extract the value for the key called "poster_path"
                String moviePoster = currentMovie.getString("poster_path");
                // Extract the value for the key called "backdrop_path"
                String backdrop = currentMovie.getString("backdrop_path");
                // Extract the value for the movieInfo "overview"
                String movieInfo = currentMovie.getString("overview");
                // Extract the value for the release date
                String date = currentMovie.getString("release_date");
                // Extract the rating value
                float rating = (float) currentMovie.getDouble("vote_average");
                // Extract the value for the movie id
                int id = currentMovie.getInt("id");

                Movie movie = new Movie(title, moviePoster, backdrop, movieInfo, date, rating, id);
                movies.add(movie);
            }

        } catch (JSONException e) {
            Log.e(TAG, "Problem parsing the Movie list json response", e);
        }
        // Return the list of movies
        return movies;
    }

    private static List<String> extractTrailersJsonString(String jsonResponse) {
        List<String> trailersKey = new ArrayList<>();
        if (TextUtils.isEmpty(jsonResponse)) return null;

        try {
            JSONObject root = new JSONObject(jsonResponse);
            //Extract JSONArray with the key name "results"
            JSONArray resultsArray = root.getJSONArray("results");

            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject currentTrailer = resultsArray.getJSONObject(i);
                String videoKey = currentTrailer.getString("key");
                trailersKey.add(videoKey);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "Problem parsing the movie trailers json response", e);
        }
        return trailersKey;
    }

    public static List<String> fetchTrailers(String queryUrl) {

        URL url = createUrl(queryUrl);

        String jsonResults = null;
        try {
            jsonResults = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(TAG, "Problem making the Http request", e);
        }
        List<String> trailersKey = extractTrailersJsonString(jsonResults);
        assert trailersKey != null;
        Log.d(TAG, "LIst: " + trailersKey.size() + " " + trailersKey);
        return trailersKey;
    }

    private static List<Cast> extractCasts(String jsonResult) {
        List<Cast> casts = new ArrayList<>();
        if (TextUtils.isEmpty(jsonResult)) return null;
        try {
            JSONObject root = new JSONObject(jsonResult);
            JSONArray castArray = root.getJSONArray("cast");
            for (int i = 0; i < castArray.length(); i++) {
                JSONObject currentCast = castArray.getJSONObject(i);
                String name = currentCast.getString("name");
                String character = currentCast.getString("character");
                String imagePath = currentCast.getString("profile_path");

                casts.add(new Cast(imagePath, name, character));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "Problem parsing the casts json response", e);
        }
        return casts;
    }

    public static List<Cast> fetchCasts(String url) {
        URL queryUrl = createUrl(url);
        String results = null;
        try {
            results = makeHttpRequest(queryUrl);
        } catch (IOException e) {
            Log.e(TAG, "Problem making the Http request", e);
        }
        return extractCasts(results);
    }

    private static List<Review>  extractReview(String jsonResponse) {
        List<Review> reviews = new ArrayList<>();
        if (TextUtils.isEmpty(jsonResponse)) return null;
        try {
            JSONObject root = new JSONObject(jsonResponse);
            JSONArray reviewsArray = root.getJSONArray("results");
            for (int i = 0; i < reviewsArray.length(); i++) {
                JSONObject currentReview = reviewsArray.getJSONObject(i);
                String author = currentReview.getString("author");
                String content = currentReview.getString("content");
                reviews.add(new Review(author, content));
            }
        }catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "Problem parsing the reviews json response", e);
        }
        return reviews;
    }
    public static List<Review> fetchReviews(String queryUrl) {
        String jsonResult = null;
        URL url = createUrl(queryUrl);
        try {
            jsonResult = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(TAG, "Problem making the Http request", e);
        }
        return extractReview(jsonResult);
    }

}
