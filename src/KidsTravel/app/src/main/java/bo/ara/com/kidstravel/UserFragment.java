package bo.ara.com.kidstravel;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import bo.ara.com.kidstravel.adapter.UserAdapter;
import bo.ara.com.kidstravel.network.UserListAsyncTask;

public class UserFragment extends Fragment {

    private UserAdapter userAdapter;
    private ListView listView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        //Create Adapter
        userAdapter = new UserAdapter(getActivity());//OJO
        listView = (ListView)view.findViewById(R.id.user_list_view);
        listView.setAdapter(userAdapter);

        UserListAsyncTask userAsyncTask = new UserListAsyncTask(this);
        userAsyncTask.execute();

        //Add listener
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView adapter, View view, int position, long arg) {
//
//                Travel travel = (Travel)adapter.getItemAtPosition(position);
//                Log.d("PostsFragment", "selected travel = " + travel.getTravelRoute());
//
//                Intent intent = new Intent(getActivity(), PostDetailActivity.class);
//                intent.putExtra("UserName", post.getUser().getUsername());
//                intent.putExtra("UserImageUri", post.getUser().getPicture_url());
//
//                intent.putExtra("Title", post.getTitle());
//                intent.putExtra("Content", post.getContent());
//                intent.putExtra("PostedDate", post.getPosted_date());
//                startActivity(intent);
//            }
//        });

        return view;
    }

    public UserAdapter getUserAdapter() {
        return userAdapter;
    }
}
