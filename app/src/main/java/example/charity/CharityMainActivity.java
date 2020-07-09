package example.charity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.os.Bundle;
import android.view.MenuItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import example.charity.CharityFragments.Ch_DoneFragment;
import example.charity.CharityFragments.SearchFragment;
import example.charity.CharityFragments.UnderContactFragment;

public class CharityMainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charity_main);


        bottomNavigation = findViewById(R.id.bottom_navigation);
        //setting the nav bar listener
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        //the default fragment that opens at the start of running
        openFragment(UnderContactFragment.newInstance("",""));
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
                        case R.id.navigation_under_contact:
                            openFragment(UnderContactFragment.newInstance("", ""));
                            return true;
                        case R.id.navigation_search:
                            openFragment(SearchFragment.newInstance("", ""));
                            return true;
                        case R.id.navigation_char_delivered:
                            openFragment(Ch_DoneFragment.newInstance("", ""));
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
