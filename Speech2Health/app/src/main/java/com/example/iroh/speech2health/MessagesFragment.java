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
import android.widget.EditText;
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

/**
 * Updated by Josh on 9/30/2016.
 *
 * MessagesFragment.java
 */
public class MessagesFragment extends Fragment implements View.OnClickListener{

    public MessagesFragment() {
        // Required empty public constructor
    }

    private Button sendButton;
    //private ScrollView messageBoard;
    private EditText message;
    private RelativeLayout relativeLayout;
    private RequestQueue queue;
    private TableLayout messageBoard;
    String crimson = "#e60000";
    String grey = "#808080";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View mView = inflater.inflate(R.layout.fragment_messages, container, false);

        relativeLayout = (RelativeLayout) mView.findViewById(R.id.relativeLayoutMessagesFragment);
        //relativeLayout.setOnClickListener(this);
        sendButton = (Button) mView.findViewById(R.id.sendButtonMessagesFragment);
        messageBoard = (TableLayout) mView.findViewById(R.id.messageBoardTableLayoutMessagesFragment);
        message = (EditText) mView.findViewById(R.id.textViewMessageFragment);
        messageBoard.setOnClickListener(this);

        queue = Volley.newRequestQueue(mView.getContext());
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(message.getText().length() == 0 || message.getText().toString() == " "){
                    return;
                }

                sendMessage(mView);
                message.setText("");
            }
        });

        receiveMessages(mView);

        return mView;
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.messageBoardTableLayoutMessagesFragment: hideSoftKeyboard(v);
                break;
        }
    }

    public void sendMessage(final View view){
        //put in the volley call to the server here
        //if that doesn't work, just look at DiaryFragment and see how to do it inside the onCreateView function

        HashMap<String, String> params = new HashMap<String, String> ();
        params.put("message", message.getText().toString());

        JsonObjectRequest jorPostMessage = new JsonObjectRequest(Request.Method.POST, "http://159.203.204.9/api/v1/user/postMessage", new JSONObject(params), new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response){
                String test = response.toString();
                messageBoard.removeAllViews();

                receiveMessages(view);
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

        queue.add(jorPostMessage);

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

    public void receiveMessages(final View view){
        JsonObjectRequest jorGetMessage = new JsonObjectRequest(Request.Method.GET, "http://159.203.204.9/api/v1/user/getMessages", null, new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response){

                try {
                    //TODO: Only grab the most recent messages
                    //TODO: Talk with mentors and decide what is reasonable? Example:(Time limit?, Max # of messages?, Date range?)

                    String patient = response.getString("clientName");
                    String previousName = "";
                    JSONArray jsonArray = response.getJSONArray("messages");
                    for(int i = jsonArray.length() - 1; i >= 0; i--){
                        JSONObject jsonObj = jsonArray.getJSONObject(i);

                        String tempMessage = jsonObj.getString("message");
                        String tempSender = jsonObj.getString("sender");
                        String tempRecipient = jsonObj.getString("recipient");

                        TextView senderText = new TextView(view.getContext());
                        if(tempSender.equals(previousName)){
                            senderText.setText(" ");
                        }
                        else {
                            senderText.setText(tempSender);
                        }

                        TextView dummy = new TextView(view.getContext());
                        dummy.setText(" ");
                        TextView dummy2 = new TextView(view.getContext());
                        dummy2.setText(" ");
                        TextView messageText = new TextView(view.getContext());
                        messageText.setText(tempMessage);
                        messageText.setTextColor(Color.BLACK);
                        if(tempSender.equals(patient)){
                            messageText.setBackgroundColor(Color.parseColor(crimson));
                        }
                        else{
                            messageText.setBackgroundColor(Color.parseColor(grey));
                        }

                        messageText.setPadding(10,10,10,10);

                        TableRow senderRow = new TableRow(view.getContext());
                        if(tempSender.equals(patient)){
                            //the 1f and 2f refer to the weight of the object in the row
                            //1f will take up 1/total f's (in this case 3) and 2f will take 2/3 of the row space
                            senderRow.addView(dummy, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                            senderRow.addView(senderText, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2f));
                            senderRow.setPadding(0, 0, 10, 0);
                        }
                        else {
                            senderRow.addView(senderText, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2f));
                            senderRow.addView(dummy, new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                            senderRow.setPadding(10,0,0,0);
                        }
                        TableRow messageRow = new TableRow(view.getContext());
                        if(tempSender.equals(patient)){

                            messageRow.addView(dummy2, new TableRow.LayoutParams(1, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                            messageRow.addView(messageText, new TableRow.LayoutParams(1, TableRow.LayoutParams.WRAP_CONTENT, 2f));
                            messageRow.setPadding(0,0,10,0);
                        }
                        else {
                            messageRow.addView(messageText, new TableRow.LayoutParams(1, TableRow.LayoutParams.WRAP_CONTENT, 2f));
                            messageRow.addView(dummy2, new TableRow.LayoutParams(1, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                            messageRow.setPadding(10,0,0,0);
                        }
                        messageBoard.addView(senderRow);
                        messageBoard.addView(messageRow);
                        previousName = tempSender;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //TODO:Write error statements here like "Couldn't connect to server

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

        queue.add(jorGetMessage);

    }
}
