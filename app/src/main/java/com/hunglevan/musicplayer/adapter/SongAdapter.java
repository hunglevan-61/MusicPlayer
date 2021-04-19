package com.hunglevan.musicplayer.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hunglevan.musicplayer.R;
import com.hunglevan.musicplayer.model.Song;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> implements Filterable {

    private ArrayList<Song> songs;
    private List<Song> songList;
    private Callback callback;

    public SongAdapter(ArrayList<Song> songs, Callback callback){
        this.songs = songs;
        this.callback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_song, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        View view = holder.getView();
        final Song song = songs.get(position);

        TextView txtTitle = view.findViewById(R.id.txtSong);
        txtTitle.setText(song.getTitle());

        TextView txtArtist = view.findViewById(R.id.txtArtist);
        txtArtist.setText(song.getArtistName());

        TextView txtDuration = view.findViewById(R.id.txtDuration);
        txtDuration.setText(song.getDurationFormat((int) song.getDuration()));

        CircleImageView artistProfile = view.findViewById(R.id.profile_artist);
        artistProfile.setImageBitmap(song.getArtistProfile());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onItemClick(position, songs);
            }
        });
    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    // setup for SearchView
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final ArrayList<Song> results = new ArrayList<Song>();
                if (songList == null)
                    songList  = songs;
                if (constraint != null){
                    if(songList !=null & songList.size()>0 ){
                        for ( final Song song :songList) {
                            if (song.getTitle().toLowerCase().contains(constraint.toString().toLowerCase()))
                            {
                                results.add(song);
                            }
                        }
                    }
                    oReturn.values = results;
                }
                return oReturn;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                songs = (ArrayList<Song>)results.values;
                notifyDataSetChanged();
            }
        };
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        View view;

        ViewHolder(View view) {
            super(view);
            this.view = view;
        }

        View getView() {
            return view;
        }
    }

}

