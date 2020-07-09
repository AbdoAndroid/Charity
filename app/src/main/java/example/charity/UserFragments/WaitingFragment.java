package example.charity.UserFragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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
import example.charity.Model.Donation;
import example.charity.R;
import example.charity.ViewDonationActivity;

import static android.content.Context.MODE_PRIVATE;

public class WaitingFragment extends Fragment {

    ListView listView;
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

    public WaitingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view=inflater.inflate(R.layout.fragment_waiting, container, false);


        listView=view.findViewById(R.id.lv_waiting_donations);

        //getting all the the donations of the current user
        sharedPreferences=getContext().getSharedPreferences("user",MODE_PRIVATE);
        queryGetMyDonations=dataRefDonation.orderByChild("doner").equalTo(sharedPreferences.getString("id","non"));
        queryGetMyDonations.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    myDonations=new ArrayList<>();
                    Map<String, Map> data = (Map) dataSnapshot.getValue();
                    Collection<Map> donations=  data.values();
                    //Log.i("donations",donations.toString());

                    for (Map<String,String> item:donations) {
                        if (item.get("state").equals("waiting")) {
                            myDonations.add(new Donation(item.get("id"), item.get("doner"), item.get("charity"), item.get("type"),
                                    item.get("city"), item.get("name"), item.get("state"), item.get("description")));
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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Donation donation=myDonations.get(position);
                Bundle bundle=new Bundle();
                bundle.putString("id",donation.getId());
                bundle.putString("type",donation.getType());
                bundle.putString("name",donation.getName());
                bundle.putString("desc",donation.getDescription());
                bundle.putString("state",getString(R.string.waiting_reply));
                bundle.putBoolean("show",true);
                bundle.putString("charity",donation.getCharity());
                Intent intent=new Intent(getContext(), ViewDonationActivity.class);
                intent.putExtra("bundle",bundle);
                startActivity(intent);
            }
        });


        return view;
    }
}
