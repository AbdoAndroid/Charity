package example.charity.UserFragments;

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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import example.charity.Model.Donation;
import example.charity.R;
import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;


public class AddFragment extends Fragment {


    FusedLocationProviderClient mFusedLocationClient;
    String _location;

    //root database ref
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    //ref of database donation to add the process inside
    DatabaseReference dataRefDonation = database.getReference().child("donation");
    //ref of database charity to get charities names
    DatabaseReference dataRefCharity = database.getReference().child("charity");


    //defining arrayLists to get the charities from database into them
    final List<String> charitiesNames = new ArrayList<>();
    final List<String> charitiesIds = new ArrayList<>();


    SharedPreferences sharedPreferences;
    Donation donation;

    // declaring Views
    Button btnSave;
    Spinner type, charity;
    EditText name, desc;

    //constructor
    public AddFragment() {
        // Required empty public constructor
    }

    /*
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotificationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddFragment newInstance(String param1, String param2) {
        AddFragment fragment = new AddFragment();
        /*Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);*/
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            /*mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);*/


        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //Button btnSave=(Button) getView().findViewById(R.id.btnConfDonation);
        /**/
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        //binding views
        type=view.findViewById(R.id.spinner_donation_type);
        name=view.findViewById(R.id.et_donation_name);
        desc=view.findViewById(R.id.et_donation_desc);
        charity=view.findViewById(R.id.spinner_charity);

//getting location from the bottom functions
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        getLastLocation();



        //getting the charities names with ids into the above Lists
        dataRefCharity.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String, Map> charityData = (Map) dataSnapshot.getValue();
                Collection<Map> charities=  charityData.values();
                Log.i("Array",charities.toString());
                for (Map<String,String> item:charities) {
                    charitiesIds.add(item.get("id"));
                    charitiesNames.add(item.get("name"));
                }
                Log.i("Array",charitiesNames+", "+charitiesIds);

                //converting the charities list to array so that the adapter can handle it
                String[] charitiesArr = charitiesNames.toArray(new String[charitiesNames.size()]);
                ArrayAdapter<String> adapter =new ArrayAdapter<String>(getContext(),android.R.layout.simple_spinner_item, charitiesArr);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                charity.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        sharedPreferences=getContext().getSharedPreferences("user", Context.MODE_PRIVATE);

        btnSave= view.findViewById(R.id.btnConfDonation);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                donation=new Donation(dataRefDonation.push().getKey(),sharedPreferences.getString("id","non"),
                        charitiesIds.get(charity.getSelectedItemPosition()),String.valueOf(type.getSelectedItemPosition()),
                        _location,name.getText().toString(), "waiting",desc.getText().toString());

                //injecting the donation into the database
                dataRefDonation.child(donation.getId()).setValue(donation);


                charity.setSelection(0);
                type.setSelection(0);
                name.setText("");
                desc.setText("");
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
                                    _location=location.getLatitude()+","+location.getLongitude();
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
            _location=mLastLocation.getLatitude()+","+mLastLocation.getLongitude();
        }
    };
}
