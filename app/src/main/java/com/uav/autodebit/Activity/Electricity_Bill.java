package com.uav.autodebit.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.uav.autodebit.BO.Electricity_BillBO;
import com.uav.autodebit.BO.MetroBO;
import com.uav.autodebit.R;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.vo.CustomerVO;
import com.uav.autodebit.vo.DMRC_Customer_CardVO;
import com.uav.autodebit.vo.DataAdapterVO;
import com.uav.autodebit.vo.OxigenQuestionsVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Electricity_Bill extends AppCompatActivity  implements View.OnClickListener {

    EditText amount,operator;
    ImageView back_activity_button;
    String operatorcode,operatorname=null;
    Button proceed;
    TextView fetchbill;
    CardView amountlayout;

    LinearLayout dynamicCardViewContainer;

    List<OxigenQuestionsVO> questionsVOS= new ArrayList<OxigenQuestionsVO>();

    boolean valid=true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_electricity__bill);
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
                    Intent intent =new Intent(Electricity_Bill.this, Listview_With_Image.class);
                    Gson gson = new Gson();
                    String data = gson.toJson(getDataList());
                    intent.putExtra("datalist", data);
                    intent.putExtra("title","Operator");
                    startActivityForResult(intent,100);
                }
                return false;
            }
        });
    }

    public ArrayList<DataAdapterVO> getDataList(){
        ArrayList<DataAdapterVO> datalist = new ArrayList<>();
        String operator= Session.getSessionByKey(Electricity_Bill.this,Session.CACHE_ELECTRICITY_OPERATOR);
        try {
            JSONArray jsonArray =new JSONArray(operator);

            Log.w("dataoperator",jsonArray.toString());
            for(int i=0;i<jsonArray.length();i++){
                DataAdapterVO dataAdapterVO = new DataAdapterVO();
                JSONObject object =jsonArray.getJSONObject(i);
                dataAdapterVO.setText(object.getString("name"));
                dataAdapterVO.setQuestionsData(object.getString("questionsData"));
                // dataAdapterVO.setImagename(object.getString("serviceName").toLowerCase());
                dataAdapterVO.setAssociatedValue(object.getString("service"));
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

                        if(dynamicCardViewContainer.getChildCount()>0) dynamicCardViewContainer.removeAllViews();
                        questionsVOS.clear();


                        //Create dynamic cards of edit text
                        if(dataAdapterVO.getQuestionsData() !=null){
                            JSONArray jsonArray = new JSONArray(dataAdapterVO.getQuestionsData());
                            for(int i=0; i<jsonArray.length(); i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                Gson gson = new Gson();
                                OxigenQuestionsVO oxigenQuestionsVO = gson.fromJson(jsonObject.toString(), OxigenQuestionsVO.class);

                                CardView cardView = Utility.getCardViewStyle(this);
                               // EditText et = new EditText(new ContextThemeWrapper(this,R.style.edittext));

                                EditText et = Utility.getEditText(Electricity_Bill.this);
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
            e.printStackTrace();
            Utility.exceptionAlertDialog(Electricity_Bill.this,"Alert!","Something went wrong, Please try again!","Report",Utility.getStackTrace(e));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back_activity_button1:
                finish();
                break;
            case R.id.proceed:

                try {
                        valid=true;

                        JSONObject dataarray=getQuestionLabelDate(true);
                        if(!valid)return;

                        JSONObject jsonObject =new JSONObject();
                        jsonObject.put("operatorcode",operatorcode);
                        jsonObject.put("amount",amount.getText().toString());
                        jsonObject.put("questionLabelDate",dataarray.toString());

                        proceedRecharge(jsonObject);

                }catch (Exception e){
                    e.printStackTrace();
                    Utility.exceptionAlertDialog(Electricity_Bill.this,"Alert!","Something went wrong, Please try again!","Report",Utility.getStackTrace(e));

                }


                break;
            case R.id.fetchbill:
                try {
                    JSONObject dataarray=getQuestionLabelDate(false);
                    if(!valid)return;
                    JSONObject jsonObject =new JSONObject();
                    jsonObject.put("operatorcode",operatorcode);
                    jsonObject.put("questionLabelData",dataarray);

                    proceedFetchBill(jsonObject);

                }catch (Exception e){
                    e.printStackTrace();
                    Utility.exceptionAlertDialog(Electricity_Bill.this,"Alert!","Something went wrong, Please try again!","Report",Utility.getStackTrace(e));
                }
                valid=true;
                break;
        }
    }

    private JSONObject getQuestionLabelDate(boolean fetchBill) throws Exception{
        if(fetchBill){
            if(amount.getText().toString().equals("")){
                amount.setError("this filed is required");
                valid=false;
            }
        }

        if(operator.getText().toString().equals("")){
            operator.setError("this filed is required");
            valid=false;
        }



        JSONObject jsonObject =new JSONObject();
        for(OxigenQuestionsVO oxigenQuestionsVO:questionsVOS){

            EditText editText =(EditText) findViewById(oxigenQuestionsVO.getElementId());
            editText.setError(null);
            if(editText.getText().toString().equals("")){

                editText.setError(  Utility.getErrorSpannableStringDynamicEditText(this, "this field is required"));
                valid=false;
            }else if(oxigenQuestionsVO.getMinLength()!=null && (editText.getText().toString().length() < Integer.parseInt(oxigenQuestionsVO.getMinLength()))){
                editText.setError(oxigenQuestionsVO.getMinLength());
                valid=false;
            }else if(oxigenQuestionsVO.getMaxLength()!=null && (editText.getText().toString().length() > Integer.parseInt(oxigenQuestionsVO.getMaxLength()))){
                editText.setError(oxigenQuestionsVO.getMaxLength());
                valid=false;
            }

            jsonObject.put(oxigenQuestionsVO.getQuestionLabel(),editText.getText().toString());
            //oxigenQuestionsVO.getJsonKey();
            //editText.getText().toString();

        }
        return jsonObject;
    }


    private  void proceedRecharge(JSONObject jsonObject){

    }



    private void proceedFetchBill(JSONObject jsonObject) throws Exception{
        Log.w("proceedFetchBill",jsonObject.toString());

        try {
            Gson gson =new Gson();

            HashMap<String, Object> params = new HashMap<String, Object>();
            ConnectionVO connectionVO = Electricity_BillBO.oxiFetchBill();

            CustomerVO customerVO =new CustomerVO();
            customerVO.setCustomerId(Integer.parseInt(Session.getCustomerId(Electricity_Bill.this)));
            jsonObject.put("customer",customerVO);
            params.put("volley", gson.toJson(jsonObject));
            connectionVO.setParams(params);

            VolleyUtils.makeJsonObjectRequest(Electricity_Bill.this,connectionVO, new VolleyResponseListener() {
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

                        Utility.showSingleButtonDialog(Electricity_Bill.this,"Error !",sb.toString(),false);
                    }else {


                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Utility.exceptionAlertDialog(Electricity_Bill.this,"Alert!","Something went wrong, Please try again!","Report",Utility.getStackTrace(e));

        }
    }
}
