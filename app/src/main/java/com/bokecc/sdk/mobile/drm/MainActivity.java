package com.bokecc.sdk.mobile.drm;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;


/**
 * 破解CC Version 6 PCM加密
 * */

public class MainActivity extends AppCompatActivity {



    File file;
    public static String token = "127,14,127,127,15,125,8,122,122,14,8,1,120,122,14,8,124,122,0,120,122,14,9,0,11,123,12,125,124,1,125,124";
    /***
     * token自行抓包获取
     */

    private TextView mTextMessage;
    private Button mbtn_decrypt;
    private Button btn_openfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_openfile = (Button) findViewById(R.id.btn_openfile);
        btn_openfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent,1);
            }
        });

        mTextMessage = (TextView) findViewById(R.id.message);
        mbtn_decrypt = (Button) findViewById(R.id.btn_decrypt);
        mbtn_decrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                decryptVideo();
            }
        });

    }

    void decryptVideo() {
        //输出mp4路径
        String directory_path = Environment.getExternalStorageDirectory().getPath() + "/CCDownload/";
        File filedir = new File(directory_path);
        if (!filedir.exists()) {
            filedir.mkdirs();
        }

        if (file==null) {
            return;
        }
        String suffix = file.getName().substring(file.getName().lastIndexOf(".") + 1);
        if (!"pcm".equals(suffix)) {
            return;
        }

        String key = DESUtil.getDecryptString(token);

        String outputName = file.getName().substring(0, file.getName().length()-4);
        outputName = outputName+".mp4";

        File fileout = new File(Environment.getExternalStorageDirectory().getPath() + File.separator
                + "CCDownload" + File.separator
                + outputName);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(fileout);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        DataOutputStream dos = new DataOutputStream(fos);
        try {
            DecryptVideo.parseLocal(dos, file, key.getBytes());
            Toast.makeText(this, "解密成功", Toast.LENGTH_LONG);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {//是否选择，没选择就不会继续
            Uri uri = data.getData();//得到uri，后面就是将uri转化成file的过程。
            String filepath = uri.toString();

            if (filepath.contains("content")) {
                //content格式
                String[] proj = { MediaStore.Files.FileColumns.DATA };
                Cursor cursor = getContentResolver().query(uri, proj, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                filepath = cursor.getString(column_index);
            } else {
                //file格式
                filepath = Environment.getExternalStorageDirectory().getPath() + File.separator
                        + filepath.substring(15, filepath.length());

                //filepath = filepath.substring(7, filepath.length());
            }

            file = new File(filepath);

            if (file.isFile()) {
                mTextMessage.setText(file.toString() + "是一个文件");
            } else {
                mTextMessage.setText(file.toString() + "不是一个文件");
            }
            Toast.makeText(MainActivity.this, file.toString(), Toast.LENGTH_SHORT).show();


        }
    }
}
