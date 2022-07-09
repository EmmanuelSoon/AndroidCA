package nus.iss.androidca;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
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
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {

    private EditText textInput;
    private Button fetch;

    private ProgressDialog progressBar;
    private int progressBarStatus = 0;
    private Handler processBarHandler = new Handler();
    private int imgCount = 0;

    private String urlInput;
    //private GridLayout myGridLayout;
    //private ImageView imv1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textInput = findViewById(R.id.editText);
        fetch = findViewById(R.id.btnFetch);


        fetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                urlInput = textInput.getText().toString();

                //creating progress bar dialog
                progressBar = new ProgressDialog(v.getContext());
                progressBar.setCancelable(true);
                progressBar.setMessage("Fetching Images ...");
                progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressBar.setProgress(0);
                progressBar.setMax(100);
                progressBar.show();

                //rest progress bar and filesize status
                progressBarStatus = 0;
                imgCount = 0;

                new Thread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void run() {
                        List<Bitmap> bitmaps = scrapeUrlsForBitmaps(urlInput);
                        while (progressBarStatus < 100) {

                            progressBarStatus = (int) bitmaps.stream().count() * 5;
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            processBarHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setProgress(progressBarStatus);
                                }
                            });


                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                setViews(bitmaps);
                            }
                        });


                        if (progressBarStatus >= 100) {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            progressBar.dismiss();
                        }
                    }
                }).start();
            }
        });
        //myGridLayout = findViewById(R.id.grid_layout);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    protected List<Bitmap> scrapeUrlsForBitmaps(String urlInput) {
        org.jsoup.nodes.Document doc = null;
        try {
            doc = Jsoup.connect(urlInput).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Elements elements = doc.getElementsByTag("img");
        List<String> urls = elements.stream()
                .map(x -> x.absUrl("src"))
                .filter(x -> x.substring(x.length() - 4).equals(".jpg"))
                .limit(20)
                .collect(Collectors.toList());

        List<Bitmap> bitmaps = new ArrayList<>();
        for (String imgURL : urls) {

            try {
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

        androidx.gridlayout.widget.GridLayout myGrid = findViewById(R.id.grid_layout);

        for (int i = 0; i < myBitmaps.size(); i++) {
            ImageView imageview = new ImageView(this);
            //how to set the width and height dynamically?
            imageview.setLayoutParams(new android.view.ViewGroup.LayoutParams(300, 300));
            imageview.setImageBitmap(myBitmaps.get(i));
            myGrid.addView(imageview);

        }
    }


}