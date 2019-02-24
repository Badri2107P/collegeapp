package in.care.ac.caregroupofinstitutions;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.READ_CONTACTS;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
EditText rollno,password;
    Button b1;
    private ProgressDialog pDialog;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        session = new SessionManager(getApplicationContext());

        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }else {

            rollno = (EditText) findViewById(R.id.rollno);
            password = (EditText) findViewById(R.id.password);
            b1 = (Button) findViewById(R.id.sign_in_button);
            b1.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    String rno = rollno.getText().toString().trim();
                    String pass = password.getText().toString().trim();

                    // Check for empty data in the form
                    if (!rno.isEmpty() && !pass.isEmpty()) {
                        // login user
                        new checkLogin().execute();
                    } else {
                        // Prompt user to enter credentials
                        Toast.makeText(getApplicationContext(),
                                "Please enter the credentials!", Toast.LENGTH_LONG)
                                .show();
                    }
                }
            });
        }
    }
    private class checkLogin extends AsyncTask<Void, Void, Void> {

        protected boolean IsLoggedin;
        protected String stryr,endyear,name,rollno1,branch,sem,programcode;

        String url="a.php?";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setMessage("Logging in...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {

            String tempurl=session.mainurl+url;
            String url1= addLocationToUrl(tempurl);

            HTTPHandler sh = new HTTPHandler();

            Log.i("urldata",url1);
            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url1);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);


                    JSONArray marksarray = jsonObj.getJSONArray("studinfo");
                    JSONObject c = marksarray.getJSONObject(0);

                    name = c.getString("name");
                    rollno1= c.getString("rollno");
                    branch = c.getString("branch");
                    stryr= c.getString("startyear");
                    endyear= c.getString("endyear");
                    sem= c.getString("sem");
                    programcode= c.getString("programcode");

                    if (jsonObj.getString("success").equals("1")){
                        IsLoggedin=true;
                        Log.i("isloggedin","true");
                    }else{
                        IsLoggedin=false;
                        Log.i("isloggedin","false");
                    }

                    // Getting JSON Array node
                  //  String loginarray = jsonObj.getJSONObject().getString("success");

                //    Log.i("c",loginarray.toString());


              /*      // looping through All Contacts
                    for (int i = 0 ; i < marksarray.length(); i++) {
                        JSONObject c = marksarray.getJSONObject(i);

                        String marksSub = c.getString("sub_name");
                        String marksCode = c.getString("sub_code");
                        String marksMark=c.getString("sub_mark");

                        Log.i("markssub",marksSub);
                        Log.i("markscode",marksCode);
                        Log.i("marks",marksMark);

                        // tmp hash map for single contact
                        HashMap<String, String> marksa = new HashMap<>();
                        // adding each child node to HashMap key => value

                        marksa.put("markssub",marksSub);
                        marksa.put("markscode",marksCode);
                        marksa.put("marks",marksMark);

                        // adding contact to contact list
                      //  Log.i("attnds",marksa.toString());
                    //    Log.i("Marks",MarkList.toString());
                    //    MarkList.add(marksa);

                    }*/
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());


                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");


            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Date c = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("yyyy");
            String date = df.format(c);
            int temp=(Integer.parseInt(date)-Integer.parseInt(stryr))+1;
            String d=Integer.toString(temp);
            session.setLogin(IsLoggedin,rollno1,name,branch,sem,d,endyear,programcode);
            if (pDialog.isShowing())
                pDialog.dismiss();
            if (IsLoggedin){
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }else{
                Toast.makeText(LoginActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
            }

        }


        protected String addLocationToUrl(String url){

            String rno = rollno.getText().toString().trim();
            String pass = password.getText().toString().trim();

            if(!url.endsWith("?"))
                url += "?";
            List<NameValuePair> params = new LinkedList<NameValuePair>();



            if (pass!=null && rno!=null ) {
                params.add(new BasicNameValuePair("id",rno));
                params.add(new BasicNameValuePair("pass", pass));
            }else{
                Toast.makeText(LoginActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
            }
            String paramString = URLEncodedUtils.format(params, "utf-8");

            url += paramString;
            return url;
        }


    }
}


