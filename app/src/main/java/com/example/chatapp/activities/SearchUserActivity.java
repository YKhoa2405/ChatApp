package com.example.chatapp.activities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.R;
import com.example.chatapp.adapters.SearchUserAdapter;
import com.example.chatapp.model.SearchUserModel;
import com.example.chatapp.util.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import io.github.muddz.styleabletoast.StyleableToast;

public class SearchUserActivity extends AppCompatActivity {

    private SearchView searchView;
    private RecyclerView recycleSearchResult;
    private SearchUserAdapter adapter;
    private ImageButton btnGoBack;
    TextView txtDeleteHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        // Initialize views
        searchView = findViewById(R.id.searchView);
        recycleSearchResult = findViewById(R.id.recycleSearchResult);
        btnGoBack = findViewById(R.id.btnGoBack);
        txtDeleteHistory =findViewById(R.id.txtDeleteHistory);

        searchView.requestFocus();

        setUpSearchHistoryRecycleView();
        // Set up search button click listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                setUpSearchRecycleView(newText);
                return true;
            }
        });


        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        txtDeleteHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Xóa lịch sử tìm kiếm")
                        .setMessage("Bạn có chắc chắn muốn xóa lịch sử tìm kiếm không?")
                        .setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Lấy tham chiếu đến bộ sưu tập lịch sử tìm kiếm
                                FirebaseUtil.getSearchHistoryCollection(FirebaseUtil.currentUserUid()).get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    // Xóa từng tài liệu trong bộ sưu tập
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        document.getReference().delete()
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            // Thực hiện hành động sau khi xóa thành công
                                                                            StyleableToast.makeText(SearchUserActivity.this, "Xóa lịch sử tìm kiếm thành công", R.style.successToast).show();

                                                                        } else {
                                                                            // Xử lý lỗi xóa tài liệu
                                                                            StyleableToast.makeText(SearchUserActivity.this, "Có lỗi xảy ra, vui lòng thử lại", R.style.errorToast).show();
                                                                        }
                                                                    }
                                                                });
                                                    }
                                                } else {
                                                    StyleableToast.makeText(SearchUserActivity.this, "Có lỗi xảy ra, vui lòng thử lại", R.style.errorToast).show();
                                                }
                                            }
                                        });
                            }
                        })
                        .setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });




    }


    private void setUpSearchRecycleView(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            // Hiển thị lại lịch sử tìm kiếm
            setUpSearchHistoryRecycleView();
            return;
        }

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
        recycleSearchResult.setAdapter(adapter);
        adapter.startListening();
    }

    private void setUpSearchHistoryRecycleView(){
        Query query = FirebaseUtil.getSearchHistoryCollection(FirebaseUtil.currentUserUid());
        FirestoreRecyclerOptions<SearchUserModel> options = new FirestoreRecyclerOptions.Builder<SearchUserModel>()
                .setQuery(query, SearchUserModel.class)
                .build();

        if (adapter != null) {
            adapter.stopListening();
        }

        // Create the adapter
        adapter = new SearchUserAdapter(options, getApplicationContext());
        recycleSearchResult.setLayoutManager(new LinearLayoutManager(this));
        recycleSearchResult.setAdapter(adapter);
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

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }
}
