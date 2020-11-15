package com.example.tunnect;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class SearchListAdaptor extends RecyclerView.Adapter<SearchListAdaptor.ViewHolder> {
    private Context context;
    private List<Song> songs;
    private String user_id;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView songTitle;
        public TextView artist;
        public Button add_btn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            songTitle = itemView.findViewById(R.id.song_title);
            artist = itemView.findViewById(R.id.artist);
            add_btn = itemView.findViewById(R.id.add_btn);
        }
    }

    public SearchListAdaptor(Context context, List<Song> songs, String USER_ID) {
        this.context = context;
        this.songs = songs;
        this.user_id = USER_ID;
    }

    @Override
    public SearchListAdaptor.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.search_layout, parent, false);

        SearchListAdaptor.ViewHolder viewHolder = new SearchListAdaptor.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Song song = songs.get(position);
        holder.songTitle.setText(song.getName());
        holder.artist.setText(song.getArtist());

        holder.add_btn.setOnClickListener(view -> {
            updateSongs(song.getName(), holder);
        });
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    private void addSong(String song, ViewHolder holder, JSONArray user_songs) throws JSONException {
        String url = "http://52.188.167.58:3000/userstore/" + user_id;
        RequestQueue queue = Volley.newRequestQueue(context);
        JSONArray addArray = new JSONArray();
        JSONObject songObject = new JSONObject();
        user_songs.put(song);
        songObject.put("propName", "songs");
        songObject.put("value", user_songs);
        addArray.put(songObject);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.PATCH, url, addArray, response -> {
            Toast.makeText(context, "Song Added", Toast.LENGTH_LONG).show();
            holder.add_btn.setText("Added");
        }, error -> {
            Toast.makeText(context, "Song Added Kinda", Toast.LENGTH_LONG).show();
            holder.add_btn.setText("Added");
        });
        queue.add(jsonArrayRequest);
    }

    private void updateSongs(String song, ViewHolder holder) {
        String url = "http://52.188.167.58:3000/userstore/" + user_id;
        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
            try {
                addSong(song, holder, response.optJSONArray("songs"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Toast.makeText(context, "Couldn't get user", Toast.LENGTH_LONG).show();
        });
        queue.add(jsonObjectRequest);
    }
}