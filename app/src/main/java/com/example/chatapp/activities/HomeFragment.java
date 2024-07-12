package com.example.chatapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.R;
import com.example.chatapp.adapters.ChatAdapter;
import com.example.chatapp.model.ChatData;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;
    private ImageButton btnSearch;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Khởi tạo RecyclerView
        btnSearch = view.findViewById(R.id.btnSearch);
        recyclerView = view.findViewById(R.id.recycleChat);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Khởi tạo danh sách dữ liệu giả lập
        List<ChatData> chatDataList = generateDummyContacts();

        // Khởi tạo và thiết lập Adapter
        chatAdapter = new ChatAdapter(getContext(), chatDataList); // Truyền context vào đây
        recyclerView.setAdapter(chatAdapter);


        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), SearchUserActivity.class);
                startActivity(intent);
            }
        });


        return view;
    }

    // Phương thức để tạo dữ liệu giả lập danh sách ChatData
    private List<ChatData> generateDummyContacts() {
        List<ChatData> chatData = new ArrayList<>();
        chatData.add(new ChatData("Nguyen Y Khoa", "Xin chào việt nam","https://res.cloudinary.com/dsbebvfff/image/upload/v1718730800/pexels-pok-rie-33563-982263_jyw5od.jpg", "20 giờ trước"));
        chatData.add(new ChatData("Nguyen Y Khoa", "Xin chào việt nam","https://res.cloudinary.com/dsbebvfff/image/upload/v1718730800/pexels-pok-rie-33563-982263_jyw5od.jpg", "20 giờ trước"));
        chatData.add(new ChatData("Nguyen Y Khoa", "Xin chào việt nam","https://res.cloudinary.com/dsbebvfff/image/upload/v1718730800/pexels-pok-rie-33563-982263_jyw5od.jpg", "20 giờ trước"));
        chatData.add(new ChatData("Nguyen Y Khoa", "Xin chào việt nam","https://res.cloudinary.com/dsbebvfff/image/upload/v1718730800/pexels-pok-rie-33563-982263_jyw5od.jpg", "20 giờ trước"));
        chatData.add(new ChatData("Nguyen Y Khoa", "Xin chào việt nam","https://res.cloudinary.com/dsbebvfff/image/upload/v1718730800/pexels-pok-rie-33563-982263_jyw5od.jpg", "20 giờ trước"));
        chatData.add(new ChatData("Nguyen Y Khoa", "Xin chào việt nam","https://res.cloudinary.com/dsbebvfff/image/upload/v1718730800/pexels-pok-rie-33563-982263_jyw5od.jpg", "20 giờ trước"));
        chatData.add(new ChatData("Nguyen Y Khoa", "Xin chào việt nam","https://res.cloudinary.com/dsbebvfff/image/upload/v1718730800/pexels-pok-rie-33563-982263_jyw5od.jpg", "20 giờ trước"));
        chatData.add(new ChatData("Nguyen Y Khoa", "Xin chào việt nam","https://res.cloudinary.com/dsbebvfff/image/upload/v1718730800/pexels-pok-rie-33563-982263_jyw5od.jpg", "20 giờ trước"));
        chatData.add(new ChatData("Nguyen Y Khoa", "Xin chào việt nam","https://res.cloudinary.com/dsbebvfff/image/upload/v1718730800/pexels-pok-rie-33563-982263_jyw5od.jpg", "20 giờ trước"));
        chatData.add(new ChatData("Nguyen Y Khoa", "Xin chào việt nam","https://res.cloudinary.com/dsbebvfff/image/upload/v1718730800/pexels-pok-rie-33563-982263_jyw5od.jpg", "20 giờ trước"));
        chatData.add(new ChatData("Nguyen Y Khoa", "Xin chào việt nam","https://res.cloudinary.com/dsbebvfff/image/upload/v1718730800/pexels-pok-rie-33563-982263_jyw5od.jpg", "20 giờ trước"));
        chatData.add(new ChatData("Nguyen Y Khoa", "Xin chào việt nam","https://res.cloudinary.com/dsbebvfff/image/upload/v1718730800/pexels-pok-rie-33563-982263_jyw5od.jpg", "20 giờ trước"));
        chatData.add(new ChatData("Nguyen Y Khoa", "Xin chào việt nam","https://res.cloudinary.com/dsbebvfff/image/upload/v1718730800/pexels-pok-rie-33563-982263_jyw5od.jpg", "20 giờ trước"));
        chatData.add(new ChatData("Nguyen Y Khoa", "Xin chào việt nam","https://res.cloudinary.com/dsbebvfff/image/upload/v1718730800/pexels-pok-rie-33563-982263_jyw5od.jpg", "20 giờ trước"));
        chatData.add(new ChatData("Nguyen Y Khoa", "Xin chào việt nam","https://res.cloudinary.com/dsbebvfff/image/upload/v1718730800/pexels-pok-rie-33563-982263_jyw5od.jpg", "20 giờ trước"));
        chatData.add(new ChatData("Nguyen Y Khoa", "Xin chào việt nam","https://res.cloudinary.com/dsbebvfff/image/upload/v1718730800/pexels-pok-rie-33563-982263_jyw5od.jpg", "20 giờ trước"));
        chatData.add(new ChatData("Nguyen Y Khoa", "Xin chào việt nam","https://res.cloudinary.com/dsbebvfff/image/upload/v1718730800/pexels-pok-rie-33563-982263_jyw5od.jpg", "20 giờ trước"));


        // Thêm các ChatData khác nếu cần



        return chatData;
    }

}
