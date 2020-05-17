package app.mcs.projectoneeight.techhub;

/**
 * Created by Jayanth on 02-06-2018.
 */
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;


public class TwoFragment extends Fragment {
    private View v;
    FirebaseAuth mAuth;
    SwipeRefreshLayout mySwipeRefreshLayout;
    public TwoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v= inflater.inflate(R.layout.fragment_two, container, false);
        mAuth=FirebaseAuth.getInstance();
        return  v;
    }

    private void refresh() {
    }
}
