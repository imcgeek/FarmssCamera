package android.farmss.com.farmsscamera;

import android.Manifest;
import android.content.Context;
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
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PTest extends AppCompatActivity {
    Button button1,button2;
    ImageView imageView1;
    ImageView imageView2;
    TextView textView;
    String TAG = "Farmss";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ptest);
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
                Intent intent = new Intent(PTest.this,MainActivity.class);
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
//        int C_GreyScale=0;
        int FinalGS_unknown;
        //int known_calibratedStrip_value = 73;
        //int FinalGS_Calibrated;
        double Y;
        double n1= -14.159;
        double n2= 973.96;
        Bitmap bp = (Bitmap) data.getExtras().get("data");
        Bitmap greybp = toGrayscale(bp);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] data1 = stream.toByteArray();
        if(isStoragePermissionGranted()){
            //Code For ROI
            int width = greybp.getWidth();
            int height = greybp.getHeight();
            //Standard Strip
//            for(int i=42;i<50;i++){
//                for(int j=150;j<162;j++){
//                    int temp2 = greybp.getPixel(i,j);
//                    Log.i(TAG,"("+String.valueOf(i)+","+String.valueOf(j)+")"+" :"+String.valueOf(temp2));
//                    C_GreyScale = C_GreyScale + Color.red(temp2);
//                    count++;
//                }
//            }
//            Log.i("TAG ","No of Pixels : "+String.valueOf(count));
//            FinalGS_Calibrated = C_GreyScale / count;
//            int difference = known_calibratedStrip_value - FinalGS_Calibrated;

            //Unknown Strip
            for(int i=72;i<80;i++){
                for(int j=150;j<162;j++){
                    int temp1 = greybp.getPixel(i,j);
                    Log.i(TAG,"("+String.valueOf(i)+","+String.valueOf(j)+")"+" :"+String.valueOf(temp1));
                    GreyScale = GreyScale + Color.red(temp1);
                    count++;
                }
            }
            Log.i("TAG ","No of Pixels : "+String.valueOf(count));
            FinalGS_unknown = GreyScale / count;
//            FinalGS_unknown = FinalGS_unknown+difference;
            Y = n1*FinalGS_unknown+n2;

            sdRoot = Environment.getExternalStorageDirectory();
            dir = "/DCIM/Farmss/PTest/";
            fileName = "PTest_GS="+String.valueOf(FinalGS_unknown)+" "+new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()).toString() + ".jpg";
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
            //|| Difference B/w Std. Strip  : "+String.valueOf(difference)+"
            //|| Std.GreyScale value : "+String.valueOf(FinalGS_Calibrated)+"

            textView.setText(" Image-Width "+String.valueOf(width)+" || Image-height "+String.valueOf(height)+" || Unknown GreyScale value : "+String.valueOf(FinalGS_unknown)+" || Concantration : "+String.valueOf(Y));
            imageView1.setImageBitmap(bp);
            imageView2.setImageBitmap(greybp);
        }else {
            button1.setText("Camera");
            Context context = getApplicationContext();
            CharSequence text = "Try Again !!";
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();

        }


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
