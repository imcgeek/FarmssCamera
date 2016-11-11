package android.farmss.com.farmsscamera;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by vishal on 18/10/16.
 */
public class NTest extends AppCompatActivity {
    Button button1,button2;
    ImageView imageView1;
    ImageView imageView2;
    TextView textView;
    String TAG = "FarmssCamera";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ntest);
        button1 = (Button)findViewById(R.id.camera);
        button2 = (Button)findViewById(R.id.home);
        imageView1 =(ImageView)findViewById(R.id.imageView1);
        imageView2 =(ImageView)findViewById(R.id.imageView2);
        textView =(TextView)findViewById(R.id.textview);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(i,0);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NTest.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        File sdRoot;
        String dir;
        String fileName;
        int count=0;
        int GreyScale=0;
//        int temp_GreyScale=0;
        int FinalGS;
        double Y;
        double n1= -0.1525;
        double n2= 138.75;
        Bitmap bp = (Bitmap) data.getExtras().get("data");
//        Get the RGB value of color-bitmap image
//        int bitmap_pixel_value_1 = bp.getPixel(72,150);
//        int color_r1 = Color.red(bitmap_pixel_value_1);
//        int color_g1 = Color.green(bitmap_pixel_value_1);
//        int color_b1 = Color.blue(bitmap_pixel_value_1);
//        Log.d(TAG,"(72,150) R="+String.valueOf(color_r1)+" G="+String.valueOf(color_g1)+" B="+String.valueOf(color_b1));
//
//        int bitmap_pixel_value_2 = bp.getPixel(80,150);
//        int color_r2 = Color.red(bitmap_pixel_value_2);
//        int color_g2 = Color.green(bitmap_pixel_value_2);
//        int color_b2 = Color.blue(bitmap_pixel_value_2);
//        Log.d(TAG,"(80,150) R="+String.valueOf(color_r2)+" G="+String.valueOf(color_g2)+" B="+String.valueOf(color_b2));
//
//        int bitmap_pixel_value_3 = bp.getPixel(72,162);
//        int color_r3 = Color.red(bitmap_pixel_value_3);
//        int color_g3 = Color.green(bitmap_pixel_value_3);
//        int color_b3 = Color.blue(bitmap_pixel_value_3);
//        Log.d(TAG,"(72,162) R="+String.valueOf(color_r3)+" G="+String.valueOf(color_g3)+" B="+String.valueOf(color_b3));
//
//        int bitmap_pixel_value_4 = bp.getPixel(80,162);
//        int color_r4 = Color.red(bitmap_pixel_value_4);
//        int color_g4 = Color.green(bitmap_pixel_value_4);
//        int color_b4 = Color.blue(bitmap_pixel_value_4);
//        Log.d(TAG,"(80,162) R="+String.valueOf(color_r4)+" G="+String.valueOf(color_g4)+" B="+String.valueOf(color_b4));
//
//        int bitmap_pixel_value_5 = bp.getPixel(65,150);
//        int color_r5 = Color.red(bitmap_pixel_value_5);
//        int color_g5 = Color.green(bitmap_pixel_value_5);
//        int color_b5 = Color.blue(bitmap_pixel_value_5);
//        Log.d(TAG,"(65,150) R="+String.valueOf(color_r5)+" G="+String.valueOf(color_g5)+" B="+String.valueOf(color_b5));
//


//        Log.d(TAG,"Bmp,H*W ="+String.valueOf(bp.getHeight())+"*"+String.valueOf(bp.getWidth()));
        Bitmap greybp = toGrayscale(bp);
//        Log.d(TAG,"GreyBmp H*W ="+String.valueOf(greybp.getHeight())+"*"+String.valueOf(greybp.getWidth()));
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] data1 = stream.toByteArray();
        if(isStoragePermissionGranted()){
            //Code For ROI
//            int width = greybp.getWidth();
//            int height = greybp.getHeight();
            int width = bp.getWidth();
            int height = bp.getHeight();

            for(int i=72;i<80;i++){
                for(int j=150;j<162;j++){
                    int temp2 = greybp.getPixel(i,j);
//                    Log.d(TAG,"ROI-Color= ("+String.valueOf(i)+","+String.valueOf(j)+") ="+String.valueOf(Color.red(temp2)));
                    GreyScale = GreyScale + Color.red(temp2);
                    count++;
                }
            }

            //RGB Experiment
            int temp_H = Math.abs(height/4)*2;
            int temp_W = Math.abs(width/3);

            int init_k = Math.abs(height/4);
            int init_l = Math.abs(width/3);

            int target_k = Math.abs(height/4)*3;
            int target_l = Math.abs(width/3)*2;

            int num_of_pixel = 0;
            int[][] arr= new int[temp_H][temp_W];

            int x1=0,y1=0,GSx1y1=0,x2=0,y2=0,GSx2y2=0;

            for(int l=init_l;l<target_l;l++){ //width
                for(int k= init_k;k<target_k;k++){  //height

                    int temp3 = bp.getPixel(l,k);
                    int temp_GreyScale = Color.red(temp3);

                    int a1=l-init_l;
                    int a2=k-init_k;

                    arr[a2][a1] = temp_GreyScale;
                    num_of_pixel++;
                   Log.d(TAG,"R : ("+String.valueOf(l)+","+String.valueOf(k)+") ="+String.valueOf(arr[a2][a1]));
                }
            }
            Log.d(TAG,"Total Pixels :"+String.valueOf(num_of_pixel));

            /**
             * Finding Max GS and Min GS on Semi Sqaure
             */
            int Max_GS = findMaxGS(arr);
            int Min_GS = findMinGS(arr);
            Log.d(TAG, "onActivityResult: Max_R :"+String.valueOf(Max_GS));
            Log.d(TAG, "onActivityResult: Min_R :"+String.valueOf(Min_GS));

            /**
             * Find x1,y1
             */
            outerloop:
            for (int n=init_k;n<target_k;n++){
            for (int m=init_l;m<target_l;m++){
                   int temp4 = bp.getPixel(m,n);
                    int temp4_greyScale = Color.red(temp4);

                    if(temp4_greyScale<Min_GS+Min_GS/3){
                        x1=m;
                        y1=n;
                        GSx1y1=temp4_greyScale;
                        break outerloop;
                    }
                }
            }
            Log.d(TAG,"(x1,y1) :("+String.valueOf(x1)+","+String.valueOf(y1)+")"+" R ="+String.valueOf(GSx1y1));

            /**
             * Find x2,y2
             */
            outerloop1:
            for(int o=target_l;o>init_l;o--){
                for(int p=target_k;p>init_k;p--){
                    int temp5 = bp.getPixel(o,p);
                    int temp5_greyScale = Color.red(temp5);

                    if(temp5_greyScale<Min_GS+Min_GS/3){
                        x2=o;
                        y2=p;
                        GSx2y2=temp5_greyScale;
                        break outerloop1;
                    }
                }
            }
            Log.d(TAG,"(x2,y2) :("+String.valueOf(x2)+","+String.valueOf(y2)+")"+" Red ="+String.valueOf(GSx2y2));


//            //Array declaration
//
//            int temp_H = Math.abs(height/4)*2;
//            int temp_W = Math.abs(width/3);
//
//            int init_k = Math.abs(height/4);
//            int init_l = Math.abs(width/3);
//
//            int target_k = Math.abs(height/4)*3;
//            int target_l = Math.abs(width/3)*2;
//
//            int num_of_pixel = 0;
//            int[][] arr= new int[temp_H][temp_W];
//
//            int x1=0,y1=0,GSx1y1=0,x2=0,y2=0,GSx2y2=0;
//
//            for(int l=init_l;l<target_l;l++){ //width
//                for(int k= init_k;k<target_k;k++){  //height
//
//                    int temp3 = greybp.getPixel(l,k);
//                    int temp_GreyScale = Color.red(temp3);
//
//                    int a1=l-init_l;
//                    int a2=k-init_k;
//
//                    arr[a2][a1] = temp_GreyScale;
//                    num_of_pixel++;
////                   Log.d(TAG,"ROI-unknown= ("+String.valueOf(l)+","+String.valueOf(k)+") ="+String.valueOf(arr[a2][a1]));
//                }
//            }
//            Log.d(TAG,"Total Pixels :"+String.valueOf(num_of_pixel));
//
//            /**
//             * Finding Max GS and Min GS on Semi Sqaure
//             */
//            int Max_GS = findMaxGS(arr);
//            int Min_GS = findMinGS(arr);
//            Log.d(TAG, "onActivityResult: Max_GS :"+String.valueOf(Max_GS));
//            Log.d(TAG, "onActivityResult: Min_GS :"+String.valueOf(Min_GS));
//
//            /**
//             * Find x1,y1
//             */
//            outerloop:
//            for (int n=init_k;n<target_k;n++){
//            for (int m=init_l;m<target_l;m++){
////                for (int n=init_k;n<target_k;n++){
//                   int temp4 = greybp.getPixel(m,n);
//                    int temp4_greyScale = Color.red(temp4);
//
//                    if(temp4_greyScale<Min_GS+Min_GS/3){
//                        x1=m;
//                        y1=n;
//                        GSx1y1=temp4_greyScale;
//                        break outerloop;
//                    }
//                }
//            }
//            Log.d(TAG,"(x1,y1) :("+String.valueOf(x1)+","+String.valueOf(y1)+")"+" GS ="+String.valueOf(GSx1y1));
//
//            /**
//             * Find x2,y2
//             */
//            outerloop1:
////            for (int m=init_l;m<target_l;m++){
////                for (int n=init_k;n<target_k;n++){
//            for(int o=target_l;o>init_l;o--){
//                for(int p=target_k;p>init_k;p--){
//                    int temp5 = greybp.getPixel(o,p);
//                    int temp5_greyScale = Color.red(temp5);
//
//                    if(temp5_greyScale<Min_GS+Min_GS/3){
//                        x2=o;
//                        y2=p;
//                        GSx2y2=temp5_greyScale;
//                        break outerloop1;
//                    }
//                }
//            }
//            Log.d(TAG,"(x2,y2) :("+String.valueOf(x2)+","+String.valueOf(y2)+")"+" GS ="+String.valueOf(GSx2y2));

            FinalGS = GreyScale / count;
            Y = n1*FinalGS+n2;

            sdRoot = Environment.getExternalStorageDirectory();
            dir = "/DCIM/Farmss/NTest/";
            fileName = "NTest_GS="+String.valueOf(FinalGS)+" "+ new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()).toString() + ".jpg";
            File mkDir = new File(sdRoot, dir);
            if(!mkDir.exists()){
                mkDir.mkdirs();
            }

            File pictureFile = new File(sdRoot, dir + fileName);
            try {
                FileOutputStream purge = new FileOutputStream(pictureFile);
                purge.write(data1);
                purge.close();
            } catch (FileNotFoundException e) {
                Log.d("DG_DEBUG", "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d("DG_DEBUG", "Error accessing file: " + e.getMessage());
            }
            //" GSImage-Width ="+String.valueOf(width)+" \n GSImage-height ="+String.valueOf(height)+
            textView.setText("GreyScale value ="+String.valueOf(FinalGS)+" \n\n Concentration ="+String.valueOf(Y));
            imageView1.setImageBitmap(bp);
            imageView2.setImageBitmap(greybp);
        }else {
            button1.setText("Camera");
        }


    }

    private int findMaxGS(int array[][]) {
        int max_gs= -99999;
        int[] maxArray = new int[2];
        for(int row=0; row < array.length;row++){
            for(int col=0; col<array[row].length; col++){
                if (array[row][col]>max_gs){
                    max_gs = array[row][col];
                    maxArray[0]=row;
                    maxArray[1]=col;
                }
            }
        }
        return max_gs;
    }
    private int findMinGS(int array[][]) {
        int min_gs= +99999;
        int[] minArray = new int[2];
        for(int row=0; row < array.length;row++){
            for(int col=0; col<array[row].length; col++){
                if (array[row][col]<min_gs){
                    min_gs = array[row][col];
                    minArray[0]=row;
                    minArray[1]=col;
                }
            }
        }
        return min_gs;
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("Permission : ","Permission is granted");
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                Log.v("Permission : ","Permission is revoked");
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("Permission : ","Permission is granted");
            return true;
        }
    }

    public Bitmap toGrayscale(Bitmap bmpOriginal)
    {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
