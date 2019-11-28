package com.uav.autodebit.Activity;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.uav.autodebit.R;
import com.uav.autodebit.adpater.ImageTextAdapter;
import com.uav.autodebit.override.ExpandableHeightListView;
import com.uav.autodebit.util.ExceptionHandler;
import com.uav.autodebit.vo.DataAdapterVO;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Listview_With_Image extends AppCompatActivity {

    ImageView back_activity_button;
    ExpandableHeightListView listView;
    String operatorcode,operatorname;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.activity_listview_with_image);
        getSupportActionBar().hide();

        TextView title=findViewById(R.id.title);


        Intent intent =getIntent();
        Gson gson = new Gson();
        Type type = new TypeToken<List<DataAdapterVO>>(){}.getType();
        final ArrayList<DataAdapterVO> dataAdapterVOS = gson.fromJson(intent.getStringExtra("datalist"), type);

        title.setText(intent.getStringExtra("title"));

        listView=findViewById(R.id.listview);
        listView.setExpanded(true);


        back_activity_button=findViewById(R.id.back_activity_button1);

        back_activity_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ImageTextAdapter myAdapter=new ImageTextAdapter(this, dataAdapterVOS, R.layout.round_image_with_text);
        listView.setAdapter(myAdapter);
      //  listView.setNestedScrollingEnabled(false);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // String s=(String)parent.getItemAtPosition(position);
                int i=position;
                DataAdapterVO dataAdapterVO = (DataAdapterVO)dataAdapterVOS.get(position);

                operatorcode=dataAdapterVO.getAssociatedValue();
                operatorname=dataAdapterVO.getText();

                Intent intent = new Intent();
                intent.putExtra("operator",operatorcode);
                intent.putExtra("operatorname",operatorname);
                intent.putExtra("datavo",dataAdapterVO);

                setResult(RESULT_OK,intent);
                finish() ;





            }
        });
    }











}
