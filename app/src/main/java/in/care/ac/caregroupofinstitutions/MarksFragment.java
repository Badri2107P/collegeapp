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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


public class MarksFragment extends Fragment {


    public MarksFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MarksFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MarksFragment newInstance(String param1, String param2) {
        MarksFragment fragment = new MarksFragment();
        return fragment;
    }

    private ProgressDialog pDialog;
    private SessionManager session;

    private static String  url = "marks.php?";
    private  static  String rollno;
    private String TAG = MainActivity.class.getSimpleName();
    // URL to get contacts JSON
    ArrayList<HashMap<String, String>> MarkList;


    Button b1;
    Spinner s1;
    ListView lv;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        session=new SessionManager(getActivity().getApplicationContext());
        rollno=session.GetId();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_marks, container, false);

        lv = (ListView) view.findViewById(R.id.list);
        s1=(Spinner) view.findViewById(R.id.testspinner);
        MarkList = new ArrayList<>();
        return view;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        s1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                new GetMarks().execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
                Log.e("None","None");
            }

        });
    }

    private class GetMarks extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MarkList.clear();
            // Showing progress dialog
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Gathering Data..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
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

                    // Getting JSON Array node
                    JSONArray marksarray = jsonObj.getJSONArray("marks");

                    // looping through All Contacts
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
                     //   Log.i("attnds",marksa.toString());
                      //  Log.i("Marks",MarkList.toString());
                        MarkList.add(marksa);

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
            pDialog.dismiss();
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            Log.i("Marks",MarkList.toString());
            ListAdapter adapter = new SimpleAdapter(
                    getActivity(), MarkList,
                    R.layout.mark_items, new String[]{"markssub", "markscode",
                    "marks"}, new int[]{R.id.subname,
                    R.id.subcode, R.id.marks});



            lv.setAdapter(adapter);
            //Log.i("Marks",MarkList.toString());

        }


        protected String addLocationToUrl(String url){
            if(!url.endsWith("?"))
                url += "?";
            List<NameValuePair> params = new LinkedList<NameValuePair>();
            String s=SpinnerValue(s1.getSelectedItem().toString().trim());
            HashMap<String,String> details=session.GetDetails();
            String sem=details.get("sem");
            String branch=details.get("branch");
            String pc=details.get("programcode");

            if (s!=null && rollno!=null ) {
                params.add(new BasicNameValuePair("rollno",rollno));
                params.add(new BasicNameValuePair("test", s));
                params.add(new BasicNameValuePair("sem",sem));
                params.add(new BasicNameValuePair("bc",branch));
                params.add(new BasicNameValuePair("pc",pc));
            }
            String paramString = URLEncodedUtils.format(params, "utf-8");

            url += paramString;
            return url;
        }


    }

    protected String SpinnerValue(String s){

        Log.i("test",s);
        String temp="s";

        if(s.equalsIgnoreCase("unit test 1")){
            temp= "ut1";
        }

        if(s.equalsIgnoreCase("unit test 2")){
            temp= "ut2";
        }

        if(s.equalsIgnoreCase("cycle test 1")){
            temp= "ct1";
        }

        if(s.equalsIgnoreCase("cycle test 2")){
            temp= "ct2";
        }

        if(s.equalsIgnoreCase("model exam")){
            temp= "mdl";
        }
        Log.i("test",temp);
        return temp;
    }

}
