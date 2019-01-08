package com.chaisync.AppTrojanDetector;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.provider.Settings.Secure;
import android.widget.Toast;
import android.graphics.Color;

import android.content.pm.PackageManager;
import android.Manifest;
import android.support.v4.content.ContextCompat;
import android.support.v4.app.ActivityCompat;
import android.content.pm.ApplicationInfo;
import java.util.List;
import java.util.zip.*;
import java.io.InputStream;
import java.io.IOException;
import java.util.Scanner;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private TextView appName_display;
    private TextView permission_display;
    private TextView appName2_display;
    private TextView permission2_display;

    private ImageButton appButton1;
    private ImageButton appButton2;
    private ImageButton appButton3;
    private ImageButton appButton5;
    private ImageButton appButton6;
    private ImageButton appButton7;
    private ImageButton appButton10;
    private TextView detectResult_display;
    SharedPreferences sharedPref;
    Context sync_context = this;
    PackageManager pm0;
    List<ApplicationInfo> apps;
    int appPermissionListSize1;
    int appPermissionListSize2;

    public MainActivity(){
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPref = this.getSharedPreferences("com.Chaisync.sharedPref", Context.MODE_PRIVATE);

        appName_display = (TextView) findViewById(R.id.appname_textview);
        permission_display = (TextView) findViewById(R.id.permission_textview);
        appName2_display = (TextView) findViewById(R.id.appname2_textview);
        permission2_display = (TextView) findViewById(R.id.permission2_textview);
        detectResult_display = (TextView) findViewById(R.id.detectresult_text);

        // generate app manifext text
        pm0 = this.getPackageManager();
        apps = pm0.getInstalledApplications(
                PackageManager.GET_META_DATA | PackageManager.GET_SHARED_LIBRARY_FILES
        );

        /*for (int i = 0; i < apps.size(); i++)
        {
            Log.d("ManifestGetter",  i + ", " +apps.get(i).publicSourceDir);
        }*/
        // whale camera = 61


        // Listen for button click to fire message from client to server
        appButton1 = (ImageButton) findViewById(R.id.app1);
        appButton1.setOnClickListener(new View.OnClickListener(){
          public void onClick(View v){
              int currentapp = 0; // 0, 30, 113
              ApplicationInfo tempApp = apps.get(currentapp);
              updateTextInfo_box1(tempApp.className, tempApp.publicSourceDir);
              Toast.makeText(sync_context, "App1", Toast.LENGTH_LONG).show();
            }
        });

        appButton6 = (ImageButton) findViewById(R.id.app6);
        appButton6.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                int currentapp = 0; // 0, 30, 113
                ApplicationInfo tempApp = apps.get(currentapp);
                updateTextInfo_box2(tempApp.className, tempApp.publicSourceDir);
                Toast.makeText(sync_context, "App6", Toast.LENGTH_LONG).show();
            }
        });

        appButton2 = (ImageButton) findViewById(R.id.app2);
        appButton2.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                int currentapp = 0; // 0, 30, 113
                ApplicationInfo tempApp = apps.get(currentapp);
                updateTextInfo_box1(tempApp.className, tempApp.publicSourceDir);
                Toast.makeText(sync_context, "App2", Toast.LENGTH_LONG).show();
            }
        });

        appButton7 = (ImageButton) findViewById(R.id.app7);
        appButton7.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                int currentapp = 0; // 0, 30, 113
                ApplicationInfo tempApp = apps.get(currentapp);
                updateTextInfo_box2(tempApp.className, tempApp.publicSourceDir);
                Toast.makeText(sync_context, "App7", Toast.LENGTH_LONG).show();
            }
        });

        appButton3 = (ImageButton) findViewById(R.id.app3);
        appButton3.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                int currentapp = 30; // 0, 30, 113
                ApplicationInfo tempApp = apps.get(currentapp);
                updateTextInfo_box2(tempApp.className, tempApp.publicSourceDir);
                Toast.makeText(sync_context, "App3", Toast.LENGTH_LONG).show();
            }
        });

        appButton5 = (ImageButton) findViewById(R.id.app5);
        appButton5.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                int currentapp = 61; // 61 = whale camera
                ApplicationInfo tempApp = apps.get(currentapp);
                updateTextInfo_box1("Whale Camera", tempApp.publicSourceDir);
                Toast.makeText(sync_context, "App5", Toast.LENGTH_LONG).show();
            }
        });

        appButton10 = (ImageButton) findViewById(R.id.app10);
        appButton10.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                //int currentapp = 61; // 61 = whale camera
                //ApplicationInfo tempApp = apps.get(currentapp);
                ContextCompat.checkSelfPermission(sync_context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                ActivityCompat.requestPermissions((Activity) sync_context,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        0);
                updateTextInfo_box2("Whale Camera ApkPure", "/storage/emulated/0/Download/Whale Camera_v3.4.3_apkpure.com.apk");
                Toast.makeText(sync_context, "App10", Toast.LENGTH_LONG).show();
            }
        });
    };

    private void updateTextInfo_box1(String appName, String appDirectory){
        String classname = appName;
        String permission_text = "";
        String manifest_text =  classname + "\n " + appDirectory;

        appName_display.setText("" + manifest_text);
        permission_text = getManifestData(appDirectory,1 );
        permission_display.setText("" + permission_text);
    }

    private void updateTextInfo_box2(String appName, String appDirectory){
        String classname2 = appName;
        String permission_text2 = "";
        String manifest_text2 =  classname2 + "\n " + appDirectory;

        appName2_display.setText("" + manifest_text2);
        permission_text2 = getManifestData(appDirectory, 2);
        permission2_display.setText("" + permission_text2);
        printResult();
    }

    //extract AndroidManifest.xml from app apk
    String getManifestData(String appDirectory, int appNumber) {
        String permission_text2 = "";
        String[] permissionList = new String[0];
        try {
            ZipFile apk = new ZipFile(appDirectory);
            ZipEntry manifest = apk.getEntry("AndroidManifest.xml");
            if (manifest != null){
                InputStream stream = apk.getInputStream(manifest);
                Scanner s = new Scanner(stream);
                String result = "";
                while (s.hasNext()) {
                        result += s.next();
                }
                result = result.replaceAll("[^a-zA-Z]", "");
                //Log.d("Mani0festGetter",  result);
                permissionList = result.split("androidpermission");
                if (permissionList.length > 0)
                    for (int i = 1; i < permissionList.length; i++){

                        permissionList[i] = permissionList[i].split("(?=[a-z])")[0];
                        //Log.d("ManifestGetter", "permisson = " + permissionList[i]);
                        permission_text2 = permission_text2 + permissionList[i]  + "\n";
                    }
                Log.d("ManifestGetter", "Manifest size = " + manifest.getSize() + ", " + result);
                stream.close();
            }
            apk.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if ( appNumber == 1 )
            appPermissionListSize1 = permissionList.length;
        else
            appPermissionListSize2 = permissionList.length  ;
        return permission_text2;
    }

    private void printResult(){
        if (appPermissionListSize1 == appPermissionListSize2 ) {
            detectResult_display.setText("Clean!");
            detectResult_display.setTextColor(Color.GREEN);
            permission2_display.setTextColor(Color.GREEN);
        }
        else {
            detectResult_display.setText("=!=Virus Found=!=");
            detectResult_display.setTextColor(Color.RED);
            permission2_display.setTextColor(Color.RED);
        }
    }



}