package com.example.user.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by User on 6/22/2016.
 */
public class PhoneAdapter extends ArrayAdapter {
    public ArrayList<Phone> list = new ArrayList();

    public PhoneAdapter(Context context, int resource) {
        super(context, resource);
    }

    public void add(Phone object) {
        super.add(object);
        list.add(object);
    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row;
        row = convertView;
        PhoneHolder phoneHolder;
        if(row==null){
            LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = layoutInflater.inflate(R.layout.row_layout, parent, false);
            phoneHolder = new PhoneHolder();
            phoneHolder.tx_name = (TextView) row.findViewById(R.id.tx_name);
            phoneHolder.tx_status = (TextView) row.findViewById(R.id.tx_status);
            row.setTag(phoneHolder);
        }
        else{
            phoneHolder = (PhoneHolder) row.getTag();
        }

        Phone phone = (Phone) this.getItem(position);
        phoneHolder.tx_name.setText(phone.getPhoneName());
        if(phone.getIsLost().equals("0")) {
            phoneHolder.tx_status.setText("Status: Normal");
        }
        else{
            phoneHolder.tx_status.setText("Status: Lost");
        }
        return row;
    }

    static class PhoneHolder{
        TextView tx_name, tx_status;
    }

}
