package ke.or.mamacare.mamacare;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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

public class PatientsActivity extends ListActivity {
    private ProgressDialog pDialog;
    //    ListView lv;   // Creating JSON Parser object
    JSONParser jsonParser = new JSONParser();
    ArrayList<HashMap<String, String>> albumsList;
    JSONArray albums = null;
String hospital_id;
    // JSON url
    private static final String URL_HOSP = "http://www.mamacare.or.ke/applicationfiles/patients.php";
        private static  String URL_HOSP2 ="nulllink";
    String userID;
    String CheckResponseJSONLength = "blank";

    // ALL JSON node names
    private static final String TAG_ID = "id";
    private static final String TAG_NAME = "patient_name";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patients);

        Intent i = getIntent();
        hospital_id = i.getStringExtra("hospital_id");
URL_HOSP2 = URL_HOSP+"?idquery="+hospital_id;
        albumsList = new ArrayList<HashMap<String, String>>();

        // Loading News JSON in Background Thread
        new PatientsActivity.LoadAlbums().execute();
        ListView lv = getListView();
//         lv = (ListView) findViewById(R.id.list);


        /**
         * Listview item click listener
         * TrackListActivity will be lauched by passing album id
         * */
        lv.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
                Intent i = new Intent(getApplicationContext(), SinglePatientActivity.class);
                String item_id = ((TextView) view.findViewById(R.id.item_id)).getText().toString();
                i.putExtra("patient_id", item_id);
                Toast.makeText(PatientsActivity.this, "Id: "+item_id, Toast.LENGTH_SHORT).show();

                startActivity(i);
            }
        });
    }


    /**
     * Background Async Task to Load all News by making http request
     */
    class LoadAlbums extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(PatientsActivity.this);
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
            String json = jsonParser.makeHttpRequest(URL_HOSP2, "GET", params);

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
                    Log.d("Patients: ", "null");
//                    Toast.makeText(TransactionsActivity.this, "No transactions found.", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(PatientsActivity.this, "There are no patients available", Toast.LENGTH_SHORT).show();
            }

            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed JSON data into ListView
                     * */
                    ListAdapter adapter = new SimpleAdapter
                            (
                                    PatientsActivity.this, albumsList, R.layout.list_item_hosp, new String[]
                                    {TAG_ID, TAG_NAME}, new int[]
                                    {R.id.item_id, R.id.HospName});

                    // updating listview
                    setListAdapter(adapter);
                }
            });
        }
    }
    public void addPatient(View v){
        Intent ff = new Intent(getBaseContext(), AddPatientActivity.class);
        startActivityForResult(ff, 0);
    }
}