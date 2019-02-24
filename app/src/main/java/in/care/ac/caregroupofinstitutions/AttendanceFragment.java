package in.care.ac.caregroupofinstitutions;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static android.R.attr.name;
import static android.R.attr.state_empty;

public class AttendanceFragment extends Fragment {

    private ProgressDialog pDialog;
    private SessionManager session;

    private static String  url = "attendance.php?";
    private String rollno;
    private String TAG = MainActivity.class.getSimpleName();
    // URL to get contacts JSON
    ArrayList<HashMap<String, String>> AttendList;
    TextView t1;
    public  ListView lv1;
    public String date;
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_attendance, container, false);

        t1=(TextView) view.findViewById(R.id.attndviewdate);
        lv1=(ListView) view.findViewById(R.id.attndlistmain);
        AttendList = new ArrayList<>();
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        date = df.format(c);
        t1.setText(date);
        new GetAttendance().execute();
        return view;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        t1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                showDatePicker();
            }
        });
    }

    private class GetAttendance extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Gathering Data..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
            AttendList.clear();

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

                    // Getting JSON Array node
                    JSONArray attnd = jsonObj.getJSONArray("attendance");

                    // looping through All Contacts
                    for (int i = 0 ; i < attnd.length(); i++) {
                  //      Log.i("i",Integer.toString(i));
                        JSONObject c = attnd.getJSONObject(i);

                        String atndFlag = c.getString("atnd_flag");
                        String attndHour = c.getString("atnd_hour");
                        String attndSub= c.getString("atnd_sub");
                        String attndCode= c.getString("atnd_code");

                        // tmp hash map for single contact
                        HashMap<String, String> attnds = new HashMap<>();
                        int a = Integer.parseInt(atndFlag);
                        if(a==1){
                            attnds.put("attndFlag", "P");
                        }else {
                            attnds.put("attndFlag", "A");
                        }
                        // adding each child node to HashMap key => value

                        attnds.put("attndHour",attndHour+". ");
                        attnds.put("attndSub",attndSub.trim());
                        attnds.put("attndCode",attndCode);

                        // adding contact to contact list
                        AttendList.add(attnds);

                    }
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
            Log.i("Attend",AttendList.toString());
            ListAdapter adapter1 = new SimpleAdapter(
                    getActivity(), AttendList,
                    R.layout.attendance_list, new String[]{"attndFlag", "attndHour","attndSub", "attndCode"},
                    new int[]{R.id.attndstatus, R.id.attndhour, R.id.attndname ,R.id.attndcode});

            t1.setText(date.toString());
            Log.i("Adapter",adapter1.toString());
            lv1.deferNotifyDataSetChanged();
            lv1.setAdapter(adapter1);

           //  adapter1.notify();
            //  Parsedata();

            pDialog.dismiss();
            if (pDialog.isShowing())
                pDialog.dismiss();


        }


        protected String addLocationToUrl(String url){

            session=new SessionManager(getActivity().getApplicationContext());
            if(!url.endsWith("?"))
                url += "?";
            List<NameValuePair> params = new LinkedList<NameValuePair>();

            rollno=session.GetId();
          //  Log.d("roll",rollno);

            if (date != null && rollno!=null ) {

                params.add(new BasicNameValuePair("rollno",rollno));
                params.add(new BasicNameValuePair("date", date                                                                                ));
           }
            String paramString = URLEncodedUtils.format(params, "utf-8");

            url += paramString;
            return url;
        }


    }

    public void Parsedata(){
//Log.i("arraysize",Integer.toString(AttendList.size()));
        for (int i=1; i<= AttendList.size();i++){
            String n="attnd"+i;
           // Log.i("Hash",AttendList.get(i-1).get("attndFlag"));
            changeText(this.getActivity(),n,AttendList.get(i-1).get("attndFlag"));
        }

    }
    public void changeText(Activity a, String textViewId, String text) {
        String packageName = getActivity().getPackageName();
        int resId = getResources().getIdentifier(textViewId, "id", packageName);
        //Log.i("hash1",textViewId);

        TextView tv = (TextView) a.findViewById(resId);
        tv.setText(text);
    }

    private void showDatePicker() {
        DatePickerFragment date = new DatePickerFragment();
        /**
         * Set Up Current Date Into dialog
         */
        Calendar calender = Calendar.getInstance();
        Bundle args = new Bundle();
        args.putInt("year", calender.get(Calendar.YEAR));
        args.putInt("month", calender.get(Calendar.MONTH));
        args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
        date.setArguments(args);
        /**
         * Set Call back to capture selected date
         */
        date.setCallBack(ondate);
        date.show(getFragmentManager(), "Date Picker");
    }

    DatePickerDialog.OnDateSetListener ondate = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {

            t1.setText(String.valueOf(year) + "-" + String.valueOf(monthOfYear+1)
                    + "-" + String.valueOf(dayOfMonth));
            date=String.valueOf(year) + "-" + String.valueOf(monthOfYear+1)
                    + "-" + String.valueOf(dayOfMonth);
            new GetAttendance().execute();
        }
    };




}