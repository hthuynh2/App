package com.example.user.app;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class Home_Activity extends AppCompatActivity {
    static String username;
    TextView textView;
    String json_string;
    JSONArray jsonArray;
    JSONObject jsonObject;
    PhoneAdapter phoneAdapter;
    ListView listView;


    @Override
    public void onBackPressed() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_);
        Bundle extras = getIntent().getExtras();
        phoneAdapter = new PhoneAdapter(getBaseContext(), R.layout.row_layout);
        username = extras.getString("username");
        textView = (TextView) findViewById(R.id.textView);
        listView = (ListView) findViewById(R.id.listView);
        set_num_devices(username);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Phone selectedPhone = phoneAdapter.list.get(position);
                Intent intent = new Intent(getApplicationContext(), PhoneInfo_Activity.class);
                intent.putExtra("username", username);
                intent.putExtra("isLost", selectedPhone.getIsLost());
                intent.putExtra("phoneName", selectedPhone.getPhoneName());
                startActivity(intent);
            }
        });
    }

    protected void set_num_devices(final String username) {
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String message = bundle.getString("message");
                String text = "You have " + message + " devices";
                textView.setText(text);
                if(message.equals("0")){
                    AlertDialog.Builder alert_builder= new AlertDialog.Builder(Home_Activity.this);
                    alert_builder.setMessage("You have no device in the list. Please add one.").setCancelable(true).setPositiveButton("Add device", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getApplicationContext(), Add_Device_Activity.class);
                            intent.putExtra("username", username);
                            startActivity(intent);
                        }
                    }).setNegativeButton("close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog alertDialog = alert_builder.create();
                    alertDialog.show();
                }
            }
        };

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://hieuhuynh.x10host.com/App_getNumber_Row.php/");
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);

                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    String post_data = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8");
                    bufferedWriter.write(post_data);
                    bufferedWriter.flush();
                    bufferedWriter.close();

                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                    String result = "";
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        result += line;
                    }
                    bufferedReader.close();
                    inputStream.close();
                    httpURLConnection.disconnect();

                    ////////////
                    if(!result.equals("0")) {
                        String JSON_STRING;
                        try {
                            URL url1 = new URL("http://hieuhuynh.x10host.com/App_json_get_phone_data.php");
                            HttpURLConnection httpURLConnection1 = (HttpURLConnection) url1.openConnection();
                            httpURLConnection1.setRequestMethod("POST");
                            httpURLConnection1.setDoOutput(true);
                            httpURLConnection1.setDoInput(true);
                            OutputStream outputStream1 = httpURLConnection1.getOutputStream();
                            BufferedWriter bufferedWriter1 = new BufferedWriter(new OutputStreamWriter(outputStream1, "UTF-8"));
                            String post_data1 = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8");
                            bufferedWriter1.write(post_data1);
                            bufferedWriter1.flush();
                            bufferedWriter1.close();

                            InputStream inputStream1 = httpURLConnection1.getInputStream();
                            BufferedReader bufferedReader1 = new BufferedReader(new InputStreamReader(inputStream1, "iso-8859-1"));

                            StringBuilder stringBuilder = new StringBuilder();

                            while ((JSON_STRING = bufferedReader1.readLine()) != null) {
                                stringBuilder.append(JSON_STRING + "\n");
                            }
                            bufferedReader1.close();
                            inputStream.close();
                            httpURLConnection1.disconnect();
                            json_string = stringBuilder.toString().trim();

                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            jsonObject = new JSONObject(json_string);
                            jsonArray = jsonObject.getJSONArray("phone_data");
                            int count =0;
                            String phoneName, isLost, ringRequest;
                            while(count<jsonArray.length()){
                                JSONObject jo = jsonArray.getJSONObject(count);
                                phoneName = jo.getString("phoneName");
                                isLost = jo.getString("isLost");
                                ringRequest = jo.getString("ringRequest");


                                Phone phone = new Phone(phoneName, isLost, ringRequest);
                                phoneAdapter.add(phone);
                                count++;
                            }



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    /////////////////
                    Message message = handler.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putString("message", result);
                    message.setData(bundle);
                    handler.sendMessage(message);


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
        try {
            thread.join();
            listView.setAdapter(phoneAdapter);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void addDevice(View view){
        Intent intent = new Intent(getApplicationContext(), Add_Device_Activity.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }


}
