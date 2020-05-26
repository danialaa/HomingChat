package com.example.homing.models.helpers;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;

import java.io.File;
import java.util.Objects;

public class S3Helper {
    private static String bucketName = "homingBucket";

    public static Uri downloadImage(String id, Context context) {
        AmazonS3Client s3Client = new AmazonS3Client(CognitoHelper.getINSTANCE(context).getCredentialsProvider());
        final Uri[] imageUri = {null};

        try {
            File outputDir = context.getCacheDir();
            final File tempCacheFile = File.createTempFile("images", "extension", outputDir);

            TransferUtility transferUtility = TransferUtility.builder().context(context.getApplicationContext())
                    .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                    .s3Client(s3Client).build();

            TransferObserver downloadObserver = transferUtility.download(id, tempCacheFile);
            downloadObserver.setTransferListener(new TransferListener() {
                @Override
                public void onStateChanged(int id, TransferState state) {
                    if (TransferState.COMPLETED == state) {
                        Log.d("AWS S3", "Image downloaded successfully");

                        imageUri[0] = Uri.fromFile(new File(tempCacheFile.getAbsolutePath()));
                    }
                }

                @Override
                public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                    float uploadingPercentage = ((float)bytesCurrent / (float)bytesTotal) * 100;
                    int percentageDone = (int)uploadingPercentage;

                    Log.d("AWS S3", "Downloading image is at " + percentageDone + "%");
                }

                @Override
                public void onError(int id, Exception ex) {
                    Log.d("AWS S3", "Downloading image error " + ex.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return imageUri[0];
    }

    public static void uploadImage(Uri imageUri, String id, Context context) {
        AmazonS3Client s3Client = new AmazonS3Client(CognitoHelper.getINSTANCE(context).getCredentialsProvider());

        try {
            File file = new File(getPath(context, imageUri));
            Log.d("lol", file.getAbsolutePath());
            s3Client.putObject(bucketName, id, file);
        } catch (AmazonServiceException e) {
            Log.d("AWS S3", e.getErrorMessage());
        }
    }

    private static String getPath(Context context, Uri uri) {
        String result = null;
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);

        assert cursor != null;
        if (cursor.moveToNext()) {
            int col = cursor.getColumnIndexOrThrow(projection[0]);
            result = cursor.getString(col);
        }

        cursor.close();

        return result;
    }

//    public static void uploadImage(Uri imageUri, String id, Context context) {
//        File file = new File(Objects.requireNonNull(imageUri.getPath()));
//        AmazonS3Client s3Client = new AmazonS3Client(CognitoHelper.getINSTANCE(context).getCredentialsProvider());
//        TransferUtility transferUtility = TransferUtility.builder().context(context.getApplicationContext())
//                .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
//                .s3Client(s3Client).build();
//
//        TransferObserver uploadObserver = transferUtility.upload(id, file);
//        uploadObserver.setTransferListener(new TransferListener() {
//            @Override
//            public void onStateChanged(int id, TransferState state) {
//                if (TransferState.COMPLETED == state) {
//                    Log.d("AWS S3", "Image uploaded successfully");
//                }
//            }
//
//            @Override
//            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
//                float uploadingPercentage = ((float)bytesCurrent / (float)bytesTotal) * 100;
//                int percentageDone = (int)uploadingPercentage;
//
//                Log.d("AWS S3", "Uploading image is at " + percentageDone + "%");
//            }
//
//            @Override
//            public void onError(int id, Exception ex) {
//                Log.d("AWS S3", "Uploading image error " + ex.getMessage());
//            }
//        });
//    }
}
