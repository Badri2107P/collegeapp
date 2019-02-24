package in.care.ac.caregroupofinstitutions;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by Balaji on 02-09-2018.
 */

public class SessionManager {
    private static String TAG = SessionManager.class.getSimpleName();
    public static String mainurl="http://192.168.0.103/clg/";

    // Shared Preferences
    SharedPreferences pref;

    SharedPreferences.Editor editor;
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "Login";

    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();

    }

    public void setLogin(boolean isLoggedIn,String rollno,String name,String branch,String sem,String startyr,String endyr,String programcode) {

        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);
        editor.putString("rollno",rollno );
        editor.putString("name",name );
        editor.putString("branch",branch );
        editor.putString("sem",sem );
        editor.putString("startyear",startyr );
        editor.putString("endyear",endyr);
        editor.putString("programcode",programcode );

        // commit changes
        editor.commit();

        Log.d(TAG, "User login session modified!");
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }

    public String GetId(){
        String id="";
        if(isLoggedIn()){
            id=pref.getString("rollno",null);
        }
        return id;
    }

    public HashMap<String, String> GetDetails(){
        HashMap<String, String> det = new HashMap<>();
        // adding each child node to HashMap key => value
        String rollno=pref.getString("rollno",null);
        String name=pref.getString("name",null);
        String branch=pref.getString("branch",null);
        String sem=pref.getString("sem",null);
        String startyear=pref.getString("startyear",null);
        String endyear=pref.getString("endyear",null);
        String programcode=pref.getString("programcode",null);
        det.put("rollno",rollno);
        det.put("name",name);
        det.put("branch",branch);
        det.put("sem",sem);
        det.put("startyear",startyear);
        det.put("endyear",endyear);
        det.put("programcode",programcode);
        return det;
    }

    public void setfragreplace(){
        editor.putString("replace", "1");
    }

    public boolean fragreplace(){
        String b= pref.getString("replace",null);
        boolean s;
        if (b=="1")
            s=true;
        else
            s=false;
        return s;
    }
}