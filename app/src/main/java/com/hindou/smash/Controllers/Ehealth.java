package com.hindou.smash.Controllers;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.hindou.smash.Models.HealthInfo;
import com.hindou.smash.Models.WaterDrink;
import com.hindou.smash.R;
import com.hindou.smash.Adapters.ViewPagerAdapter;
import com.hindou.smash.Services.HealthServices;

import io.realm.Realm;
import io.realm.RealmResults;

import static com.google.android.gms.cast.CastRemoteDisplayLocalService.startService;
import static com.google.android.gms.cast.CastRemoteDisplayLocalService.stopService;

/**
 * Created by HP on 23/03/2018.
 */

public class Ehealth extends AppCompatActivity {
    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;
    public RealmResults<HealthInfo> list;
    public RealmResults<WaterDrink> list2;
    Button start, stop;

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
        Adapter.addFragment(new InfoFragementActivity(), "Info");
        Adapter.addFragment(new HealthFragementActivity(), "Health Tracking");
        Adapter.addFragment(new BikerFragementActivity(),"User Tracking");
        Adapter.addFragment(new DrinkFragementActivity(),"Water Tracking");

        viewPager.setAdapter(Adapter);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);


        HealthServices.startstop=true;


      /*  stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopService(new Intent(getBaseContext(),HealthServices.class));
                HealthServices.startstop = false;
            }
        });
*/
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
