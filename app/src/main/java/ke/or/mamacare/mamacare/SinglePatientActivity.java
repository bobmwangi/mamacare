package ke.or.mamacare.mamacare;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SinglePatientActivity extends AppCompatActivity {
    private ProgressDialog pDialog, pDialog2;
    // Creating JSON Parser object
    JSONParser jsonParser = new JSONParser();
    // items JSONArray
    JSONArray albums = null;
    // item id
    String patient_id = null;

    String _id,_pname, _hosp, _date;
    private static final String TAG_ID= "id";
    private static final String TAG_PNAME = "patient_name";
    private static final String TAG_HOSP = "hospital";
    private static final String TAG_DATE = "date_joined";

//    ArrayList<MyLocations> String;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_patient);

        //notif bar color
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        Intent i = getIntent();
        patient_id = i.getStringExtra("patient_id");
        // calling background thread
        new LoadSingleEvent().execute();

        //json stuff

//        loadJSONFromAsset();
       /* String json = null;
        try {
            InputStream is = getAssets().open("jsondata.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
         //   return null;
        }*/
//        return json;
//        String jsonString = json;
//        String jsonString = { "name":"John", "age":31, "city":"New York" };
//        myObj = { "name":"John", "age":31, "city":"New York" };


       /* GraphView graph = (GraphView) findViewById(R.id.graph);
        int intArray[]={1, 5, 3, 2, 6};
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {


                new DataPoint(0, intArray[0]),
                new DataPoint(1, intArray[1]),
                new DataPoint(2, intArray[2]),
                new DataPoint(3, intArray[3]),
                new DataPoint(4, intArray[4])
        });
        graph.addSeries(series);
        graph.setTitle("Patient's reading");
        graph.getGridLabelRenderer().setHorizontalAxisTitle("Hor");
        graph.getGridLabelRenderer().setVerticalAxisTitle("Ver");*/
    }

    /**
     * Background Async Task to get values
     * */
    class LoadSingleEvent extends AsyncTask<String, String, String>
    {
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SinglePatientActivity.this);
            pDialog.setMessage("Loading details...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * getting song json and parsing
         * */
        protected String doInBackground(String... args)
        {

            // List<NameValuePair> params = new ArrayList<NameValuePair>();
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            //  List<NameValuePair> params = new ArrayList<NameValuePair>();
            String URL_STAFF = "http://www.mamacare.or.ke/applicationfiles/singlepatient.php/?idquery="+patient_id;
            // post album id, song id as GET parameters
            params.add(new BasicNameValuePair("album", patient_id));
            // getting JSON string from URL
            String json = jsonParser.makeHttpRequest(URL_STAFF, "GET", params);

            // Check your log cat for JSON reponse
            Log.d("Single item JSON: ", json);

            try {
                JSONArray jsonArr = new JSONArray(json);

                JSONObject jObj = jsonArr.getJSONObject(0);
                if(jObj != null){

                    _id = jObj.getString(TAG_ID);
                    _pname = jObj.getString(TAG_PNAME);
                    _hosp = jObj.getString(TAG_HOSP);
                    _date= jObj.getString(TAG_DATE);
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getApplication(), "Exception Error", Toast.LENGTH_SHORT).show();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting song information
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable()
            {
                public void run()
                {
                    TextView txt_id = (TextView) findViewById(R.id.pId);
                    TextView txt_name = (TextView) findViewById(R.id.pName);
                    TextView txt_hosp = (TextView) findViewById(R.id.pHosp);
                    TextView txt_date = (TextView) findViewById(R.id.date);

                    // displaying song data in view
                    txt_id.setText("ID: "+_id);
                    txt_name.setText("Patient: "+_pname);
                    txt_hosp.setText("Hospital: "+_hosp);
                    txt_date.setText("Since: "+_date);
                }
            });

        }

    }


    public void generateGraph (View v)
    {
//        loadJSONFromAsset();
        new LoadPatientRecords().execute();
    }
    public void connectDevice (View v)
    {

        Intent io = new Intent(getApplicationContext(), DeviceListActivity.class);
        io.putExtra("pid", _id);
        io.putExtra("pname", _pname);
        startActivity(io);

    }


    /**
     * Background Async Task to get values
     * */
    class LoadPatientRecords extends AsyncTask<String, String, String>
    {
        String jsonValues;
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog2 = new ProgressDialog(SinglePatientActivity.this);
            pDialog2.setMessage("Loading details...");
            pDialog2.setIndeterminate(false);
            pDialog2.setCancelable(true);
            pDialog2.show();
        }

        /**
         * getting song json and parsing
         * */
        protected String doInBackground(String... args)
        {

            // List<NameValuePair> params = new ArrayList<NameValuePair>();
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            //  List<NameValuePair> params = new ArrayList<NameValuePair>();
            String URL_ = "http://www.mamacare.or.ke/applicationfiles/getuservalues.php/?idquery="+patient_id;
            // post album id, song id as GET parameters
            params.add(new BasicNameValuePair("data", patient_id));
            // getting JSON string from URL
            jsonValues = jsonParser.makeHttpRequest(URL_, "GET", params);

            // Check your log cat for JSON reponse
            Log.d("JSON values: ", jsonValues);

            try {
                JSONArray jsonArr = new JSONArray(jsonValues);

                JSONObject jObj = jsonArr.getJSONObject(0);
                if(jObj != null){

//                    _id = jObj.getString(TAG_ID);
                }

            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getApplication(), "Exception Error", Toast.LENGTH_SHORT).show();
            }

            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting song information
            pDialog2.dismiss();
//            arrayy(jsonValues);
//            Toast.makeText(SinglePatientActivity.this, "Data received "+jsonValues, Toast.LENGTH_SHORT).show();
            String json1 = jsonValues.replace("[{\"distance\":\"", "[{\"values\":[");
            String json2 = json1.replace("\"},{\"distance\":\"", ",");
            String json3 = json2.replace("\"}]", "]}]");
//            Toast.makeText(SinglePatientActivity.this, "New"+json3, Toast.LENGTH_SHORT).show();
            arrayy(json3);
            // updating UI from Background Thread
            runOnUiThread(new Runnable()
            {
                public void run()
                {
//                    TextView txt_id = (TextView) findViewById(R.id.pId);
//                    txt_date.setText("Date joined: "+_date);
                }
            });

        }

    }













































    //FETCH FROM LOCAL
    public String loadJSONFromAsset() {
        String json = null;
        try {

            InputStream is = getAssets().open("jsondata.json");

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }


//        Toast.makeText(this, "Info "+json, Toast.LENGTH_SHORT).show();

        String string = json;
        Toast.makeText(this, "json "+ string, Toast.LENGTH_SHORT).show();
        arrayy(string);

        return json;
    }

public void arrayy(String result) {
    String[] mArray = null;
    String[] mArray2 = null;
//    Integer[] intarray=null;
    String datte = "bpb";
    try {
        JSONArray arr = new JSONArray(result);
        JSONObject jObj = arr.getJSONObject(0);
        datte = jObj.getString("values");
//        mArray = arr.join(",").split(",");


        datte = datte.replace("[", "");
        datte = datte.replace("]", "");
        mArray2 = datte.split(",");

    } catch (JSONException e) {
        // Recovery
    }

    Toast.makeText(this, "Array length "+mArray2.length, Toast.LENGTH_SHORT).show();
    if (mArray2.length<=3){
        Toast.makeText(this, "Not enough values for graph(<=3)", Toast.LENGTH_SHORT).show();
    }
    if (mArray2.length==4){
//        Toast.makeText(this, "Not enough values for graph(=4)", Toast.LENGTH_SHORT).show();

            int one = Integer.parseInt(mArray2[0]);
            int two = Integer.parseInt(mArray2[1]);
            int thre = Integer.parseInt(mArray2[2]);
            int four = Integer.parseInt(mArray2[3]);


            GraphView graph = (GraphView) findViewById(R.id.graph);
//        int intArray[]={1, 5, 3, 2, 6};
            LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[]{
                    new DataPoint(0, one),
                    new DataPoint(1, two),
                    new DataPoint(2, thre),
                    new DataPoint(3, four),
            });
            graph.addSeries(series);
            graph.setTitle("Patient's reading");
            graph.getGridLabelRenderer().setHorizontalAxisTitle("Time (hrs)");
            graph.getGridLabelRenderer().setVerticalAxisTitle("Distance (mm)");


    }
    if (mArray2.length==5){
            int one = Integer.parseInt(mArray2[0]);
            int two = Integer.parseInt(mArray2[1]);
            int thre = Integer.parseInt(mArray2[2]);
            int four = Integer.parseInt(mArray2[3]);
            int five = Integer.parseInt(mArray2[4]);


            GraphView graph = (GraphView) findViewById(R.id.graph);
//        int intArray[]={1, 5, 3, 2, 6};
            LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[]{
                    new DataPoint(0, one),
                    new DataPoint(1, two),
                    new DataPoint(2, thre),
                    new DataPoint(3, four),
                    new DataPoint(4, five),
            });
            graph.addSeries(series);
            graph.setTitle("Patient's reading");
            graph.getGridLabelRenderer().setHorizontalAxisTitle("Time (hrs)");
            graph.getGridLabelRenderer().setVerticalAxisTitle("Distance (mm)");


//        Toast.makeText(this, "Not enough values for graph(=5)", Toast.LENGTH_SHORT).show();
    }
if (mArray2.length==6){
//    Toast.makeText(this, "Not enough values for graph(=6)", Toast.LENGTH_SHORT).show();
        int one = Integer.parseInt(mArray2[0]);
        int two = Integer.parseInt(mArray2[1]);
        int thre = Integer.parseInt(mArray2[2]);
        int four = Integer.parseInt(mArray2[3]);
        int five = Integer.parseInt(mArray2[4]);
        int six = Integer.parseInt(mArray2[5]);


        GraphView graph = (GraphView) findViewById(R.id.graph);
//        int intArray[]={1, 5, 3, 2, 6};
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[]{
                new DataPoint(0, one),
                new DataPoint(1, two),
                new DataPoint(2, thre),
                new DataPoint(3, four),
                new DataPoint(4, five),
                new DataPoint(5, six),
        });
        graph.addSeries(series);
        graph.setTitle("Patient's reading");
        graph.getGridLabelRenderer().setHorizontalAxisTitle("Time (hrs)");
        graph.getGridLabelRenderer().setVerticalAxisTitle("Distance (mm)");


}
    else if(mArray2.length==7)
{
    int one = Integer.parseInt(mArray2[0]);// + 2;
    int two = Integer.parseInt(mArray2[1]);// + 2;
    int thre = Integer.parseInt(mArray2[2]);// + 2;
    int four = Integer.parseInt(mArray2[3]);// + 2;
    int five = Integer.parseInt(mArray2[4]);// + 2;
    int six = Integer.parseInt(mArray2[5]);// + 2;
    int seven = Integer.parseInt(mArray2[6]);// + 2;


        GraphView graph = (GraphView) findViewById(R.id.graph);
//        int intArray[]={1, 5, 3, 2, 6};
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[]{
                new DataPoint(0, one),
                new DataPoint(1, two),
                new DataPoint(2, thre),
                new DataPoint(3, four),
                new DataPoint(4, five),
                new DataPoint(5, six),
                new DataPoint(6, seven)
        });
        graph.addSeries(series);
        graph.setTitle("Patient's reading");
        graph.getGridLabelRenderer().setHorizontalAxisTitle("Time (hrs)");
        graph.getGridLabelRenderer().setVerticalAxisTitle("Distance (mm)");

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
