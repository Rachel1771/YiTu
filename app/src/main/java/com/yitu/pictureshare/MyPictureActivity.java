package com.yitu.pictureshare;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.yitu.pictureshare.adapter.MySharesAdapter;
import com.yitu.pictureshare.bean.ShareBean;
import com.yitu.pictureshare.common.AppAuthorization;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MyPictureActivity extends AppCompatActivity {

    private long userId;
    private String username;
    private int current;
    private List<ShareBean> myShares;
    private MySharesAdapter mySharesAdapter;
    private ListView listView;
    private Button button_focus_back;
    private static Handler handler;
    private int resultCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_picture);

        handler = new Handler(Looper.getMainLooper());

        listView = findViewById(R.id.list_my_picture);
        button_focus_back = findViewById(R.id.button_my_picture_back);

        myShares = new ArrayList<>();//初始化shares(List<Map<String,String>)

        Context ctx = MyPictureActivity.this;//获取已登录的userId
        SharedPreferences sp = ctx.getSharedPreferences("SP", Context.MODE_PRIVATE);
        username = sp.getString("username",null);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ShareBean shareBean = (ShareBean) adapterView.getItemAtPosition(i);//获取该条目的ShareBean
                Intent intent = new Intent(MyPictureActivity.this, ShareInfoActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra("shareId", shareBean.getShareId());
                intent.putExtra("intentImageUrl", shareBean.getImageUrl());
                intent.putExtra("pUserName",username);
                intent.putExtra("hasCollect",shareBean.getHasCollect());
                intent.putExtra("from",5);//5代表从MyPictureFragment中传入shareInfo的信息
                System.out.println("以userId="+sp.getString("id",null)+"进入title为"+shareBean.getTitle()+"的图文分享详情");
                startActivityForResult(intent, 555);//222指从HomeFragment发出的Intent
            }
        });

        button_focus_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("---------------------resultCode--------------------"+resultCode);
        if(resultCode == 2 || mySharesAdapter == null) {
            current =  1;
            getMyPicture();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("_________________________________requestCode:"+requestCode);
        System.out.println("_________________________________resultCode:"+resultCode);
        this.resultCode = resultCode;
    }

    public void getMyPicture(){

        String url = "http://47.107.52.7:88/member/photo/share/myself";
//        String url = "http://35.241.95.124:8081/user/login";
        OkHttpClient client = new OkHttpClient();
        //获取已登录的userId
        Context ctx = MyPictureActivity.this;
        SharedPreferences sp = ctx.getSharedPreferences("SP", Context.MODE_PRIVATE);
        userId = Long.parseLong(sp.getString("id",null));

        String appId = AppAuthorization.getAppId(sp);
        String appSecret = AppAuthorization.getAppSecret(sp);

//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("userId",userId);
//        jsonObject.put("current",current);
//        jsonObject.put("size",10);

        //构建请求
//        RequestBody body = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json;charset=utf-8"));
        Request request=new Request.Builder()
                .addHeader("appId",appId)
                .addHeader("appSecret",appSecret)
                .url(url+"?userId="+userId+"&current="+current+"&size=10")
                .get()
                .build();

        //异步请求
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("register", "连接失败" + e.getLocalizedMessage());
                Looper.prepare();
                Toast.makeText(MyPictureActivity.this, "连接失败", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
//                System.out.println(response.body().string());
                String result_string = response.body().string();
                Map result = JSON.parseObject(result_string);
                System.out.println("————————————响应信息————————————\n"+result.toString());



                if (result.get("code").toString().equals("200")) {
                    if(result.get("data") != null){
                        ///////////////////////////////////////////////////////我的分享数据解析/////////////////////////////////////////////////////
                        Map result_data = JSON.parseObject(result.get("data").toString());
                        List share_data_origin = (List) result_data.get("records");
                        System.out.println("______________share_data_origin________________\n"+share_data_origin);

                        ArrayList<ShareBean> myShares_temp = new ArrayList<>();

                        for(int i = 0; i < share_data_origin.size();i++) {
                            Map item = JSON.parseObject(share_data_origin.get(i).toString());
//                        Share item_share = new Share();
//                        item_share.setId(item.get("id").toString());
//                        item_share.setImageUrlList((List<String>)item.get("imageUrlList"));
//                        item_share.setpUserId(item.get("pUserId").toString());
//                        item_share.setUsername(item.get("username").toString());
                            ShareBean shareBean = new ShareBean();
                            shareBean.setShareId(item.get("id").toString());
                            List<String> temp_list = (List<String>) item.get("imageUrlList");

                            if (temp_list.size() != 0) {
                                shareBean.setImageUrl(temp_list.get(0));
                            } else {
                                shareBean.setImageUrl("");
                            }

                            shareBean.setTitle(item.get("title").toString());
                            shareBean.setUserId(item.get("pUserId").toString());

                            myShares_temp.add(shareBean);
                        }

                        ///////////////////////////////////////////////////////首页数据解析end/////////////////////////////////////////////////////
                        handler.post(new Runnable() {
                            //handler用于post非ui线程向ui线程的操作
                            @Override
                            public void run() {
                                System.out.println(("--------------------------current="+current+"------------------------"));
                                System.out.println(("--------------------------current="+current+"-----------------------"));
                                System.out.println(("--------------------------current="+current+"----------------------"));
                                System.out.println(("--------------------------current="+current+"---------------------"));
                                System.out.println(("--------------------------current="+current+"--------------------"));
                                System.out.println(("--------------------------current="+current+"-------------------"));
                                System.out.println(("--------------------------current="+current+"------------------"));
                                System.out.println(("--------------------------current="+current+"-----------------"));
                                System.out.println(("--------------------------current="+current+"----------------"));
                                System.out.println(("--------------------------current="+current+"---------------"));
                                System.out.println(("--------------------------current="+current+"--------------"));
                                System.out.println(("--------------------------current="+current+"-------------"));
                                if(current == 1) {
                                    myShares.clear();
                                    myShares.addAll(myShares_temp);

                                    if(mySharesAdapter != null)
                                        mySharesAdapter.notifyDataSetChanged();

                                    mySharesAdapter = new MySharesAdapter(MyPictureActivity.this, myShares);
                                    listView.setAdapter(mySharesAdapter);
                                } else {
                                    ArrayList<ShareBean> shares_temp_old = new ArrayList<>(myShares);
                                    myShares.clear();
                                    shares_temp_old.addAll(myShares_temp);
                                    myShares.addAll(shares_temp_old);
                                    mySharesAdapter.notifyDataSetChanged();
                                }

                                listView.setOnScrollListener(new AbsListView.OnScrollListener() {
                                    @Override
                                    public void onScrollStateChanged(AbsListView view, int scrollState) {
                                        switch (scrollState) {
                                            // 当不滚动时
                                            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                                                // 判断滚动到底部
                                                if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
                                                    current++;
                                                    getMyPicture();
                                                }else if (view.getFirstVisiblePosition() == 0) {
                                                    Toast.makeText(MyPictureActivity.this, "已经到顶部了，可点击主页图标刷新", Toast.LENGTH_SHORT).show();
                                                }
                                                break;
                                        }
                                    }

                                    @Override
                                    public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                                    }
                                });

                            }
                        });
                    } else {
                        Looper.prepare();
                        Toast.makeText(MyPictureActivity.this, "我的分享列表为空", Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }

                }else{
                    Looper.prepare();
                    Toast.makeText(MyPictureActivity.this, (String)result.get("msg"), Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
                response.body().close();
            }
        });
    }


}