package example.charity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import example.charity.Model.Donation;
import example.charity.UserFragments.AddFragment;
import example.charity.UserFragments.DoneFragment;
import example.charity.UserFragments.WaitingFragment;



public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigation;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //the bottom navigation bar linking to design
        bottomNavigation = findViewById(R.id.bottom_navigation);
        //setting the nav bar listener
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        //the default fragment that opens at the start of running
        openFragment(WaitingFragment.newInstance("",""));


        Bundle bundle=new Bundle();

    }

    //helping fun -> it toggles between the fragments of the bottom nav bar
    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    //(defining) dedicated navigation listener for the bottom nav bar(instead of putting inside onCreate fun)
    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.navigation_waiting:
                            openFragment(WaitingFragment.newInstance("", ""));
                            return true;
                        case R.id.navigation_donate:
                            openFragment(AddFragment.newInstance("", ""));
                            return true;
                        case R.id.navigation_delivered:
                            openFragment(DoneFragment.newInstance("", ""));
                            return true;
                        case R.id.navigation_menu:
                            openFragment(MenuFragment.newInstance("", ""));
                            return true;
                    }
                    return false;
                }
            };


    @Override
    public void onBackPressed() {
        finish();
    }


}
