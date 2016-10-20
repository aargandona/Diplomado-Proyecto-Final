package bo.ara.com.kidstravel.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import bo.ara.com.kidstravel.R;
import bo.ara.com.kidstravel.model.User;

/**
 * Created by LENOVO on 8/20/2016.
 */
public class UserAdapter extends ArrayAdapter<User> {

    private List<User> userList;

    public UserAdapter(Context context) {
        super(context, R.layout.user_row_layout);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Get object by position
        User user =  getItem(position);

        View currentView;
        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            currentView = inflater.inflate(R.layout.user_row_layout, parent, false);
        }
        else{
            currentView = convertView;
        }

        //Get control references from row layout
        ImageView userImage = (ImageView) currentView.findViewById(R.id.user_image);
        TextView userPersonName = (TextView) currentView.findViewById(R.id.user_person_name);
        TextView userName = (TextView) currentView.findViewById(R.id.user_name);

        //Fill data into row layout controls
        Glide.with(getContext()).load(user.getImageUrl()).into(userImage);
        userPersonName.setText(user.getPerson().getFullName());
        userName.setText(user.getUsername());

        return currentView;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    @Override
    public Filter getFilter(){
        return new Filter(){

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                constraint = constraint.toString().toLowerCase();
                FilterResults result = new FilterResults();

                if (constraint != null && constraint.toString().length() > 0) {
                    List<User> founded = new ArrayList<User>();
                    for(User user: userList){
                        if(user.getPerson().getFullName().toString().toLowerCase().contains(constraint)){
                            founded.add(user);
                        }
                    }

                    result.values = founded;
                    result.count = founded.size();
                }else {
                    result.values = userList;
                    result.count = userList.size();
                }
                return result;


            }
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                clear();
                for (User user : (List<User>) results.values) {
                    add(user);
                }
                notifyDataSetChanged();
            }
        };
    }
}
