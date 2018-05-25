package com.example.cameraalbumtest.net;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.baidu.aip.face.AipFace;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Administrator on 2018/5/25.
 * Email:gowufang@gmail.com
 * Description:
 */

public class DataRequest extends AsyncTask<String,Void,String> {
    JSONObject res;
    Handler mHandler;
    String image;
    public DataRequest(String image,Handler mHandler) {
        this.mHandler = mHandler;
        this.image=image;
    }

    //设置APPID/AK/SK
    public static final String APP_ID = "11044996";
    public static final String API_KEY = "rYZFT2hFcjl86BFPsQo35Ydz";
    public static final String SECRET_KEY = "PiwaMGt88Ycbfbdpto2tlE4dZlPEpcsY";
    @Override
    protected String doInBackground(String... params) {
        // 初始化一个AipFace
        AipFace client = new AipFace(APP_ID, API_KEY, SECRET_KEY);

        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);

//调用百度sdk，搜索人脸
        // 传入可选参数调用接口
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("quality_control", "NORMAL");
        options.put("liveness_control", "LOW");
//        options.put("user_id", "233451");
        options.put("max_user_num", "3");

//        String image = "取决于image_type参数，传入BASE64字符串或URL字符串或FACE_TOKEN字符串";
        String imageType = "BASE64";
        String groupIdList = "wu,wang,chao";

        // 人脸搜索
         res = client.search(image, imageType, groupIdList, options);
        parseSearchedFace(res);
        Log.d("searchedface",res.toString());
        return res.toString();
    }
    private static void parseSearchedFace(JSONObject jsonData) {
        try {
            JSONObject myJsonData=jsonData;  //创建jsonObject对象
            JSONObject result = myJsonData.getJSONObject("result");
//            System.out.println(result);
            Log.d("parseSearchedFace",result.toString());

            JSONArray userList=result.getJSONArray("user_list");
            Log.d("parseSearchedFace",userList.toString());
            for(int i=0;i<userList.length();++i){
                JSONObject faceItem=userList.getJSONObject(i);
                double score=faceItem.getInt("score");
                String userId=faceItem.getString("user_id");
                if (score>80)
//                    System.out.println(score+"this is "+userId);
                Log.d("Searchedface" ,score+"this is "+userId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onPostExecute(String str) {
        super.onPostExecute(str);
        Message msg=mHandler.obtainMessage();
        if (str!=null){
            msg.what=1;
            msg.obj=res;//

        }
        mHandler.sendMessage(msg);
    }

}
