package com.example.iroh.speech2health;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.iroh.speech2health.R;
//import info.androidhive.materialtabs.R;
/**
 * Created by Iroh on 9/30/2016.
 *
 * HomeFragment.java
 */
public class MessagesFragment extends Fragment implements View.OnClickListener{

    public MessagesFragment() {
        // Required empty public constructor
    }

    private Button sendButton;
    private ScrollView messageBoard;
    private TextView message;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mView = inflater.inflate(R.layout.fragment_diary, container, false);

        sendButton = (Button) mView.findViewById(R.id.sendButtonMessagesFragment);
        messageBoard = (ScrollView) mView.findViewById(R.id.messageBoardScrollViewMessagesFragment);
        //only initialize message when the user wants to send a message
        //the assumption is that the user will be reading messages more than they will be sending them


        return mView;
    }


    @Override
    public void onClick(View v) {

    }
}
