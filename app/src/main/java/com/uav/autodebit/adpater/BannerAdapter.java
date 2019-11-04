package com.uav.autodebit.adpater;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.uav.autodebit.Activity.Splash_Screen;
import com.uav.autodebit.R;
import com.uav.autodebit.androidFragment.Home_Menu;
import com.uav.autodebit.util.DiskLruImageCache;
import com.uav.autodebit.util.DownloadTask;
import com.uav.autodebit.util.Utils_Cache;
import com.uav.autodebit.vo.BannerVO;
import com.uav.autodebit.vo.ServiceTypeVO;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class BannerAdapter extends PagerAdapter {
    private Context context;
    private List<BannerVO> banners;


    public BannerAdapter(Context context, List<BannerVO> banners) {
        this.context = context;
        this.banners = banners;
    }

    @Override
    public int getCount() {
        return banners.size();
    }



    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {


        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        BannerVO bannerVO = banners.get(position);

        View view = inflater.inflate(R.layout.item_slider, null);

        ImageView imgView = (ImageView) view.findViewById(R.id.bannerImage);
        new DiskLruImageCache(context, Utils_Cache.CACHE_FILEPATH_BANNER,Utils_Cache.CACHE_FILE_SIZE, Bitmap.CompressFormat.PNG,100);
        imgView.setImageBitmap(DiskLruImageCache.containsKey(Utils_Cache.BANNER_PREFIX+bannerVO.getBannerId()) ? DiskLruImageCache.getBitmap(Utils_Cache.BANNER_PREFIX+bannerVO.getBannerId()) :null);

        container.addView(view);

        /*ViewPager viewPager = (ViewPager) container;
        viewPager.addView(view, 0);*/

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        /*ViewPager viewPager = (ViewPager) container;
        View view = (View) object;
        viewPager.removeView(view);*/
        container.removeView((View) object);
    }


}
