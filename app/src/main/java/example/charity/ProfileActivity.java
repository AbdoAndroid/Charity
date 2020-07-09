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

import example.charity.Model.Doner;

public class ProfileActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    EditText name,phone;
    ImageView edit_iv;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dataRefDonor=database.getReference().child("donor");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sharedPreferences=getSharedPreferences("user",MODE_PRIVATE);

        name=findViewById(R.id.et_name);
        phone=findViewById(R.id.et_phone);
        name.setText(sharedPreferences.getString("name",""));
        phone.setText(sharedPreferences.getString("phone",""));

        edit_iv=findViewById(R.id.edit_iv);
        edit_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getBaseContext(),EditPasswordActivity.class));
            }
        });
    }

    public void confirm_OnClick(View view) {
        Doner doner=new Doner(sharedPreferences.getString("id",""),name.getText().toString(),
                phone.getText().toString(),sharedPreferences.getString("password",""));
        dataRefDonor.child(doner.getId()).setValue(doner);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString("name",doner.getFullName());
        edit.putString("phone",doner.getMobileNum());
        edit.commit();
        finish();
    }
}
