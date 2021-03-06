package example.charity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import example.charity.UserFragments.AddFragment;
import example.charity.UserFragments.DoneFragment;
import example.charity.UserFragments.WaitingFragment;



public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigation;




    //(defining) dedicated navigation listener for the bottom nav bar(instead of putting inside onCreate fun)
    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.navigation_waiting:
                            openFragment(new WaitingFragment());
                            return true;
                        case R.id.navigation_donate:
                            openFragment(new AddFragment());
                            return true;
                        case R.id.navigation_delivered:
                            openFragment(new DoneFragment());
                            return true;
                        case R.id.navigation_menu:
                            openFragment(new MenuFragment());
                            return true;
                    }
                    return false;
                }
            };

    //helping fun -> it toggles between the fragments of the bottom nav bar
    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

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
        openFragment(new WaitingFragment());

    }


    @Override
    public void onBackPressed() {
        finish();
    }


}
