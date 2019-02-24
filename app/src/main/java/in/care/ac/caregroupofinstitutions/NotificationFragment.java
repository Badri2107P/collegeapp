package in.care.ac.caregroupofinstitutions;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends Fragment {


    public NotificationFragment() {
        // Required empty public constructor
    }


    private ProgressDialog pDialog;
    private SessionManager session;

    private static String  url = "notifications.php?";
    private  static  String rollno,branch,acdendyear;
    private String TAG = MainActivity.class.getSimpleName();
    // URL to get contacts JSON
    ArrayList<HashMap<String, String>> NotificationList;
    ListView lv;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        session=new SessionManager(getActivity().getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_notification, container, false);

        lv = (ListView) view.findViewById(R.id.notificationslist);
        NotificationList=new ArrayList<>();
        new GetNotifications().execute();
        return view;
        // Inflate the layout for this fragment
    }

    private class GetNotifications extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            NotificationList.clear();
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
                    JSONArray marksarray = jsonObj.getJSONArray("notifications");

                    // looping through All Contacts
                    for (int i = 0 ; i < marksarray.length(); i++) {
                        JSONObject c = marksarray.getJSONObject(i);

                        // tmp hash map for single contact
                        HashMap<String, String> marksa = new HashMap<>();
                        // adding each child node to HashMap key => value

                        marksa.put("msg_sender",c.getString("Msg_sender"));
                        marksa.put("msg_date",c.getString("Msg_date"));
                        marksa.put("msg_notification",c.getString("Msg_notification"));

                        // adding contact to contact list
                        //   Log.i("attnds",marksa.toString());
                        //  Log.i("Marks",MarkList.toString());
                        NotificationList.add(marksa);

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
           // Log.i("Marks",MarkList.toString());
            ListAdapter adapter = new SimpleAdapter(
                    getActivity(), NotificationList,
                    R.layout.notifications_list, new String[]{"msg_sender", "msg_date",
                    "msg_notification"}, new int[]{R.id.notification_name,
                    R.id.notification_date, R.id.notification_msg});



            lv.setAdapter(adapter);
            //Log.i("Marks",MarkList.toString());

        }


        protected String addLocationToUrl(String url){
            if(!url.endsWith("?"))
                url += "?";
            List<NameValuePair> params = new LinkedList<NameValuePair>();
            HashMap<String,String> details=session.GetDetails();
            String year=details.get("endyear");
            String branch=details.get("branch");

            if (year!=null && branch!=null ) {
                params.add(new BasicNameValuePair("branch",branch));
                params.add(new BasicNameValuePair("year",year));
            }
            String paramString = URLEncodedUtils.format(params, "utf-8");

            url += paramString;
            return url;
        }


    }

}
