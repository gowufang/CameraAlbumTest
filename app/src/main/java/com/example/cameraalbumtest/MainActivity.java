package com.example.cameraalbumtest;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cameraalbumtest.net.DataRequest;
import com.example.cameraalbumtest.util.Base64Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    TextView response;//响应
    TextView name;//返回的姓名
    String responseData;

    String image;//base64的 image
    public static final int TAKE_PHOTO = 1;
    private ImageView picture;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        response = (TextView) findViewById(R.id.response);
        name = (TextView) findViewById(R.id.name);
        Button takePhoto = (Button) findViewById(R.id.take_photo);
        picture = (ImageView) findViewById(R.id.picture);


        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 创建File对象，用于存储拍照后的图片
                File outputImage = new File(getExternalCacheDir(), "output_image.jpg");
                try {
                    if (outputImage.exists()) {//如果存在，则删除
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (Build.VERSION.SDK_INT < 24) {
                    imageUri = Uri.fromFile(outputImage);
                } else {
                    imageUri = FileProvider.getUriForFile(MainActivity.this, "com.example.cameraalbumtest.fileprovider3", outputImage);
                }
                // 启动相机程序
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, TAKE_PHOTO);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        // 将拍摄的照片显示出来
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));

                        picture.setImageBitmap(bitmap);
                        image = Base64Util.bitmapToBase64(bitmap);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                Handler myHandler = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        switch (msg.what) {
                            case 1:
                                response.setText(msg.obj.toString());
                                responseData = msg.obj.toString();//将json数据赋值给responseData，准备处理Json数据
//                                parseJSONWithJSONObject(responseData);
                                String getName = parseSearchedFace((JSONObject) msg.obj);//msg.obj就是一个jsonObject对象，存的是json数据
                                name.setText("This is "+getName);
                                break;
                            default:
                                break;
                        }
                    }
                };

                DataRequest request = new DataRequest(image, myHandler);
                request.execute();
//                parseJSONWithJSONObject(responseData);

                break;
            default:
                break;
        }
    }

    private static String parseSearchedFace(JSONObject jsonData) {
        String userId=null;
        try {
            JSONObject myJsonData = jsonData;  //创建jsonObject对象
            JSONObject result = myJsonData.getJSONObject("result");
//            System.out.println(result);
            Log.d("result", result.toString());

            JSONArray userList = result.getJSONArray("user_list");
            Log.d("user_list", userList.toString());
            for (int i = 0; i < userList.length(); ++i) {
                JSONObject faceItem = userList.getJSONObject(i);
                double score = faceItem.getInt("score");
                if (score > 60){
                    Log.d("Searchedface", score + "this is " + userId);
                    userId = faceItem.getString("user_id");
                    return  userId;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  userId;
    }


}
