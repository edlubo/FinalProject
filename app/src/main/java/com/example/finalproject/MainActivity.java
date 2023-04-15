package com.example.finalproject;


import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.content.Intent;
import com.bumptech.glide.Glide;
import com.example.finalproject.api.ApiClient;
import com.example.finalproject.api.NasaApi;
import com.example.finalproject.api.NasaApiResponse;
import com.google.android.material.snackbar.Snackbar;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final int STORAGE_PERMISSION_REQUEST_CODE = 1;

    DatePicker datePicker;
    Button fetchImageButton;
    TextView dateTextView;
    TextView urlTextView;
    ImageView nasaImageView;
    Button saveImageButton;

    private String apiKey = "lTHbiPpbS17JpvyeXRJFbXYOSG65fRr2Dj1DSbga"; // Replace with your own API key

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        datePicker = findViewById(R.id.datePicker);
        fetchImageButton = findViewById(R.id.fetchImageButton);
        dateTextView = findViewById(R.id.dateTextView);
        urlTextView = findViewById(R.id.urlTextView);
        nasaImageView = findViewById(R.id.nasaImageView);
        saveImageButton = findViewById(R.id.saveImageButton);
        Button viewSavedImagesButton = findViewById(R.id.viewSavedImagesButton);
        //Button showSnackbarButton = findViewById(R.id.showSnackbarButton);

        fetchImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Fetching Image", Snackbar.LENGTH_LONG).show();
                fetchImageFromNasaApi();
            }
        });


        saveImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                saveImageToDevice();
            }
        });


        viewSavedImagesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SavedImagesActivity.class);
                startActivity(intent);
            }
        });


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION_REQUEST_CODE);
        }
        //Snackbar
       // Button button = findViewById(R.id.showSnackbarButton);
       // button.setOnClickListener(new View.OnClickListener() {
       //   @Override
        // public void onClick(View view) {
         //   Snackbar.make(view, "Button clicked", Snackbar.LENGTH_LONG).show();
          //  }
      //  });

    }

    private void fetchImageFromNasaApi() {
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year = datePicker.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String date = dateFormat.format(calendar.getTime());

        NasaApi api = ApiClient.getClient().create(NasaApi.class);
        Call<NasaApiResponse> call = api.getApod(apiKey, date);

        call.enqueue(new Callback<NasaApiResponse>() {
            @Override
            public void onResponse(Call<NasaApiResponse> call, Response<NasaApiResponse> response) {
                if (response.isSuccessful()) {
                    NasaApiResponse apiResponse = response.body();
                    if (apiResponse != null) {
                        dateTextView.setText("Date: " + apiResponse.getDate());
                        urlTextView.setText("URL: " + apiResponse.getUrl());
                        Glide.with(MainActivity.this)
                                .load(apiResponse.getUrl())
                                .into(nasaImageView);
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Failed to fetch image", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<NasaApiResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveImageToDevice() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED || Build.VERSION.SDK_INT >= 29) {
            Drawable drawable = nasaImageView.getDrawable();
            if (drawable instanceof BitmapDrawable) {
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(System.currentTimeMillis());
                String imageFileName = "JPEG_" + timeStamp + ".jpg";

                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, imageFileName);
                contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

                if (Build.VERSION.SDK_INT >= 29) {
                    contentValues.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/NASA Images");
                } else {
                    contentValues.put(MediaStore.Images.Media.DATA, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/NASA Images/" + imageFileName);
                }

                Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

                try (OutputStream out = getContentResolver().openOutputStream(uri)) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    Toast.makeText(MainActivity.this, "Image saved: " + uri.getPath(), Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Toast.makeText(MainActivity.this, "Error saving image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MainActivity.this, "No image to save", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(MainActivity.this, "Storage permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults); // Add this line

        if (requestCode == STORAGE_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Storage permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

}


