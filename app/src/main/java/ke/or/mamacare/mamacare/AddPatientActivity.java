package ke.or.mamacare.mamacare;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AddPatientActivity extends AppCompatActivity {
    EditText editName;
    ProgressDialog pDialog;
    String hospName="blank", MyREsponseFromphp;
    TextView tvInvisibleError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_patient);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //notif bar color
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

        editName = (EditText) findViewById(R.id.input_pname);

        // Set fake TextView to be in error so that the error message appears
         tvInvisibleError = (TextView)findViewById(R.id.tvInvisibleError);


        //SPINNER BEGIN
        final Spinner dynamicSpinner = (Spinner) findViewById(R.id.spinner);
        String[] items = new String[]{"Please select", "KNH", "Thika Level 5"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dynamicSpinner.setAdapter(adapter);
        dynamicSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                hospName = (String) dynamicSpinner.getSelectedItem();
                //  String Text = dynamicSpinner.getSelectedItem().toString();
                //      Toast.makeText(SafetySMSActivity.this, alertType, Toast.LENGTH_SHORT).show();
                Log.v("item", (String) parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(AddPatientActivity.this, "Please select an alarm", Toast.LENGTH_SHORT).show();
                //return;
                // TODO Auto-generated method stub
            }
        });
//SPINNER END

    }

    public void addP(View v) {
        CheckNet();
    }
    public void SendDetails ()//(String b64String)
    {
        //final ProgressDialog dialog = new ProgressDialog(AddEventsActivity.this);
//String st = encodeImage();
        pDialog = new ProgressDialog(AddPatientActivity.this);
        pDialog.setMessage("Sending...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();


        String name = editName.getText().toString();
//        String hosp = editHosp.getText().toString();

//26-06-17 removed this
     /*   SharedPreferences StoredImg = PreferenceManager.getDefaultSharedPreferences(this);
        String StoredB64 = StoredImg.getString("Image64String", "");*/

        insertToDatabase(name, hospName);
    }


    private void insertToDatabase(String name, String hosp)
    {
        class SendPostReqAsyncTask extends AsyncTask<String, Void, String>
        {
            @Override
            protected String doInBackground(String... params) {
                String paramname = params[0];
                String paramhosp = params[1];

                // InputStream is = null;

                String name = paramname;
                String hosp = paramhosp;


                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
                nameValuePairs.add(new BasicNameValuePair("name", name));
                nameValuePairs.add(new BasicNameValuePair("hosp", hosp));

                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost("http://www.mamacare.or.ke/applicationfiles/insertpatient.php");
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
                        Toast.makeText(AddPatientActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }

                } catch (ClientProtocolException e)
                {
                    Toast.makeText(AddPatientActivity.this, "ClientProtocolException", Toast.LENGTH_SHORT).show();
                } catch (IOException e)
                {
                    Toast.makeText(AddPatientActivity.this, "IOException", Toast.LENGTH_SHORT).show();
                }

                return "Success. Sent";



            }

            @Override
            protected void onPostExecute(String result)
            {
                super.onPostExecute(result);
                pDialog.dismiss();
//                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_LONG).show(); AlertDialog.Builder alertDialog = new AlertDialog.Builder(AttachmentActivity.this);
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(AddPatientActivity.this);
                alertDialog.setTitle("Success");
                alertDialog.setMessage("Patient's details have been sent.");
                alertDialog.setPositiveButton("okay",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
//                                return;
                                Intent ff = new Intent(getBaseContext(), HospitalsActivity.class);
                                startActivityForResult(ff, 0);
                            }
                        });

                alertDialog.show();
                editName.setText("");
            }
        }
        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();
        sendPostReqAsyncTask.execute(name, hosp);
    }



   /* public void btnAdd( View v)
    {
//        Toast.makeText(this, "Account to be created.", Toast.LENGTH_SHORT).show();
        CheckNet();
    }*/

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

        String nammm = editName.getText().toString();

        // check for blanks
        if (nammm.contentEquals("")) {
            Toast.makeText(AddPatientActivity.this, "Fill all fields.",    Toast.LENGTH_SHORT).show();
            return;
        }
        if (hospName.contentEquals("Please select")) {
            Toast.makeText(AddPatientActivity.this, "Select a hospital.",    Toast.LENGTH_SHORT).show();

            final Spinner spnMySpinner = (Spinner) findViewById(R.id.spinner);
            View view = spnMySpinner.getSelectedView();

            // Set TextView in Secondary Unit spinner to be in error so that red (!) icon
            // appears, and then shake control if in error
            TextView tvListItem = (TextView)view;

            tvListItem.setError("Select a hospital.");
            tvListItem.requestFocus();

            // Shake the spinner to highlight that current selection
            // is invalid -- SEE COMMENT BELOW
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
            spnMySpinner.startAnimation(shake);

           /* tvInvisibleError.requestFocus();
            tvInvisibleError.setError("Select a hospital.");*/
            TextView tvInvisibleError2 = (TextView) findViewById(R.id.tvInvisibleError2);
            tvInvisibleError2.setText("Please select a hospital");


            return;
        }


        else {
            TextView tvInvisibleError2 = (TextView) findViewById(R.id.tvInvisibleError2);
            tvInvisibleError2.setVisibility(View.GONE);
            SendDetails();
        }
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