package com.rubiks.lehoang.rubikssolver;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ToggleButton;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends Activity {

    Cube cube = null;
    boolean isClockwise;
    private Uri fileUri;

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final String FACE = "com.rubiks.lehoang.face";

    public void setupAndSendIntent(String face){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = Uri.fromFile(getOutputMediaFile(MEDIA_TYPE_IMAGE));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        intent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        intent.putExtra(FACE, face);
        startActivityForResult(intent, 100);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button captureTop = (Button) findViewById(R.id.captop);
        Button captureFront = (Button) findViewById(R.id.capfront);
        Button captureBottom = (Button) findViewById(R.id.capbottom);
        Button captureBack = (Button) findViewById(R.id.capback);
        Button captureLeft = (Button) findViewById(R.id.capleft);
        Button captureRight = (Button) findViewById(R.id.capright);

        try {
            cube = new Cube("state.txt", MainActivity.this.getApplicationContext());

            captureBottom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setupAndSendIntent("BOTTOM");
                }
            });

            captureLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setupAndSendIntent("LEFT");
                }
            });

            captureRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setupAndSendIntent("RIGHT");
                }
            });

            captureBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setupAndSendIntent("BACK");
                }
            });

            captureTop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setupAndSendIntent("TOP");
                }
            });
            captureFront.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    setupAndSendIntent("FRONT");
                }
            });


        }catch (Exception e){

            Util.LogError(Log.getStackTraceString(e));
            Util.LogError("SUMMAT WENT WRONG BRUH");
        }




    }


    private static Colour getColour(int[] pixels){
        int count = 0;
        int r = 0;
        int g = 0;
        int b = 0;
        for (int i = 0; i<pixels.length; i++){

            if(pixels[i] != 0) {
                count++;
                r += ( pixels[i] >> 16) & 0xFF;
                g += ( pixels[i]>>  8) & 0xFF;
                b += ( pixels[i]      ) & 0xFF;

            }
        }

        Util.LogDebug(r/count + "r" + g/count + "g" + b/count + "b");

        float hsb[] = new float[3];
        Color.RGBToHSV(r/count,g/count,b/count,hsb);
        Util.LogDebug(r+"r"+g+"g"+b+"b");

        Util.LogDebug("Brightness" + hsb[2]);
        Util.LogDebug("Saturation" + hsb[1]);
        if      (hsb[1] < 0.3 && hsb[2] > 0.3) return Colour.WHITE;
        else if (hsb[2] < 0.1) return Colour.UNKNOWN;
        else {

            Util.LogDebug(hsb[0]+"");
            float deg = hsb[0];
            int shift = 0;
            //use phase shift?


            if      (deg >=   (0+shift)%360 && deg <  (15+shift)%360) return Colour.RED;
            else if (deg >=  (15+shift)%360 && deg <  (40+shift)%360) return Colour.ORANGE;
            else if (deg >=  (40+shift)%360 && deg <  (90+shift)%360) return Colour.YELLOW;
            else if (deg >=  (90+shift)%360 && deg < (165+shift)%360) return Colour.GREEN;
            else if (deg >= (165+shift)%360 && deg < (195+shift)%360) return hsb[2] > 0.7 ? Colour.WHITE : Colour.UNKNOWN;
            else if (deg >= (195+shift)%360 && deg < (270+shift)%360) return Colour.BLUE;
            else if (deg >= (270+shift)%360 && deg < (330+shift)%360) return Colour.UNKNOWN;
            else if (deg >= (330+shift)%360 && deg < (360+shift)) return Colour.RED;
            else return Colour.UNKNOWN;
        }

    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode==100){
             if(resultCode == RESULT_OK){
                try{
                    BitmapFactory.Options options = new BitmapFactory.Options();

                    options.inSampleSize = 4;

                    Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);

                    Util.LogDebug("Width" + bitmap.getWidth());
                    Util.LogDebug("Height" + bitmap.getHeight());

                    bitmap.getPixel(100,100);
                    int[] topLeft = new int[bitmap.getWidth()*bitmap.getWidth()];
                    int[] topMid = new int[bitmap.getWidth()*bitmap.getWidth()];
                    Colour[][] face = new Colour[3][3];
                    String faceString = data.getStringExtra(FACE);
                    int start = bitmap.getWidth()/6;
                    int increment = bitmap.getWidth()/3;
                    for(int i = 0 ; i < 3; i++){
                        for(int j = 0; j < 3;j++){
                            topLeft = new int[bitmap.getWidth()*bitmap.getWidth()];
                            bitmap.getPixels(topLeft, 0, bitmap.getWidth(),start + j*increment, start+i*increment,50,50);
                            face[i][j] = getColour(topLeft);
                            Util.LogDebug(face[i][j].toString());
                        }
                    }

                    Util.addFaceToConfig("config.txt", "Top", face, this);

                    Util.printConfigFile("config.txt", this);

                }catch (NullPointerException e){

                }
                fileUri.getPath();
             }
        }
    }
    /** Create a File for saving an image or video */
    public static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
