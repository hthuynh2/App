package com.example.user.app;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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

public class PhoneInfo_Activity extends AppCompatActivity {

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), Home_Activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("username", username);
        startActivity(intent);
    }

    String username, phoneName, isLost;
    Button button_setLost, button_setRing, button_location;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_info_);
        Bundle extras = getIntent().getExtras();
        username = extras.getString("username");
        phoneName = extras.getString("phoneName");
        isLost = extras.getString("isLost");
        button_location = (Button) findViewById(R.id.button_location);
        button_setLost = (Button) findViewById(R.id.button_setLost);
        button_setLost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isLost.equals("1")){
                    AlertDialog.Builder alert_builder = new AlertDialog.Builder(PhoneInfo_Activity.this);
                    alert_builder.setCancelable(true).setMessage("This phone is already in Lost Mode. Do you want to set it to Normal mode?")
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            setLost(username, phoneName, String.valueOf("1"));
                        }
                    });
                    AlertDialog alert = alert_builder.create();
                    alert.show();
                }
                else{
                    setLost(username, phoneName, String.valueOf("0"));
                }

            }
        });
        button_setRing = (Button) findViewById(R.id.button_setRing);
    }


    private void setLost(final String username, final String phonename, final String found){
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String message = bundle.getString("message");
                if(message!=null){
                    Toast.makeText(PhoneInfo_Activity.this, message, Toast.LENGTH_LONG).show();
                }

            }
        };

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://hieuhuynh.x10host.com/App_setLost.php/");
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);

                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    String post_data = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8") + "&"
                            + URLEncoder.encode("phoneName", "UTF-8") + "=" + URLEncoder.encode(phonename, "UTF-8") +"&"
                            +URLEncoder.encode("found", "UTF-8") + "=" + URLEncoder.encode(found, "UTF-8");

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
    }




}
