package example.charity.CharityFragments;

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
import example.charity.CharityViewDonationActivity;
import example.charity.Model.Donation;
import example.charity.R;

import static android.content.Context.MODE_PRIVATE;

public class Ch_DoneFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

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


    public Ch_DoneFragment() {
        // Required empty public constructor
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SmsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Ch_DoneFragment newInstance(String param1, String param2) {
        Ch_DoneFragment fragment = new Ch_DoneFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_charity_done, container, false);


        listView=view.findViewById(R.id.lv_done_donations);

        //getting all the the donations of the current user
        sharedPreferences=getContext().getSharedPreferences("user",MODE_PRIVATE);
        queryGetMyDonations=dataRefDonation.orderByChild("charity").equalTo(sharedPreferences.getString("id","non"));
        queryGetMyDonations.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                try {
                    myDonations=new ArrayList<>();
                    Map<String, Map> data = (Map) dataSnapshot.getValue();
                    Collection<Map> donations=  data.values();
                    //Log.i("donations",donations.toString());
                    if (donations.size()>0)
                        for (Map<String,String> item:donations) {
                            if (item.get("state").equals("done")) {
                                myDonations.add(new Donation(item.get("id"), item.get("doner"), item.get("charity"), item.get("type"),
                                        item.get("city"), item.get("name"), item.get("state"), item.get("description")));
                            }
                        }
                    Log.i("donations", myDonations.toString());
                    adapter=new ItemAdapter(getContext(),myDonations);
                    listView.setAdapter(adapter);
                }catch (Exception e){

                }
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
                bundle.putString("state",getString(R.string.delivered));
                bundle.putBoolean("show",false);
                bundle.putBoolean("fab",false);
                Intent intent=new Intent(getContext(), CharityViewDonationActivity.class);
                intent.putExtra("bundle",bundle);
                startActivity(intent);
            }
        });

        return view;    }


}
