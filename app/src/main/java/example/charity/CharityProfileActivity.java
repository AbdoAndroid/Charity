package example.charity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import example.charity.Model.Charity;

public class CharityProfileActivity extends AppCompatActivity {


    SharedPreferences sharedPreferences;
    EditText name,phone,number;
    ImageView edit_iv;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dataRefCharity =database.getReference().child("charity");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charity_profile);


        sharedPreferences=getSharedPreferences("user",MODE_PRIVATE);

        name=findViewById(R.id.et_name);
        phone=findViewById(R.id.et_phone);
        number=findViewById(R.id.et_number);
        name.setText(sharedPreferences.getString("name",""));
        phone.setText(sharedPreferences.getString("phone",""));
        number.setText(sharedPreferences.getString("charityNum",""));

        edit_iv=findViewById(R.id.edit_iv);
        edit_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(),EditPasswordActivity.class));
            }
        });

    }
    public void confirm_OnClick(View view) {
        Charity charity=new Charity(sharedPreferences.getString("id",""),name.getText().toString(),
                number.getText().toString(),phone.getText().toString(),sharedPreferences.getString("password",""));
        dataRefCharity.child(charity.getId()).setValue(charity);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString("name",charity.getName());
        edit.putString("phone",charity.getMobileNum());
        edit.putString("charityNum",charity.getNumber());
        edit.commit();
        finish();
    }
}
