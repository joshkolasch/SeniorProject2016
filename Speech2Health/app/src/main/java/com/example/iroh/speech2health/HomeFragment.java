package com.example.iroh.speech2health;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Updated by Josh on 11/17/2016.
 *
 * HomeFragment.java
 *
 * Used the following link for the progress circle window
 * https://github.com/CardinalNow/Android-CircleProgressIndicator
 * Added my own twist by creating two more progress bars overlapping the first two.
 * This allows for a new color around the ring when the percent > 100%
 */
public class HomeFragment extends Fragment implements View.OnClickListener {

    public HomeFragment() {
        // Required empty public constructor
    }

    private TextView tview;
    private ProgressBar mProgressbar;
    private double percentage;
    private ProgressBar mProgressExceededBar;
    private ProgressBar mProgressBarBackground;
    private ProgressBar mProgressBarExceededBackground;
    private RelativeLayout mRelativeLayout;
    private RequestQueue queue;
    private SimpleDateFormat sdf;

    //TODO: NOTE:this function was commented out before
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mv =  inflater.inflate(R.layout.fragment_home, container, false);
        tview = (TextView) mv.findViewById(R.id.currentCaloriesHomeFragment);
        mProgressBarBackground = (ProgressBar) mv.findViewById(R.id.progressBackground);
        mProgressBarExceededBackground = (ProgressBar) mv.findViewById(R.id.progressExceededBackground);
        mRelativeLayout = (RelativeLayout) mv.findViewById(R.id.relativeLayoutHomeFragment);
        queue = Volley.newRequestQueue(mv.getContext());

        final GlobalVariablesClass globalVariable = GlobalVariablesClass.getInstance();

        mProgressbar = (ProgressBar) mv.findViewById(R.id.circle_progress_bar);
        mProgressExceededBar = (ProgressBar) mv.findViewById(R.id.circle_progress_bar_exceeded);

        mRelativeLayout.setOnClickListener(this);

        sdf = new SimpleDateFormat("yyyy-MM-dd");
        String startTime = sdf.format(new Date());
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("startTime", startTime);
        params.put("endTime", startTime);

        JsonObjectRequest jor = new JsonObjectRequest(Request.Method.POST, "http://159.203.204.9/api/v1/user/getTodaysCals", new JSONObject(params), new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response){
                Integer cal_value = 0;
                String limit = globalVariable.getPatientLimit();

                try {
                    JSONArray jsonArray = response.getJSONArray("food");
                    for(int i = 0; i < jsonArray.length(); i++){
                        JSONObject jsonObj = jsonArray.getJSONObject(i);
                        cal_value += Integer.parseInt(jsonObj.getString("calorie"));
                        //String blah = "2";
                    }
                }
                catch(JSONException e){
                    //something went wrong
                }

                tview.setText(cal_value.toString() + " / " + limit);
                //TODO:Make sure doubleValue returns cal_value as a double
                percentage = (cal_value.doubleValue() / Integer.parseInt(limit)) * 100;

                //NOTE: there are 2 overlapping progress bars
                // one for when the patient is under their limit and one for when the user exceeds their limit

                //Logic for progress bar when user consumes more calories than their daily limit
                if (percentage > 100) {
                    mProgressExceededBar.setProgress((int) (percentage - 100));
                    mProgressbar.setVisibility(View.INVISIBLE);
                    mProgressExceededBar.setVisibility(View.VISIBLE);
                    mProgressBarBackground.setVisibility(View.INVISIBLE);
                    mProgressBarExceededBackground.setVisibility(View.VISIBLE);
                    //Make current calories appears as RED text
                    tview.setTextColor(Color.RED);
                }
                //Logic for progress bar when the user is still under their limit
                else {
                    mProgressbar.setProgress((int) percentage);
                    mProgressExceededBar.setVisibility(View.INVISIBLE);
                    mProgressbar.setVisibility(View.VISIBLE);
                    mProgressBarBackground.setVisibility(View.VISIBLE);
                    mProgressBarExceededBackground.setVisibility(View.INVISIBLE);
                    //Make current calories appears as BLACK text
                    tview.setTextColor(Color.BLACK);
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //String test2 = "error";
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

        // Inflate the layout for this fragment
        return mv;
    }

    public void hideSoftKeyboard(View v){
        InputMethodManager imm = (InputMethodManager)
                getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    public void onClick(View v){
        switch(v.getId()){
            case R.id.relativeLayoutHomeFragment: hideSoftKeyboard(v);
                break;
        }
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
}
