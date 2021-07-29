package com.c0mm4nd.paindroid;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.emoji.bundled.BundledEmojiCompatConfig;
import androidx.emoji.text.EmojiCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.c0mm4nd.paindroid.databinding.ActivityMainBinding;
import com.c0mm4nd.paindroid.utils.Uploader;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initializing the emoji
        EmojiCompat.Config config = new BundledEmojiCompatConfig(this);
        EmojiCompat.init(config);

        // init binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();

        setContentView(view);

        // init auth
        mAuth = FirebaseAuth.getInstance();
        assert mAuth.getCurrentUser() != null;

        setSupportActionBar(binding.appBar.toolbar);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home, R.id.nav_record, R.id.nav_report, R.id.nav_map)
                .setOpenableLayout(binding.drawerLayout)
                .build();
        FragmentManager fragmentManager = getSupportFragmentManager();
        NavHostFragment navHostFragment = (NavHostFragment) fragmentManager.findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(binding.navView, navController);
        NavigationUI.setupWithNavController(binding.appBar.toolbar, navController, appBarConfiguration);

        initWorker();
    }

    public boolean replaceFragment(Fragment nextFragment) {
        if (nextFragment == null) {
            return false;
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment, nextFragment)
                .commit();
        return true;
    }

    public boolean popupFragment(Fragment nextFragment) {
        if (nextFragment == null) {
            return false;
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.nav_host_fragment, nextFragment)
                .addToBackStack(null)
                .commit();
        return true;
    }

    @NonNull
    public FirebaseAuth getAuth() {
        return mAuth;
    }

    private void initWorker() {
        final int UPLOAD_HOUR = 22; // 10PM
        long delayInMillis;

        Calendar tenPMToday = Calendar.getInstance();
        tenPMToday.set(Calendar.HOUR_OF_DAY, UPLOAD_HOUR);

        long now = System.currentTimeMillis();

        if (now < tenPMToday.getTimeInMillis()) {
            delayInMillis = tenPMToday.getTimeInMillis() - now;
        } else {
            delayInMillis = tenPMToday.getTimeInMillis() + 86400 * 1000 - now;
        }

//        PeriodicWorkRequest.Builder workBuilder = new PeriodicWorkRequest.Builder(Uploader.class, 24, TimeUnit.HOURS);
        PeriodicWorkRequest.Builder workBuilder = new PeriodicWorkRequest.Builder(Uploader.class, 15, TimeUnit.MINUTES); // minimum repeat for test
//        workBuilder.setInitialDelay(delayInMillis, TimeUnit.MILLISECONDS); // comment me when testing
        PeriodicWorkRequest workRequest = workBuilder.build();
        WorkManager.getInstance(getApplicationContext())
                .enqueue(workRequest);
    }
}
