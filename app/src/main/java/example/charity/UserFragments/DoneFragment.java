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

public class DoneFragment extends Fragment {

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
    ListView listView;


    public DoneFragment() {
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
        View view= inflater.inflate(R.layout.fragment_done, container, false);

        listView=view.findViewById(R.id.lv_done_donations);
        //getting all the the donations of the current user
        sharedPreferences=getContext().getSharedPreferences("user",MODE_PRIVATE);
        queryGetMyDonations=dataRefDonation.orderByChild("doner").equalTo(sharedPreferences.getString("id","non"));
        Log.i("reachedHere","1");
        queryGetMyDonations.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    Log.i("reachedHere","3");

                    myDonations=new ArrayList<>();
                    Map<String, Map> data = (Map) dataSnapshot.getValue();
                    Collection<Map> donations = data.values();
                    Log.i("reachedHere","4");

                    for (Map<String, String> item : donations) {
                        if (item.get("state").equals("done") || item.get("state").equals("contact")) {
                            myDonations.add(new Donation(item.get("id"), item.get("doner"), item.get("charity"), item.get("type"),
                                    item.get("city"), item.get("name"), item.get("state"), item.get("description")));
                        }
                    }
                    Log.i("reachedHere", myDonations.toString());
                    adapter = new ItemAdapter(getContext(), myDonations);
                    listView.setAdapter(adapter);

                } catch (Exception e) {
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        Log.i("reachedHere","2");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Donation donation=myDonations.get(position);
                Bundle bundle=new Bundle();
                bundle.putString("type",donation.getType());
                bundle.putString("name",donation.getName());
                bundle.putString("desc",donation.getDescription());
                if (donation.getState().equals("contact")){
                    bundle.putString("state",getString(R.string.contacting));
                }else {
                    bundle.putString("state",getString(R.string.delivered));
                }
                bundle.putBoolean("show",false);
                bundle.putString("charity",donation.getCharity());
                Intent intent=new Intent(getContext(), ViewDonationActivity.class);
                intent.putExtra("bundle",bundle);
                startActivity(intent);
            }
        });

        return  view;

    }


}
