package com.helper.weighing;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private static final String[] TAGS = {"batch", "plan", "check", "tally"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fm = getSupportFragmentManager();
        if (savedInstanceState == null) {
            Fragment[] fs = {new BatchFragment(), new PlanFragment(), new CheckFragment(), new TallyFragment()};
            FragmentTransaction ft = fm.beginTransaction();
            for (int i = 0; i < fs.length; i++) {
                ft.add(R.id.container, fs[i], TAGS[i]);
                if (i != 0) ft.hide(fs[i]);
            }
            ft.commitNow();
        }

        BottomNavigationView nav = findViewById(R.id.nav);
        nav.setOnItemSelectedListener(item -> {
            select(item.getItemId());
            return true;
        });
    }

    private void select(int itemId) {
        int idx;
        if (itemId == R.id.nav_batch) idx = 0;
        else if (itemId == R.id.nav_plan) idx = 1;
        else if (itemId == R.id.nav_check) idx = 2;
        else idx = 3;

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        for (int i = 0; i < TAGS.length; i++) {
            Fragment f = fm.findFragmentByTag(TAGS[i]);
            if (f == null) continue;
            if (i == idx) ft.show(f);
            else ft.hide(f);
        }
        ft.commit();
    }
}
