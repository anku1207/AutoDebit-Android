package com.uav.autodebit.Activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.uav.autodebit.BO.BannerBO;
import com.uav.autodebit.BO.MetroBO;
import com.uav.autodebit.R;
import com.uav.autodebit.adpater.BannerAdapter;
import com.uav.autodebit.adpater.UitilityAdapter;
import com.uav.autodebit.androidFragment.Home_Menu;
import com.uav.autodebit.androidFragment.Profile;
import com.uav.autodebit.constant.ApplicationConstant;
import com.uav.autodebit.override.UAVProgressDialog;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.BackgroundAsyncService;
import com.uav.autodebit.util.BackgroundServiceInterface;
import com.uav.autodebit.util.DialogInterface;
import com.uav.autodebit.util.ExceptionHandler;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.BannerVO;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.vo.CustomerStatusVO;
import com.uav.autodebit.vo.CustomerVO;
import com.uav.autodebit.vo.DMRC_Customer_CardVO;
import com.uav.autodebit.vo.LevelVO;
import com.uav.autodebit.vo.LocalCacheVO;
import com.uav.autodebit.vo.ServiceTypeVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.uav.autodebit.util.Utility.fromJson;

public class Home extends AppCompatActivity
        implements View.OnClickListener {

    SharedPreferences sharedPreferences;
    TextView mainwallet,profile,linked,all,wallet,regular,
            delinkservice,faqs,reportbug,condition,setting,logoutbtn;
    ImageView closemenuactivity;
    DrawerLayout drawer;
    BottomNavigationView navigation;
    public static ProgressDialog dialog;
    UAVProgressDialog uavProgressDialog ;
    /////19-10-2019

    ViewPager viewPager;
    TabLayout bannerIndicator;

    List<BannerVO> banners;
    Integer level=null;

    ImageView  allutilityservice;

    Context cntxt;
    UAVProgressDialog pd ;
    ServiceTypeVO selectedService ;

    RecyclerView recyclerView;
    public static String clickServiceId;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        String customername=Session.getCustomerName(Home.this);
        toolbar.setTitle(Utility.capitalize(customername));
        setSupportActionBar(toolbar);


        dialog=new ProgressDialog(Home.this);
        pd = new UAVProgressDialog(this);

        selectedService=null;
        level=null;







        try {
       //check customer level and start activity
        Gson gson =new Gson();
        CustomerVO customerVO = gson.fromJson(Session.getSessionByKey(Home.this,Session.CACHE_CUSTOMER), CustomerVO.class);
        if(customerVO.getLevel().getLevelId()<=2){
            if(customerVO.getLevel().getLevelId()==1){
                startActivity(new Intent(Home.this,PanVerification.class));
                finish();
                return;
            }else if(customerVO.getLevel().getLevelId()==2){
                startActivity(new Intent(Home.this,Credit_Score_Report.class));
                finish();
                return;
            }
        }

            // override local cache
            overrideLocalCache(customerVO);
        }catch (Exception e){
            Utility.exceptionAlertDialog(Home.this,"Alert!","Something went wrong, Please try again!","Report",Utility.getStackTrace(e));
        }

        mainwallet=findViewById(R.id.mainwallet);
        profile=findViewById(R.id.profile);
        linked=findViewById(R.id.linked);
        all=findViewById(R.id.all);


        //19-10-2019
        recyclerView=findViewById(R.id.recyclerview);
        allutilityservice=findViewById(R.id.allutilityservice);


        logoutbtn=findViewById(R.id.logoutbtn);


        wallet=findViewById(R.id.wallet);
        regular=findViewById(R.id.regular);
        delinkservice=findViewById(R.id.delinkservice);
        faqs=findViewById(R.id.faqs);

        reportbug=findViewById(R.id.reportbug);
        condition=findViewById(R.id.condition);
        setting=findViewById(R.id.setting);
        closemenuactivity=findViewById(R.id.closemenuactivity);

        mainwallet.setOnClickListener(this);
        profile.setOnClickListener(this);
        linked.setOnClickListener(this);
        all.setOnClickListener(this);
        wallet.setOnClickListener(this);
        regular.setOnClickListener(this);
        delinkservice.setOnClickListener(this);
        faqs.setOnClickListener(this);
        reportbug.setOnClickListener(this);
        condition.setOnClickListener(this);
        setting.setOnClickListener(this);
        closemenuactivity.setOnClickListener(this);



        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove(Session.CACHE_CUSTOMER);
                editor.remove(Session.CACHE_USER_LOGINID);
                editor.commit();
                startActivity(new Intent(Home.this,Login.class));
                finish();
            }
        });


        sharedPreferences = getSharedPreferences(ApplicationConstant.SHAREDPREFENCE, Context.MODE_PRIVATE);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


       // loadFragment(new Home_Menu());

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //show png image in bottom menu bar
       // navigation.setItemIconTintList(null);
       // navigation.setSelectedItemId(R.id.bottom_home);

        //19-10-2018
        loadDateInRecyclerView();


    }


    public void loadDateInRecyclerView(){
        //19-10-2018
        Gson gson = new Gson();
        LocalCacheVO  localCacheVO = gson.fromJson( Session.getSessionByKey(this, Session.LOCAL_CACHE), LocalCacheVO.class);

        /* Banner code started here*/
        viewPager=(ViewPager) findViewById(R.id.viewPager);
        bannerIndicator=(TabLayout) findViewById(R.id.indicator);

        banners = localCacheVO.getBanners();
        viewPager.setAdapter(new BannerAdapter(this, banners ));
        bannerIndicator.setupWithViewPager(viewPager, true);

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new SliderTimer(), 4000, 6000);


        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        recyclerView.setNestedScrollingEnabled(true);
        recyclerView.addItemDecoration(new DividerItemDecorator(4,2,false));

        List<ServiceTypeVO> utilityServices = localCacheVO.getUtilityBills();
        List<ServiceTypeVO> addservice =new ArrayList<>();


        String utilityService =localCacheVO.getUitiyServiceHomePage();
        String[] utilServiceArr =  utilityService.split(",");

        for(ServiceTypeVO utility:utilityServices){
            for(int c=0; c<utilServiceArr.length; c++ ){
                if( utilServiceArr[c].equals( utility.getServiceTypeId().toString()) ){
                    addservice.add(utility);
                }
            }
        }
        UitilityAdapter utility=new UitilityAdapter(this,addservice ,R.layout.two_tailes, pd);
        recyclerView.setAdapter(utility);

        List<ServiceTypeVO> mImgIds=new ArrayList<>();
        mImgIds=localCacheVO.getSerives();
        setHorizontalScrollView(mImgIds,R.id.id_servicegallery ,R.layout.services_gallery);
    }




    /*Banner slider*/
    private class SliderTimer extends TimerTask {

        @Override
        public void run() {
            Home.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (viewPager.getCurrentItem() < banners.size() - 1) {
                        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                    } else {
                        viewPager.setCurrentItem(0);
                    }
                }
            });
        }
    }


    /*set horizontal scroll view layout elementes*/
    private void setHorizontalScrollView(final List<ServiceTypeVO> dataList,  final int layout, final int activity ){
        LinearLayout mGallery = (LinearLayout) findViewById(layout);
        mGallery.removeAllViewsInLayout();
        level= Session.getCustomerLevel(this);
        for (final ServiceTypeVO serviceTypeVO: dataList){

            //View galView = mInflater.inflate( activity ,  mGallery, false);

            View galView = getLayoutInflater().inflate(activity, null);

            final LinearLayout activitylayout=galView.findViewById(R.id.layout_servicesgallery);
            activitylayout.setTag(serviceTypeVO.getServiceTypeId());
            ImageView img = (ImageView) galView.findViewById(R.id.id_index_gallery_item_image);
            final ImageView activeservice=galView.findViewById(R.id.serviceactive);
            img.setImageDrawable(Utility.GetImage(this,serviceTypeVO.getAppIcon()));

             if(serviceTypeVO.getAdopted()==1){
                activeservice.setVisibility(View.VISIBLE);
            }else {
                activeservice.setVisibility(View.GONE);
            }


            TextView txt = (TextView) galView.findViewById(R.id.id_index_gallery_item_text);
            txt.setText(serviceTypeVO.getTitle());

            mGallery.addView(galView);

            activitylayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activitylayout.setEnabled(false);
                    BackgroundAsyncService backgroundAsyncService = new BackgroundAsyncService(pd,true, new BackgroundServiceInterface() {
                        @Override
                        public void doInBackGround() {
                            Gson gson = new Gson();
                            LocalCacheVO  localCacheVO = gson.fromJson( Session.getSessionByKey(Home.this, Session.LOCAL_CACHE), LocalCacheVO.class);
                            List<ServiceTypeVO> serviceTypeVOS = localCacheVO.getSerives();

                            level= Session.getCustomerLevel(Home.this);
                            for(ServiceTypeVO serviceTypeVO :  serviceTypeVOS){
                                if(serviceTypeVO.getServiceTypeId()== Integer.parseInt(activitylayout.getTag().toString() ) ){
                                    selectedService=serviceTypeVO;
                                    break;
                                }
                            }
                        }

                        @Override
                        public void doPostExecute() {

                            activitylayout.setEnabled(true);

                            if(selectedService !=null && selectedService.getAdopted()!=null && selectedService.getAdopted()==1){
                                startService(activitylayout.getTag().toString());
                            }else {
                                clickServiceId=activitylayout.getTag().toString();
                                startActivityForResult(new Intent(Home.this, AdditionalService.class),200);
                            }
                        }
                    });
                    backgroundAsyncService.execute();
                }
            });
        }
    }

    public  void startService(String id){
        if(level< selectedService.getLevel().getLevelId()){
            pd.dismiss();
            level++;
            String[] changePass = {"Cancel","Next"};
            switch (level){
                case 2:
                    startActivity(new Intent(Home.this, PanVerification.class));
                    break;
                case 3:
                    startActivity(new Intent(Home.this, Credit_Score_Report.class));
                    break;
                case 4:
                    Utility.confirmationDialog(new DialogInterface() {
                        @Override
                        public void confirm(Dialog dialog) {
                            dialog.dismiss();
                            startActivity(new Intent(Home.this, Enach_Mandate.class));
                        }
                        @Override
                        public void modify(Dialog dialog) {
                            dialog.dismiss();
                        }
                    }, Home.this, null, "Bank Verification","Mandate",changePass);
                    break;
                case 5:
                    Utility.confirmationDialog(new DialogInterface() {
                        @Override
                        public void confirm(Dialog dialog) {
                            dialog.dismiss();
                            startActivity(new Intent(Home.this, Enach_Mandate.class));
                        }
                        @Override
                        public void modify(Dialog dialog) {
                            dialog.dismiss();
                        }
                    }, Home.this, null, "Bank Verification","Mandate",changePass);
                    break;
                case 6:

                    Utility.confirmationDialog(new DialogInterface() {
                        @Override
                        public void confirm(Dialog dialog) {
                            dialog.dismiss();
                            startActivity(new Intent(Home.this, SI_First_Data.class));
                        }
                        @Override
                        public void modify(Dialog dialog) {
                            dialog.dismiss();
                        }
                    }, Home.this, null, "SI Verification","Mandate",changePass);

                    break;
            }
        }else {
            switch (id) {
                case "1":
                    startActivity(new Intent(Home.this, IRCTC.class));
                    break;
                case "2":
                    dmrcCardRequest();
                    break;
                case "3":
                    startActivity(new Intent(Home.this, Hyd_Metro.class));
                    break;
                case "4":
                    startActivity(new Intent(Home.this, Mum_Metro.class));
                    break;
            }
        }
    }



    public void dmrcCardRequest(){
        try {
            HashMap<String, Object> params = new HashMap<String, Object>();
            ConnectionVO connectionVO = MetroBO.getDmrcCustomerList();

            CustomerVO customerVO=new CustomerVO();
            customerVO.setCustomerId(Integer.valueOf(Session.getCustomerId(Home.this)));
            Gson gson =new Gson();
            String json = gson.toJson(customerVO);
            params.put("volley", json);
            connectionVO.setParams(params);

            VolleyUtils.makeJsonObjectRequest(Home.this,connectionVO, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                }
                @Override
                public void onResponse(Object resp) throws JSONException {
                    JSONObject response = (JSONObject) resp;
                    Gson gson = new Gson();
                    DMRC_Customer_CardVO dmrc_customer_cardVO = gson.fromJson(response.toString(), DMRC_Customer_CardVO.class);

                    if(dmrc_customer_cardVO.getStatusCode().equals("400")){
                        ArrayList error = (ArrayList) dmrc_customer_cardVO.getErrorMsgs();
                        StringBuilder sb = new StringBuilder();
                        for(int i=0; i<error.size(); i++){
                            sb.append(error.get(i)).append("\n");
                        }
                        pd.dismiss();
                        // Utility.alertDialog(PanVerification.this,"Alert",sb.toString(),"Ok");
                        Utility.showSingleButtonDialog(Home.this,"Error !",sb.toString(),false);
                    }else {

                        Session.set_Data_Sharedprefence(Home.this,Session.CACHE_DMRC_MIN_CARD_CHARGE,dmrc_customer_cardVO.getAnonymousString());

                        if(dmrc_customer_cardVO.getDmrcCustomerList().size()>0){


                            Intent intent =new Intent(Home.this,DMRC_Cards_List.class);
                            intent.putExtra("dmrccard",Utility.toJson(dmrc_customer_cardVO.getDmrcCustomerList()));
                            startActivity(intent);
                        }else {
                            Intent intent =new Intent(Home.this,Dmrc_Card_Request.class);
                            intent.putExtra("onetimecharges",dmrc_customer_cardVO.getAnonymousString());
                            intent.putExtra("isdisable",true);
                            startActivity(intent);
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void overrideLocalCache(CustomerVO customerVO){
        Session.set_Data_Sharedprefence(Home.this, Session.LOCAL_CACHE,customerVO.getLocalCache());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(resultCode==RESULT_OK){
            if(requestCode==200){
                loadDateInRecyclerView();
                LocalCacheVO  localCacheVO = new Gson().fromJson( Session.getSessionByKey(this, Session.LOCAL_CACHE), LocalCacheVO.class);
                List<ServiceTypeVO> serviceTypeVOS =localCacheVO.getSerives();
                for(ServiceTypeVO serviceTypeVO :serviceTypeVOS){
                    if(serviceTypeVO.getServiceTypeId()==Integer.parseInt(clickServiceId)){
                        if(serviceTypeVO.getAdopted()==1){
                            startService(clickServiceId);
                        }
                    }
                }
               // loadFragment(new Home_Menu());
            }

            if(requestCode==100){
                loadDateInRecyclerView();
                LocalCacheVO  localCacheVO = new Gson().fromJson( Session.getSessionByKey(this, Session.LOCAL_CACHE), LocalCacheVO.class);
                List<ServiceTypeVO> serviceTypeVOS =localCacheVO.getUtilityBills();
                for(ServiceTypeVO serviceTypeVO :serviceTypeVOS){
                    if(serviceTypeVO.getServiceTypeId()==Integer.parseInt(clickServiceId)){
                        if(serviceTypeVO.getAdopted()==1 && level>=serviceTypeVO.getLevel().getLevelId()){
                            startUtillService(serviceTypeVO.getServiceTypeId());
                        }
                    }
                }
                // loadFragment(new Home_Menu());
            }
        }
    }

   public void startUtillService(final Integer serviceId){

       BackgroundAsyncService backgroundAsyncService = new BackgroundAsyncService(pd,true, new BackgroundServiceInterface() {
           @Override
           public void doInBackGround() {
               Intent intent;
               switch (serviceId){
                   case 5 :
                       intent =new Intent(Home.this, Mobile_Prepaid_Recharge_Service.class);
                       intent.putExtra("serviceid",serviceId.toString());
                       startActivity(intent);
                       break;
                   case 13 :
                       intent =new Intent(Home.this, DTH_Recharge_Service.class);
                       intent.putExtra("serviceid",serviceId.toString());
                       startActivity(intent);
                       break;
                   case 7 :
                       intent =new Intent(Home.this, LandlineBill.class);
                       intent.putExtra("serviceid",serviceId.toString());
                       startActivity(intent);
                       break;
                   case 8 :
                       intent =new Intent(Home.this, Broadband.class);
                       intent.putExtra("serviceid",serviceId.toString());
                       startActivity(intent);
                       break;
                   case 14 :
                       intent =new Intent(Home.this, Mobile_Postpaid.class);
                       intent.putExtra("serviceid",serviceId.toString());
                       startActivity(intent);
                       break;
                   case 12 :
                       intent =new Intent(Home.this, Water.class);
                       intent.putExtra("serviceid",serviceId.toString());
                       startActivity(intent);
                       break;
                   case 11 :
                       intent =new Intent(Home.this, Gas_Bill.class);
                       intent.putExtra("serviceid",serviceId.toString());
                       startActivity(intent);
                       break;
                   case 10:
                       intent =new Intent(Home.this, Electricity_Bill.class);
                       intent.putExtra("serviceid",serviceId.toString());
                       startActivity(intent);
                       break;
                   case 15:
                       intent =new Intent(Home.this, All_Service.class);
                       intent.putExtra("serviceid",serviceId.toString());
                       startActivity(intent);
                       break;
                   default:
                       pd.dismiss();
               }
           }

           @Override
           public void doPostExecute() {

           }
       });
       backgroundAsyncService.execute();

   }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            String[] buttons = {"Cancel","Ok"};

            Utility.confirmationDialog(new DialogInterface() {
                public String leftButton="Cancel";
                @Override
                public void confirm(Dialog dialog) {
                    dialog.dismiss();
                    // ActivityCompat.finishAffinity( Home.this);
                    finishAffinity();
                }
                @Override
                public void modify(Dialog dialog) {
                    dialog.dismiss();
                }
            },Home.this,null,"Do you want to exit","App Exit", buttons);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
    }


    BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment = null;
            switch (item.getItemId()) {
                case R.id.bottom_home:
                    //fragment=new Home_Menu();
                    break;
                case R.id.bottom_profile:
                   // fragment=new Profile();
                    startActivity(new Intent(Home.this,Profile_Activity.class));
                    finish();

                    break;
                case R.id.bottom_history:
                    Toast.makeText(Home.this, "bottom_history", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.bottom_help:
                    Toast.makeText(Home.this, "bottom_help", Toast.LENGTH_SHORT).show();
                    break;
            }
            return loadFragment(fragment);
        }
    };

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.mainwallet:
                Toast.makeText(this, "mainwallet", Toast.LENGTH_SHORT).show();
                break;
            case R.id.profile:
                startActivity(new Intent(this,Profile_Activity.class));
                break;

            case R.id.linked  :
                Toast.makeText(this, "linked", Toast.LENGTH_SHORT).show();
                break;
            case R.id.regular  :
                Toast.makeText(this, "regular", Toast.LENGTH_SHORT).show();
                break;
            case R.id.all  :
                startActivity(new Intent(this,All_Service.class));
                break;
            case R.id.wallet  :
                Toast.makeText(this, "wallet", Toast.LENGTH_SHORT).show();
                break;
            case R.id.delinkservice  :
                Toast.makeText(this, "delinkservice", Toast.LENGTH_SHORT).show();
                break;


            case R.id.faqs  :
                Toast.makeText(this, "faqs", Toast.LENGTH_SHORT).show();
                break;


            case R.id.reportbug  :
                Toast.makeText(this, "reportbug", Toast.LENGTH_SHORT).show();
                break;

            case R.id.condition  :
                Toast.makeText(this, "condition", Toast.LENGTH_SHORT).show();
                break;
            case R.id.setting  :
                Toast.makeText(this, "setting", Toast.LENGTH_SHORT).show();
                break;
            case R.id.closemenuactivity:
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
    }
}
