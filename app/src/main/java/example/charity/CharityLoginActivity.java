package example.charity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import example.charity.Model.Charity;

public class CharityLoginActivity extends AppCompatActivity {

    EditText etPassword,etPhoneNum;
    TextView tvRegister;
    Button btnLogin;

    //dataBase objects that are used for login process
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dataRefDonor=database.getReference().child("charity");
    Query query_userExist;


    //shared preferences to save the session info
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charity_login);

        //getting the data inside file "user" from shared pref wich contains the session info
        sharedPreferences= getSharedPreferences("user" ,MODE_PRIVATE);

        btnLogin = findViewById(R.id.btnChLogin);
        etPhoneNum = findViewById(R.id.et_phone_num);
        etPassword = findViewById(R.id.etPassword);
        tvRegister = findViewById(R.id.btnRegister);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                query_userExist=  dataRefDonor.orderByChild("mobileNum").equalTo(String.valueOf(etPhoneNum.getText()));
                query_userExist.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.exists()) {
                            //the number hasn't been registered
                            Toast.makeText(getApplicationContext(),"This number hasn't registered   ",Toast.LENGTH_LONG).show();

                        } else{//the phone number already exists, checking on the password
                            try {
                                Map<String, Map> root = (Map) dataSnapshot.getValue();

                                Collection<Map> donations=  root.values();
                                //Log.i("donations",donations.toString());
                                Map<String, String> child=new HashMap<>();
                                for (Map<String,String> item:donations) {
                                    child =item;
                                    break;
                                }

                                Charity charity=new Charity(child.get("id"),child.get("name"),child.get("number"),child.get("mobileNum"),child.get("password"));
                                Log.i("fullName",/*map+"  "+*/"objectCreated");
                                if (child.get("password").equals(etPassword.getText().toString())){
                                    SharedPreferences.Editor edit = sharedPreferences.edit();
                                    edit.putString("type","charity");
                                    edit.putString("id",child.get("id"));
                                    edit.putString("name",child.get("name"));
                                    edit.putString("charityNum",child.get("number"));
                                    edit.putString("phone",child.get("mobileNum"));
                                    edit.putString("location",child.get("location"));
                                    edit.putString("password",child.get("password"));
                                    edit.commit();
                                    finish();
                                    startActivity(new Intent(getBaseContext(),CharityMainActivity.class));

                                }else{
                                    Toast.makeText(getApplicationContext(),"Wrong Password " ,Toast.LENGTH_LONG).show();
                                }
                            }catch (Exception ex){
                                Log.d("logIn",ex.getMessage());
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("logIn",databaseError.getMessage());

                    }
                });
            }
        });





        tvRegister.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent it = new Intent(CharityLoginActivity.this, CharitySignupActivity.class);

                startActivity(it);
            }
        });
    }
}
