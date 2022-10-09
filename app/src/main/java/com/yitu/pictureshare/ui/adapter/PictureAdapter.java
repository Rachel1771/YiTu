package com.yitu.pictureshare.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.yitu.pictureshare.IndexActivity;
import com.yitu.pictureshare.R;
import com.yitu.pictureshare.bean.Picture;
import com.yitu.pictureshare.ui.home.HomeViewModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PictureAdapter extends RecyclerView.Adapter<PictureAdapter.PictureViewHolder> {

    private List<Picture> pictureList;
    private final Context context;
    private static HomeViewModel homeViewModel;

    public PictureAdapter(Context context) {
        this.context = context;
    }

    public PictureAdapter(Context context, HomeViewModel homeViewModel) {
        this.context = context;
        this.homeViewModel = homeViewModel;
    }

    public PictureAdapter(List<Picture> pictureList, Context context) {
        this.pictureList = pictureList;
        this.context = context;
    }

    public void setPictureList(List<Picture> pictureList) {
        this.pictureList = pictureList;
    }

    @NonNull
    @Override
    public PictureViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_picture, parent, false);
        PictureViewHolder viewHolder = new PictureViewHolder(view);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull PictureViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.imageViewPicture.setImageBitmap(pictureList.get(position).getPictureData());
        holder.textViewTitle.setText(pictureList.get(position).getTitle());
        holder.textViewAuthor.setText(pictureList.get(position).getAuthor());
        holder.textViewLikeNum.setText(String.valueOf(pictureList.get(position).getLikeNum()));
        holder.buttonLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Picture picture = pictureList.get(position);
                giveLike(picture.getId());
                picture.setLikeNum(1 + picture.getLikeNum());
                MutableLiveData<List<Picture>> livePictureList = homeViewModel.getLivePictureList();
                livePictureList.setValue(pictureList);
                homeViewModel.setLivePictureList(livePictureList);
            }
        });
        holder.buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File("/sdcard/Pictures/", "pictureName.jpg");
                if (file.exists()) {
                    file.delete();
                }
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    Bitmap bitMap = ((BitmapDrawable) holder.imageViewPicture.getDrawable()).getBitmap();//通过强制转化weiBitmapDrable然后获取Bitmap
                    bitMap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);//然后按照指定的图片格式转换，并以stream方式保存文件
                    Toast toast = Toast.makeText(context,
                            "保存成功",
                            Toast.LENGTH_SHORT);
                    toast.show();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
        holder.buttonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(
                        context.getContentResolver(),
                        pictureList.get(position).getPictureData(),
                        null,
                        null));
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);   //设置分享行为
                intent.setType("image/*");              //设置分享内容的类型
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                intent = Intent.createChooser(intent, "分享");
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return pictureList == null ? 0 : pictureList.size();
    }

    public static class PictureViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageViewPicture;
        private final TextView textViewTitle;
        private final TextView textViewAuthor;
        private final TextView textViewLikeNum;
        private final Button buttonLike;
        private final Button buttonSave;
        private final Button buttonShare;

        public PictureViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewPicture = itemView.findViewById(R.id.imageview_picture);
            textViewTitle = itemView.findViewById(R.id.textview_picture_title);
            textViewAuthor = itemView.findViewById(R.id.textview_picture_author);
            textViewLikeNum = itemView.findViewById(R.id.textViewLikeNum);
            buttonLike = itemView.findViewById(R.id.button_like);
            buttonSave = itemView.findViewById(R.id.button_save);
            buttonShare = itemView.findViewById(R.id.button_share);
            itemView.findViewById(R.id.button_delete).setVisibility(View.GONE);
        }
    }

    public void giveLike(String pictureId) {

//        String url="http://35.241.95.124:8081/picture/giveLike";
        String url = "http://10.0.2.2:8081/picture/giveLike";
        OkHttpClient client = new OkHttpClient();
        //构建表单参数
        HashMap<String, String> map = new HashMap<>();
        map.put("id", pictureId);
        Gson gson = new Gson();
        String data = gson.toJson(map);
        //添加请求体
        RequestBody formBody;
        formBody = RequestBody.create(IndexActivity.JSON, data);
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        //异步请求
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("picture", "连接失败" + e.getLocalizedMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                if (result.trim().equals("Give a like successfully.")) {
                    Log.d("picture", result);
                    Message message = new Message();
                    message.obj = result;
                    message.what = 2;
                    handler.sendMessage(message);
                } else
                    Log.d("picture", result);
                response.body().close();
            }
        });
    }

    Handler handler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            List<Map<String, Object>> list;
            switch (msg.what) {
                case 1://获取主页面数据
                    break;
                case 2: //点赞
                    break;
                case 3: //上传
                    break;
            }
        }
    };
}
