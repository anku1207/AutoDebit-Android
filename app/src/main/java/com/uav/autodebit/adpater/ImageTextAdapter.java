package com.uav.autodebit.adpater;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.uav.autodebit.R;
import com.uav.autodebit.override.UAVTextView;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.DataAdapterVO;


import java.util.ArrayList;

public class ImageTextAdapter extends BaseAdapter {
    private Context context;
    ArrayList<DataAdapterVO> dataList;
    private int design;
    private LayoutInflater layoutInflater;
    private int length;

    public ImageTextAdapter(Context context, ArrayList<DataAdapterVO> dataList, int design){
        this.context=context;
        this.dataList = dataList;
        this.design = design;
        layoutInflater=((Activity)context).getLayoutInflater();
        this.length = dataList.size();
    }
    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        convertView = LayoutInflater.from(context).
                    inflate(this.design, parent, false);

        DataAdapterVO  dataAdapterVO = (DataAdapterVO)dataList.get(position);

        if(dataAdapterVO!=null){
            ImageView imageView=(ImageView)convertView.findViewById(R.id.listimage);
            UAVTextView textView=(UAVTextView) convertView.findViewById(R.id.listtext);


            if(Utility.GetImage(context,dataAdapterVO.getImagename())!=null){
                imageView.setImageDrawable(Utility.GetImage(context,dataAdapterVO.getImagename()));
            }
            textView.setTxtAssociatedValue( dataAdapterVO.getAssociatedValue());

            textView.setText( dataAdapterVO.getText());
        }


        return convertView;
    }
}
