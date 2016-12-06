package bo.ara.com.kidstravel.adapter;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import bo.ara.com.kidstravel.R;
import bo.ara.com.kidstravel.model.Travel;
//import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by LENOVO on 8/6/2016.
 */
public class TravelAdapter extends ArrayAdapter<Travel> {

    private List<Travel> travelList;

    public TravelAdapter(Context context) {
        super(context, R.layout.travel_row_layout);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Get object by position
        Travel travel =  getItem(position);

        View currentView;
        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            currentView = inflater.inflate(R.layout.travel_row_layout, parent, false);
        }
        else{
            currentView = convertView;
        }

        //Get control references from row layout
        ImageView userImage = (ImageView) currentView.findViewById(R.id.travel_user_image);
        TextView applicantName = (TextView) currentView.findViewById(R.id.travel_applicant_name);
        TextView travelRoute = (TextView) currentView.findViewById(R.id.travel_route);
        TextView startDate = (TextView) currentView.findViewById(R.id.travel_start_date);
        ImageView statusImage = (ImageView) currentView.findViewById(R.id.travel_state_image);

        //Fill data into row layout controls
        //imageUser.setImageResource(R.drawable.user01);
        Glide.with(getContext()).load(travel.getUser().getImageUrl()).into(userImage);
        applicantName.setText(travel.getApplicant().getFullName());
        travelRoute.setText(travel.getTravelRoute());
        startDate.setText(travel.getStartDate());

        //Set state image
        if(travel.getStatus().equals("Requested"))
            statusImage.setImageResource(R.drawable.ticket_requested);
        else if(travel.getStatus().equals("Authorized"))
            statusImage.setImageResource(R.drawable.ticket_authorized);
        else if(travel.getStatus().equals("Approved"))
            statusImage.setImageResource(R.drawable.ticket_approved);
        else if(travel.getStatus().equals("Rejected"))
            statusImage.setImageResource(R.drawable.ticket_rejected);
        //>>

        return currentView;
    }

    public void setTravelList(List<Travel> travelList) {
        this.travelList = travelList;
    }

    @Override
    public Filter getFilter(){
        return new Filter(){

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                constraint = constraint.toString().toLowerCase();
                FilterResults result = new FilterResults();

                if (constraint != null && constraint.toString().length() > 0) {
                    List<Travel> founded = new ArrayList<Travel>();
                    for(Travel travel: travelList){
                        if(travel.getTravelRoute().toString().toLowerCase().contains(constraint)){
                            founded.add(travel);
                        }
                    }

                    result.values = founded;
                    result.count = founded.size();
                }else {
                    result.values = travelList;
                    result.count = travelList.size();
                }
                return result;


            }
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                clear();
                for (Travel travel : (List<Travel>) results.values) {
                    add(travel);
                }
                notifyDataSetChanged();
            }
        };
    }
}
