package com.example.iroh.speech2health;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.iroh.speech2health.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
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
    //private ScrollView messageBoard;
    private TextView message;
    private RelativeLayout relativeLayout;
    private RequestQueue queue;
    private TableLayout messageBoard;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View mView = inflater.inflate(R.layout.fragment_messages, container, false);

        relativeLayout = (RelativeLayout) mView.findViewById(R.id.backgroundRelativeLayoutMessagesFragment);
        relativeLayout.setOnClickListener(this);
        sendButton = (Button) mView.findViewById(R.id.sendButtonMessagesFragment);
        messageBoard = (TableLayout) mView.findViewById(R.id.messageBoardTableLayoutMessagesFragment);
        //only initialize message when the user wants to send a message
        //the assumption is that the user will be reading messages more than they will be sending them

        queue = Volley.newRequestQueue(mView.getContext());
        sendButton.setOnClickListener(this);

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, "http://159.203.204.9/api/v1/user/getMessages", null, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response){
                String test = response.toString();

                try {
                    //JSONObject jsonObject = response.getJSONObject("messages");
                    //TODO: Do stuff here
                    JSONArray jsonArray = response.getJSONArray("messages");
                    for(int i = 0; i < jsonArray.length(); i++){
                        JSONObject jsonObj = jsonArray.getJSONObject(i);
                        String tempMessage = jsonObj.getString("message");
                        String tempSender = jsonObj.getString("sender");
                        String tempRecipient = jsonObj.getString("recipient");
                        TextView senderText = new TextView(mView.getContext());
                        senderText.setText(tempSender);
                        senderText.setTextColor(Color.BLUE);
                        TextView dummy = new TextView(mView.getContext());
                        dummy.setText(" ");
                        TextView dummy2 = new TextView(mView.getContext());
                        dummy2.setText(" ");
                        TextView messageText = new TextView(mView.getContext());
                        messageText.setText(tempMessage);
                        messageText.setTextColor(Color.GREEN);

                        //messageBoard.setStretchAllColumns(true);

                        TableRow senderRow = new TableRow(mView.getContext());
                        senderRow.addView(dummy, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                        senderRow.addView(senderText, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                        TableRow messageRow = new TableRow(mView.getContext());
                        messageRow.addView(dummy2, new TableRow.LayoutParams(1, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                        messageRow.addView(messageText, new TableRow.LayoutParams(1, TableRow.LayoutParams.WRAP_CONTENT, 1f));

                        messageBoard.addView(senderRow);
                        messageBoard.addView(messageRow);
                        //board.addView();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String test2 = "error";
            }

        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                headers.put("Api-Key", readID("userID"));
                return headers;
            }
        };


        queue.add(jor);


        return mView;
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.sendButtonMessagesFragment: SendMessage(v);
                break;
            case R.id.backgroundRelativeLayoutMessagesFragment: hideSoftKeyboard(v);
                break;
        }
    }

    public void SendMessage(View v){
        //put in the volley call to the server here
        //if that doesn't work, just look at DiaryFragment and see how to do it inside the onCreateView function

    }

    public void hideSoftKeyboard(View v){
        InputMethodManager imm = (InputMethodManager)
                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public String readID(String file_name) {
        try {
            String Message;
            FileInputStream inputStream = getActivity().openFileInput(file_name);
            InputStreamReader streamReader = new InputStreamReader(inputStream);
            BufferedReader buffReader = new BufferedReader(streamReader);
            StringBuffer stringBuffer = new StringBuffer();
            while ((Message=buffReader.readLine()) != null) {
                stringBuffer.append(Message);
            }

            return stringBuffer.toString();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /*public JsonObjectRequest getMessages(ScrollView board, View view){
        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, "http://159.203.204.9/api/v1/user/getMessages", null, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response){
                String test = response.toString();

                try {
                    //JSONObject jsonObject = response.getJSONObject("messages");
                    //TODO: Do stuff here
                    JSONArray jsonArray = response.getJSONArray("messages");
                    for(int i = 0; i < jsonArray.length(); i++){
                        JSONObject jsonObj = jsonArray.getJSONObject(i);
                        String tempMessage = jsonObj.getString("message");
                        String tempSender = jsonObj.getString("sender");
                        String tempRecipient = jsonObj.getString("recipient");
                        TextView sender = new TextView(view.getContext());

                        //board.addView();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String test2 = "error";
            }

        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                headers.put("Api-Key", readID("userID"));
                return headers;
            }
        };

        return jor;
    }*/
}
