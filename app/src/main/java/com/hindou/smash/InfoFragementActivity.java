package com.hindou.smash;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.hindou.smash.Models.Health;
import com.hindou.smash.Models.HealthInfo;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by HP on 12/04/2018.
 */

public class InfoFragementActivity extends Fragment {
    View view;
    EditText weight, age;
    RadioButton male, femele;
    RadioGroup gender;
    Button start;
    TextView calories;
    private Realm realm;
    private RealmResults<HealthInfo> list;

    public InfoFragementActivity() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_info,container,false);

        return view;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        weight = (EditText) view.findViewById(R.id.weight_data);
        age = (EditText) view.findViewById(R.id.age_data);
        male = (RadioButton) view.findViewById(R.id.male);
        femele = (RadioButton) view.findViewById(R.id.female);
        gender = (RadioGroup) view.findViewById(R.id.gender);
        //get an instance of Realm database
        realm = Realm.getDefaultInstance();
        //get all database lines of HealthInfo class/table
        //list = realm.where(HealthInfo.class).findAll();
        list = ((Ehealth)getActivity()).list;

        //if it contains a line then set it to the Edit texts
        if(list.size() >= 1){
            gender.check(list.get(0).getGender());
           weight.setText(String.valueOf(list.get(0).getWeight()));
            age.setText(String.valueOf(list.get(0).getAge()));
        }

        start = (Button) view.findViewById(R.id.start1);
        gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                persistData();
            }
        });
    }
    private void persistData(){
        int genderid = gender.getCheckedRadioButtonId();
        int weightint = Integer.valueOf(weight.getText().toString());
        int ageint = Integer.valueOf(age.getText().toString());

        final HealthInfo healthInfo = new HealthInfo(genderid, weightint, ageint);

        //test if the database has a record of healthinfo
        if(list.size() == 0){
            //if not then add a new record

            realm.beginTransaction();
            realm.copyToRealm(healthInfo);
            realm.commitTransaction();
            Log.d("realm", "Added successfully ");

        }else{
            //if yes then update the existing record
            realm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    HealthInfo health = realm.where(HealthInfo.class).findFirst();
                    health.setAge(healthInfo.getAge());
                    health.setGender(healthInfo.getGender());
                    health.setWeight(healthInfo.getWeight());
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    //On successfully updating Realm database do something
                    Log.d("realm", "Updated successfully ");

                }
            });
        }

    }

}
