package com.example.myapplication.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.example.myapplication.MainActivity;
import com.example.myapplication.R;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

public class utils {

    public static void setOpen(boolean flag){
        if (flag){
            // 开启接收
            MainActivity.imageView.setImageResource(R.drawable.waiting);
            MainActivity.openOrClose.setText(R.string.close);
        } else {
            MainActivity.imageView.setImageResource(R.drawable.disconnect);
            MainActivity.openOrClose.setText(R.string.open);
        }
    }

    public static void setWorking(boolean flag, String src_type, String data){
        if (flag){
            MainActivity.isWorking = true;
            if (src_type.equals("1")){
                MainActivity.imageView.setImageBitmap(getBase64Bitmap(data));
            }
            if (src_type.equals("2")){
                MainActivity.imageView.setImageBitmap(getUrlBitmap(data));
            }
        } else {
            MainActivity.isWorking = false;
            MainActivity.imageView.setImageResource(R.drawable.waiting);
            MainActivity.editText.setText("");
        }
    }

    public static Bitmap getUrlBitmap(String url){
        Bitmap bitmap = null;

        try {
            URL iconUrl = new URL(url);
            URLConnection connection = iconUrl.openConnection();
            HttpURLConnection URLConnection = null;

            if (url.charAt(4) == ':'){
                URLConnection = (HttpURLConnection) connection;
            }

            if (url.charAt(4) == 's'){
                URLConnection = (HttpsURLConnection) connection;
            }

            int length = URLConnection.getContentLength();

            connection.connect();

            InputStream inputStream = connection.getInputStream();
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream, length);
            bitmap = BitmapFactory.decodeStream(bufferedInputStream);
            bufferedInputStream.close();
            inputStream.close();
        } catch (Exception e) {
            System.out.println(e.getCause().getMessage());
        }

        return bitmap;
    }

    public static Bitmap getBase64Bitmap(String base64){

        int index = base64.indexOf(",");

        byte[] decodeString = Base64.decode(base64.substring(index), Base64.DEFAULT);

        Bitmap decodeByte = BitmapFactory.decodeByteArray(decodeString, 0, decodeString.length);

        return decodeByte;
    }
}
