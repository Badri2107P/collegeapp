package in.care.ac.caregroupofinstitutions;

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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

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



public class EventsFragment extends Fragment {

    private ProgressDialog pDialog;
    private SessionManager session;

    private static String  url = "events.php";
    private String TAG = MainActivity.class.getSimpleName();
    // URL to get contacts JSON

    ListView lv;
    public EventsFragment() {
        // Required empty public constructor
    }

    public static EventsFragment newInstance(String param1, String param2) {
        EventsFragment fragment = new EventsFragment();
        return fragment;
    }
    ArrayList<HashMap<String, String>> EventList;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_events, container, false);
        EventList=new ArrayList<>();
        lv=(ListView) view.findViewById(R.id.eventlistmain);
        new GetEvents().execute();
        return  view;
    }


    private class GetEvents extends AsyncTask<Void, Void, Void> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            EventList.clear();
            // Showing progress dialog
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Gathering Data..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {

            String url1=session.mainurl+url;

            HTTPHandler sh = new HTTPHandler();

            Log.i("urldata",url1);
            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url1);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray eventsarray = jsonObj.getJSONArray("events");

                    // looping through All Contacts
                    for (int i = 0 ; i < eventsarray.length(); i++) {
                        JSONObject c = eventsarray.getJSONObject(i);

                        String eventid = c.getString("Event_id");
                        String eventname = c.getString("Event_name");
                        String eventdesc = c.getString("Event_shortdesc");
                        String eventdate=c.getString("Event_date");
                        String eventtime = c.getString("Event_time");


                        // tmp hash map for single contact
                        HashMap<String, String> events = new HashMap<>();
                        // adding each child node to HashMap key => value

                        events.put("eventid",eventid);
                        events.put("eventname",eventname);
                        events.put("eventdesc",eventdesc);
                        events.put("eventdate",eventdate);
                        events.put("eventtime",eventtime);

                        // adding contact to contact list
                        //   Log.i("attnds",marksa.toString());
                        //  Log.i("Marks",MarkList.toString());
                        EventList.add(events);

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
            //Log.i("Marks",EventList.toString());
            ListAdapter adapter = new SimpleAdapter(
                    getActivity(), EventList,
                    R.layout.events_list, new String[]{"eventname","eventdesc", "eventdate",
                    "eventtime"}, new int[]{R.id.listeventname,
                    R.id.listeventdesc, R.id.listeventdate,R.id.listeventtime});



            lv.setAdapter(adapter);
            //Log.i("Marks",MarkList.toString());

        }

    }

}
