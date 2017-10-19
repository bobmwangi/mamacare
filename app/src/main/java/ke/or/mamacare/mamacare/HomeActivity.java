package ke.or.mamacare.mamacare;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import ke.or.mamacare.mamacare.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class HomeActivity extends AppCompatActivity {
//TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        TextView txt3 = (TextView) findViewById(R.id.usernameTV);
        TextView txt4 = (TextView) findViewById(R.id.usernmaeTVIN);

        SharedPreferences myprefs = PreferenceManager.getDefaultSharedPreferences(HomeActivity.this);
        String  username = myprefs.getString("username", "");
//        Toast.makeText(HomeActivity.this, "Username:" + username, Toast.LENGTH_SHORT).show();
        if (username.contentEquals("")) {
            txt3.setText("Hello, User");
            txt4.setText("U");
        }
        if (!username.isEmpty()){
            txt3.setText("Hello, "+username);
            String in=username.substring(0,1);
            txt4.setText(in.toUpperCase());
        }
//        tv = (TextView) findViewById(R.id.time);


//        Calendar calendar = Calendar.getInstance();
//        SimpleDateFormat mdformat = new SimpleDateFormat("EEE d, MMM yyyy, kk:mm:ss"); // MM / dd "); "EEE, MMM d, ''yy"   Wed, July 10, '96
//        String strDate =  mdformat.format(calendar.getTime());


//        tv.setText("Today: "+strDate);

        String font2 = "fonts/CaviarDreams.ttf";
        TextView txt = (TextView) findViewById(R.id.about);

        Typeface tf3 = Typeface.createFromAsset(getAssets(), font2);
        txt.setTypeface(tf3);
        txt3.setTypeface(tf3);
    }
    public boolean onKeyDown(int keycode, KeyEvent event) {
        if (keycode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
        }
        return super.onKeyDown(keycode, event);
    }

    public void hospitals(View v)
    {
        ConnectivityManager cn = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo nf = cn.getActiveNetworkInfo();
        if (nf != null && nf.isConnected() == true) {
            Intent pIntent = new Intent(getBaseContext(), HospitalsActivity.class);
            startActivityForResult(pIntent, 0);
        }
        else Snackbar();
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.settings) {
            set();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void set (){
        Intent pIntent = new Intent(getBaseContext(), SettingsActivity.class);
        startActivityForResult(pIntent, 0);
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_home, menu);//Menu Resource, Menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar actions click
        switch (item.getItemId()) {

            case R.id.action_settings:
                st();
                break;
            //return true;
            default:
                return super.onOptionsItemSelected(item);
        }

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
    }*/
    public void st(View v){
        Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
        startActivity(i);
    }
}
