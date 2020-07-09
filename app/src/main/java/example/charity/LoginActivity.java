package example.charity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
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

import example.charity.Model.Donation;
import example.charity.Model.Doner;

public class LoginActivity extends AppCompatActivity {
    EditText etPassword,et_phoneNum;
    TextView tvRegister,tvLoginChar;
    Button btnLogin;

    //shared preferences to save the session info
    SharedPreferences sharedPreferences;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dataRefDonor=database.getReference().child("donor");
    Query query_userExist;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //getting the data inside file "user" from shared pref which contains the session info
        sharedPreferences= getSharedPreferences("user" ,MODE_PRIVATE);
        String userType= sharedPreferences.getString("type","non");
        if (userType.equals("doner")){
            finish();
            startActivity(new Intent(this,MainActivity.class));
        }else if (userType.equals("charity")){
            finish();
            startActivity(new Intent(this,CharityMainActivity.class));
        }
        //binding views
        btnLogin = findViewById(R.id.btnAlreadyHaveAcc);
        et_phoneNum = findViewById(R.id.et_phonenum);
        etPassword = findViewById(R.id.etPassword);
        tvRegister = findViewById(R.id.btnRegister);
        tvLoginChar= findViewById(R.id.tvLoginChar);
        //button login click event -- the login process and validation
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                query_userExist=  dataRefDonor.orderByChild("mobileNum").equalTo(String.valueOf(et_phoneNum.getText()));
                /*query_userExist.addValueEventListener(new ValueEventListener() {
                    @SuppressLint("CommitPrefEdits")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.d("logIn",databaseError.getMessage());

                    }
                });*/
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
                                //Map<String, String> child =root.get(et_phoneNum.getText().toString());
                                //Doner doner=new Doner(child.get("id"),child.get("fullName"),child.get("mobileNum"),child.get("password"));
                                //Log.i("fullName",/*map+"  "+*/"UserCreated");
                                if (child.get("password").equals(etPassword.getText().toString())){
                                    SharedPreferences.Editor edit = sharedPreferences.edit();
                                    edit.putString("type","doner");
                                    edit.putString("id",child.get("id"));
                                    edit.putString("name",child.get("fullName"));
                                    edit.putString("phone",child.get("mobileNum"));
                                    edit.putString("location",child.get("location"));
                                    edit.putString("password",child.get("password"));
                                    edit.commit();
                                    finish();
                                    startActivity(new Intent(getBaseContext(),MainActivity.class));
                                    //Toast.makeText(getApplicationContext(),"Correct Password",Toast.LENGTH_LONG).show();
                                }else{
                                    Toast.makeText(getApplicationContext(),"Wrong Password ",Toast.LENGTH_LONG).show();
                                }
                            }catch (Exception ex){
                                Log.d("logIn",ex.getMessage());
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });




        //bottom textViews click Listeners
        tvRegister.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent it = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(it);
            }
        });
        tvLoginChar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(LoginActivity.this, CharityLoginActivity.class);
                startActivity(it);
            }
        });
    }
}
