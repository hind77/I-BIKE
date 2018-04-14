package com.hindou.smash;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.hindou.smash.Models.HealthInfo;
import com.hindou.smash.Models.User;
import com.hindou.smash.Models.WaterDrink;
import com.hindou.smash.adapter.ReservationAdapter;
import com.hindou.smash.adapter.ViewPagerAdapter;
import com.hindou.smash.utils.SessionsManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

/**
 * Created by HP on 23/03/2018.
 */

public class Ehealth extends AppCompatActivity {
    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;
    public RealmResults<HealthInfo> list;
    public RealmResults<WaterDrink> list2;

    private Realm realm;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set screen title
        setTitle(R.string.health_frag_title);
        //Init realm database
        Realm.init(this);
        Realm.getDefaultInstance();
        realm=Realm.getDefaultInstance();
        list = realm.where(HealthInfo.class).findAll();
        list2 = realm.where(WaterDrink.class).findAll();

        setContentView(R.layout.activity_ehealth);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        ViewPagerAdapter Adapter = new ViewPagerAdapter(getSupportFragmentManager());
        Adapter.addFragment(new InfoFragementActivity(), "info");
        Adapter.addFragment(new HealthFragementActivity(), "health");
        Adapter.addFragment(new DrinkFragementActivity(),"Drink");
        viewPager.setAdapter(Adapter);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                list = realm.where(HealthInfo.class).findAll();
                list2 = realm.where(WaterDrink.class).findAll();

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return true;
    }
}
