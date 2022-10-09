package com.yitu.pictureshare.ui.home;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yitu.pictureshare.IndexActivity;
import com.yitu.pictureshare.bean.Picture;
import com.yitu.pictureshare.common.PictureParseTool;
import com.yitu.pictureshare.databinding.FragmentHomeBinding;
import com.yitu.pictureshare.ui.adapter.PictureAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;

    PictureAdapter pictureAdapter = new PictureAdapter(null, getActivity());
    RecyclerView recyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        pictureAdapter = new PictureAdapter(getActivity(), homeViewModel);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        recyclerView = binding.recyclerviewIndex;
        View root = binding.getRoot();
        getPicture();
        recyclerView.setAdapter(pictureAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, true));
        homeViewModel.getLivePictureList().observe(getViewLifecycleOwner(), new Observer<List<Picture>>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChanged(List<Picture> pictures) {
                pictureAdapter.setPictureList(pictures);
                pictureAdapter.notifyDataSetChanged();
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void getPicture(){

//        String url="http://35.241.95.124:8081/picture/getPictures";
        String url="http://10.0.2.2:8081/picture/getPictures";
        OkHttpClient client=new OkHttpClient();
        //构建表单参数
        FormBody.Builder requestBuild = new FormBody.Builder();
        //添加请求体
        String data = "";
        RequestBody formBody;
        formBody = RequestBody.create(IndexActivity.JSON,data);
        Request request=new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        System.out.println(request.toString());
        //异步请求
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("Home", "连接失败" + e.getLocalizedMessage());
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                String result = Objects.requireNonNull(response.body()).string();
                Log.d("Home", result);
                List<Map<String, Object>> list = new ArrayList<Map<String, Object>>(PictureParseTool.jsonJXGetPicture(result));
                Message message = new Message();
                message.obj = list;
                message.what = 1;
                handler.sendMessage(message);
                Log.d("Home", "成功");
                Objects.requireNonNull(response.body()).close();
            }
        });
    }
    Handler handler = new Handler(Looper.getMainLooper())
    { public void handleMessage(Message msg) {
        switch (msg.what) {
            case 1:     //获取首页数据
                List<Picture> pictureList = new ArrayList<>();
                List<Map<String, Object>> list = (List<Map<String, Object>>) msg.obj;
                for(int i = 0 ; i < list.size() ; i ++){
                    Map<String,Object> map = list.get(i);
                    Picture picture = new Picture() ;
                    picture.setId(map.get("id").toString());
                    picture.setUserId( map.get("userId").toString());
                    picture.setPictureData( PictureParseTool.base64ToBitmap(map.get("pictureData").toString()));
                    picture.setLikeNum(Integer.valueOf(map.get("likeNum").toString()));
                    picture.setTitle(map.get("title").toString());
                    picture.setAuthor(map.get("author").toString());
                    pictureList.add(picture);
                };
//                MutableLiveData<List<Picture>> livePictureList = homeViewModel.getLivePictureList();
//                livePictureList.setValue(pictureList);
//                homeViewModel.setLivePictureList(livePictureList);
                break;
        }
    } };
}