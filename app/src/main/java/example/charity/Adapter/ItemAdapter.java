package example.charity.Adapter;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import example.charity.Model.Donation;
import example.charity.R;

public class ItemAdapter extends BaseAdapter {
    List<Donation> donations;
    Context context;
    public ItemAdapter(Context context, List<Donation> donations) {
        this.donations=donations;
        this.context=context;
    }

    @Override
    public int getCount() {
        return donations.size();
    }

    @Override
    public Donation getItem(int position) {
        return donations.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder=null;
        if (convertView==null){
            LayoutInflater inflater=LayoutInflater.from(context);
            convertView= inflater.inflate(R.layout.item_row,parent,false);
            holder=new ViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder= (ViewHolder) convertView.getTag();
        }

        Donation donation = getItem(position);
        holder.city.setText(getCityFromCord(donation.getCity()));
        holder.name.setText(donation.getName());
        String _type=context.getResources().getStringArray(R.array.type_array)[Integer.parseInt(donation.getType())];
        holder.type.setText(_type);
        return convertView;

    }
    String getCityFromCord(String coords){
        String[] location_a=coords.split(",");
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(Double.parseDouble(location_a[0]),
                    Double.parseDouble(location_a[1]), 1);
            String address = addresses.get(0).getAddressLine(0);
            String[] listAddress=address.split(",");
            String cityName=listAddress[listAddress.length-3];
            // -1 for the country , -2 for the governorate, -3 city , and so on
            Log.i("locationHere",cityName);
            return cityName;
        } catch (IOException e) {
            Log.i("locationHere",e.getMessage());
        }
        return  "";
    }


}

class  ViewHolder{
    TextView city,type,name;

    public ViewHolder(View view) {
        city= view.findViewById(R.id.tvCityCard);
        type=view.findViewById(R.id.tvTypeCard);
        name=view.findViewById(R.id.tvNameCard);
    }

}
