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
import android.widget.ExpandableListView;
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
import java.util.List;
import java.util.Map;

public class ProgressFragment extends Fragment implements View.OnClickListener{

    public ProgressFragment() {
        // Required empty public constructor
    }

    protected JSONObject job;
    protected RequestQueue queue;
    protected String mOutput;
    protected String temp;
    protected String[] tokens;
    //protected TextView outputTextView;
    protected String test;
    private Button refreshButton;
    private TableLayout mprogressTable;

    //ExpandableListView stuff
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;

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
        mprogressTable = (TableLayout) mview.findViewById(R.id.tableLayoutProgressFragment);
        refreshButton = (Button) mview.findViewById(R.id.refreshButtonProgressFragment);
        
        // get the listview
        expListView = (ExpandableListView) mview.findViewById(R.id.expandableListViewProgressFragment);

        // preparing list data
        prepareListData();

        listAdapter = new ExpandableListAdapter(getContext(), listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);
        expListView.bringToFront();

        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                String parentHeader = listDataHeader.get(groupPosition);
                String childHeader = listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition);

                listDataHeader.remove(0);
                listDataChild.remove(parentHeader);

                // Adding child data
                listDataHeader.add(childHeader);

                // Adding child data
                List<String> nutrients = new ArrayList<String>();
                nutrients.add("Calories");
                nutrients.add("Carbs");
                nutrients.add("Proteins");
                nutrients.add("Lipids");

                listDataChild.put(listDataHeader.get(0), nutrients); // Header, Child data

                expListView.collapseGroup(0);
                return false;
            }
        });

        //expListView.bringChildToFront();

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mprogressTable.removeAllViews();

                JsonObjectRequest jor = new JsonObjectRequest(Request.Method.GET, "http://159.203.204.9/api/v1/user/getFood", null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //String test = response.toString();
                        String result;

                        //parentHeader will contain calories, lipid, carbs, or proteins
                        //this will be used to parse that particular nutritional value from the received json
                        String parentHeader = listDataHeader.get(0);
                        parentHeader = parentHeader.toLowerCase();
                        if(parentHeader.equals("carbs")){
                            parentHeader = "carbohydrate";
                        }
                        else{
                            //my listView variable names have an 's' at the end, the json variables don't contain it
                            parentHeader = parentHeader.substring(0, parentHeader.length() -1);
                        }

                        result = response.toString();
                        //outputTextView.setText(result);
                        try {
                            //API sends JSONArray called 'food' with 'name', 'calorie', and 'ctime' attached (JSONObjects)
                            JSONArray jsonArray = response.getJSONArray("food");
                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject jsonObj = jsonArray.getJSONObject(i);

                                TextView col1 = new TextView(mview.getContext());
                                TextView col2 = new TextView(mview.getContext());
                                TextView col3 = new TextView(mview.getContext());

                                //TODO:Modify Progress page to display calories or proteins or other additional nutrient information (fats, sugars...)
                                //String modifiedTime = jsonObj.getString("ctime");
                                TableRow row = new TableRow(mview.getContext());

                                //TODO:make the columns have weights like i did in MessagesFragment
                                col1.setText("    " + jsonObj.getString("name") + "                  ");
                                if(jsonObj.getString(parentHeader) == "null"){
                                    col2.setText("???" + "             ");
                                }
                                else {
                                    col2.setText(jsonObj.getString(parentHeader) + "             ");
                                }
                                col3.setText(jsonObj.getString("ctime"));

                                row.addView(col1);
                                row.addView(col2);
                                row.addView(col3);
                                mprogressTable.addView(row);
                            }
                        } catch (JSONException e) {
                            //something went wrong
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //TODO: insert some sort of an error response to the user
                    }

                }) {
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


    //initializes ExpandableListView values
    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding child data
        listDataHeader.add("Calories");

        // Adding child data
        List<String> nutrients = new ArrayList<String>();
        nutrients.add("Calories");
        nutrients.add("Carbs");
        nutrients.add("Proteins");
        nutrients.add("Lipids");

        listDataChild.put(listDataHeader.get(0), nutrients); // Header, Child data
    }

}
