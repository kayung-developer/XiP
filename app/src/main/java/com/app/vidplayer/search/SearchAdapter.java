package com.app.vidplayer.search;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.vidplayer.R;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends  RecyclerView.Adapter<SearchAdapter.ViewHolder> implements Filterable {
    private ArrayList<SearchModel> searchModel;
    private ArrayList<SearchModel> searchModelFull;


    public SearchAdapter(ArrayList<SearchModel> searchModel) {
        this.searchModel = searchModel;
        searchModelFull = new ArrayList<>(searchModel);
    }


    @NonNull
    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.ViewHolder sholder, int posn) {
        SearchModel currentItem = searchModel.get(posn);
        sholder.image.setImageResource(currentItem.getImage());
        sholder.title.setText(currentItem.getTitle());
    }

    @Override
    public int getItemCount() {
        return searchModel.size();
    }

    @Override
    public Filter getFilter() {
        return searchFilter;
    }

    private Filter searchFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<SearchModel> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(searchModelFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (SearchModel item : searchModelFull) {
                    if(item.getTitle().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            searchModel.clear();
            searchModel.addAll((ArrayList) results.values);
            notifyDataSetChanged();
        }
    };


    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title;


        ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imgview);
            title = itemView.findViewById(R.id.textview);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                }
            });

        }
    }
}

