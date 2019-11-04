package com.uav.autodebit.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.uav.autodebit.BO.SignUpBO;
import com.uav.autodebit.R;
import com.uav.autodebit.constant.ApplicationConstant;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.DialogInterface;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.vo.CustomerStatusVO;
import com.uav.autodebit.vo.CustomerVO;
import com.uav.autodebit.vo.OTPVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class User_Registration extends AppCompatActivity {
    Button otpgeneratebtn;
    EditText username,userphone,useremail;
    Intent intent=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user__registration);
        getSupportActionBar().hide();

        username=findViewById(R.id.username);
        userphone=findViewById(R.id.userphone);
        useremail=findViewById(R.id.useremail);
        otpgeneratebtn=findViewById(R.id.otpgeneratebtn);



        otpgeneratebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean inputvalid=true;
                if(username.getText().toString().equals("")){
                    username.setError("this filed is required");
                    inputvalid=false;
                }
                if(userphone.getText().toString().equals("")){
                    userphone.setError("this filed is required");
                    inputvalid=false;
                }
                if(useremail.getText().toString().equals("")){
                    useremail.setError("this filed is required");
                    inputvalid=false;
                }

                if(!useremail.getText().toString().equals("") && Utility.validatePattern(useremail.getText().toString().trim(), ApplicationConstant.EMAIL_VALIDATION)!=null){

                    useremail.setError(Utility.validatePattern(useremail.getText().toString().trim(),ApplicationConstant.EMAIL_VALIDATION));
                    inputvalid=false;
                }
                if (!userphone.getText().toString().equals("") &&  Utility.validatePattern(userphone.getText().toString().trim(),ApplicationConstant.MOBILENO_VALIDATION)!=null){
                    userphone.setError(Utility.validatePattern(userphone.getText().toString().trim(),ApplicationConstant.MOBILENO_VALIDATION));
                    inputvalid=false;
                }

                if(!inputvalid) return;

                verifydialog();
                //signuser();


            }
        });
    }





    public void verifydialog(){

        try{
            JSONArray jsonArray=new JSONArray();
            JSONObject object =new JSONObject();
            object.put("key","Name");
            object.put("value",username.getText().toString());
            jsonArray.put(object);

            object =new JSONObject();
            object.put("key","Email");
            object.put("value",useremail.getText().toString());
            jsonArray.put(object);

            object =new JSONObject();
            object.put("key","Mobile Number");
            object.put("value",userphone.getText().toString());
            jsonArray.put(object);


            Utility.confirmationDialog(new DialogInterface() {
                @Override
                public void confirm(Dialog dialog) {
                    dialog.dismiss();
                    signuser();
                }
                @Override
                public void modify(Dialog dialog) {
                    dialog.dismiss();
                }

            },User_Registration.this,jsonArray,null,"Please Confirm Detail");
        }catch (Exception e){
            Utility.exceptionAlertDialog(User_Registration.this,"Alert!","Something went wrong, Please try again!","Report",Utility.getStackTrace(e));
        }
    }




    public  void signuser(){


        HashMap<String, Object> params = new HashMap<String, Object>();
        ConnectionVO connectionVO = SignUpBO.signuser();

        CustomerVO customerVO =new CustomerVO();
        customerVO.setEmailId(useremail.getText().toString().trim());
        customerVO.setMobileNumber(userphone.getText().toString().trim());
        customerVO.setName(username.getText().toString().trim());

        Gson gson = new Gson();
        String json = gson.toJson(customerVO);
        params.put("volley", json);
        connectionVO.setParams(params);


        VolleyUtils.makeJsonObjectRequest(this,connectionVO, new VolleyResponseListener() {
            @Override
            public void onError(String message) {
            }
            @Override
            public void onResponse(Object resp) throws JSONException {
                JSONObject response = (JSONObject) resp;

                Gson gson = new Gson();
                CustomerVO customerVO = gson.fromJson(response.toString(), CustomerVO.class);


                Log.w("responsesignup",response.toString());
                if(customerVO.getStatusCode().equals("400")){
                    ArrayList error = (ArrayList) customerVO.getErrorMsgs();
                    StringBuilder sb = new StringBuilder();
                    for(int i=0; i<error.size(); i++){
                        sb.append(error.get(i)).append("\n");
                    }
                    Utility.alertDialog(User_Registration.this,"Alert",sb.toString(),"Ok");

                }else {
                    Session.set_Data_Sharedprefence_BoolenvValue(User_Registration.this,Session.CACHE_IS_NEW_USER,false);


                    customerVO.setUserid(userphone.getText().toString().trim());
                    customerVO.setLoginType("mobile");

                    if(customerVO.getStatus().getStatusId().equals(CustomerStatusVO.CREATED)){
                        otpverifyactivity(customerVO);
                    }else{
                        ArrayList error = (ArrayList) customerVO.getErrorMsgs();
                        StringBuilder sb = new StringBuilder();
                        for(int i=0; i<error.size(); i++){
                            sb.append(error.get(i)).append("\n");
                        }
                        Utility.activityAlertDialog(User_Registration.this,"Alert",sb.toString(),"Ok");

                    }
                }
            }
        });
    }

    public void otpverifyactivity(CustomerVO  customerVO){
        Gson gson = new Gson();


        Intent intent=new Intent(User_Registration.this,Verify_OTP.class);
        customerVO.setActionname("verifySignUp");
        customerVO.setAnonymousString(customerVO.getOtpExpiredMobile().toString());

        String json = gson.toJson(customerVO); // myObject - instance of MyObject
        intent.putExtra("resp",json);
        startActivityForResult(intent,100);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if(requestCode==100){
                if(data!=null){
                    if( Integer.parseInt(data.getStringExtra("key"))==(CustomerStatusVO.SIGNUP_MOBILE_OTP_VERIFIED)){
                        Intent intent =new Intent(User_Registration.this,Password.class);
                        intent.putExtra("customerid",data.getStringExtra("value"));
                        intent.putExtra("methodname","setCustomerPassword");
                        startActivity(intent);
                        finish();
                    }else if(Integer.parseInt(data.getStringExtra("key"))==(CustomerStatusVO.CUSTOMER_VERFIED)){
                        startActivity(new Intent(getApplicationContext(),Login.class));
                        finish();
                    }
                }

            }
        }
    }

}
