package com.rubiks.lehoang.rubikssolver;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ToggleButton;

import java.io.IOException;


public class MainActivity extends Activity {

    Cube cube = null;
    boolean isClockwise;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button testButton = (Button) findViewById(R.id.test);
        Button dButton = (Button) findViewById(R.id.d);
        Button r = (Button) findViewById(R.id.r);
        Button l = (Button) findViewById(R.id.l);
        Button f = (Button) findViewById(R.id.f);
        Button b = (Button) findViewById(R.id.b);
        ToggleButton clockwise = (ToggleButton) findViewById(R.id.togglebutton);

        clockwise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isClockwise = ((ToggleButton) v).isChecked();
            }
        });

        try {
            cube = new Cube("state.txt", MainActivity.this.getApplicationContext());


            testButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {

                        cube.performSequence("U" + (isClockwise ? "" : "'"));
                        Util.LogDebug(cube.toString());
                    }catch (Exception e){

                        Util.LogError(Log.getStackTraceString(e));
                        Util.LogError("SUMMAT WENT WRONG BRUH");
                    }
                }
            });

            r.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        cube.performSequence("R" + (isClockwise ? "" : "'"));
                        Util.LogDebug(cube.toString());
                    }catch (Exception e){
                        Util.LogError(Log.getStackTraceString(e));
                        Util.LogError("SUMMAT WENT WRONG BRUH");
                    }
                }
            });

            f.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        cube.performSequence("F" + (isClockwise ? "" : "'"));
                        Util.LogDebug(cube.toString());
                    } catch (Exception e) {
                        Util.LogError(Log.getStackTraceString(e));
                        Util.LogError("SUMMAT WENT WRONG BRUH");
                    }
                }
            });

            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        cube.performSequence("B" + (isClockwise ? "" : "'"));
                        Util.LogDebug(cube.toString());
                    } catch (Exception e) {
                        Util.LogError(Log.getStackTraceString(e));
                        Util.LogError("SUMMAT WENT WRONG BRUH");
                    }
                }
            });

            l.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        cube.performSequence("L" + (isClockwise ? "" : "'"));
                        Util.LogDebug(cube.toString());
                    } catch (Exception e) {
                        Util.LogError(Log.getStackTraceString(e));
                        Util.LogError("SUMMAT WENT WRONG BRUH");
                    }
                }
            });

            dButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        cube.performSequence("D" + (isClockwise ? "" : "'"));
                        Util.LogDebug(cube.toString());
                    }catch (Exception e){
                        Util.LogError(Log.getStackTraceString(e));
                        Util.LogError("SUMMAT WENT WRONG BRUH");
                    }
                }
            });


        }catch (Exception e){

            Util.LogError(Log.getStackTraceString(e));
            Util.LogError("SUMMAT WENT WRONG BRUH");
        }




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
