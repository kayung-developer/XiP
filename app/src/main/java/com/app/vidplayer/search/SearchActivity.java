package com.app.vidplayer.search;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import com.app.vidplayer.R;

import java.util.ArrayList;


public class SearchActivity extends AppCompatActivity {
    private SearchAdapter searchAdapter;
    private ArrayList<SearchModel> vidModel;
    RecyclerView searchlist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        fillVidModel();
        setRecyclerView();
        getIntent();
    }
    private void fillVidModel(){
        vidModel = new ArrayList<>();
        ContentResolver content = getContentResolver();
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        Cursor curso = content.query(uri, null, null, null, null);

        //looping through all rows and adding to list
        if (curso != null && curso.moveToFirst()) {
            do {
                String title = curso.getString(curso.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                int image = curso.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA);

                SearchModel searchModel = new SearchModel();
                searchModel.setTitle(title);
                searchModel.setImage(curso.getInt(image));

                searchAdapter.onAttachedToRecyclerView(searchlist);
                vidModel.add(searchModel);

            } while (curso.moveToNext());

        }
        searchAdapter.notifyDataSetChanged();
    }


    private void setRecyclerView(){
        searchlist = findViewById(R.id.recycler);
        searchlist.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        searchAdapter = new SearchAdapter(vidModel);
        searchlist.setLayoutManager(layoutManager);
        searchlist.setAdapter(searchAdapter);
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
         MenuItem searchItem = menu.findItem(R.id.search);
         SearchView searchView = (SearchView) searchItem.getActionView();
         searchView = (SearchView) findViewById(R.id.search);
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
         searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(vidModel.contains(query)){
                    searchAdapter.getFilter().filter((CharSequence) query);
                    do {
                        return false;
                    } while(true);
                }
                Toast.makeText(getApplicationContext(), "No Match Found", (int)1).show();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                searchAdapter.getFilter().filter((CharSequence) newText);
                return false;
            }
        });
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        //menu item selected
        switch (item.getItemId()) {

        }
        return super.onOptionsItemSelected(item);
    }

}