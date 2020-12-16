package com.example.software;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends AppCompatActivity {

    private static String IP_ADDRESS = "220.69.208.116";
    private static String TAG = "phptest";

    private EditText mEditTextID;
    private EditText mEditTextPassword;
    private EditText mEditTextName;
    private EditText mEditTextAge;
    private EditText mEditTextPhone;
    private TextView mTextViewResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEditTextID = (EditText) findViewById(R.id.ID);
        mEditTextPassword = (EditText) findViewById(R.id.Password);
        mEditTextName = (EditText) findViewById(R.id.Name);
        mEditTextAge = (EditText) findViewById(R.id.Age);
        mEditTextPhone = (EditText) findViewById(R.id.Phone);

        mTextViewResult = (TextView)findViewById(R.id.sign);

        Button buttonInsert = (Button)findViewById(R.id.signup);
        buttonInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String id = mEditTextID.getText().toString();
                String password = mEditTextPassword.getText().toString();
                String name = mEditTextName.getText().toString();
                String age = mEditTextAge.getText().toString();
                String phone = mEditTextPhone.getText().toString();

                InsertData task = new InsertData();
                task.execute("http://" + IP_ADDRESS + "/insertsignup.php", id, password, name, age, phone);

                mEditTextID.setText("");
                mEditTextPassword.setText("");
                mEditTextName.setText("");
                mEditTextAge.setText("");
                mEditTextPhone.setText("");

            }
        });
        Button buttoncancel = (Button)findViewById(R.id.cancel);
        buttoncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);

            }
        });
    }
    class InsertData extends AsyncTask<String, Void, String>{ //첫번째는 doln~ 의 인수 타입, 두번째는 onPro~ 의 인수 타입, 세번째는 onPost~의 인수 타입.
        ProgressDialog progressDialog; //로딩중 화면 띄워주는 역할.

        @Override
        protected void onPreExecute() { //로딩 화면
            super.onPreExecute();

            progressDialog = ProgressDialog.show(MainActivity.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();//로딩창 종료
            Toast toast = Toast.makeText(getApplicationContext(),result, Toast.LENGTH_LONG);
            Log.d(TAG, "POST response  - " + result);
        }


        @Override
        protected String doInBackground(String... params) {

            String serverURL = (String) params[0];
            String id = (String) params[1];
            String password = (String) params[2];
            String name = (String) params[3];
            String age = (String) params[4];
            String phone = (String) params[5];

            String postParameters = "id=" + id + "&password=" + password + "&name=" + name + "&age=" + age + "&phone=" + phone;

            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST"); //POST 방식인거 명시
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
    }


}
