package com.example.user.app;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
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

public class Register_Activity extends AppCompatActivity {
    EditText name, email, username, password, password_confirm;
    Button button_reg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_);
        button_reg = (Button) findViewById(R.id.button_register);
        button_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = (EditText) findViewById(R.id.et_name);
                email = (EditText) findViewById(R.id.et_email);
                username = (EditText) findViewById(R.id.et_username);
                password = (EditText) findViewById(R.id.et_password);
                password_confirm = (EditText) findViewById(R.id.et_password_confirm);

                final String str_name, str_email, str_username, str_password, str_confirm_password;
                str_name = name.getText().toString();
                str_email = email.getText().toString();
                str_username = username.getText().toString();
                str_password = password.getText().toString();
                str_confirm_password = password_confirm.getText().toString();
                if (str_name.equals("") || str_email.equals("") || str_username.equals("") || str_password.equals("") || str_confirm_password.equals("")) {
                    Toast.makeText(Register_Activity.this, "Error! Not Enough Infomation. Please check again!" , Toast.LENGTH_LONG).show();
                } else {
                    if (!str_password.equals(str_confirm_password)) {
                        Toast.makeText(Register_Activity.this, "Error! Password does not match with Confirm Password. Please check again!" , Toast.LENGTH_LONG).show();
                    } else {
                        final Handler handler = new Handler(){
                            @Override
                            public void handleMessage(Message msg) {
                                Bundle bundle = msg.getData();
                                String message = bundle.getString("message");
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                                if(message.equals("Register Successful")){
                                    Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
                                    intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent1);
                                }
                            }
                        };
                       Runnable runnable = new Runnable() {
                           @Override
                           public void run() {
                               try {
                                   URL url = new URL("http://hieuhuynh.x10host.com/App_register.php/");
                                   HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                                   httpURLConnection.setRequestMethod("POST");
                                   httpURLConnection.setDoInput(true);
                                   httpURLConnection.setDoOutput(true);

                                   OutputStream outputStream = httpURLConnection.getOutputStream();
                                   BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                                   String post_data = URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(str_name, "UTF-8") + "&"
                                           + URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(str_email, "UTF-8") + "&"
                                           + URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(str_username, "UTF-8") + "&"
                                           + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(str_password, "UTF-8");
                                   bufferedWriter.write(post_data);
                                   bufferedWriter.flush();
                                   bufferedWriter.close();
                                   outputStream.close();

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
                                   Message msg = handler.obtainMessage();
                                   Bundle bundle = new Bundle();
                                   bundle.putString("message", result);
                                   msg.setData(bundle);
                                   handler.sendMessage(msg);

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
            }

        });
    }

}
