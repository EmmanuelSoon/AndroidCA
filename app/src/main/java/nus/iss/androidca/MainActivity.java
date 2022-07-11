package nus.iss.androidca;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.GridLayoutAnimationController;
import android.widget.Button;
import android.widget.EditText;

import androidx.gridlayout.widget.GridLayout;

import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
    private Button fetch, start, stop;
    private ProgressDialog progressBar;
    private int progressBarStatus = 0;
    private int fileCount =0;
    private Handler progressBarHandler = new Handler();
    private Runnable progressBarRunner;
    private Context context;

    private List<Integer> selectedBitmap = new ArrayList<>();
    private List<Bitmap> myBitmaps = new ArrayList<>();
    private Thread bkgdThread;
    private androidx.gridlayout.widget.GridLayout myGrid;

    private boolean isDownloading;
    private String[] cardFiles;

    private ActivityResultLauncher<Intent> rlGameActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getBaseContext();
        textInput = findViewById(R.id.editText);
        myGrid = findViewById(R.id.grid_layout);
        isDownloading = false;

        rlGameActivity = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                        deleteStoredFiles();
                        System.out.println("files deleted");
                    }
                }
        );

        fetch = findViewById(R.id.btnFetch);
        fetch.setOnClickListener((view -> {
            runFetch(view);
        }));

        Button stop = findViewById(R.id.btnStop);
        stop.setOnClickListener((view -> runStop()));

        start = findViewById(R.id.start_btn);
        start.setVisibility(View.GONE);
        start.setEnabled(false);

        start.setOnClickListener((view -> runStart()));
    }

    private void runStop(){
        if(isDownloading){
            bkgdThread.interrupt();
            isDownloading = false;
            cleanUp();
        }
    }

    private void runStart(){
        boolean downloaded = downloadImage(myBitmaps, selectedBitmap);
        Toast msg;
        if (!downloaded) {
            msg = Toast.makeText(MainActivity.this,
                    "Download Failed", Toast.LENGTH_LONG);
            msg.show();
            return;

        } else {
            msg = Toast.makeText(MainActivity.this,
                    "Download Success", Toast.LENGTH_LONG);
        }
        runNextActivity();
    }

    private void runFetch(View view){
        GridLayout myGrid = findViewById(R.id.grid_layout);

        if (!isUrl(textInput.getText().toString())) {
            Toast.makeText(MainActivity.this, "Wrong URL", Toast.LENGTH_SHORT).show();
            return;
        }

        if(isDownloading){
            bkgdThread.interrupt();
            isDownloading = false;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "Restarting Download", Toast.LENGTH_SHORT).show();
                    myGrid.removeAllViews();
                    cleanUp();
                    setBkgdThread();
                    createProgressBarDialog(view);
                }
            }, 1000);
        }
        else{
            myGrid.removeAllViews();
            setBkgdThread();
            createProgressBarDialog(view);
        }
    }

    private void setBkgdThread(){
        bkgdThread = new Thread(new Runnable() {
            @Override
            public void run() {
                if(Thread.interrupted()) return;
                isDownloading =true;
                myBitmaps = scrapeUrlsForBitmaps(textInput.getText().toString());
            }
        });
        bkgdThread.start();
    }

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
                //To implement ability to search other websties that don't hold .jpg images
                .filter(x -> x.substring(x.length() - 4).equals(".jpg"))
                .limit(20)
                .collect(Collectors.toList());

        ArrayList<Bitmap> bitmaps  = new ArrayList<>();
        fileCount = 0;
        for (String imgURL : urls) {
            try {
                if(!isDownloading) break;
                URL url = new URL(imgURL);
                URLConnection conn = url.openConnection();
                InputStream input = conn.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                bitmaps.add(myBitmap);
                fileCount++;
                setViews(myBitmap, fileCount);
                updateProgressBar(fileCount);
            }  catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmaps;
    }

    private void updateProgressBar(int fileCount){
        if(fileCount < 20){
            progressBar.setProgress(fileCount);
        }
        else{
            progressBar.dismiss();
        }
    }

    //show image in gridlayout
    protected void setViews(Bitmap myBitmap, int fileCount) {
        androidx.gridlayout.widget.GridLayout myGrid = findViewById(R.id.grid_layout);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ImageView imageview = new ImageView(MainActivity.this);
                imageview.setLayoutParams(new android.view.ViewGroup.LayoutParams(250, 300));
                imageview.setImageBitmap(myBitmap);
                imageview.setScaleType(ImageView.ScaleType.CENTER_CROP);
                RelativeLayout.LayoutParams layoutPara = new RelativeLayout.LayoutParams(imageview.getLayoutParams());
                layoutPara.setMargins(10, 5, 10, 5);
                imageview.setLayoutParams(layoutPara);
                imageview.setId(View.generateViewId());
                imageview.setTag(fileCount);
                imageview.isShown();
                myGrid.addView(imageview);

                imageview.setOnClickListener((view) -> {
                    imageListener(imageview);
                });
            }
        });
    }

    private void imageListener(ImageView imageview) {
        Button start = findViewById(R.id.start_btn);

        start.setVisibility(View.VISIBLE);
        if (selectedBitmap.contains(imageview.getId())){
            imageview.clearColorFilter();
            selectedBitmap.remove(Integer.valueOf(imageview.getId()));
            if( selectedBitmap.size() < 6){
                start.setEnabled(false);
            }
        }
        //do a check for selected size, cannot select unless there is less than 6 items. to change when difficulty level is implemented
        else if (selectedBitmap.size() < 6) {
            selectedBitmap.add(imageview.getId());
            imageview.setColorFilter(Color.argb(175, 255, 255, 255));
            if (selectedBitmap.size() == 6) {
                start.setEnabled(true);
            }
        }
    }

    //progress bar functions
    private void createProgressBarDialog(View view) {
        //creating progress bar dialog
        progressBar = new ProgressDialog(view.getContext());
        progressBar.setCancelable(true);
        progressBar.setMessage("Fetching Images ...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressBar.setProgress(0);
        progressBar.setMax(20);
        progressBar.show();

        //reset progress bar and filesize status
        progressBarStatus = 0;
        fileCount = 0;
    }


    protected boolean isUrl(String url) {
        try {
            if (!url.isEmpty() && url.contains("https")) {
                return true;
            }
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return false;
    }


    protected boolean downloadImage(List<Bitmap> myBitmaps, List<Integer> selectedBitmap) {

        try {
            cardFiles = new String[myBitmaps.size()];
            File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            for (int i = 0; i < selectedBitmap.size(); i++) {

                String filename = UUID.randomUUID().toString() + i;
                File myFile = new File(dir, filename);

                ImageView imageView = findViewById(selectedBitmap.get(i));
                int index = Integer.parseInt(imageView.getTag().toString());
                Bitmap bitmap = myBitmaps.get(index);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
                byte[] bitmapData = bos.toByteArray();

                FileOutputStream fos = new FileOutputStream(myFile);
                fos.write(bitmapData);
                fos.flush();
                fos.close();

                cardFiles[i] = myFile.getAbsolutePath();

            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    protected void cleanUp() {

        if (myBitmaps != null) myBitmaps.clear();
        if (selectedBitmap != null) selectedBitmap.clear();
        if (progressBar != null) {
            progressBar.cancel();
            progressBar.setProgress(0);
        }

    }

    protected void runNextActivity(){


        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("cardFiles", cardFiles);
        rlGameActivity.launch(intent);


    }

    protected void deleteStoredFiles(){
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (dir.isDirectory()){
            String[] imgFiles = dir.list();
            for (int i = 0; i < imgFiles.length; i++){
                new File(dir, imgFiles[i]).delete();
            }
        }
    }


}




