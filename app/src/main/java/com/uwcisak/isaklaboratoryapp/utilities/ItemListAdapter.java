package com.uwcisak.isaklaboratoryapp.utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.uwcisak.isaklaboratoryapp.R;

import java.util.List;

public class ItemListAdapter extends ArrayAdapter<Item> {

    public ItemListAdapter(Context context, List<Item> objects) {
        super(context, R.layout.layout_item_fragment , objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) throws NullPointerException {

        if( convertView == null ) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());

            View rowView = layoutInflater.inflate( R.layout.layout_item_fragment , parent, false );
            final String itemName = getItem(position).getName();
            String code = getItem(position).getCode();

            TextView itemNameBox = rowView.findViewById( R.id.itemNameBox );
            TextView itemCodeBox = rowView.findViewById( R.id.itemCodeBox );

            itemNameBox.setText( itemName );
            itemCodeBox.setText( code );

            return rowView;
        }
        else {
            return convertView;
        }
    }
}
