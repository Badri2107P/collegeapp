package in.care.ac.caregroupofinstitutions;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.ContentValues.TAG;


public class ProfileFragment extends Fragment {

TextView name,branch;
    RelativeLayout attend,exam,sched;
    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_profile, container, false);
        name=(TextView)view.findViewById(R.id.nameprofile);
        branch=(TextView)view.findViewById(R.id.branchprofile);
        final SessionManager session=new SessionManager(getActivity().getApplicationContext());
        HashMap<String,String> details=session.GetDetails();
        name.setText(details.get("name"));
        String ss=GetYear(details.get("startyear"));
        branch.setText(details.get("branch")+", "+ss);
        attend=(RelativeLayout)view.findViewById(R.id.attendanceprofile);
        exam=(RelativeLayout)view.findViewById(R.id.examprofile);
        sched=(RelativeLayout)view.findViewById(R.id.scheduleprofile);
        final FragmentManager fm = getActivity().getSupportFragmentManager();
        exam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                fm.beginTransaction().replace(R.id.fragment_container,new MarksFragment()).commit();
                session.setfragreplace();
                // mainActivity.ChangeFrag(new MarksFragment());
            }
        });
        return view;
    }

    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        attend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                MainActivity mainActivity=new MainActivity();
                mainActivity.ChangeFrag(new MarksFragment());
            }
        });


    }
    public String GetYear(String s){
        //   Log.i("Year",s);
        String t="";
        if(s.equals("1")){
            t="First Year";
        }
        if(s.equals("2")){
            t="Second Year";
        }
        if(s.equals("3")){
            t="Third Year";
        }
        if(s.equals("4")){
            t="Fourth Year";
        }if(s.equals("5")){
            t="Fifth Year";
        }if(s.equals("6")){
            t="Sixth Year";
        }if(s.equals("7")){
            t="Seventh Year";
        }
        return t;
    }
}
