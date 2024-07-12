package com.example.chatapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.R;
import com.example.chatapp.adapters.UserAdapter;
import com.example.chatapp.model.UserModel;
import com.example.chatapp.util.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class SearchUserActivity extends AppCompatActivity {

    ImageButton btnSearch;
    private EditText searchView;
    private RecyclerView recycleSearchResult;
    private UserAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        searchView = findViewById(R.id.searchView);
        recycleSearchResult = findViewById(R.id.recycleSearchResult);
        btnSearch = findViewById(R.id.btnSearch);

        searchView.requestFocus();

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchText = searchView.getText().toString().trim();
                if(searchText.isEmpty()){
                    searchView.setError("Không hợp lệ");
                    return;
                }
                setUpSearchRecycleView(searchText);
            }
        });



    }

    public void setUpSearchRecycleView(String searchText){
        Query query = FirebaseUtil.allUserCollection().whereGreaterThanOrEqualTo("user_name",searchText);

        FirestoreRecyclerOptions<UserModel> options = new FirestoreRecyclerOptions.Builder<UserModel>().setQuery(query,UserModel.class).build();

        adapter = new UserAdapter(options,getApplicationContext());
        recycleSearchResult.setLayoutManager(new LinearLayoutManager(this));
        recycleSearchResult.setAdapter(adapter);
        adapter.startListening();
    }

    protected void onStart(){
        super.onStart();
        if(adapter!=null){
            adapter.startListening();
        }
    }

    protected void onStop(){
        super.onStop();
        if(adapter!=null){
            adapter.stopListening();
        }
    }



}
