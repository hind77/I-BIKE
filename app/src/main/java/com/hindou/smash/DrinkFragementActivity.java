package com.hindou.smash;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.hindou.smash.Models.User;
import com.hindou.smash.Models.WaterDrink;
import com.hindou.smash.utils.SessionsManager;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by HP on 12/04/2018.
 */

public class DrinkFragementActivity extends Fragment {
    View view;
    private MaterialDialog mDrinkDialog;
    private User connectedUser;
    private ImageButton imageButton;
    private ProgressBar progressBar;
    private Realm realm;
    private RealmResults<WaterDrink> list;


    public DrinkFragementActivity() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_drink,container,false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        imageButton = (ImageButton) view.findViewById(R.id.imagewater);
        progressBar = (ProgressBar) view.findViewById(R.id.drinkbar);
        //get an instance of Realm database
        realm = Realm.getDefaultInstance();
        //get all database lines of HealthInfo class/table
        list = ((Ehealth)getActivity()).list2;
        progressBar.setProgress(list.get(0).getDrink());
        imageButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                boolean wrapInScrollView = true;
                mDrinkDialog = new MaterialDialog.Builder(getContext())
                        .title(R.string.water_title)
                        .customView(R.layout.drinked_water, wrapInScrollView)
                        .positiveText(R.string.add_water_btn)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                View itemView = mDrinkDialog.getCustomView();
                                connectedUser = SessionsManager.getInstance(getContext()).getUser();
                                EditText drink = itemView.findViewById(R.id.drink);

                                if(!drink.getText().toString().equals(""))
                                {
                                    if(list.size() >= 1){

                                        progressBar.setProgress(Integer.valueOf(drink.getText().toString())+(list.get(0).getDrink()));
                                    }
                                    else { progressBar.setProgress(Integer.valueOf(drink.getText().toString()));}

                                }

                                else
                                {Toast.makeText(getContext(), "Sorry you need to enter how much you drinked", Toast.LENGTH_SHORT).show();}

                            }
                        })
                        .show();
            }
        });


    int prog = progressBar.getProgress();
    final WaterDrink waterDrink = new WaterDrink(prog);
    //test if the database has a record of healthinfo
    if(list.size() == 0){
        //if not then add a new record

        realm.beginTransaction();
        realm.copyToRealm(waterDrink);
        realm.commitTransaction();
        Log.d("realm", "Added successfully ");

    }else{
        //if yes then update the existing record
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                WaterDrink waterDrink1 = realm.where(WaterDrink.class).findFirst();
                waterDrink1.setDrink(waterDrink.getDrink());

            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                //On successfully updating Realm database do something
                Log.d("realm", "Updated successfully ");

            }
        });
    }
}}
