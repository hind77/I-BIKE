package com.hindou.smash.Controllers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.hindou.smash.Models.User;
import com.hindou.smash.R;
import com.hindou.smash.utils.SessionsManager;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

public class UserActivity extends AppCompatActivity{

    private SessionsManager sessionsManager;
    private User connectedUser;
    private Toolbar toolbar;
    private Context mContext;
    private Drawer mDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        initializeComponents();

        //setting up the drawer
        if(SessionsManager.getInstance(this).isActive()) setUpDrawer();
    }

    private void initializeComponents(){
        mContext = this;
        sessionsManager = SessionsManager.getInstance(this);
        if (!sessionsManager.isActive()){
            changeActivity(LoginActivity.class, true);
        }else{
            connectedUser = sessionsManager.getUser();
            //Set screen title
            setTitle(connectedUser.getFname() + " " + connectedUser.getLname());

        }
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setUpDrawer(){

        // Create the AccountHeader
        AccountHeader headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header_background)
                .addProfiles(
                        new ProfileDrawerItem()
                                .withIcon(R.drawable.heliantha)
                                .withName(connectedUser.getFname() + " " + connectedUser.getLname())
                                .withEmail(connectedUser.getEmail())

                )
                .build();

        //Primary items
        PrimaryDrawerItem item1 = new PrimaryDrawerItem()
                .withIcon(getResources().getDrawable(R.drawable.ic_directions_bike_blue_grey_400_24dp))
                .withName(getResources().getString(R.string.drawer_bike_title))
                .withIconTintingEnabled(true)
                .withEnabled(false)
                .withIdentifier(1),

                item2 = new PrimaryDrawerItem()
                        .withIcon(getResources().getDrawable(R.drawable.ic_map_blue_grey_400_24dp))
                        .withName(getResources().getString(R.string.drawer_station_title))
                        .withIconTintingEnabled(true)
                        .withIdentifier(5),

                item3 = new PrimaryDrawerItem()
                        .withIcon(getResources().getDrawable(R.drawable.ic_healing_blue_grey_400_24dp))
                        .withName(getResources().getString(R.string.drawer_health_title))
                        .withIconTintingEnabled(true)
                        .withIdentifier(6),
                item4 = new PrimaryDrawerItem()
                        .withIcon(getResources().getDrawable(R.drawable.ic_date_range_grey_400_24dp))
                        .withName(R.string.event_activity_title)
                        .withIconTintingEnabled(true)
                        .withIdentifier(9);



        //Secondary items
        SecondaryDrawerItem secItem1 = new SecondaryDrawerItem()
                .withName(R.string.drawer_book_bike_title)
                .withIdentifier(2),
                secItem2 = new SecondaryDrawerItem()
                        .withName(R.string.drawer_lock_bike_title)
                        .withIdentifier(3),
                secItem3 = new SecondaryDrawerItem()
                        .withName(R.string.drawer_cancel)
                        .withIdentifier(4),
                secItem4 = new SecondaryDrawerItem()
                        .withName(R.string.start_item)
                        .withIdentifier(7),
                secItem5 = new SecondaryDrawerItem()
                        .withName(R.string.history_item)
                        .withIdentifier(8);

        mDrawer =  new DrawerBuilder()
                .withToolbar(toolbar)
                .withAccountHeader(headerResult)
                .withActivity(this)
                .addDrawerItems(
                        item1,
                        secItem1,
                        secItem2,
                        secItem3,
                        item2,
                        item3,
                        secItem4,
                        secItem5,
                        item4

                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

                        switch (position){
                            case 2 :
                                changeActivity(ReserveActivity.class, false);
                                return false;

                            case 3 :
                                changeActivity(LockBikesActivity.class, false);
                                return false;

                            case 5 :
                                changeActivity(StationActivity.class, false);
                                return false;
                            case 4:
                                changeActivity(CancelReservationActivity.class,false);
                                return false;
                            case 7 :
                                changeActivity(DeviceListActivity.class,false);
                                return false;
                            case 8 :
                                changeActivity(HealthHistoryActivity.class,false);
                                return false;
                            case 9 :
                                changeActivity(EventActivity.class,false);
                                return false;

                            default: return false;
                        }
                    }
                })
                .build();
    }

    private void changeActivity(Class<?> destination, boolean flag) {
        Intent intent = new Intent(UserActivity.this, destination);
        startActivity(intent);
        if(flag) finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }else if(id == R.id.logout){
            sessionsManager.destroySession();
            changeActivity(LoginActivity.class, true);
        }

        return super.onOptionsItemSelected(item);
    }

}
