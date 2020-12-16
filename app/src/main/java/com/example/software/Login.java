package com.example.software;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class Login extends AppCompatActivity {

    private static String IP_ADDRESS = "220.69.208.116";
    private static String TAG = "phptest";
    PersonalData personalData = new PersonalData();
    private EditText mEditTextID;
    private EditText mEditTextPASSWORD;
    private TextView mTextViewResult;

    private String mJsonString;
    private String Get_id;
    private String Get_password;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        builder = new AlertDialog.Builder(this);
        mEditTextID = (EditText)findViewById(R.id.ID2);
        mEditTextPASSWORD = (EditText)findViewById(R.id.password);
        mTextViewResult = (TextView)findViewById(R.id.textView_main_result);

        mTextViewResult.setMovementMethod(new ScrollingMovementMethod());

        Button button_login = (Button) findViewById(R.id.login_button);
        button_login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Get_id = mEditTextID.getText().toString();
                Get_password = mEditTextPASSWORD.getText().toString();

                GetData task = new GetData();
                task.execute( "http://" + IP_ADDRESS + "/login.php", Get_id);

                mEditTextID.setText("");
                mEditTextPASSWORD.setText(""); //이 시벌 위에꺼 다 하는게 아니라 여기가 먼저 되버리니까 안되는것처럼 보였지 이 시벌 시벌 시벌 시벌
            }
        });
    }
    private class GetData extends AsyncTask<String, Void, String>{

        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(Login.this,
                    "Please Wait", null, true, true);
        }
        @Override
        protected void onPostExecute(String result) { //쓸모 없다. JSON 파싱 화면. 이  result가 json 인데 이걸 어떻게 읽는건지를 모르겠네.
            super.onPostExecute(result);

            progressDialog.dismiss();
            mTextViewResult.setText(result);
            Log.d(TAG, "response - " + result);

            if (result == null){
                mTextViewResult.setText(errorString);
            }
            else {
                mJsonString = result;
                showResult();
            }
        }
        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];
            String id = params[1];


            String postParameters = "id=" + id;
            try {
                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);

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
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString().trim();


            } catch (Exception e) {

                Log.d(TAG, "GetData : Error ", e);
                errorString = e.toString();
                return null;
            }
        }
    }
    private void showResult(){

        String TAG_JSON="root";
        String TAG_NAME = "name";
        String TAG_AGE ="age";
        String TAG_ID = "id";
        String TAG_PASSWORD = "password";
        String TAG_PHONE = "phone";

        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0;i<jsonArray.length();i++){ //각 행마다 검사. 로그인 할때 이걸 이용하면 될 것 같다. id와 비밀 번호를 검사해서 이름을 보내는 거지. ㅇㅇ

                JSONObject item = jsonArray.getJSONObject(i);

                String name = item.getString(TAG_NAME);
                String age = item.getString(TAG_AGE);
                String id = item.getString(TAG_ID);
                String password = item.getString(TAG_PASSWORD);
                String phone = item.getString(TAG_PHONE);

                if(password.equals(Get_password)){

                    personalData.setMember_name(name);
                    personalData.setMember_age(age);
                    personalData.setMember_id(id);
                    personalData.setMember_password(password);
                    personalData.setMember_phone(phone);   //personaldData에 저장.
                }
                else{
                    builder.setTitle("경고")        // 제목 설정
                            .setMessage("비밀번호가 틀립니다!")        // 메세지 설정
                            .setPositiveButton("확인", new DialogInterface.OnClickListener(){
                                // 확인 버튼 클릭시 설정
                                public void onClick(DialogInterface dialog, int whichButton){

                                }
                            });
                    builder.show();
                }
            }
        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
    }
}