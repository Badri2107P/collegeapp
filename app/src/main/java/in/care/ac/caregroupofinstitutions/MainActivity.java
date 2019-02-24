package in.care.ac.caregroupofinstitutions;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {


    final Fragment fragment1 = new HomeFragment();
    final Fragment fragment2 = new BusFragment();
    final Fragment fragment3 = new NotificationFragment();
    final Fragment fragment4 = new ProfileFragment();
    final Fragment fragment5 = new MarksFragment();
    final Fragment fragment6 = new AttendanceFragment();
    final FragmentManager fm = getSupportFragmentManager();
    public Fragment active;
    Fragment fragment=new ProfileFragment();



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.home_menu:
                    ChangeFrag(fragment1);
                    break;

                case R.id.notification_menu:
                    ChangeFrag(fragment2);
                    break;

                case R.id.bus_menu:
                    ChangeFrag(fragment3);
                    break;

                case R.id.profile_menu:
                    ChangeFrag(fragment4);
                    break;
            }

            return true ;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fm.beginTransaction().add(R.id.fragment_container, fragment6, "6").hide(fragment6).commit();
        fm.beginTransaction().add(R.id.fragment_container, fragment5, "5").hide(fragment5).commit();
        fm.beginTransaction().add(R.id.fragment_container, fragment4, "4").hide(fragment4).commit();
        fm.beginTransaction().add(R.id.fragment_container, fragment3, "3").hide(fragment3).commit();
        fm.beginTransaction().add(R.id.fragment_container, fragment2, "2").hide(fragment2).commit();
        fm.beginTransaction().add(R.id.fragment_container,fragment1, "1").commit();
        active=fragment1;
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }
    public void ChangeFrag(Fragment frag) {
       // Log.i("frag",active.toString());
        final SessionManager sessionManager=new SessionManager(getApplicationContext());
        if (!sessionManager.fragreplace()){
            fm.beginTransaction().hide(active).show(frag).commit();

        }
        else {
            fm.beginTransaction().replace(R.id.fragment_container,frag).commit();
        }
        active=frag;
    }

}
