package com.example.lauren.beachbonfireplanner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class FrontPage extends Activity {

    public static final String TAG = FrontPage.class.getSimpleName();

    private TideInformation mTideInformation;

    @InjectView(R.id.goodOrBadView) TextView mGoodOrBadLabel;
    @InjectView(R.id.lowTideView) TextView mLowTideLabel;
    @InjectView(R.id.highTideView) TextView mHighTideLabel;
    @InjectView(R.id.sunsetView) TextView mSunsetLabel;
    @InjectView(R.id.submitButton) Button mSubmitButton;
    @InjectView(R.id.progressBar) ProgressBar mProgressBar;
    @InjectView(R.id.bonfireCity) EditText mBonfireCity;
    @InjectView(R.id.bonfireState) EditText mBonfireState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front_page);
        ButterKnife.inject(this);

        mProgressBar.setVisibility(View.INVISIBLE);

        //final String bonfireStateString = "MA";
        //final String bonfireCityString = "Boston";
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String bonfireCityString = mBonfireCity.getText().toString();
                final String bonfireStateString = mBonfireState.getText().toString();
                //Log.d(TAG, bonfireCityString);
                //Log.d(TAG, bonfireStateString);

                //Make sure city and state entered.
                if (bonfireCityString.length()> 0 && bonfireStateString.length()>0){
                    getTides(bonfireCityString, bonfireStateString);
                }
                else{
                    Toast.makeText(getApplicationContext(), "Must enter city and state.", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void getTides(String bonfireCity, String bonfireState) {
        String apiKey = "f1a5e509d1c59e7f";
        String url = "http://api.wunderground.com/api/" + apiKey + "/tide/q/" + bonfireState +
                "/" + bonfireCity + ".json";

        if (isNetworkAvailable()) {
            toggleRefresh();

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();
                        }
                    });
                    alertUserAboutError();
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();
                        }
                    });

                    try {
                        String jsonData = response.body().string();
                        //Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
                            mTideInformation = getCurrentTide(jsonData);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //clearDisplay();
                                    updateDisplay();
                                }
                            });

                        } else {
                            alertUserAboutError();
                        }
                    }
                    catch (IOException e) {
                        Log.e(TAG, "Exception caught: ", e);
                    }
                    catch (JSONException e){
                        Log.e(TAG, "Exception caught: ", e);
                    }
                }
            });
        }
        else{
            Toast.makeText(this, getString(R.string.network_unavailable_message),
                    Toast.LENGTH_LONG).show();
        }
    }

    private void toggleRefresh() {
        if (mProgressBar.getVisibility() == View.INVISIBLE)
        {
            mProgressBar.setVisibility(View.VISIBLE);
        }
        else{
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void updateDisplay() {
        clearDisplay();
        mGoodOrBadLabel.setText(mTideInformation.getGoodOrBad());
        mHighTideLabel.setText("High Tide: " + mTideInformation.getHighTideTime());
        mLowTideLabel.setText("Low Tide: " + mTideInformation.getLowTideTime());
        mSunsetLabel.setText("Sunset: " + mTideInformation.getSunsetTime());
    }

    private void clearDisplay(){
        mGoodOrBadLabel.setText("");
        mHighTideLabel.setText("High Tide:");
        mLowTideLabel.setText("Low Tide:");
        mSunsetLabel.setText("Sunset:");
    }

    private TideInformation getCurrentTide(String jsonData) throws JSONException {
        JSONObject tideForecast = new JSONObject(jsonData);
        TideInformation tideInformation = new TideInformation();

        JSONObject response = tideForecast.getJSONObject("response");

        //City exists, incorrect state
        if (response.has("results")){
            stateErrorAlert();
            //tideInformation.setGoodOrBad("Error: This city/state combination is incorrect. Please re-enter city and state.");
        }
        //City does not exist
        else if (response.has("error")){
            cityErrorAlert();
            //tideInformation.setGoodOrBad("Error: This city can not be found. Please re-enter city and state.");
        }
        else{
            JSONObject tide = tideForecast.getJSONObject("tide");
            JSONArray tideSummary = tide.getJSONArray("tideSummary");

            tideInformation.setHighTideTime(null);
            tideInformation.setLowTideTime(null);
            tideInformation.setSunsetTime(null);

            //City has no tide
            if (tideSummary.length()== 0){
                //Log.v(TAG, "No tide");
                tideErrorAlert();
                //tideInformation.setGoodOrBad("Error: This city/state combination has no tidal information. Please re-enter city and state.");
            }
            else{
                //iterate through first 8 tide info points and fill in times
                for (int i = 0; i < 8; i++){
                    JSONObject tideArray = tideSummary.getJSONObject(i);
                    JSONObject date = tideArray.getJSONObject("date");
                    JSONObject data = tideArray.getJSONObject("data");
                    tideInformation.setDataType(data.getString("type"));


                    String type = tideInformation.getDataType();
                    if (type.equals("High Tide") && (tideInformation.getHighTideTime() == null)){
                        tideInformation.setHighTideTime(date.getString("pretty"));
                        tideInformation.setHighTideHour(date.getString("hour"));
                    }
                    else if (type.equals("Sunset") && (tideInformation.getSunsetTime() == null)){
                        tideInformation.setSunsetTime(date.getString("pretty"));
                        tideInformation.setSunsetHour(date.getString("hour"));
                    }
                    else if (type.equals("Low Tide") && (tideInformation.getLowTideTime() == null)){
                        tideInformation.setLowTideTime(date.getString("pretty"));
                        tideInformation.setLowTideHour(date.getString("hour"));
                    }
                }
                tideInformation.setGoodOrBad(tideInformation.getVerdict());

            }
        }

        return tideInformation;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()){
            isAvailable = true;
        }
        return isAvailable;
    }

    private void alertUserAboutError() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(), "error_dialog");

    }

    private void cityErrorAlert(){
        CityErrorFragment dialog = new CityErrorFragment();
        dialog.show(getFragmentManager(), "city_error_dialog");
    }

    private void stateErrorAlert(){
        StateErrorFragment dialog = new StateErrorFragment();
        dialog.show(getFragmentManager(), "state_error_dialog");
    }

    private void tideErrorAlert(){
        NoTideErrorFragment dialog = new NoTideErrorFragment();
        dialog.show(getFragmentManager(), "tide_error_dialog");
    }

}
