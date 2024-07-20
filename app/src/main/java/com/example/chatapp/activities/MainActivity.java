package com.example.chatapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.chatapp.R;
import com.example.chatapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    private  int selectTab =1 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.frameLayout, HomeFragment.class, null)
                    .commit();

            binding.homeTxt.setVisibility(View.VISIBLE);
            binding.homeLayout.setBackgroundResource(R.drawable.bottom_naviga_round);
        }


        binding.homeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectTab!=1){

                    getSupportFragmentManager()
                            .beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.frameLayout, HomeFragment.class, null)
                            .commit();

                    binding.profileTxt.setVisibility(View.GONE);
                    binding.contactTxt.setVisibility(View.GONE);

                    binding.homeTxt.setVisibility(View.VISIBLE);
                    binding.homeLayout.setBackgroundResource(R.drawable.bottom_naviga_round);


                    binding.profileLayout.setBackgroundColor(getResources().getColor(R.color.white));
                    binding.contactLayout.setBackgroundColor(getResources().getColor(R.color.white));

                    ScaleAnimation scaleAnimation = new ScaleAnimation(0.8f,1.0f,1f,1f, Animation.RELATIVE_TO_SELF,0.0f,Animation.RELATIVE_TO_SELF,0.0f);
                    scaleAnimation.setDuration(200);
                    scaleAnimation.setFillAfter(true);

                    binding.homeLayout.startAnimation(scaleAnimation);

                    selectTab=1;

                }
            }
        });

        binding.contactLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectTab!=2){
                    getSupportFragmentManager()
                            .beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.frameLayout,ContactFragment.class, null)
                            .commit();

                    binding.profileTxt.setVisibility(View.GONE);
                    binding.homeTxt.setVisibility(View.GONE);

                    binding.contactTxt.setVisibility(View.VISIBLE);
                    binding.contactLayout.setBackgroundResource(R.drawable.bottom_naviga_round);

                    binding.profileLayout.setBackgroundColor(getResources().getColor(R.color.white));
                    binding.homeLayout.setBackgroundColor(getResources().getColor(R.color.white));


                    ScaleAnimation scaleAnimation = new ScaleAnimation(0.8f,1.0f,1f,1f, Animation.RELATIVE_TO_SELF,0.0f,Animation.RELATIVE_TO_SELF,0.0f);
                    scaleAnimation.setDuration(200);
                    scaleAnimation.setFillAfter(true);

                    binding.contactLayout.startAnimation(scaleAnimation);

                    selectTab=2;
                }

            }
        });

        binding.profileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectTab!=3){
                    getSupportFragmentManager()
                            .beginTransaction()
                            .setReorderingAllowed(true)
                            .replace(R.id.frameLayout, ProfileFragment.class, null)
                            .commit();

                    binding.contactTxt.setVisibility(View.GONE);
                    binding.homeTxt.setVisibility(View.GONE);

                    binding.profileTxt.setVisibility(View.VISIBLE);
                    binding.profileLayout.setBackgroundResource(R.drawable.bottom_naviga_round);


                    binding.homeLayout.setBackgroundColor(getResources().getColor(R.color.white));
                    binding.contactLayout.setBackgroundColor(getResources().getColor(R.color.white));

                    ScaleAnimation scaleAnimation = new ScaleAnimation(0.8f,1.0f,1f,1f, Animation.RELATIVE_TO_SELF,0.0f,Animation.RELATIVE_TO_SELF,0.0f);
                    scaleAnimation.setDuration(200);
                    scaleAnimation.setFillAfter(true);

                    binding.profileLayout.startAnimation(scaleAnimation);

                    selectTab=3;
                }

            }
        });
    }



}
