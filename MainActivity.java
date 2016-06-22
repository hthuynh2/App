package com.example.user.app;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.renderscript.ScriptGroup;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class MainActivity extends AppCompatActivity {

    EditText username, password;
    Button button_login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button_login = (Button) findViewById(R.id.button_login);
        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = (EditText) findViewById(R.id.et_username);
                password = (EditText) findViewById(R.id.et_password);

                final Handler handler = new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        Bundle bundle = msg.getData();
                        String message = bundle.getString("message");
                        if(message.equals("Login success!")){
                            Intent intent = new Intent(getApplicationContext(), Home_Activity.class);
                            intent.putExtra("username", username.getText().toString());
                            startActivity(intent);
                        }
                        else{
                            Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                        }
                    }
                };

                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            URL url = new URL("http://hieuhuynh.x10host.com/App_login.php/");
                            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                            httpURLConnection.setRequestMethod("POST");
                            httpURLConnection.setDoOutput(true);
                            httpURLConnection.setDoInput(true);

                            OutputStream outputStream = httpURLConnection.getOutputStream();
                            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                            String post_data = URLEncoder.encode("user_name", "UTF-8") + "=" + URLEncoder.encode(username.getText().toString(), "UTF-8") + "&"
                                    + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password.getText().toString(), "UTF-8");
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
        });
    }

    public void register(View view){
        Intent intent = new Intent (MainActivity.this, Register_Activity.class);
        startActivity(intent);
    }
}
