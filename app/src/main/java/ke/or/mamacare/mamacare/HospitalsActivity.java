package ke.or.mamacare.mamacare;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HospitalsActivity extends ListActivity {
    private ProgressDialog pDialog;
    //    ListView lv;   // Creating JSON Parser object
    JSONParser jsonParser = new JSONParser();
    ArrayList<HashMap<String, String>> albumsList;
    JSONArray albums = null;

    // JSON url
    private static final String URL_HOSP = "http://www.mamacare.or.ke/applicationfiles/hospitals.php";
//    private static  String URL_HOSP ="nulllink";
    String userID;
    String CheckResponseJSONLength = "blank";

    // ALL JSON node names
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "hospital_name";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospitals);

        albumsList = new ArrayList<HashMap<String, String>>();

        // Loading News JSON in Background Thread
        new LoadAlbums().execute();
        ListView lv = getListView();
//         lv = (ListView) findViewById(R.id.list);


        /**
         * Listview item click listener
         * TrackListActivity will be lauched by passing album id
         * */
        lv.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
                                      @Override
                                      public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
                                          String reqPass;
                                          SharedPreferences myprefs = PreferenceManager.getDefaultSharedPreferences(HospitalsActivity.this);
                                          reqPass = myprefs.getString("requirepassword", "");
                                          Toast.makeText(HospitalsActivity.this, "Stt:"+ reqPass, Toast.LENGTH_SHORT).show();
                                          if (reqPass.contentEquals("yes")) {

                                              final String item_id = ((TextView) view.findViewById(R.id.item_id)).getText().toString();
                                              final String hosp = ((TextView) view.findViewById(R.id.HospName)).getText().toString();
                                              Toast.makeText(HospitalsActivity.this, "Id: " + item_id, Toast.LENGTH_SHORT).show();
                                              LayoutInflater inflater = getLayoutInflater();

                                              View alertLayout = inflater.inflate(R.layout.layout_custom_dialog, null);
                                              final EditText Edphone = (EditText) alertLayout.findViewById(R.id.passwordTxt);



                                              android.support.v7.app.AlertDialog.Builder alert = new android.support.v7.app.AlertDialog.Builder(HospitalsActivity.this);
                                              alert.setView(alertLayout);
                                              alert.setTitle("Enter hospital password");
                                              // disallow cancel of AlertDialog on click of back button and outside touch
                                              alert.setCancelable(false);
                                              alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                  @Override
                                                  public void onClick(DialogInterface dialog, int which) {

                                                  }
                                              });

                                              alert.setPositiveButton("Okay", new DialogInterface.OnClickListener() {

                                                  @Override
                                                  public void onClick(DialogInterface dialog, int which) {
                                                      String pw = Edphone.getText().toString();
                                                      if (pw.isEmpty()) {
                                                          Toast.makeText(HospitalsActivity.this, "Please enter the password", Toast.LENGTH_SHORT).show();
                                                          return;
                                                      } else {
                                                          String SHpass;
                                                          SharedPreferences myprefs = PreferenceManager.getDefaultSharedPreferences(HospitalsActivity.this);
                                                          SHpass = myprefs.getString("pass", "");
//                            Toast.makeText(HospitalsActivity.this, "Pass: "+SHpass, Toast.LENGTH_SHORT).show();

                                                          if (pw.contentEquals(SHpass)) {
//                                Toast.makeText(HospitalsActivity.this, "Password okay", Toast.LENGTH_SHORT).show();
                                                              Intent i = new Intent(getApplicationContext(), PatientsActivity.class);
                                                              i.putExtra("hospital_id", item_id);
                                                              startActivity(i);
                                                          } else
                                                              Toast.makeText(HospitalsActivity.this, "Password rejected", Toast.LENGTH_SHORT).show();
                                                      }
                                                  }


                                              });
                                              android.support.v7.app.AlertDialog dialog = alert.create();
                                              dialog.show();
                                          } else if(reqPass.contentEquals("no")) {
                                              String item_id = ((TextView) view.findViewById(R.id.item_id)).getText().toString();
                                              Intent io = new Intent(getApplicationContext(), PatientsActivity.class);
                                              io.putExtra("hospital_id", item_id);
                                              startActivity(io);
                                          }
                                          else{
                                              Toast.makeText(HospitalsActivity.this, "Please select password preferences from Settings menu.", Toast.LENGTH_SHORT).show();
                                              String item_id = ((TextView) view.findViewById(R.id.item_id)).getText().toString();
                                              Intent i = new Intent(getApplicationContext(), PatientsActivity.class);
                                              i.putExtra("hospital_id", item_id);
                                              startActivity(i);
                                          }
                                      }

//                Intent i = new Intent(getApplicationContext(), PatientsActivity.class);
//                i.putExtra("hospital_id", item_id);
//                startActivity(i);

        });
    }


    /**
     * Background Async Task to Load all items by making http request
     */
    class LoadAlbums extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(HospitalsActivity.this);
            pDialog.setMessage("Loading...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * getting Albums JSON
         */
        protected String doInBackground(String... args) {
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            // getting JSON string from URL
            String json = jsonParser.makeHttpRequest(URL_HOSP, "GET", params);

            // Check your log cat for JSON reponse
            Log.d("Albums JSON: ", "> " + json);
            CheckResponseJSONLength = json;

            try {
                albums = new JSONArray(json);

                if (albums != null) {
                    // looping through All albums
                    for (int i = 0; i < albums.length(); i++) {
                        JSONObject c = albums.getJSONObject(i);

                        // Storing each json item values in variable
                        String id = c.getString(TAG_ID);
                        String hosp = c.getString(TAG_NAME);
                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        map.put(TAG_ID, id);
                        map.put(TAG_NAME, hosp);
                        // adding HashList to ArrayList
                        albumsList.add(map);
                    }
                } else {
                    Log.d("Hospitals: ", "null");
                    showNullJsonResponse();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all albums
            pDialog.dismiss();
//            int l = CheckResponseJSONLength.length();
//            Toast.makeText(TransactionsActivity.this, "cont "+CheckResponseJSONLength, Toast.LENGTH_SHORT).show();
//            Toast.makeText(TransactionsActivity.this, "length  "+String.valueOf(l), Toast.LENGTH_SHORT).show();
            if(CheckResponseJSONLength.length()<5){
                Toast.makeText(HospitalsActivity.this, "There are no hospitals available", Toast.LENGTH_SHORT).show();
            }

            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                    ListAdapter adapter = new SimpleAdapter
                            (
                                    HospitalsActivity.this, albumsList, R.layout.list_item_hosp, new String[]
                                    {TAG_ID, TAG_NAME}, new int[]
                                    {R.id.item_id, R.id.HospName});

                    // updating listview
                    setListAdapter(adapter);
                }
            });
        }
    }

    public void showNullJsonResponse(){
        Toast.makeText(this, "No response. Try again later", Toast.LENGTH_SHORT).show();
    }
    public void addPatient(View v){
        Intent ff = new Intent(getBaseContext(), AddPatientActivity.class);
        startActivityForResult(ff, 0);
    }
}