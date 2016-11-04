package com.example.iroh.speech2health;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
//import info.androidhive.materialtabs.R;
/**
 * Created by Iroh on 9/30/2016.
 *
 * ProgressFragment.java
 */
import android.content.Intent;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProgressFragment extends Fragment implements View.OnClickListener{

    public ProgressFragment() {
        // Required empty public constructor
    }

    protected JSONObject job;
    //protected String id = "205";//TODO:read this variable from the core data inside the onCreate function
    protected RequestQueue queue;
    protected String mOutput;
    protected String temp;
    protected String[] tokens;
    //protected TextView outputTextView;
    protected String test;
    private Button refreshButton;
    private TableLayout mprogressTable;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        final View mview = inflater.inflate(R.layout.fragment_progress, container, false);

        queue = Volley.newRequestQueue(this.getContext());
        //outputTextView = (TextView) mview.findViewById(R.id.outputProfileText);
        mprogressTable = (TableLayout) mview.findViewById(R.id.tableLayoutProgressFragment);
        refreshButton = (Button) mview.findViewById(R.id.refreshButtonProgressFragment);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mprogressTable.removeAllViews();

                //TODO:Set up my own scrollview class and set it's max height so it doesn't bleed into the button
                JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, "http://159.203.204.9/api/v1/user/getFood", null, new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response){
                        //String test = response.toString();
                        String result;

                        result = response.toString();
                        //outputTextView.setText(result);
                        try {
                            JSONArray jsonArray = response.getJSONArray("food");
                            for(int i = 0; i < jsonArray.length(); i++){
                                JSONObject jsonObj = jsonArray.getJSONObject(i);
                                //String blah = "2";
                                TextView col1 = new TextView(mview.getContext());
                                TextView col2 = new TextView(mview.getContext());
                                TextView col3 = new TextView(mview.getContext());
                                TableRow row = new TableRow(mview.getContext());
                                col1.setText("    " + jsonObj.getString("name") + "            ");
                                col2.setText(jsonObj.getString("calorie") + "             ");
                                col3.setText(jsonObj.getString("ctime"));
                                row.addView(col1);
                                row.addView(col2);
                                row.addView(col3);
                                mprogressTable.addView(row);
                            }
                        }
                        catch(JSONException e){
                            //something went wrong
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //TODO: insert some sort of an error response to the user
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
            }
        });

        return mview;
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


    public void onClick(View v){}


}
