package com.uav.autodebit.Activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.uav.autodebit.R;
import com.uav.autodebit.override.UAVEditText;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.DialogInterface;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.DataAdapterVO;
import com.uav.autodebit.vo.OxigenQuestionsVO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LandlineBill extends AppCompatActivity implements View.OnClickListener {
    EditText amount,operator;
    ImageView back_activity_button;
    String operatorcode,operatorname=null;
    Button proceed;
    TextView fetchbill;
    CardView amountlayout;

    LinearLayout dynamicCardViewContainer;

    List<OxigenQuestionsVO> questionsVOS= new ArrayList<OxigenQuestionsVO>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landline_bill);
        getSupportActionBar().hide();
        amount=findViewById(R.id.amount);
        back_activity_button=findViewById(R.id.back_activity_button1);

        proceed=findViewById(R.id.proceed);
        fetchbill=findViewById(R.id.fetchbill);
        amountlayout=findViewById(R.id.amountlayout);
        operator=findViewById(R.id.operator);
        dynamicCardViewContainer =findViewById(R.id.dynamiccards);

        amountlayout.setVisibility(View.GONE);


        back_activity_button.setOnClickListener(this);
        proceed.setOnClickListener(this);
        fetchbill.setOnClickListener(this);


        operator.setClickable(false);

        operator.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(MotionEvent.ACTION_UP == motionEvent.getAction()) {
                    //startActivity(new Intent(Mobile_Prepaid_Recharge_Service.this,Listview_With_Image.class));
                    Intent intent =new Intent(LandlineBill.this, Listview_With_Image.class);
                    Gson gson = new Gson();
                    String data = gson.toJson(getDataList());
                    intent.putExtra("datalist", data);
                    intent.putExtra("title","Landline Operator");
                    startActivityForResult(intent,100);
                }
                return false;
            }
        });
    }

    public ArrayList<DataAdapterVO> getDataList(){
        ArrayList<DataAdapterVO> datalist = new ArrayList<>();
        String operator= Session.getSessionByKey(LandlineBill.this,Session.CACHE_LANDLINE_OPERATOR);
        try {
            JSONArray jsonArray =new JSONArray(operator);

            Log.w("dataoperator",jsonArray.toString());
            for(int i=0;i<jsonArray.length();i++){
                DataAdapterVO dataAdapterVO = new DataAdapterVO();
                JSONObject object =jsonArray.getJSONObject(i);
                dataAdapterVO.setText(object.getString("name"));
                dataAdapterVO.setQuestionsData(object.getString("questionsData"));
                // dataAdapterVO.setImagename(object.getString("serviceName").toLowerCase());
                dataAdapterVO.setAssociatedValue(object.getString("serviceName"));
                datalist.add(dataAdapterVO);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  datalist;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try{
            if(resultCode==RESULT_OK){
                switch (requestCode) {
                    case 100:
                        operatorname =data.getStringExtra("operatorname");
                        operatorcode=data.getStringExtra("operator");

                        amountlayout.setVisibility(View.VISIBLE);


                        DataAdapterVO dataAdapterVO = (DataAdapterVO) data.getSerializableExtra("datavo");
                        operator.setText(operatorname);
                        operator.setTag(operatorcode);

                        operator.setError(null);
                        amount.setError(null);
                        //Remove dynamic cards from the layout and arraylist

                        if(dynamicCardViewContainer.getChildCount()>0)
                            dynamicCardViewContainer.removeAllViews();

                        questionsVOS.clear();


                        //Create dynamic cards of edit text
                        if(dataAdapterVO.getQuestionsData() !=null){
                            JSONArray jsonArray = new JSONArray(dataAdapterVO.getQuestionsData());
                            for(int i=0; i<jsonArray.length(); i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                Gson gson = new Gson();
                                OxigenQuestionsVO oxigenQuestionsVO = gson.fromJson(jsonObject.toString(), OxigenQuestionsVO.class);

                                CardView cardView = Utility.getCardViewStyle(this);
                                EditText et = new EditText(new ContextThemeWrapper(LandlineBill.this,R.style.edittext));
                                et.getBackground().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

                                et.setCompoundDrawablesWithIntrinsicBounds(R.drawable.mobile,0,0,0);
                                et.setId(View.generateViewId());
                                et.setHint(oxigenQuestionsVO.getQuestionLabel());
                                cardView.addView(et);
                                dynamicCardViewContainer.addView(cardView);
                                if(oxigenQuestionsVO.getInstructions()!=null){
                                    TextView tv = Utility.getTextView(this, oxigenQuestionsVO.getInstructions());
                                    dynamicCardViewContainer.addView(tv);
                                }
                                oxigenQuestionsVO.setElementId(et.getId());
                                questionsVOS.add(oxigenQuestionsVO);
                            }
                        }

                        break;
                }
            }
        }catch (Exception e){
        }
    }

    @Override
    public void onClick(View view) {

        try {
            switch (view.getId()){
                case R.id.back_activity_button1:
                    finish();
                    break;
                case R.id.proceed:

                    if( validatefiled("proceed")){

                        JSONArray jsonArray =new JSONArray();
                        JSONObject object;


                        object =new JSONObject();
                        object.put("key","Operator");
                        object.put("value",operator.getText().toString());
                        jsonArray.put(object);


                        for(OxigenQuestionsVO oxigenQuestionsVO:questionsVOS){

                            EditText editText =(EditText) findViewById(oxigenQuestionsVO.getElementId());
                            editText.setError(null);

                            if(editText.getText().toString().equals("")){

                                editText.setError("this field is required");


                            }else if(oxigenQuestionsVO.getMinLength()!=null && (editText.getText().toString().length() < Integer.parseInt(oxigenQuestionsVO.getMinLength()))){
                                editText.setError(oxigenQuestionsVO.getMinLength());
                            }else if(oxigenQuestionsVO.getMaxLength()!=null && (editText.getText().toString().length() > Integer.parseInt(oxigenQuestionsVO.getMaxLength()))){
                                editText.setError(oxigenQuestionsVO.getMaxLength());
                            }


                            object =new JSONObject();
                            object.put("key",oxigenQuestionsVO.getQuestionLabel());
                            object.put("value",editText.getText().toString());
                            jsonArray.put(object);

                            oxigenQuestionsVO.getJsonKey();
                            editText.getText().toString();
                            Toast.makeText(this, ""+editText.getText().toString(), Toast.LENGTH_SHORT).show();
                        }

                        object =new JSONObject();
                        object.put("key","Amount");
                        object.put("value",amount.getText().toString());
                        jsonArray.put(object);

                        object =new JSONObject();
                        object.put("key","Convenience Fee" );
                        object.put("value","0");
                        jsonArray.put(object);

                        object =new JSONObject();
                        object.put("key","Total" );
                        object.put("value",amount.getText().toString());
                        jsonArray.put(object);



                        Utility.confirmationDialog(
                                new DialogInterface() {
                                    @Override
                                    public void confirm(Dialog dialog) {
                                        dialog.dismiss();

                                    }

                                    @Override
                                    public void modify(Dialog dialog) {
                                        dialog.dismiss();
                                    }
                                }, this, jsonArray, null,"Please Confirm Detail");
                    }

                    break;
                case R.id.fetchbill:
                    if( validatefiled("fetchbill")){
                        amount.setError(null);
                        operator.setError(null);

                        Toast.makeText(this, "sdfsd", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }catch (Exception e){

        }

    }

    public boolean validatefiled(String type){

        boolean valid=true;
        operator.setError(null);
        amount.setError(null);
        fetchbill.setVisibility(View.VISIBLE);

        if(operator.getText().toString().equals("")){
            operator.setError("this filed is required");
            valid=false;
        }


       if(type.equals("proceed")){
           if(amount.getText().toString().equals("")){
               amount.setError("this filed is required");
               valid=false;
           }
       }

       return valid;
    }
}
