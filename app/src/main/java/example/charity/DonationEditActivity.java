package example.charity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

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

public class DonationEditActivity extends AppCompatActivity {

    //root database ref
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    //ref of database donation to add the process inside
    DatabaseReference dataRefDonation=database.getReference().child("donation");
    //ref of database charity to get charities names
    DatabaseReference dataRefCharity=database.getReference().child("charity");



    //defining arrayLists to get the charities from database into them
    final List<String> charitiesNames=new ArrayList<>();
    final List<String> charitiesIds=new ArrayList<>();


    SharedPreferences sharedPreferences;
    Donation donation;

    // declaring Views
    Button btnSave;
    Spinner type,charity;
    EditText name,desc;


    String donationID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donation_edit);
        donationID=this.getIntent().getStringExtra("id");
        Log.i("donationID",donationID);




        //binding views
        type=findViewById(R.id.spinner_donation_type);
        name=findViewById(R.id.et_donation_name);
        desc=findViewById(R.id.et_donation_desc);
        charity=findViewById(R.id.spinner_charity);





        //getting the donation
        dataRefDonation.child(donationID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String,String> _donation= (Map<String, String>) dataSnapshot.getValue();
                //tvCharity.setText(_donation.get("name"));
                donation=new Donation(_donation.get("id"),_donation.get("doner"),_donation.get("charity"),_donation.get("type"),
                        _donation.get("city"),_donation.get("name"),_donation.get("state"),_donation.get("description"));
                type.setSelection(Integer.parseInt(donation.getType()));
                name.setText(donation.getName());
                desc.setText(donation.getDescription());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        //getting the charities names with ids into the above Lists
        //setting the values of the spinner of the charity and select the prev one
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
                ArrayAdapter<String> adapter =new ArrayAdapter<String>(getBaseContext(),android.R.layout.simple_spinner_item, charitiesArr);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                charity.setAdapter(adapter);
                //selecting the already saved charity
                for (String item: charitiesIds){
                    if (item.equals(donation.getCharity())){
                        charity.setSelection(charitiesIds.indexOf(item),true);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        sharedPreferences=getSharedPreferences("user", Context.MODE_PRIVATE);

        btnSave= findViewById(R.id.btnConfDonation);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                donation.setCharity(charitiesIds.get(charity.getSelectedItemPosition()));
                donation.setType(String.valueOf(type.getSelectedItemPosition()));
                donation.setName(name.getText().toString());
                donation.setDescription(desc.getText().toString());

                //injecting the donation into the database
                dataRefDonation.child(donation.getId()).setValue(donation);

            }
        });



    }
}
