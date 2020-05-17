package app.mcs.projectoneeight.techhub;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * Created by Jayanth on 14-06-2018.
 */

public class Contact extends Fragment {

    View v;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.contact, container, false);
        v.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if((motionEvent.getAction()==MotionEvent.ACTION_UP)||(motionEvent.getAction()==MotionEvent.ACTION_DOWN)){
                    Toast.makeText(getActivity(),"Swiped up or down contact",Toast.LENGTH_SHORT).show();
                }

                return true;
            }
        });
        return v;
    }
}
