package example.charity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MenuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MenuFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;



    //views references
    ImageView iv_profile,iv_aboutus,iv_logout;
    TextView tv_profile,tv_aboutus,tv_logout;

    //session info
    SharedPreferences sharedPreferences;


    public MenuFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MenuFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MenuFragment newInstance(String param1, String param2) {
        MenuFragment fragment = new MenuFragment();
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
        View view= inflater.inflate(R.layout.fragment_menu, container, false);


        sharedPreferences=getContext().getSharedPreferences("user", Context.MODE_PRIVATE);

        //profile views and event
        iv_profile=view.findViewById(R.id.profile_iv);
        tv_profile=view.findViewById(R.id.profile_tv);
        iv_profile.setOnClickListener(profile_onClick);
        tv_profile.setOnClickListener(profile_onClick);

        //about us views and event
        iv_aboutus=view.findViewById(R.id.aboutus_iv);
        tv_aboutus=view.findViewById(R.id.aboutus_tv);
        iv_aboutus.setOnClickListener(aboutus_onClick);
        tv_aboutus.setOnClickListener(aboutus_onClick);

        //logout views and event
        iv_logout=view.findViewById(R.id.logout_iv);
        tv_logout=view.findViewById(R.id.logout_tv);
        iv_logout.setOnClickListener(logout_onClick);
        tv_logout.setOnClickListener(logout_onClick);
        return view;
    }




    View.OnClickListener logout_onClick=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SharedPreferences.Editor edit = sharedPreferences.edit();
            edit.putString("type","non");
            edit.putString("id"," ");
            edit.commit();
            getActivity().finish();
            getActivity().startActivity(new Intent(getContext(),LoginActivity.class));
        }
    };

    View.OnClickListener aboutus_onClick=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            getActivity().startActivity(new Intent(getContext(),AboutUsActivity.class));
        }
    };

    View.OnClickListener profile_onClick=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (sharedPreferences.getString("type","non").equals("doner"))
                getActivity().startActivity(new Intent(getContext(),ProfileActivity.class));
            else
                getActivity().startActivity(new Intent(getContext(),CharityProfileActivity.class));
        }
    };

}
