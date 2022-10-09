package com.yitu.pictureshare;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextPassword;
    private EditText editTextName;
    private EditText editTextConfirmPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        editTextPassword  = findViewById(R.id.editViewRegisterPassword);
        editTextName = findViewById(R.id.edittextRegisterUserName);
        editTextConfirmPassword = findViewById(R.id.editViewRegisterConfirmPassword);
        Button buttonRegister = findViewById(R.id.buttonCreateAccount);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editTextName.getText().toString();
                String password = editTextPassword.getText().toString();
                String confirmPassword = editTextConfirmPassword.getText().toString();
                if (password.equals(confirmPassword)) {
                    register(username, password);
                }else{
                    Toast.makeText(getApplicationContext(), "两次输入密码不一致", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void register(String username, String password) {
        String url = "http://47.107.52.7:88/member/photo/user/register";
//        String url = "http://35.241.95.124:8081/user/login";
        OkHttpClient client = new OkHttpClient();

        String appId = "fe5dfc29e21e468f8a8c01861331b9d9";
        String appSecret = "598422f155b9e538d49f99a217bc9fdfe2f38";

        JSONObject jsonObject = new JSONObject();

        jsonObject.put("username",username);
        jsonObject.put("password",password);
        //构建表单参数
        //添加请求体
        RequestBody body = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json;charset=utf-8"));
//        formBody=RequestBody.create(IndexActivity.JSON,data);
        Request request=new Request.Builder()
                .addHeader("appId",appId)
                .addHeader("appSecret",appSecret)
                .url(url)
                .post(body)
                .build();
        System.out.println("——————————请求信息——————————\n"+request);
        //异步请求
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("register", "连接失败" + e.getLocalizedMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Map result = JSON.parseObject(response.body().string());
                System.out.println("————————————响应信息————————————\n"+result.toString());
                if (result.get("code").toString().equals("200")) {
                    Intent intent = new Intent(RegisterActivity.this, com.yitu.pictureshare.LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    Looper.prepare();
                    Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                    Looper.loop();
                }else{
                    Looper.prepare();
                    Toast.makeText(RegisterActivity.this, (String)result.get("msg"), Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }

                response.body().close();
            }
        });
    }
};