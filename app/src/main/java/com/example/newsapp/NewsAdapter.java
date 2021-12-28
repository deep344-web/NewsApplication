package com.example.newsapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;


public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewholder> {

    ArrayList<NewsUtils> arrayList;
    OnNewsClickListner onNewsClickListner;
    public NewsAdapter(ArrayList<NewsUtils> arrayList, OnNewsClickListner onNewsClickListner){
        this.arrayList = arrayList;
        this.onNewsClickListner = onNewsClickListner;
    }
    @NonNull
    @Override
    public NewsViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.news_item, parent, false);
        return new NewsViewholder(view, onNewsClickListner);

    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewholder holder, int position) {

        NewsUtils newsUtils = arrayList.get(position);
        holder.news_title_view.setText(newsUtils.getTitle());


        ImageLoad imageLoad = new ImageLoad(holder.news_image_view, holder.ImageProgressBar);
        imageLoad.execute(newsUtils.getImgURL());
    }

    private class ImageLoad extends AsyncTask<String, Void, Bitmap>{

        ImageView imageView;
        ProgressBar imageProgress;


        public ImageLoad(ImageView imageView, ProgressBar progressBar){
            this.imageView = imageView;
            imageProgress = progressBar;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            imageProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            InputStream inputStream = null;
            try {
                inputStream = new URL(strings[0]).openStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imageProgress.setVisibility(View.INVISIBLE);
            imageView.setImageBitmap(bitmap);
        }
    }



    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class NewsViewholder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView news_image_view;
        TextView news_title_view;
        ProgressBar ImageProgressBar;

        OnNewsClickListner onNewsClickListner;
        public NewsViewholder(@NonNull View itemView, OnNewsClickListner onNewsClickListner) {
            super(itemView);
            news_image_view = itemView.findViewById(R.id.news_imageview);
            news_title_view = itemView.findViewById(R.id.news_title_textview);
            ImageProgressBar = itemView.findViewById(R.id.ImageProgress);

            this.onNewsClickListner = onNewsClickListner;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            onNewsClickListner.onclickListener(getAdapterPosition());
        }
    }

    public interface OnNewsClickListner{
        void onclickListener(int position);
    }

    public void setData(ArrayList<NewsUtils> data) {
        this.arrayList = data;
        notifyDataSetChanged();
    }
}
