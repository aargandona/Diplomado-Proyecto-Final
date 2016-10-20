package bo.ara.com.kidstravel.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import bo.ara.com.kidstravel.R;
import bo.ara.com.kidstravel.model.MinorItem;

/**
 * Created by LENOVO on 8/23/2016.
 */
public class MinorAdapter extends ArrayAdapter<MinorItem> {
    public MinorAdapter(Context context) {
        super(context, R.layout.minor_row_layout);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Get object by position
        MinorItem minorItem =  getItem(position);

        View currentView;
        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            currentView = inflater.inflate(R.layout.minor_row_layout, parent, false);
        }
        else{
            currentView = convertView;
        }

        //Get control references from row layout
        CheckBox minorSelected = (CheckBox) currentView.findViewById(R.id.minor_selected);
        TextView minorFullName = (TextView) currentView.findViewById(R.id.minor_fullName);

        //Fill data into row layout controls
        minorSelected.setChecked(minorItem.getMinorSelected());
        minorFullName.setText(minorItem.getMinorFullName());

        minorSelected.setTag(minorItem);
        minorSelected.setEnabled(minorItem.getEnabled());

        // If CheckBox is toggled, update the planet it is tagged with.
        minorSelected.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                CheckBox checkBox = (CheckBox) view;
                MinorItem item = (MinorItem) checkBox.getTag();
                item.setMinorSelected(checkBox.isChecked());
            }
        });

        return currentView;
    }

    public void setCheckBox(int position){
        //Update status of checkbox
        MinorItem minorItem = this.getItem(position);
        minorItem.setMinorSelected(!minorItem.getMinorSelected());
        notifyDataSetChanged();
    }
}
