package com.uav.autodebit.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.uav.autodebit.BO.BannerBO;
import com.uav.autodebit.R;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.BitmapInterface;
import com.uav.autodebit.util.DiskLruImageCache;
import com.uav.autodebit.util.DownloadTask;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.util.Utils_Cache;
import com.uav.autodebit.vo.BannerVO;
import com.uav.autodebit.vo.BitmapVO;
import com.uav.autodebit.vo.LocalCacheVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class Splash_Screen extends AppCompatActivity implements BitmapInterface {
    ProgressBar progressBar;

    List<BannerVO> slBannerVos= new ArrayList<>();
    List<BannerVO> plBannerVos =new ArrayList<>();
    List<BitmapVO> imageVos =new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash__screen);


        ImageView imageView = (ImageView) findViewById( R.id.appstarticon);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        startDownloadCache();

    }



    private  void dowork() throws InterruptedException {
        for(int i=0; i<100;i+=10){
            Thread.sleep(100);
            progressBar.setProgress(i);

        }
    }

    private  void startNextActivity(){


        Log.e("isuse",Session.getSessionByKey_BoolenValue(Splash_Screen.this,Session.CACHE_IS_NEW_USER).toString());

        if(!Session.getSessionByKey_BoolenValue(Splash_Screen.this,Session.CACHE_IS_NEW_USER)){
            if(!Session.check_Exists_key(Splash_Screen.this,Session.CACHE_CUSTOMER) ){

                Intent intent = new Intent(this, Login.class);
                finish();
                startActivity(intent);
            }else {
                loadHomeActivity();
            }
        }else {
            Intent intent = new Intent(this, User_Registration.class);
            startActivity(intent);
            finish();
        }




    }

    private  void loadHomeActivity(){
        Intent intent=new Intent(Splash_Screen.this,Login.class);
        startActivity(intent);
        finish();
    }



    private void startDownloadCache(){
        try {
            VolleyUtils.makeJsonObjectRequest(this, BannerBO.getLocalCache(), new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                    Utility.alertDialog(Splash_Screen.this,"Alert!","Something went wrong, Please try again!","Ok");
                }
                @Override
                public void onResponse(Object resp) throws JSONException {
                    JSONObject response = (JSONObject) resp;

                    Gson gson = new Gson();
                    LocalCacheVO localCacheVO = gson.fromJson(response.toString(), LocalCacheVO.class);
                    slBannerVos=localCacheVO.getBanners();

                    Session.set_Data_Sharedprefence(Splash_Screen.this,Session.MOBILE_OPERATOR_LIST,localCacheVO.getMobileOperator());
                    Session.set_Data_Sharedprefence(Splash_Screen.this,Session.MOBILE_STATE_LIST,localCacheVO.getMobileStateRegion());
                    Session.set_Data_Sharedprefence(Splash_Screen.this,Session.DTH_OPERATOR_LIST,localCacheVO.getDthOperator());
                    Session.set_Data_Sharedprefence(Splash_Screen.this,Session.CACHE_LANDLINE_OPERATOR,localCacheVO.getLandline());
                    Session.set_Data_Sharedprefence(Splash_Screen.this,Session.CACHE_BROADBAND_OPERATOR,localCacheVO.getBoradBand());
                    Session.set_Data_Sharedprefence(Splash_Screen.this,Session.CACHE_WATER_OPERATOR,localCacheVO.getWater());

                    Session.set_Data_Sharedprefence(Splash_Screen.this,Session.CACHE_GAS_OPERATOR,localCacheVO.getGas());
                    Session.set_Data_Sharedprefence(Splash_Screen.this,Session.CACHE_ELECTRICITY_OPERATOR,localCacheVO.getElectricity());

                    Session.set_Data_Sharedprefence(Splash_Screen.this, Session.LOCAL_CACHE,response.toString());

                    managecachedata();






                  /*  List<BitmapVO> bitmapVOs=new ArrayList<>();
                    for(int i=0 ; i<slBannerVos.size() ; i++){
                        BitmapVO bitmapVO =new BitmapVO();
                        bitmapVO.setURL(slBannerVos.get(i).getBannerImage());
                        bitmapVO.setFileName(Utils_Cache.BANNER_PREFIX+i);
                        bitmapVO.setImageView(null);
                        bitmapVO.setLocalCacheFolder( Utils_Cache.CACHE_FILEPATH_BANNER);
                        bitmapVO.setLocalCacheFolderSize(Utils_Cache.CACHE_FILE_SIZE);
                        bitmapVO.setImageFormat(Bitmap.CompressFormat.PNG);
                        bitmapVO.setImageQuality(100);
                        bitmapVOs.add(bitmapVO);
                    }
                    */
                    if(imageVos.size()>0){
                        new DownloadTask(  Splash_Screen.this, Splash_Screen.this ,imageVos);
                    }else {
                        startNextActivity();
                    }





                }
            });
        } catch (Exception e) {
            Utility.exceptionAlertDialog(Splash_Screen.this,"Alert!","Something went wrong, Please try again!","Report",Utility.getStackTrace(e));
        }
    }


    public void managecachedata(){

        new DiskLruImageCache(Splash_Screen.this, Utils_Cache.CACHE_FILEPATH_BANNER,Utils_Cache.CACHE_FILE_SIZE,null ,100);


        JsonArray bannerList = new JsonArray();
        if( !Session.check_Exists_key(Splash_Screen.this,Session.BANNER_LIST)){
            for (BannerVO bannerVO : slBannerVos){
                 setBitmap(bannerVO);
            }

        }else{
           List<BannerVO>  sessBannerVOs = (List<BannerVO>) new Gson().fromJson(Session.getSessionByKey(this,Session.BANNER_LIST),new TypeToken<List<BannerVO>>() {}.getType());
            for (BannerVO bannerVO : slBannerVos){
                    boolean isfound= false;
                    for(BannerVO sessBannerVO: sessBannerVOs){
                        if(bannerVO.getBannerId() == sessBannerVO.getBannerId()){
                            isfound=true;
                            if(!bannerVO.getUpdateAt().equals(sessBannerVO.getUpdateAt())){
                                setBitmap(bannerVO);
                            }else if( !DiskLruImageCache.containsKey(Utils_Cache.BANNER_PREFIX+sessBannerVO.getBannerId())) {
                                setBitmap(bannerVO);
                            }
                        }
                    }
                    if(!isfound){
                        setBitmap(bannerVO);

                    }
            }

        }

         bannerList = (JsonArray) new Gson().toJsonTree(slBannerVos,
                new TypeToken<List<BannerVO>>() {
                }.getType());
        Session.set_Data_Sharedprefence(this,Session.BANNER_LIST,bannerList.toString());
    }


    @Override
    public void downloadComplete(List<BitmapVO> bitmapVOs) {
            startNextActivity();
    }

    @Override
    public void error(String error) {

    }


    private void setBitmap(BannerVO bannerVO){
        BitmapVO bitmapVO =new BitmapVO();
        bitmapVO.setURL(bannerVO.getBannerImage());
        bitmapVO.setFileName(Utils_Cache.BANNER_PREFIX+bannerVO.getBannerId());
        bitmapVO.setImageView(null);
        bitmapVO.setLocalCacheFolder( Utils_Cache.CACHE_FILEPATH_BANNER);
        bitmapVO.setLocalCacheFolderSize(Utils_Cache.CACHE_FILE_SIZE);
        bitmapVO.setImageFormat(Bitmap.CompressFormat.PNG);
        bitmapVO.setImageQuality(100);
        imageVos.add(bitmapVO);
    }
}
