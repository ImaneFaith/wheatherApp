package com.example.weatherapp;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    TextView ville;
    TextView temps;
    TextView tmpMin;
    TextView tmpMax;
    TextView pression;
    TextView humid;
    TextView datetxt;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchManager searchManager= (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView= (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        ville=findViewById(R.id.text_name_city);
        datetxt=findViewById(R.id.text_time);
        temps=findViewById(R.id.text_temp);
        tmpMax=findViewById(R.id.text_temp_max_content);
        tmpMin=findViewById(R.id.text_temp_min_content);
        pression=findViewById(R.id.text_pression_content);
        humid=findViewById(R.id.text_humidity_content);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
                String url="http://api.openweathermap.org/data/2.5/weather?q="+query+"&appid=78e9da963d1902dc04e41ccc07c0d944";
                StringRequest stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("myLog",response);
                        try {
                            JSONObject jsonObject=new JSONObject(response);
                            Date date=new Date(jsonObject.getLong("dt")*1000);
                            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MMM-yyyy ' T ' HH:mm");
                            String dateString=simpleDateFormat.format(date);
                            JSONObject main=jsonObject.getJSONObject("main");
                            int temp= (int) (main.getDouble("temp")-273.15);
                            int tempMax= (int) (main.getDouble("temp_max")-273.15);
                            int tempMin =(int) (main.getDouble("temp_min")-273.15);
                            int pressure= (int) (main.getDouble("pressure"));
                            int humidity= (int) (main.getDouble("humidity"));
                            datetxt.setText(dateString);
                            temps.setText(String.valueOf(temp)+" °C");
                            tmpMax.setText(String.valueOf(tempMax)+" °C");
                            tmpMin.setText(String.valueOf(tempMin) +" °C");
                            pression.setText(String.valueOf(pressure) +" hPa");
                            humid.setText(String.valueOf(humidity) +" %");
                            JSONArray weather= jsonObject.getJSONArray("weather");
                            String meteo=weather.getJSONObject(0).getString("main");
                            setImage(meteo);



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ville.setText("City");
                        Log.i("error","=========connection prolem====================");
                        String errors="city not found";
                       Intent intent=new Intent(getApplicationContext(),Error.class);
                       intent.putExtra("error",errors);
                        startActivity(intent);
                    }
                });
                requestQueue.add(stringRequest);
                ville.setText(query);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

        });




        return true;
    }
    public void setImage(String s ){
        imageView=findViewById(R.id.cityState);
        if(s.equals("Rain")){
            imageView.setImageResource(R.drawable.ic_wi_rain);
        }
        if(s.equals("Clear")){
            imageView.setImageResource(R.drawable.ic_wi_night_clear);
        }
        if(s.equals("Thunderstorm")){
            imageView.setImageResource(R.drawable.ic_wi_thunderstorm);
        }
        if(s.equals("Clouds")){
            imageView.setImageResource(R.drawable.ic_wi_cloud);
        }





    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
