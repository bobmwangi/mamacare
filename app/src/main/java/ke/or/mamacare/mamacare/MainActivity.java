package ke.or.mamacare.mamacare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import ke.or.mamacare.mamacare.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set passwords
        /*SharedPreferences myprefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        SharedPreferences.Editor editor = myprefs.edit();
        editor.putString("pass", "1234");
        editor.putString("firstrun", "yes");
        editor.apply();*/

        String font2 = "fonts/CaviarDreams.ttf";
        // text view label
        TextView txt3 = (TextView) findViewById(R.id.welcome);
        TextView txt4 = (TextView) findViewById(R.id.tagline);
        // Loading Font Face
        Typeface tf3 = Typeface.createFromAsset(getAssets(), font2);
        // Applying font
        txt3.setTypeface(tf3);
        txt4.setTypeface(tf3);


        //notif bar color
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        int sDelayed = 3;
        new Handler().postDelayed(new Runnable() {
            public void run() {
//                CheckFirst();

                Intent ff = new Intent(getBaseContext(), HomeActivity.class);
                startActivityForResult(ff, 0);

            }
        }, sDelayed * 1000);

    }
}
