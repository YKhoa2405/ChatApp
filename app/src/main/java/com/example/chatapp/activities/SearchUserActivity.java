package com.example.chatapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.R;
import com.example.chatapp.adapters.SearchUserAdapter;
import com.example.chatapp.model.SearchUserModel;
import com.example.chatapp.util.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class SearchUserActivity extends AppCompatActivity {

    private SearchView searchView;
    private RecyclerView recycleSearchResult;
    private SearchUserAdapter adapter;
    private ImageButton btnGoBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        // Initialize views
        searchView = findViewById(R.id.searchView);
        recycleSearchResult = findViewById(R.id.recycleSearchResult);
        btnGoBack = findViewById(R.id.btnGoBack);



        // Set up search button click listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.trim().isEmpty()) {
                    searchView.setQueryHint("Không hợp lệ");
                    return false;
                }
                setUpSearchRecycleView(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                setUpSearchRecycleView(newText);
                return true;
            }
        });
        searchView.requestFocus();

        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void setUpSearchRecycleView(String searchText) {
        Query query = FirebaseUtil.allUserCollection()
                .whereGreaterThanOrEqualTo("user_name", searchText)
                .whereLessThanOrEqualTo("user_name", searchText + "\uf8ff");

        FirestoreRecyclerOptions<SearchUserModel> options = new FirestoreRecyclerOptions.Builder<SearchUserModel>()
                .setQuery(query, SearchUserModel.class)
                .build();

        if (adapter != null) {
            adapter.stopListening();
        }

        // Create the adapter
        adapter = new SearchUserAdapter(options, getApplicationContext());
        recycleSearchResult.setLayoutManager(new LinearLayoutManager(this));


        // Set up the RecyclerView
        recycleSearchResult.setAdapter(adapter);

        // Start listening for changes
        adapter.startListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }
}
