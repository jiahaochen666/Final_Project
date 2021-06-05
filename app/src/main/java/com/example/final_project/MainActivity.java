package com.example.final_project;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private TextView hello;
    private TextView comment;
    private Switch block;
    private int room;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.welcome);
        hello = findViewById(R.id.hello);
        comment = findViewById(R.id.textView3);
        block = findViewById(R.id.switch1);

        Handler handler = new Handler();

        final Runnable r = new Runnable() {
            public void run() {
                try {
                    WifiManager manager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    WifiInfo info = manager.getConnectionInfo();
                    String ip = intToIp(manager.getDhcpInfo().ipAddress);
                    String gateway = intToIp(manager.getDhcpInfo().gateway);
                    connect(gateway);
                    get_sound();
                    block.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked) {
                                block.setText("Your roomate is now disconnected!");
                                upload_block(1);
                            } else {
                                block.setText("Your roomate is now connected!");
                                upload_block(0);
                            }
                        }
                    });
                    download_block();
                    get_rule_break();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                handler.postDelayed(this, 1000);
            }
        };

        handler.postDelayed(r, 1000);
    }

    public void get_rule_break(){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://18.117.75.236:5000/api/getbreak/?room=" + room;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        comment.setText("You have been too noisy for " + response + " times!");
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        queue.add(stringRequest);
    }

    public void download_block(){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://18.117.75.236:5000/api/download/?room=" + room;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        WifiManager manager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                        if (response.equals("1")){
                            manager.setWifiEnabled(false);
                        }
                        else
                            manager.setWifiEnabled(true);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        queue.add(stringRequest);
    }

    public void upload_block(int x){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url;
        if (room == 1)
            url = "http://18.117.75.236:5000/api/updateblock/?room=" + 2 + "&status=" + x;
        else
            url = "http://18.117.75.236:5000/api/updateblock/?room=" + 1 + "&status=" + x;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        queue.add(stringRequest);
    }

    public void connect(String gateway) throws Exception {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://18.117.75.236:5000/api/room/?gateway=" + gateway;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        textView.setText("Hello, you are in Room " + response.toString());
                        room = Integer.parseInt(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                textView.setText("You are not connected!");
            }
        });
        queue.add(stringRequest);
    }

    public String intToIp(int addr) {
        return ((addr & 0xFF) + "." +
                ((addr >>>= 8) & 0xFF) + "." +
                ((addr >>>= 8) & 0xFF) + "." +
                ((addr >>>= 8) & 0xFF));
    }

    public void get_sound() throws Exception {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://18.117.75.236:5000/get/";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject js = new JSONObject(response);
                            if (room == 2)
                                hello.setText(js.getString("h"));
                            else
                                hello.setText(js.getString("t"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        queue.add(stringRequest);
    }
}