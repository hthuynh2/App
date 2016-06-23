package com.example.user.app;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
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

public class Add_Device_Activity extends AppCompatActivity {
    TextView et_phone_name;
    String username;
    Button button_add;


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(),Home_Activity.class);
        intent.putExtra("username", username);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__device_);

        Bundle extras = getIntent().getExtras();
        username = extras.getString("username");

        et_phone_name = (EditText) findViewById(R.id.phoneName);
        button_add = (Button) findViewById(R.id.button_add);
        button_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_phone_name = et_phone_name.getText().toString();
                if(str_phone_name.equals("")){
                    Toast.makeText(Add_Device_Activity.this, "Please enter the name of your phone", Toast.LENGTH_LONG).show();
                }
                else{
                    String phone_name = et_phone_name.getText().toString();
                    addPhone(username, phone_name);
                }
            }
        });
    }

    private void addPhone(final String username, final String phone_name) {
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String message = bundle.getString("message");
                assert message!=null;
                if(message.equals("Successfully add one device")){
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), Home_Activity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                }
            }
        };
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                try {
                    URL url = new URL("http://hieuhuynh.x10host.com/App_add_device.php/");
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("POST");
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setDoInput(true);

                    OutputStream outputStream = httpURLConnection.getOutputStream();
                    BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                    String post_data = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(username, "UTF-8") + "&"
                            + URLEncoder.encode("phoneName", "UTF-8") + "=" + URLEncoder.encode(phone_name, "UTF-8");
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
