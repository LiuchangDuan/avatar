package com.example.demotouxiang;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private static int CAMERA_REQUEST_CODE = 1;

    private static int GALLERY_REQUEST_CODE = 2;

    private static int CROP_REQUEST_CODE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn_camera = (Button) findViewById(R.id.btn_camera);
        btn_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            }
        });

        Button btn_gallery = (Button) findViewById(R.id.btn_gallery);
        btn_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_REQUEST_CODE);
            }
        });

    }

    /**
     * 返回的是file类型的uri
     * @param bm
     * @return
     */
    private Uri saveBitmap(Bitmap bm) {
        File tempDir = new File(Environment.getExternalStorageDirectory() + "/com.example.demotouxiang");
        if (!tempDir.exists()) {
            tempDir.mkdir();
        }
        File img = new File(tempDir.getAbsolutePath() + "demotouxiang.png");
        try {
            FileOutputStream fos = new FileOutputStream(img);
            // 要压缩的格式，质量
            bm.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            return Uri.fromFile(img);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void startImageZoom(Uri uri) {
        Intent intent = new Intent(("com.android.camera.action.CROP"));
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true"); // 显示位置为可裁剪的
        intent.putExtra("aspectX", 1); // 宽高比例
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 150); // 宽高
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROP_REQUEST_CODE);
    }

    /**
     * file://类型的uri to content://类型的uri
     * @param uri
     * @return
     */
    private Uri convertUri(Uri uri) {
        InputStream is = null;
        try {
            is = getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(is); // InputStream to Bitmap
            is.close();
            return saveBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 当startActivityForResult结束之后
     * 再次返回该应用时会调用该方法
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST_CODE) {
            // 如果用户点击的是取消
            if (data == null) {
                return;
            } else {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    Bitmap bm = extras.getParcelable("data");
//                    ImageView imageView = (ImageView) findViewById(R.id.imageView);
//                    imageView.setImageBitmap(bm);
                    Uri uri = saveBitmap(bm);
                    startImageZoom(uri); // 必须是file类型的uri
                }
            }
        } else if (requestCode == GALLERY_REQUEST_CODE) {
            if (data == null) {
                return;
            }
            Uri uri;
            uri = data.getData();
//            Toast.makeText(MainActivity.this, uri.toString(), Toast.LENGTH_LONG).show(); // content://类型的uri图片
            Uri fileUri = convertUri(uri);
            startImageZoom(fileUri);
        } else if (requestCode == CROP_REQUEST_CODE) {
            if (data == null) {
                return;
            }
            Bundle extras = data.getExtras();
            Bitmap bm = extras.getParcelable("data");
            ImageView imageView = (ImageView) findViewById(R.id.imageView);
            imageView.setImageBitmap(bm);
//            sendImage(bm);
        }
    }

//    private void sendImage(Bitmap bm) {
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
//        byte[] bytes = stream.toByteArray();
//        String img = new String(Base64.encodeToString(bytes, Base64.DEFAULT));
//
//        AsyncHttpClient client = new AsyncHttpClient();
//        RequestParams params = new RequestParams();
//        params.add("img", img);
//        client.post("http://192.168.56.1/ImgUpload.php", params, new AsyncHttpResponseHandler() {
//            @Override
//            public void onSuccess(int i, org.apache.http.Header[] headers, byte[] bytes) {
//
//            }
//
//            @Override
//            public void onFailure(int i, org.apache.http.Header[] headers, byte[] bytes, Throwable throwable) {
//
//            }
//        });
//
//    }

}
