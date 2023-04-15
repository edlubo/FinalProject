package com.example.finalproject;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SavedImagesActivity extends AppCompatActivity {

    private ListView savedImagesListView;
    private List<ImageItem> savedImageItems;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_images);

        savedImagesListView = findViewById(R.id.savedImagesListView);

        // Load saved images from storage
        savedImageItems = loadImagesFromStorage();

        // Create an adapter to display saved images in the ListView
        SavedImagesAdapter adapter = new SavedImagesAdapter(this, savedImageItems);

        // Set the adapter on the ListView
        savedImagesListView.setAdapter(adapter);

        // Set the click listener for each item in the ListView
        savedImagesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the clicked ImageItem
                ImageItem clickedImage = savedImageItems.get(position);

                // Start the ViewImageActivity with the clicked ImageItem's URI
                Intent intent = new Intent(SavedImagesActivity.this, ViewImageActivity.class);
                intent.putExtra("imageUri", clickedImage.getUri().toString());
                startActivity(intent);
            }
        });
    }

    private List<ImageItem> loadImagesFromStorage() {
        List<ImageItem> imageItemList = new ArrayList<>();

        ContentResolver contentResolver = getContentResolver();
        Uri imageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME};
        String selection = MediaStore.Images.Media.DATA + " like ?";
        String[] selectionArgs = new String[]{"%Pictures/NASA Images%"};
        String sortOrder = MediaStore.Images.Media.DATE_TAKEN + " DESC";

        Cursor cursor = contentResolver.query(imageUri, projection, selection, selectionArgs, sortOrder);

        if (cursor != null) {
            int idColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
            int nameColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME);

            while (cursor.moveToNext()) {
                long id = cursor.getLong(idColumnIndex);
                String displayName = cursor.getString(nameColumnIndex);
                Uri uri = ContentUris.withAppendedId(imageUri, id);

                ImageItem imageItem = new ImageItem(displayName, uri);
                imageItemList.add(imageItem);
            }

            cursor.close();
        }

        return imageItemList;
    }

}
