package nus.iss.androidca;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.animation.GridLayoutAnimationController;
import android.widget.Button;
import android.widget.EditText;
import androidx.gridlayout.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    private EditText textInput;
    private Button fetch;
    //private ProgressBar bar;
    private String urlInput;
    //private GridLayout myGridLayout;
    private ImageView imv1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        textInput = findViewById(R.id.editText);
        fetch = findViewById(R.id.btnFetch);


        //bar = findViewById(R.id.progress_bar);
        //bar.setMax(100);


        fetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                urlInput = textInput.getText().toString();
                new Thread(new Runnable(){
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void run() {
                        List<Bitmap> bitmaps = scrapeUrlsForBitmaps(urlInput);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setViews(bitmaps);
                            }
                        });

                    }
                }).start();


            }
        });

        //myGridLayout = findViewById(R.id.grid_layout);


    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    protected List<Bitmap> scrapeUrlsForBitmaps(String urlInput){
        org.jsoup.nodes.Document doc = null;
        try {
            doc = Jsoup.connect(urlInput).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Elements elements = doc.getElementsByTag("img");
        List<String> urls = elements.stream()
                .map(x->x.absUrl("src"))
                .filter(x-> x.substring(x.length()-4).equals(".jpg"))
                .limit(20)
                .collect(Collectors.toList());

        List<Bitmap> bitmaps = new ArrayList<>();
        for(String imgURL : urls){
            try{
                URL url = new URL(imgURL);
                URLConnection conn = url.openConnection();
                InputStream input = conn.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                bitmaps.add(myBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return bitmaps;
    }


    protected boolean downloadImage(String imgURL, File file) {
        try {
            URL url = new URL(imgURL);
            URLConnection conn = url.openConnection();

            InputStream in = conn.getInputStream();
            FileOutputStream out = new FileOutputStream(file);

            byte[] buf = new byte[1024];
            int bytesRead = -1;
            while ((bytesRead = in.read(buf)) != -1) {
                out.write(buf, 0, bytesRead);
            }

            out.close();
            in.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    protected void setViews(List<Bitmap> myBitmaps) {
        //sorry if this is confusing, i kept the id name grid_layout so i don't have to change so much code
        imv1 = findViewById(R.id.grid_layout);
        imv1.setImageBitmap(myBitmaps.get(0));


// uncomment here, and in activity_main for the gridlayout

//        for (int i =0; i < myBitmaps.size(); i++){
//            ImageView imageview = new ImageView(this);
//            GridLayout.LayoutParams  layoutParams = new GridLayout.LayoutParams();
//            layoutParams.width = myGridLayout.getMeasuredWidth()/4;
//            layoutParams.height = myGridLayout.getMeasuredHeight()/5;
//            imageview.setLayoutParams(layoutParams);
//            imageview.setImageBitmap(myBitmaps.get(i));
//
//
//        }
    }

}