package example.charity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import example.charity.Model.Charity;


public class CharitySignupActivity extends AppCompatActivity {
    EditText et_name, et_phoneNum,et_Pswrd, et_chrityNum,et_rePswrd;
    TextView tvLogin;
    Button btnRegister;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference dataRefCharity =database.getReference().child("charity");
    Query query_existBefore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charity_signup);


        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.btnAlreadyHaveAcc);
        et_chrityNum = findViewById(R.id.et_charity_num);
        et_Pswrd = findViewById(R.id.et_pswrd);
        et_name = findViewById(R.id.et_fullname);
        et_phoneNum = findViewById(R.id.et_phonenum);
        et_rePswrd=findViewById(R.id.et_ch_re_pswrd);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //checking if the name has been entered
                if (et_name.getText().toString().length()<3){
                    Toast.makeText(getApplicationContext(),"Please enter your name",Toast.LENGTH_LONG).show();
                }
                //checks if there is a mobile value has entered correctly
                else if (et_phoneNum.getText().toString().length()<11&&et_phoneNum.getText().toString().length()>14){
                    Toast.makeText(getApplicationContext(),"Please enter a right mobile",Toast.LENGTH_LONG).show();
                }
                //checking if the password is short
                else if (et_Pswrd.getText().toString().length()<6){
                    Toast.makeText(getApplicationContext(),"The password is too short, try to make a longer one for your security",Toast.LENGTH_LONG).show();
                }
                // checks if the 2 passwords are the same
                else if (!et_Pswrd.getText().toString().trim().equals( et_rePswrd.getText().toString().trim())){
                    Toast.makeText(getApplicationContext(),("password1 " + et_Pswrd.getText() +" password 2 "+et_rePswrd.getText()),Toast.LENGTH_LONG).show();
                }

                //the the values are complete
                // lets check if the user has registered before
                else{
                    //checking if the mobile number has signed before
                    query_existBefore=  dataRefCharity.orderByChild("mobileNum").equalTo(String.valueOf(et_phoneNum.getText()));
                    query_existBefore.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(!dataSnapshot.exists()) {
                                //the number hasn't been registered before so we will create a new account
                                createNewAcc();
                            } else{
                                //the phone number have been registered before
                                Toast.makeText(getApplicationContext(),"This numbered has registered before",Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.d("ifExistBefore", databaseError.getMessage());
                        }
                    });
                }
            }
        });




        tvLogin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                finish();
            }
        });

    }

    void createNewAcc(){
        try {
            String id = dataRefCharity.push().getKey();
            Charity charity=new Charity(id,et_name.getText().toString(),et_chrityNum.getText().toString(),et_phoneNum.getText().toString(),et_Pswrd.getText().toString());
            dataRefCharity.child(id).setValue(charity);
            Toast.makeText(getApplicationContext(),"Added Successfully, you can login now",Toast.LENGTH_LONG).show();
            finish();
        }catch (Exception ex){
            Toast.makeText(getApplicationContext(),ex.getMessage(),Toast.LENGTH_LONG).show();
        }


    }




}
