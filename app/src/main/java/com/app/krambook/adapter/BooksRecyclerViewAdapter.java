package com.app.krambook.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.krambook.R;
import com.app.krambook.activity.BookDetailActivity;
import com.app.krambook.models.Homeitem;
import com.app.krambook.utils.ImageLoader;
import com.app.krambook.utils.ImageLoaderGrid;
import com.bumptech.glide.Glide;
import com.parse.ParseFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import customfonts.MyTextView;

/**
 * Created by nubxf5 on 07/17/2017.
 */

public class BooksRecyclerViewAdapter extends RecyclerView.Adapter<BooksRecyclerViewAdapter.BooksViewHolder> {

    boolean flag = true;
    Context context;
    ImageLoader imageLoader;
    ImageLoaderGrid userimageloader;
    private ParseFile image;
    private List<Homeitem> worldpopulationlist = null;
    private ArrayList<Homeitem> arraylist;
    private Random mRandom = new Random();
    BookDetailActivity bookDetailActivity;

    public BooksRecyclerViewAdapter(Context context, List<Homeitem> worldpopulationlist){
        this.context = context;
        this.worldpopulationlist = worldpopulationlist;
        this.arraylist = new ArrayList<Homeitem>();
        this.arraylist.addAll(worldpopulationlist);
        imageLoader = new ImageLoader(context);
        userimageloader = new ImageLoaderGrid(context);
        bookDetailActivity = new BookDetailActivity();

    }

    public class BooksViewHolder extends RecyclerView.ViewHolder{

        public ImageView productimageview;
        public MyTextView bookExpireDateView;
        private Context context;

        public BooksViewHolder(Context context, View itemView) {
            super(itemView);
            productimageview = (ImageView) itemView.findViewById(R.id.deal_imageView);
            bookExpireDateView = (MyTextView) itemView.findViewById(R.id.book_expire_date);
            this.context = context;
        }

    }

    @Override
    public BooksViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_viewitem, parent, false);
        return new BooksViewHolder(parent.getContext(), view);
    }

    @Override
    public void onBindViewHolder(BooksViewHolder holder, final int position) {
        Glide.with(context).load(worldpopulationlist.get(position).getIamgeURL()).into(holder.productimageview);

        holder.productimageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent zoom = new Intent(context, BookDetailActivity.class);
                zoom.putExtra("photoID", worldpopulationlist.get(position).getID());
                context.startActivity(zoom);

            }
        });

        //holder.bookExpireDateView.getLayoutParams().height = getRandomIntInRange(250,75);

    }

    @Override
    public int getItemCount() {
        if(worldpopulationlist.size() == 0){
            return 0;
        }else if(worldpopulationlist == null){
            return 0;
        }else{
            return worldpopulationlist.size();
        }
    }


    public Object getItem(int position) {
        return worldpopulationlist.get(position);
    }

    // Custom method to get a random number between a range
    protected int getRandomIntInRange(int max, int min){
        return mRandom.nextInt((max-min)+min)+min;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // Custom method to generate random HSV color
    protected int getRandomHSVColor(){
        // Generate a random hue value between 0 to 360
        int hue = mRandom.nextInt(361);
        // We make the color depth full
        float saturation = 1.0f;
        // We make a full bright color
        float value = 1.0f;
        // We avoid color transparency
        int alpha = 255;
        // Finally, generate the color
        int color = Color.HSVToColor(alpha, new float[]{hue, saturation, value});
        // Return the color
        return color;
    }

    public void addAll(List<Homeitem> latestBooks){
        worldpopulationlist.addAll(0, latestBooks);
        notifyDataSetChanged();
    }

    public void removeItem(List<Homeitem> latestBooks){
        worldpopulationlist.remove(latestBooks);
        notifyDataSetChanged();
    }

    public void removeAt(int position) {
        worldpopulationlist.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, worldpopulationlist.size());
        notifyDataSetChanged();
    }
}
