package me.palr.palr_android;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import me.palr.palr_android.models.User;

/**
 * Created by maazali on 2016-11-20.
 */
public class ImagePickerActivity extends AppCompatActivity {
    private final static int SELECT_PICTURE = 1;

    private final Activity current = this;
    private ProgressDialog dialog = null;

    private final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        askForPermission();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_PICTURE) {
            if (resultCode == RESULT_OK) {
                setDefaultLayout();

                Uri selectedImageUri = data.getData();
                String wholeID = DocumentsContract.getDocumentId(selectedImageUri);
                Log.d("DEBUG", "Uploading file from URI: " + selectedImageUri.getPath());

                // Split at colon, use second item in the array
                String id = wholeID.split(":")[1];
                // where id is equal to
                String sel = MediaStore.Images.Media._ID + "=?";

                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        filePathColumn, sel, new String[]{ id }, null);

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

                String filePath = "";
                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(columnIndex);
                }

                cursor.close();
                Log.d("DEBUG", "Uploading file: " + filePath);
                startUpload(filePath);
            }
        }
    }

    private void setDefaultLayout() {
        setContentView(R.layout.activity_image_picker);
    }

    private void askForPermission() {
        if (ContextCompat.checkSelfPermission(this,
            Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        } else { // If app already has permission then bring up image picker
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
                }
            }
        }
    }

    private void startUpload(String filePath) {
        AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
            protected String doInBackground(String... paths) {
                Log.d("DEBUG", "Running upload task");


                // Upload to cloudinary
                PalrApplication app = (PalrApplication) getApplication();
                Cloudinary cloudinary = app.getCloudinary();
                File file = new File(paths[0]);
                @SuppressWarnings("rawtypes")
                Map cloudinaryResult;
                try {
                    // Cloudinary: Upload file using the retrieved signature and upload params
                    cloudinaryResult = cloudinary.uploader().upload(file, ObjectUtils.emptyMap());
                    Log.i("DEBUG", "Uploaded file: " + cloudinaryResult.toString());
                } catch (RuntimeException e) {
                    Log.e("ERROR", "Error uploading file: " + e);
                    return "Error uploading file: " + e.toString();
                } catch (IOException e) {
                    Log.e("ERROR", "Error uploading file: " + e);
                    return "Error uploading file: " + e.toString();
                }

                User curUser = app.getCurrentUser();
                curUser.setImageUrl((String)cloudinaryResult.get("secure_url"));
                return null;
            }

            protected void onPostExecute(String error) {
                if (dialog != null) {
                    dialog.dismiss();
                    dialog = null;
                }
                if (error == null) {
                    setResult(RESULT_OK);
                    finish();
                } else {
                    new AlertDialog.Builder(current)
                            .setTitle("Error")
                            .setMessage(error)
                            .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    finish();
                                }
                            })
                            .setCancelable(true)
                            .create().show();
                }
            }
        };
        dialog = ProgressDialog.show(this, "Uploading", "Uploading image");
        task.execute(filePath);
    }

}
