package com.example.finalproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class SavedImagesAdapter extends BaseAdapter {

    private final LayoutInflater inflater;
    private final List<ImageItem> savedImageItems;

    public SavedImagesAdapter(Context context, List<ImageItem> savedImageItems) {
        this.inflater = LayoutInflater.from(context);
        this.savedImageItems = savedImageItems;
    }

    @Override
    public int getCount() {
        return savedImageItems.size();
    }

    @Override
    public Object getItem(int position) {
        return savedImageItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder viewHolder;

        if (convertView == null) {
            view = inflater.inflate(R.layout.saved_image_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.savedImageNameTextView = view.findViewById(R.id.savedImageView);
            viewHolder.savedImageView = view.findViewById(R.id.savedImageView);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        ImageItem imageItem = savedImageItems.get(position);
        viewHolder.savedImageNameTextView.setText(imageItem.getName());

        Glide.with(viewHolder.savedImageView.getContext())
                .load(imageItem.getUri())
                .centerCrop()
                .into(viewHolder.savedImageView);

        return view;
    }

    private static class ViewHolder {
        private TextView savedImageNameTextView;
        private ImageView savedImageView;
    }
}