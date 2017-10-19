package ke.or.mamacare.mamacare;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton;

import java.util.Set;

public class SettingsActivity extends AppCompatActivity {
    CheckBox cbP;
    TextView pt, nameTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        cbP = (CheckBox) findViewById(R.id.CBP);

        String reqPass;
        SharedPreferences myprefs = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
        reqPass = myprefs.getString("requirepassword", "");
        Toast.makeText(SettingsActivity.this, "Stt:" + reqPass, Toast.LENGTH_SHORT).show();
        if (reqPass.contentEquals("yes")) {
            cbP.setChecked(true);
        } else {
            cbP.setChecked(false);
        }
        pt = (TextView) findViewById(R.id.passwordText);

/*        SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
        SharedPreferences.Editor editor = myPrefs.edit();
        editor.putString("firstrun", "yes");
        editor.apply();*/

        cbP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cbP.isChecked()) {
                    SharedPreferences myprefs = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
                    String  pw = myprefs.getString("pass", "");
                    if (pw.isEmpty()){
                        Toast.makeText(SettingsActivity.this, "You first need to set a password.", Toast.LENGTH_SHORT).show();
                        cbP.setChecked(false);
                    }
                    else {//if (!pw.isEmpty()) {
                    pt.setText("A password is required.");
//                    SharedPreferences myprefs = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
                    SharedPreferences.Editor editor = myprefs.edit();
                    editor.putString("requirepassword", "yes");
                    editor.apply();
                    }
//                    Toast.makeText(SettingsActivity.this, "Checked", Toast.LENGTH_SHORT).show();
                } else if (!cbP.isChecked()) {
                    pt.setText("A password is not required.");
                    SharedPreferences myprefs = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
                    SharedPreferences.Editor editor = myprefs.edit();
                    editor.putString("requirepassword", "no");
                    editor.apply();
                }
//                    Toast.makeText(SettingsActivity.this, "Not checked", Toast.LENGTH_SHORT).show();
//                Log.i(TAG, String.format("checkbox onClick, isSelected: %s, identityHashCode: %s", cbP.isSelected(), System.identityHashCode(cbP)));
            }
        });

        //username
        nameTV = (TextView) findViewById(R.id.TXTUSERNAME);
        String username;
//        SharedPreferences myprefs = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
        username = myprefs.getString("username", "");
        Toast.makeText(SettingsActivity.this, "Username:" + username, Toast.LENGTH_SHORT).show();
        if (username.contentEquals("")) {
            nameTV.setText("Click here to enter a user name.");
        }
        if (!username.isEmpty()){
            nameTV.setText(username);
        }

    }

    public void setName (View v){
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_custom_dialog_uname, null);
        final EditText unameEd = (EditText) alertLayout.findViewById(R.id.Edusername);
//        Edphone.setText(_telephone);
        SharedPreferences myprefs = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
        String  username = myprefs.getString("username", "");
        unameEd.setText(username);

        android.support.v7.app.AlertDialog.Builder alert = new android.support.v7.app.AlertDialog.Builder(this);
        alert.setTitle("Username");
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(true);
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = unameEd.getText().toString();
                if(!name.isEmpty()) {
                    SharedPreferences myprefs = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
                    SharedPreferences.Editor editor = myprefs.edit();
                    editor.putString("username", name);
                    editor.apply();
                    nameTV.setText(name);
                    Intent pIntent = new Intent(getBaseContext(), HomeActivity.class);
                    startActivityForResult(pIntent, 0);

                }
                else if(name.isEmpty()){
                    Toast.makeText(SettingsActivity.this, "Please enter a username", Toast.LENGTH_SHORT).show();
                }
            }
        });
        android.support.v7.app.AlertDialog dialog = alert.create();
        dialog.show();

    }
    public void setPassword(View v){
        LayoutInflater inflater = getLayoutInflater();
        View alertLayout = inflater.inflate(R.layout.layout_custom_dialog_setpassword, null);
        final EditText Edpword = (EditText) alertLayout.findViewById(R.id.EDpassword);
/*//        Edphone.setText(_telephone);
        SharedPreferences myprefs = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
        String  username = myprefs.getString("username", "");
        unameEd.setText(username);*/

        android.support.v7.app.AlertDialog.Builder alert = new android.support.v7.app.AlertDialog.Builder(this);
        alert.setTitle("Set a password");
        // this is set the view from XML inside AlertDialog
        alert.setView(alertLayout);
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(true);
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alert.setPositiveButton("Set", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String pword = Edpword.getText().toString();
                if(pword.isEmpty()){
                    Toast.makeText(SettingsActivity.this, "Please enter a username", Toast.LENGTH_SHORT).show();
                }
                if(pword.length()<4){
                    Toast.makeText(SettingsActivity.this, "Pass code must be four digits", Toast.LENGTH_SHORT).show();
                }

                else {
                    SharedPreferences myprefs = PreferenceManager.getDefaultSharedPreferences(SettingsActivity.this);
                    SharedPreferences.Editor editor = myprefs.edit();
                    editor.putString("pass", pword);
                    editor.apply();

                }

            }
        });
        android.support.v7.app.AlertDialog dialog = alert.create();
        dialog.show();

    }
}
