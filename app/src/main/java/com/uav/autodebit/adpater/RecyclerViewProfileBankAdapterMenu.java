package com.uav.autodebit.adpater;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.uav.autodebit.R;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.ServiceTypeVO;

import java.util.List;

/**
 * Created by anku on 10/26/2017.
 */
/*
recyclerview.adapter
recyclerview.viewholder
*/

public class RecyclerViewProfileBankAdapterMenu extends RecyclerView.Adapter<RecyclerViewProfileBankAdapterMenu.ProdectViewHolder>{
    Context mctx ;
    List<ServiceTypeVO> serviceTypeVOS;
    int Activityname;



    public RecyclerViewProfileBankAdapterMenu(Context mctx, List<ServiceTypeVO> serviceTypeVOS, int Activityname) {
        this.mctx = mctx;
        this.serviceTypeVOS = serviceTypeVOS;
        this.Activityname=Activityname;
    }

    @Override
    public ProdectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(Activityname, parent, false);
        return new ProdectViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(ProdectViewHolder holder, int position) {

         final ServiceTypeVO pro=serviceTypeVOS.get(position);
         holder.accountdetails.setText(pro.getTitle());
         holder.imageView.setImageDrawable(Utility.GetImage(mctx,pro.getAppIcon()));
         holder.mainlayout.setEnabled(true);

        /* holder.mailmenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               if(pro.getActivityname()!=null){
                   mctx.startActivity(new Intent(mctx,pro.getActivityname()));
               }else {
                   Toast.makeText(mctx, "Something is wrong ", Toast.LENGTH_SHORT).show();
               }


            }
        });*/



    }

    @Override
    public int getItemCount() {
        return serviceTypeVOS.size();
    }
    public  class ProdectViewHolder extends RecyclerView.ViewHolder {

        LinearLayout mainlayout;
        ImageView imageView;
        TextView accountdetails;
        public ProdectViewHolder(View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.imageView);
            accountdetails=itemView.findViewById(R.id.accountdetails);
            mainlayout=itemView.findViewById(R.id.mainlayout);

        }
    }
}
