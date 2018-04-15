package com.hindou.smash;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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

import com.hindou.smash.Models.HealthInfo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by HP on 12/04/2018.
 */

public class HealthFragementActivity extends Fragment {
    View view;
    Button start,stop;
   RadioButton male,female;
   RadioGroup sexe;
    TextView txtArduino, txtString, txtStringLength, sensorView0, sensorView1, calories;
    Handler bluetoothIn;
    boolean startstop;
    private Realm realm;
    private RealmResults<HealthInfo> list;
    private MyAsync myAsync;
    int count =0;

    final int handlerState = 0;                        //used to identify handler message
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder recDataString = new StringBuilder();

    private ConnectedThread mConnectedThread;

    // SPP UUID service - this should work for most devices
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // String for MAC address
    private static String address;
    public HealthFragementActivity(){

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        txtString = (TextView) view.findViewById(R.id.txtString);
        txtStringLength = (TextView) view.findViewById(R.id.testView1);
        sensorView0 = (TextView) view.findViewById(R.id.sensorView0);
        sensorView1 = (TextView) view.findViewById(R.id.sensorView1);
        calories = (TextView) view.findViewById(R.id.calories_data);
        sexe = view.findViewById(R.id.gender);
        male = (RadioButton) view.findViewById(R.id.male);
        female = (RadioButton) view.findViewById(R.id.female);
        start = (Button) view.findViewById(R.id.start);
        stop = (Button) view.findViewById(R.id.stop);
        realm = Realm.getDefaultInstance();
        list = ((Ehealth)getActivity()).list;
        Log.d("realm", "Health info Activity Triggered");
        //list = realm.where(HealthInfo.class).findAll();
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startstop = true;
                myAsync = new MyAsync();
                myAsync.execute(count);
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               myAsync.cancel(true);
            }
        });










        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {

                if (msg.what == handlerState) {
                    //if message is what we want
                    // byte[] readBuf = (byte[]) msg.obj;
                    // String readMessage = new String(readBuf, 0, msg.arg1);
                    String readMessage = (String) msg.obj;


                    recDataString.append(readMessage);                                      //keep appending to string until ~
                    int endOfLineIndex = recDataString.indexOf("~");                    // determine the end-of-line
                    if (endOfLineIndex > 0) {                                           // make sure there data before ~
                        String dataInPrint = recDataString.substring(0, endOfLineIndex);    // extract string
                        txtString.setText("Data Received = " + dataInPrint);
                        int dataLength = dataInPrint.length();                          //get length of data received
                        txtStringLength.setText("String Length = " + String.valueOf(dataLength));

                        if (recDataString.charAt(0) == '#')                             //if it starts with # we know it is what we are looking for
                        {
                            String sensor0 = recDataString.substring(1, 5);             //get sensor value from string between indices 1-5
                            String sensor1 = recDataString.substring(7, 10);            //same again...


                            sensorView0.setText(sensor0 + "°C");    //update the textviews with sensor values
                            sensorView1.setText(sensor1);


                        }
                        recDataString.delete(0, recDataString.length());                    //clear all string data
                        // strIncom =" ";
                        dataInPrint = " ";
                    }
                }
            }
        };
        btAdapter = BluetoothAdapter.getDefaultAdapter();       // get Bluetooth adapter
        checkBTState();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_health,container,false);

        return view;

    }
    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
        //creates secure outgoing connecetion with BT device using UUID
    }

    @Override
    public void onResume() {
        super.onResume();

        //Get MAC address from DeviceListActivity via intent
        Intent intent = getActivity().getIntent();

        //Get the MAC address from the DeviceListActivty via EXTRA

        address = intent.getStringExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS);


        //create device and set the MAC address
        BluetoothDevice device = btAdapter.getRemoteDevice(address);


        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Toast.makeText(getContext(), "Socket creation failed", Toast.LENGTH_LONG).show();
        }
        // Establish the Bluetooth socket connection.
        try
        {
            btSocket.connect();
        } catch (IOException e) {
            try
            {
                btSocket.close();
            } catch (IOException e2)
            {
                //insert code to deal with this
            }
        }
        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();


        //I send a character when resuming.beginning transmission to check device is connected
        //If it is not an exception will be thrown in the write method and finish() will be called
        mConnectedThread.write("x");

    }

    @Override
    public void onPause()
    {
        super.onPause();
        try
        {
            //Don't leave Bluetooth sockets open when leaving activity
            btSocket.close();
        } catch (IOException e2) {
            //insert code to deal with this
        }
    }

    //Checks that the Android device Bluetooth is available and prompts to be turned on if off
    private void checkBTState() {

        if(btAdapter==null) {
            Toast.makeText(getContext(), "Device does not support bluetooth", Toast.LENGTH_LONG).show();
        } else {
            if (btAdapter.isEnabled()) {
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    //create new class for connect thread
    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        //creation of the connect thread
        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                //Create I/O streams for connection
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();

            } catch (IOException e) {
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[256];
            int bytes;

            // Keep looping to listen for received messages
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);            //read bytes from input buffer
                    String readMessage = new String(buffer, 0, bytes);
                    Log.d("buffer", readMessage);
                    // Send the obtained bytes to the UI Activity via handler
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }


        //write method
        public void write(String input) {
            byte[] msgBuffer = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(msgBuffer);                //write bytes over BT connection via outstream
            } catch (IOException e) {
                //if you cannot write, close the application
                Toast.makeText(getContext(), "Connection Failure", Toast.LENGTH_LONG).show();
                getActivity().finish();

            }
        }
    }
        private class MyAsync extends AsyncTask<Integer,Integer,Integer>{
            private int counter;
            int selectedGender;
            int weightint;
            int ageint;
            int heartbeat;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                selectedGender = list.get(0).getGender();
                Log.d("selectedGender",String.valueOf(list.get(0).getGender()));
                weightint = list.get(0).getWeight();
                Log.d("weight",String.valueOf(list.get(0).getWeight()));
                ageint = list.get(0).getAge();
                Log.d("age",String.valueOf(list.get(0).getAge()));
                heartbeat = Integer.valueOf(sensorView1.getText().toString());
                counter = 0;

            }

            @Override
            protected Integer doInBackground(Integer... integers) {
                counter=integers[0];
                int cal =0;
                while(startstop){
                    try {
                        Thread.sleep(1000);
                        counter++;

                        if (selectedGender == R.id.male) {
                            cal = (int) ((-55.0969 + (0.6309 * heartbeat) + (0.1988 * weightint) + (0.2017 * ageint)) / 4.184) * 60 * (counter )/3600;
                            publishProgress(cal);
                        }
                        if (selectedGender == R.id.female){
                            cal = (int) ((-20.4022 + (0.4472 * heartbeat) - (0.1263 * weightint) + (0.074 * ageint))/4.184) * 60 * (counter )/3600;

                            publishProgress(cal);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (isCancelled()){
                        break;
                    }
                }
                Log.d("cal",String.valueOf(cal));
                return cal;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
                calories.setText(Integer.toString(values[0]));
                Log.d("values0",Integer.toString(values[0]));
            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
                calories.setText(Integer.toString(integer));
                count=integer;
                Log.d("integer",Integer.toString(integer));

            }

            @Override
            protected void onCancelled(Integer integer) {
                super.onCancelled(integer);
            }
        }

}
