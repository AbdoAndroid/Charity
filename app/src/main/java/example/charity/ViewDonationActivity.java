package example.charity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class ViewDonationActivity extends AppCompatActivity {
    LinearLayout llButtons;
    TextView tvType,tvName,tvCharity,tvDesc,tvState;
    Button btnEdit,btnDelete;

    String donationID;
    //root database ref
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    //ref of database charity to get charities names
    DatabaseReference dataRefCharity=database.getReference().child("charity");
    //ref of database donation to add the process inside
    DatabaseReference dataRefDonation=database.getReference().child("donation");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_donation);
        Bundle bundle=this.getIntent().getBundleExtra("bundle");

        llButtons=findViewById(R.id.ll_buttons);
        if (bundle.getBoolean("show")){
            llButtons.setVisibility(View.VISIBLE);
        }else{
            llButtons.setVisibility(View.INVISIBLE);
        }

        tvType=findViewById(R.id.tv_donation_type_item);
        tvType.setText(getResources().getStringArray(R.array.type_array)[Integer.parseInt(bundle.getString("type"))]);

        tvName=findViewById(R.id.tv_donation_name_item);
        tvName.setText(bundle.getString("name"));

        tvDesc=findViewById(R.id.tv_donation_desc_item);
        tvDesc.setText(bundle.getString("desc"));

        DatabaseReference charity = dataRefCharity.child(bundle.getString("charity"));
        tvCharity=findViewById(R.id.tv_charity_name_item);
        charity.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Map<String,String> AllCharity= (Map<String, String>) dataSnapshot.getValue();
                tvCharity.setText(AllCharity.get("name"));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        tvState=findViewById(R.id.tv_state_item);
        tvState.setText(bundle.getString("state"));

        donationID=bundle.getString("id");


        btnDelete=findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataRefDonation.child(donationID).removeValue();
                finish();
            }
        });
        btnEdit=findViewById(R.id.btnEdit);
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getBaseContext(),DonationEditActivity.class);
                intent.putExtra("id",donationID);
                startActivity(intent);
                finish();
            }
        });

    }
}
