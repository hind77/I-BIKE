package com.hindou.smash.Services;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.hindou.smash.Controllers.DeviceListActivity;
import com.hindou.smash.Controllers.Ehealth;
import com.hindou.smash.Controllers.HealthFragementActivity;
import com.hindou.smash.Models.HealthInfo;
import com.hindou.smash.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;

import static com.hindou.smash.Controllers.DeviceListActivity.EXTRA_DEVICE_ADDRESS;

/**
 * Created by HP on 25/04/2018.
 */

public class HealthServices extends Service {

    public static boolean startstop;
    Context context = HealthServices.this;
    RadioButton male, female;
    RadioGroup sexe;
    Handler bluetoothIn;

    private Realm realm;
    private RealmResults<HealthInfo> list;
    private HealthServices.MyAsync myAsync;
    int count = 0;
    String sensor0;
    String sensor1;
    Bundle bundle;



    final int handlerState = 0;                        //used to identify handler message
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder recDataString = new StringBuilder();


    private HealthServices.ConnectedThread mConnectedThread;

    // SPP UUID service - this should work for most devices
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // String for MAC address
    private static String address;
    public HealthServices(){

    }

    @Override
    public void onCreate() {
        super.onCreate();
        checkBTState();
    }

    //------------------------------------------------------------------------------------------------------------------------------------------------------------
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Realm.init(this);
        realm = Realm.getDefaultInstance();
        list = realm.where(HealthInfo.class).findAll();
        address = intent.getStringExtra(EXTRA_DEVICE_ADDRESS);
        Log.d("add", String.valueOf(address) );
        Log.d("realm", "Health info Activity Triggered");
        //list = realm.where(HealthInfo.class).findAll();
       if(startstop){
        myAsync = new MyAsync();
        myAsync.execute(count);}
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

                        int dataLength = dataInPrint.length();                          //get length of data received


                        if (recDataString.charAt(0) == '#')                             //if it starts with # we know it is what we are looking for
                        {
                            sensor0 = recDataString.substring(1, 5);             //get sensor value from string between indices 1-5
                            sensor1 = recDataString.substring(7, 10);//same again...
                            if (sensor1.contains("~")) sensor1 = sensor1.substring(0, 2);




                        }
                        recDataString.delete(0, recDataString.length());                    //clear all string data
                        // strIncom =" ";
                        dataInPrint = " ";
                    }
                }
            }
        };
        //create device and set the MAC address
        Log.d("addr", String.valueOf(address) );
        BluetoothDevice device = btAdapter.getRemoteDevice(address);


        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Toast.makeText(context, "Socket creation failed", Toast.LENGTH_LONG).show();
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
        btAdapter = BluetoothAdapter.getDefaultAdapter();       // get Bluetooth adapter
        checkBTState();
        return START_STICKY;
    }
    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
        //creates secure outgoing connecetion with BT device using UUID

    }



    //--------------------------------------------------------------------------------------------------------------------------------------------------------------
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;

    }

    //-----------------------------------------------------------------------------------------------------------------------------------------------------------------

    @Override
    public void onDestroy() {
        super.onDestroy();
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
            Toast.makeText(context, "Device does not support bluetooth", Toast.LENGTH_LONG).show();
        } else {
            if (btAdapter.isEnabled()) {
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
               // startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------
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
                Toast.makeText(context, "Connection Failure", Toast.LENGTH_LONG).show();


            }
        }
    }

    private class MyAsync extends AsyncTask<Integer, Integer, Integer> {
        private int counter;
        int selectedGender;
        int weightint;
        int ageint;
        int heartbeat;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            selectedGender = list.get(0).getGender();
            Log.d("selectedGender", String.valueOf(list.get(0).getGender()));
            weightint = list.get(0).getWeight();
            Log.d("weight", String.valueOf(list.get(0).getWeight()));
            ageint = list.get(0).getAge();
            Log.d("age", String.valueOf(list.get(0).getAge()));

            heartbeat = 100;
            Log.d("heart", String.valueOf(heartbeat) );


            counter = 0;


        }

        @Override
        protected Integer doInBackground(Integer... integers) {
            counter = integers[0];
            int cal = 0;
            while (startstop) {
                try {
                    Thread.sleep(1000);
                    counter++;

                    if (selectedGender == R.id.male) {
                        cal = (int) ((-55.0969 + (0.6309 * heartbeat) + (0.1988 * weightint) + (0.2017 * ageint)) / 4.184) * 60 * (counter) / 36;
                        publishProgress(cal);
                    }
                    if (selectedGender == R.id.female) {
                        cal = (int) ((-20.4022 + (0.4472 * heartbeat) - (0.1263 * weightint) + (0.074 * ageint)) / 4.184) * 60 * (counter) / 36;

                        publishProgress(cal);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (isCancelled()) {
                    break;
                }
            }
            Log.d("cal", String.valueOf(cal));
            return cal;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            // comm
            super.onProgressUpdate(values);

            Log.d("values0", Integer.toString(values[0]));
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);

            count = integer;
            Log.d("integer", Integer.toString(integer));
            Intent intent = new Intent(context,HealthFragementActivity.class);
            intent.putExtra("calories", integer);
            intent.putExtra("beats",sensor1);
            intent.putExtra("temp",sensor0);
            sendBroadcast(intent);

        }

        @Override
        protected void onCancelled(Integer integer) {
            super.onCancelled(integer);
        }
    }
}

