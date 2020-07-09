package example.charity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import example.charity.Model.Donation;


public class CharityViewDonationActivity extends AppCompatActivity {

    LinearLayout llButtons;
    TextView tvType,tvName, tvDonor,tvDesc,tvState,tvCity;
    Button btnDelivered;
    FloatingActionButton fab;

    String donorPhone;
    //root database ref
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    //ref of database charity to get charities names
    DatabaseReference dataRefDoner =database.getReference().child("donor");
    //ref of database donation to add the process inside
    DatabaseReference dataRefDonation=database.getReference().child("donation");
    DatabaseReference doner;

    Donation donation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charity_view_donation);

        final Bundle bundle=this.getIntent().getBundleExtra("bundle");


        dataRefDonation.child(bundle.getString("id")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String,String> AllDonation= (Map<String, String>) dataSnapshot.getValue();
                donation=new Donation(bundle.getString("id"),AllDonation.get("doner"),AllDonation.get("charity"),
                        AllDonation.get("type"),AllDonation.get("city"),AllDonation.get("name"),AllDonation.get("state"),
                        AllDonation.get("description"));


                //binding views

                tvType.setText(getResources().getStringArray(R.array.type_array)[Integer.parseInt(donation.getType())]);
                Log.i("charityView","2");

                tvName.setText(donation.getName());
                Log.i("charityView","3");

                tvDesc.setText(donation.getDescription());
                Log.i("charityView","4");

                tvCity.setText(getCityFromCord(donation.getCity()));
                doner = dataRefDoner.child(donation.getDoner());

                doner.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        try {
                            Map<String,String> AllDonor= (Map<String, String>) dataSnapshot.getValue();
                            tvDonor.setText(AllDonor.get("fullName"));
                            donorPhone=AllDonor.get("mobileNum");

                            Log.i("charityView","15");
                        }catch (Exception e){
                            Log.i("charityView",e.getMessage());
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });






            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        tvType=findViewById(R.id.tv_donation_type_item);
        tvName=findViewById(R.id.tv_donation_name_item);
        tvDesc=findViewById(R.id.tv_donation_desc_item);
        tvCity=findViewById(R.id.tv_city_name_item);

        Log.i("charityView","0");
        llButtons=findViewById(R.id.ll_buttons);
        if (bundle.getBoolean("show")){
            llButtons.setVisibility(View.VISIBLE);
        }else{
            llButtons.setVisibility(View.INVISIBLE);
        }
        Log.i("charityView","1");



        tvDonor =findViewById(R.id.tv_donor_name_item);

        Log.i("charityView","5");


        Log.i("charityView","6");

        tvState=findViewById(R.id.tv_state_item);
        tvState.setText(bundle.getString("state"));
        Log.i("charityView","7");

        fab = findViewById(R.id.fabContact);
        if (bundle.getBoolean("fab")){
            fab.setVisibility(View.VISIBLE);
        }else{
            fab.setVisibility(View.INVISIBLE);
        }

        Log.i("charityView","9");

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+donorPhone));
                donation.setState("contact");
                dataRefDonation.child(donation.getId()).setValue(donation);
                finish();
                startActivity(intent);
            }
        });

        Log.i("charityView","10");


        btnDelivered =findViewById(R.id.btnDelivered);
        btnDelivered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                donation.setState("done");
                dataRefDonation.child(donation.getId()).setValue(donation);
                finish();
            }
        });

        Log.i("charityView","11");



    }

//get the name of the city using coordnates
    String getCityFromCord(String coords){
        String[] location_a=coords.split(",");
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(Double.parseDouble(location_a[0]),
                    Double.parseDouble(location_a[1]), 1);
            String address = addresses.get(0).getAddressLine(0);
            String[] listAddress=address.split(",");
            String cityName=listAddress[listAddress.length-3];
            //String stateName = addresses.get(0).getAddressLine(1);
            //String countryName = addresses.get(0).getAddressLine(2);
            Log.i("locationHere",cityName);
            //Log.i("locationHere",stateName);
            //Log.i("locationHere",countryName);
            return cityName;
        } catch (IOException e) {
            Log.i("locationHere",e.getMessage());
        }
        return  "";
    }


}
