package ke.or.mamacare.mamacare;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class Bluetooth5Activity extends AppCompatActivity {

//    Button btnOn, btnOff;
    TextView bobValue, patdetails, timeview;// sensorView1, sensorView2, sensorView3;
    Handler bluetoothIn;
ProgressDialog pDialog;
    final int handlerState = 0;
    //used to identify handler message
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder recDataString = new StringBuilder();

    private ConnectedThread mConnectedThread;
    String patientname, patientid, strDate, strTime, MyREsponseFromphp;
    String readMessage="blank", username="blank";

    // SPP UUID service - this should work for most devices
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // String for MAC address
    private static String address;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bluetooth5);


        Intent i = getIntent();
        patientid = i.getStringExtra("patID");
        patientname = i.getStringExtra("patNAME");


        //username
        SharedPreferences myprefs = PreferenceManager.getDefaultSharedPreferences(Bluetooth5Activity.this);
        String  uname = myprefs.getString("username", "");
//        Toast.makeText(Bluetooth5Activity.this, "Username:" + uname, Toast.LENGTH_SHORT).show();
        if (uname.contentEquals("")) {
          username="blank";
        }
        if (!username.isEmpty()){
           username=uname;
        }

        //Link the buttons and textViews to respective views
//        btnOn = (Button) findViewById(R.id.buttonOn);
//        btnOff = (Button) findViewById(R.id.buttonOff);
//        txtString = (TextView) findViewById(R.id.txtString);
//        txtStringLength = (TextView) findViewById(R.id.TVStringlength);
        bobValue = (TextView) findViewById(R.id.bobValueTv);

        patdetails = (TextView) findViewById(R.id.patientDetailsTV);
        timeview = (TextView) findViewById(R.id.timeTV);
patdetails.setText("Patient name: "+patientname+"\nID: "+patientid);

        Calendar calendar = Calendar.getInstance();
//        SimpleDateFormat mdformat = new SimpleDateFormat("EEE d, MMM yyyy, kk:mm:ss"); // MM / dd "); "EEE, MMM d, ''yy"   Wed, July 10, '96
        SimpleDateFormat mdformat = new SimpleDateFormat("d MMM yyyy"); // MM / dd "); "EEE, MMM d, ''yy"   Wed, July 10, '96
        SimpleDateFormat mdformat2 = new SimpleDateFormat("kk:mm:ss"); // MM / dd "); "EEE, MMM d, ''yy"   Wed, July 10, '96
         strDate =  mdformat.format(calendar.getTime());
         strTime =  mdformat2.format(calendar.getTime());


        timeview.setText("Time: "+strDate+ " "+strTime);


        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what
                        == handlerState)
                {
                    //if message is what we want
                    readMessage = (String) msg.obj;
//                    String readMessage = (String) msg.obj;
                    // msg.arg1 = bytes from connect thread
//                    recDataString.append(readMessage+"mm \n");
//                    bobValue.setText("Received data:\n " + recDataString);
                    bobValue.setText(readMessage+" mm");
                    bobValue.setTextSize(19);


//                    String dataInPrint = recDataString.substring(0, endOfLineIndex);    // extract string
//                    txtString.setText("Data Received = " + dataInPrint);
                    int endOfLineIndex = recDataString.indexOf("");
                    String dataInPrint = recDataString.substring(0, endOfLineIndex);
                    int dataLength =  recDataString.length();
                    //get length of data received
//                    txtStringLength.setText("String Length = " + String.valueOf(dataLength));

                /*    //keep appending to string until ~
                    int endOfLineIndex = recDataString.indexOf("~");                    // determine the end-of-line
                    if (endOfLineIndex > 0)

                    {
                        // make sure there data before ~
                        String dataInPrint = recDataString.substring(0, endOfLineIndex);    // extract string
                        txtString.setText("Data Received = " + dataInPrint);
                        int dataLength
                                =
                                dataInPrint.length();
                        //get length of data received
                        txtStringLength.setText("String Length = " + String.valueOf(dataLength));

                        if (recDataString.charAt(0) == '#')
                        //if it starts with # we know it is what we are looking for
                        {
                            String valueb = recDataString.substring(1, 5);             //get sensor value from string between indices 1-5
                            String sensor1 = recDataString.substring(6, 10);            //same again...
                            String sensor2 = recDataString.substring(11, 15);
                            String sensor3 = recDataString.substring(16, 20);

                            bobValue.setText("Value sent is: " + valueb);    //update the textviews with sensor values

                        }
                        recDataString.delete(0,

                                recDataString.length());
                        //clear all string data
                        // strIncom =" ";
                        dataInPrint = " ";
                    }*/
                }
            }
        };

        btAdapter = BluetoothAdapter.getDefaultAdapter();       // get Bluetooth adapter
        checkBTState();

      /*  // Set up onClick listeners for buttons to send 1 or 0 to turn on/off LED
        btnOff.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mConnectedThread.write("0");    // Send "0" via Bluetooth
                Toast.makeText(getBaseContext(), "Sent 0", Toast.LENGTH_SHORT).show();
            }
        });

        btnOn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                mConnectedThread.write("1");    // Send "1" via Bluetooth
                Toast.makeText(getBaseContext(), "Sent 1", Toast.LENGTH_SHORT).show();
            }
        });*/
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
        //creates secure outgoing connecetion with BT device using UUID
    }

    @Override
    public void onResume() {
        super.onResume();

        //Get MAC address from DeviceListActivity via intent
        Intent intent = getIntent();

        //Get the MAC address from the DeviceListActivty via EXTRA
        address = intent.getStringExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS);

        //create device and set the MAC address
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_LONG).show();
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
            Toast.makeText(getBaseContext(), "Device does not support bluetooth", Toast.LENGTH_LONG).show();
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
            } catch (IOException e) { }

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
                    // Send the obtained bytes to the UI Activity via handler
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
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
                Toast.makeText(getBaseContext(), "Connection Failure", Toast.LENGTH_LONG).show();
                finish();

            }
        }
    }



    public void uploaddata(View v) {
        CheckNet();
    }
    public void SendDetails ()
    {

//        TextView ourtv = (TextView) findViewById(R.id.bobValueTv);
//String bluetoothValue= ourtv.getText().toString();

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.customdialog);
        Button Bcancel = (Button) dialog.findViewById(R.id.btnCancel);
        Button Bsend = (Button) dialog.findViewById(R.id.btnSend);
        TextView dt = (TextView) dialog.findViewById(R.id.dialogtext);
        dt.setText("Patient's name: "+patientname+" ID: "+patientid+"\nDistance: "+readMessage+" mm\nTime: "+strDate+" at "+strTime);
//        dt.setText("Patient's name: "+patientname+" ID: "+patientid+"\nDistance: "+bluetoothValue+" mm\nTime: "+strDate+" at "+strTime);
        // if button is clicked, close the custom dialog
//        dialog.setTitle("Welcome");
        Bsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog = new ProgressDialog(Bluetooth5Activity.this);
        pDialog.setMessage("Sending...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();
                dialog.dismiss();
                insertToDatabase(patientid, readMessage, strDate, strTime, username);

//                Toast.makeText(getApplicationContext(),"Send",Toast.LENGTH_SHORT).show();
//                return;
            }
        });
        Bcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getApplicationContext(),"Cancel",Toast.LENGTH_SHORT).show();
                dialog.dismiss();
//                return;
            }
        });
        dialog.show();

//        Toast.makeText(this, "Values: ID"+ patientid+ bobValue+ strDate+ strTime, Toast.LENGTH_SHORT).show();


    }


    private void insertToDatabase(String patientid, String distance, String date, String time, String u_name)
    {
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String>
        {
            @Override
            protected String doInBackground(String... params) {
                String parampid = params[0];
                String paramdistance = params[1];
                String paramdate = params[2];
                String paramtime = params[3];
                String paramu_name = params[4];

                // InputStream is = null;

                String patientid = parampid;
                String distance = paramdistance;
                String date = paramdate;
                String time = paramtime;
                String u_name = paramu_name;


                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
                nameValuePairs.add(new BasicNameValuePair("patientid", patientid));
                nameValuePairs.add(new BasicNameValuePair("distance", distance));
                nameValuePairs.add(new BasicNameValuePair("date", date));
                nameValuePairs.add(new BasicNameValuePair("time", time));
                nameValuePairs.add(new BasicNameValuePair("u_name", u_name));

                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost("http://www.mamacare.or.ke/applicationfiles/insertnewvalue.php");
                    httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    HttpResponse response = httpClient.execute(httpPost);

                    HttpEntity entity = response.getEntity();

                    final String content;
                    try {
                        content = EntityUtils.toString(entity);

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                MyREsponseFromphp = content;
                            }
                        });

                    } catch (ClientProtocolException e) {
                        Toast.makeText(Bluetooth5Activity.this, "Error", Toast.LENGTH_SHORT).show();
                    }



                } catch (ClientProtocolException e)
                {
                    Toast.makeText(Bluetooth5Activity.this, "ClientProtocolException", Toast.LENGTH_SHORT).show();
                } catch (IOException e)
                {
                    Toast.makeText(Bluetooth5Activity.this, "IOException", Toast.LENGTH_SHORT).show();
                }

                return "Success. Sent";



            }

            @Override
            protected void onPostExecute(String result)
            {
                if(MyREsponseFromphp.contentEquals("success")) {
                    super.onPostExecute(result);
                    pDialog.dismiss();
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(Bluetooth5Activity.this);
                    alertDialog.setTitle("Success");
                    alertDialog.setMessage("Distance readings have been sent.");
                    alertDialog.setPositiveButton("Okay",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    bobValue.setText("");
                                    return;
                                }
                            });
                    alertDialog.show();
                }
                else{
			   pDialog.dismiss();
                    Toast.makeText(Bluetooth5Activity.this, "Not sent, an error was encountered.", Toast.LENGTH_SHORT).show();
                }
	    }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(patientid, distance, date, time, u_name);
    }





    public void CheckNet ()
    {
        ConnectivityManager cn = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nf = cn.getActiveNetworkInfo();
        if (nf != null && nf.isConnected() == true)
        {
            Checks();

        } else {

            Snackbar();
        }
    }
    public void Snackbar() {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),
                "No internet connection!", Snackbar.LENGTH_LONG).setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        snackbar.setActionTextColor(Color.RED);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        snackbar.show();
    }

    public void Checks() {
        TextView ourtv = (TextView) findViewById(R.id.bobValueTv);
        String bv = ourtv.getText().toString();
        String read2= readMessage.toUpperCase();
//        int num;
//        Toast.makeText(this, "Value "+ readMessage, Toast.LENGTH_SHORT).show();
        // check for blanks
        if (readMessage.contentEquals("blank")) {
            Toast.makeText(Bluetooth5Activity.this, "Empty value for distance.",    Toast.LENGTH_SHORT).show();
//            return;
        }
        if (ourtv.getText().toString().isEmpty()){
            Toast.makeText(Bluetooth5Activity.this, "Empty value for distance in textview.",    Toast.LENGTH_SHORT).show();
        }
        if(read2.contains("A") || read2.contains("M") || read2.contains("C") ||read2.contains("R")||read2.contains("E")||read2.contains(" "))
        {
            Toast.makeText(this, "Value cannot be sent. Unwanted characters present.", Toast.LENGTH_SHORT).show();
        }

       /* try {
            int num = Integer.parseInt(bv);
            Toast.makeText(this, "Is int", Toast.LENGTH_SHORT).show();
        } catch (NumberFormatException e){
            Toast.makeText(this, "Not an int", Toast.LENGTH_SHORT).show();
        }*/

        else


            SendDetails();

    }
    public void cleardata(View v)
    {
        bobValue.setText("");
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.home) {
            Prof();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void Prof (){
        Intent pIntent = new Intent(getBaseContext(), HomeActivity.class);
        startActivityForResult(pIntent, 0);
    }
}