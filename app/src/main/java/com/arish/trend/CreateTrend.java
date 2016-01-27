package com.arish.trend;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CreateTrend extends AppCompatActivity {

    private ImageView mImageView;
    private EditText mTrendTitle;
    private EditText mTrendDescription;

    //Constants for IMAGE CAPTURE
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int PICK_IMAGE_ACTIVITY_REQUEST_CODE = 200;
    private String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_trend);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mTrendTitle = (EditText)findViewById(R.id.trend_title);
        mTrendDescription = (EditText)findViewById(R.id.trend_description);
        mImageView=(ImageView)findViewById(R.id.trend_image);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup();
            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postAndPushTrend();
            }
        });
    }

    private void postAndPushTrend() {
        String thumbName= new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        Bitmap img=((BitmapDrawable)mImageView.getDrawable()).getBitmap();

        final ParseObject trendData = new ParseObject("TrendData");
        final ParseFile parseFile = new ParseFile(thumbName + "_thumb.jpg", getBytesFromBitmap(img));

        parseFile.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                trendData.put("trendImage", parseFile);
                trendData.put("trendTitle", mTrendTitle.getText().toString().trim());
                trendData.put("trendDescription", mTrendDescription.getText().toString().trim());
                trendData.put("upvotesCount", 0);
                trendData.put("userId",ParseUser.getCurrentUser().getObjectId());

                //Finally save all the user details
                trendData.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Toast.makeText(CreateTrend.this, "Successfully Uploaded", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(CreateTrend.this, CurrentTrendsActivity.class);
                        startActivity(i);
                        ParsePush newTrendPush = new ParsePush();
                        JSONObject object = new JSONObject();
                        try {
                            object.put("is_background", false);
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("message","Hello! Welcome to parse notifications.") ;
                            jsonObject.put("title", "Trend");
                            jsonObject.put("category", Constants.PUSH_FOR_POST);
                            object.put("data", jsonObject);
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }


                        newTrendPush.setData(object);
                        newTrendPush.sendInBackground();
                        finish();
                    }
                });
            }
        });

    }

    private void showPopup() {

        View v = (View)findViewById(R.id.trend_image);
        PopupMenu popup = new PopupMenu(CreateTrend.this, v);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.one) {
                    captureFromCamera();

                } else if (item.getItemId() == R.id.two) {
                    captureFromGallery();
                }
                return true;
            }

        });

        popup.show();//showing popup menu
    }
    public void captureFromCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
                Toast.makeText(this, "Image is not captured successfully. Try again", Toast.LENGTH_SHORT).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        }
    }

    public void captureFromGallery() {
        String strAvatarPrompt = "Choose an image:";
        Intent pickPhoto = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        pickPhoto.setType("image/*");
        startActivityForResult(
                Intent.createChooser(pickPhoto, strAvatarPrompt),
                PICK_IMAGE_ACTIVITY_REQUEST_CODE);

    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // Image captured and saved to fileUri specified in the Intent
                Uri imageUri = Uri.parse(mCurrentPhotoPath);
                Log.e("uri", imageUri.toString());
                if (imageUri != null) {
                        mImageView.setImageURI(imageUri);
                    mImageView.setAlpha(1f);
                        //  convertImagetoByteArray(imageUri);
                } else
                    Log.d("ZTESR", "yiui ioio ");
            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
            }
        }
        if (requestCode == PICK_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Uri photoUri = data.getData();
                if (photoUri != null) {
                        mImageView.setImageURI(photoUri);
                    mImageView.setAlpha(1f);
                    //  convertImagetoByteArray(photoUri);
                } else
                    Toast.makeText(this, "Null", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
            }
        }

    }

    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Bitmap newImage=Bitmap.createScaledBitmap(bitmap,480,(bitmap.getHeight()*480/bitmap.getWidth()),true);

        newImage.compress(Bitmap.CompressFormat.JPEG, 50, stream);
        return stream.toByteArray();
    }

}
