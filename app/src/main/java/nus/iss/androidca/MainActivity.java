package nus.iss.androidca;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import android.widget.Toast;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
    private int fileCount = 0;
    private Handler processBarHandler = new Handler();

    private String urlInput;
    private List<String> selectedBitmap = new ArrayList<>();
    private List<Bitmap> myBitmaps;
    private Button start;
    private Thread bkgdThread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textInput = findViewById(R.id.editText);
        fetch = findViewById(R.id.btnFetch);
        start = findViewById(R.id.start_btn);
        start.setVisibility(View.GONE);
        start.setEnabled(false);

        fetch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bkgdThread != null){
                    bkgdThread.interrupt();
                }

                urlInput = textInput.getText().toString();

                //creating progress bar dialog
                progressBar = new ProgressDialog(v.getContext());
                progressBar.setCancelable(true);
                progressBar.setMessage("Fetching Images ...");
                progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressBar.setProgress(0);
                progressBar.setMax(20);
                progressBar.show();

                //reset progress bar and filesize status
                progressBarStatus = 0;
                fileCount = 0;

                bkgdThread = new Thread(new Runnable(){
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void run() {
                        myBitmaps = scrapeUrlsForBitmaps(urlInput);
                        while (progressBarStatus < 100) {

                            progressBarStatus = (int) myBitmaps.stream().count() * 5;
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

                    }
                });
                bkgdThread.start();
            }
        });
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        boolean downloaded = downloadImage(myBitmaps, selectedBitmap);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast msg;
                                if (downloaded) {
                                    msg = Toast.makeText(MainActivity.this,
                                            "Download Success", Toast.LENGTH_LONG);
                                } else {
                                    msg = Toast.makeText(MainActivity.this,
                                            "Download Failed", Toast.LENGTH_LONG);
                                }
                                msg.show();
                            }
                        });


                    }
                }).start();
            }
        });

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
        int fileCount = 0;
        for (String imgURL : urls) {
            try {
                URL url = new URL(imgURL);
                URLConnection conn = url.openConnection();
                InputStream input = conn.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                bitmaps.add(myBitmap);
                fileCount++;
                fetchProgressBar(fileCount);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return bitmaps;
    }

    public void fetchProgressBar(int fileCount){
        if (progressBarStatus < 20) {

            progressBarStatus = fileCount;
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

            if (progressBarStatus >= 20) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                progressBar.dismiss();
            }
        }
    }

    protected void setViews(List<Bitmap> myBitmaps) {
        //don't forget to clear the list of tags
        androidx.gridlayout.widget.GridLayout myGrid = findViewById(R.id.grid_layout);

        for (int i = 0; i < myBitmaps.size(); i++) {
            ImageView imageview = new ImageView(this);
            //how to set the width and height dynamically?
            imageview.setLayoutParams(new android.view.ViewGroup.LayoutParams(300,300));
            imageview.setImageBitmap(myBitmaps.get(i));
            imageview.setTag(i);
            myGrid.addView(imageview);
            imageview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    start.setVisibility(View.VISIBLE);
                    if (selectedBitmap.contains(imageview.getTag().toString())){
                        imageview.clearColorFilter();
                        selectedBitmap.remove(imageview.getTag().toString());
                        if( selectedBitmap.size() < 6){
                            start.setEnabled(false);
                        }
                    }
    //do a check for selected size, cannot select unless there is less than 6 items. to change when difficulty level is implemented
                    else if (selectedBitmap.size() < 6){
                        selectedBitmap.add(imageview.getTag().toString());
                        imageview.setColorFilter(Color.argb(175,255, 255, 255));
                        if( selectedBitmap.size() == 6){
                            start.setEnabled(true);
                        }
                    }
                }
            });

        }
    }

    protected boolean downloadImage(List<Bitmap> myBitmaps, List<String> selectedBitmap) {

        try {
            File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            for (String i : selectedBitmap) {

                String filename = UUID.randomUUID().toString() + i;
                File myFile = new File(dir, filename);

                Bitmap bitmap = myBitmaps.get(Integer.parseInt(i));
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
                byte[] bitmapData = bos.toByteArray();

                FileOutputStream fos = new FileOutputStream(myFile);
                fos.write(bitmapData);
                fos.flush();
                fos.close();

            }
        }
        catch (Exception e){
            e.printStackTrace();
            return false;
        }

        return true;

    }




}