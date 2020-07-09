package example.charity.CharityFragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

import example.charity.Adapter.ItemAdapter;
import example.charity.CharityViewDonationActivity;
import example.charity.Model.Donation;
import example.charity.R;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.Context.MODE_PRIVATE;

public class SearchFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;



    FusedLocationProviderClient mFusedLocationClient;
    double _lat,_long;

    ListView listView;
    Spinner spinner_AllTypes;
    ImageView imgv_Search;
    Switch nearbySwitch;
    //root database ref
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    //ref of database donation to add the process inside
    DatabaseReference dataRefDonation=database.getReference().child("donation");
    Query queryGetMyDonations;
    //pref that saves the current user info
    SharedPreferences sharedPreferences;
    //the List of my donations
    List<Donation> myDonations;

    ItemAdapter adapter;


    public SearchFragment() {
        // Required empty public constructor
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotificationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_search, container, false);


        listView=view.findViewById(R.id.lv_waiting_donations);




        //getting all the the donations of the current user
        sharedPreferences=getContext().getSharedPreferences("user",MODE_PRIVATE);
        queryGetMyDonations=dataRefDonation.orderByChild("charity").equalTo(sharedPreferences.getString("id","non"));
        queryGetMyDonations.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    myDonations=new ArrayList<>();
                    Map<String, Map> data = (Map) dataSnapshot.getValue();
                    Collection<Map> donations=  data.values();
                    //Log.i("donations",donations.toString());
                    if (donations.size()>0)
                        for (Map<String,String> item:donations) {
                            if (item.get("state").equals("waiting")) {
                                myDonations.add(new Donation(item.get("id"), item.get("doner"), item.get("charity"),
                                        item.get("type"), item.get("city"), item.get("name"), item.get("state"),
                                        item.get("description")));
                            }
                        }
                    Log.i("donations", myDonations.toString());
                    adapter=new ItemAdapter(getContext(),myDonations);
                    listView.setAdapter(adapter);
                }catch (Exception e){}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //search work
        spinner_AllTypes=view.findViewById(R.id.spinner_types_search);
        nearbySwitch =view.findViewById(R.id.swich_near);
        //getting location from the bottom functions
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        getLastLocation();

        imgv_Search=view.findViewById(R.id.imgv_search);
        imgv_Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!nearbySwitch.isChecked()){

                    if (spinner_AllTypes.getSelectedItemPosition()==0){
                        adapter=new ItemAdapter(getContext(),myDonations);
                        listView.setAdapter(adapter);
                    }
                    else{
                        List<Donation> _donations=new ArrayList<>();
                        for (Donation donation:myDonations) {
                            if (Integer.parseInt(donation.getType())==spinner_AllTypes.getSelectedItemPosition()-1){
                                _donations.add(donation);
                            }
                        }
                        adapter=new ItemAdapter(getContext(),_donations);
                        listView.setAdapter(adapter);
                    }
                }else {
                    Location current=new Location("current");
                    current.setLatitude(_lat);
                    current.setLongitude(_long);
                    String[] location_a;
                    Location aLoc;
                    if (spinner_AllTypes.getSelectedItemPosition()==0){
                        List<Donation> _donations=new ArrayList<>();
                        for (Donation donation:myDonations) {
                            location_a=donation.getCity().split(",");
                            aLoc=new Location("b");
                            aLoc.setLongitude(Double.parseDouble(location_a[1]));
                            aLoc.setLatitude(Double.parseDouble(location_a[0]));
                            if (current.distanceTo(aLoc)<5000){
                                _donations.add(donation);
                            }
                        }

                        adapter=new ItemAdapter(getContext(),_donations);
                        listView.setAdapter(adapter);
                    } else{
                        List<Donation> _donations=new ArrayList<>();
                        for (Donation donation:myDonations) {
                            location_a=donation.getCity().split(",");
                            aLoc=new Location("b");
                            aLoc.setLongitude(Double.parseDouble(location_a[1]));
                            aLoc.setLatitude(Double.parseDouble(location_a[0]));
                            if (Integer.parseInt(donation.getType())==spinner_AllTypes.getSelectedItemPosition()-1
                                    &&current.distanceTo(aLoc)<5000){
                                _donations.add(donation);
                            }
                        }
                        adapter=new ItemAdapter(getContext(),_donations);
                        listView.setAdapter(adapter);
                    }
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Donation donation=myDonations.get(position);
                Bundle bundle=new Bundle();
                bundle.putString("id",donation.getId());
                bundle.putString("state",getString(R.string.waiting_reply));
                bundle.putBoolean("show",false);
                bundle.putBoolean("fab",true);
                Intent intent=new Intent(getContext(), CharityViewDonationActivity.class);
                intent.putExtra("bundle",bundle);
                startActivity(intent);
            }
        });


        return view;

    }




    //all the below functions are for getting location
    // have no time to comment each
    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(getActivity(),new String[]{ACCESS_COARSE_LOCATION,ACCESS_FINE_LOCATION}, 1);
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Granted. Start getting the location information
            }
        }
    }
    @SuppressLint("MissingPermission")
    private void getLastLocation(){
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                    _lat=location.getLatitude();
                                    _long=location.getLongitude();
                                }
                            }
                        }
                );
            } else {
                Toast.makeText(getContext(), "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }
    @SuppressLint("MissingPermission")
    private void requestNewLocationData(){

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }
    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            _lat=mLastLocation.getLatitude();
            _long=mLastLocation.getLongitude();
        }
    };
}
