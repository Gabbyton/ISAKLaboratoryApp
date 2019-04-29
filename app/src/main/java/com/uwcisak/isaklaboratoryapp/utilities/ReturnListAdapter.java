package com.uwcisak.isaklaboratoryapp.utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.uwcisak.isaklaboratoryapp.R;
import com.uwcisak.isaklaboratoryapp.utilities.GlobalState;
import com.uwcisak.isaklaboratoryapp.utilities.Item;
import com.uwcisak.isaklaboratoryapp.utilities.ReturnItem;

import java.util.List;

public class ReturnListAdapter extends ArrayAdapter<ReturnItem> {

    Context mContext;
    List<ReturnItem> mObjects;

    public ReturnListAdapter(Context context, List<ReturnItem> objects) {
        super(context, R.layout.layout_checkbox_fragment , objects);
        mContext = context;
        mObjects = objects;
    }

    public class ViewHolder {
        public TextView nameText;
        public CheckBox tick;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) throws NullPointerException {
        ViewHolder view = null;
        LayoutInflater inflater = LayoutInflater.from(getContext());
        if( convertView == null ) {
            view = new ViewHolder();
            convertView = inflater.inflate( R.layout.layout_checkbox_fragment , null );
            view.nameText = convertView.findViewById( R.id.itemNameBox );
            view.tick = convertView.findViewById( R.id.itemCheckBox );

            view.tick.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int getPosition = (Integer) buttonView.getTag();
                    mObjects.get( getPosition ).setChecked( buttonView.isChecked() );

                    if( isChecked ) {
                        ((GlobalState) mContext).getReturnQueue().add( mObjects.get(position).getItem() );
                    }
                    else {
                        ((GlobalState) mContext).getReturnQueue().remove( mObjects.get(position).getItem() );
                    }
                }
            });

            convertView.setTag( view );
        }
        else {
            view = (ViewHolder) convertView.getTag();
        }

        view.tick.setTag( position );
        view.nameText.setText( mObjects.get(position).getItem().getName() );
        view.tick.setChecked( mObjects.get(position).isChecked() );

        return convertView;
    }

    private void displayContents() {
        StringBuilder all = new StringBuilder();
        for ( Item item : ((GlobalState) mContext).getReturnQueue() ) {
            all.append(item.getName()).append("\n");
        }
        Toast.makeText(mContext, all.toString(), Toast.LENGTH_SHORT).show();
    }
}
