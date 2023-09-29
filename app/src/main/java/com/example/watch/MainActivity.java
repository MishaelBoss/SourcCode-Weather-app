package com.example.watch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private EditText field;
    private Button ButtonMain;
    private Button Button_settings;
    private TextView result_info;
    private TextView max_temp_result_info;
    private TextView min_temp_result_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavigationView navigationView = findViewById(R.id.navigation_bar);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                switch (id){
                    case R.id.settings_btn:
                            Toast.makeText(MainActivity.this, "Settings", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.auftor_btn:
                            Toast.makeText(MainActivity.this, "Auftor", Toast.LENGTH_SHORT).show();
                        return true;

                }

                return false;
            }
        });

        field = findViewById(R.id.field);
        ButtonMain = findViewById(R.id.ButtonMain);
        Button_settings = findViewById(R.id.Button_settings);
        result_info = findViewById(R.id.result_info);
        max_temp_result_info = findViewById(R.id.max_temp_result_info);
        min_temp_result_info = findViewById(R.id.min_temp_result_info);

        ButtonMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(field.getText().toString().trim().equals(""))
                    Toast.makeText(MainActivity.this, R.string.hint_field, Toast.LENGTH_LONG).show();
                else{
                    String city = field.getText().toString();
                    String key = "6f4144bd1716d9f86b3490b668859281";
                    String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + key + "&units=metric&lang=ru";

                    new GetURLData().execute(url);
                }
            }
        });
    }

    private class GetURLData extends AsyncTask<String, String, String>{

        protected void onPreExecute() {
            super.onPreExecute();
            result_info.setText("Ожидайте...");
        }
        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection=null;
            BufferedReader reader=null;

            try{
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null)
                    buffer.append(line).append("\n");

                return buffer.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(connection != null)
                    connection.disconnect();

                try {
                    if(reader !=null)
                        reader.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            return null;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);

            try {
                JSONObject obj = new JSONObject(result);
                result_info.setText("Температура: " + obj.getJSONObject("main").getDouble("temp"));
                max_temp_result_info.setText("Максимальная температура: " + obj.getJSONObject("main").getDouble("temp_max"));
                min_temp_result_info.setText("Минимальная температура: " + obj.getJSONObject("main").getDouble("temp_min"));
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }
    }
}