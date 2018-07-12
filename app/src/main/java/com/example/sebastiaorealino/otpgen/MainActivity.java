package com.example.sebastiaorealino.otpgen;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.mvvm.data.model.QRCodeResponse;
import com.mvvm.utils.OtpGenerator;

import junit.framework.Assert;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    static {
        System.loadLibrary("native-lib");
    }

    OtpGenerator mOtpGenerator;
    QRCodeResponse mQRCodeResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mOtpGenerator = OtpGenerator.getInstance();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanQrCode();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            String data[] = scanResult.getContents().split("\n");
            Gson gson = new Gson();
            mQRCodeResponse = gson.fromJson(data[0], QRCodeResponse.class);
            createTask();
        }
    }

    @SuppressLint("CheckResult")
    private void createTask() {
        Observable.create(emitter -> {
            String sOTP = mOtpGenerator.generateOTP(mQRCodeResponse.getKey());
            emitter.onNext(sOTP);
        }).subscribe((response) -> {

            TextView tv = (TextView) findViewById(R.id.sample_text);
            tv.setText(response.toString());
            Log.d("TASK", response.toString());
            Log.d("TASK", "DEU CERTO A TASK" + response.toString());
        }, Throwable::printStackTrace);

//        Flowable.fromCallable(() -> {
//            Thread.sleep(1000); //  imitate expensive computation
//            return "Done";
//        })
//                .subscribeOn(Schedulers.io())
//                .observeOn(Schedulers.single())
//                .subscribe((response)->{
////                    TextView tv = (TextView) findViewById(R.id.sample_text);
////                    String sOTP = mOtpGenerator.generateOTP(mQRCodeResponse.getKey());
////                    tv.setText(sOTP);
//                    Log.d("TASK", "DEU CERTO A TASK" );
//                }, Throwable::printStackTrace);
    }

    private void scanQrCode() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setPrompt("Scan a barcode");
        integrator.setCameraId(0);  // Use a specific camera of the device
        integrator.setBeepEnabled(false);
        integrator.initiateScan();
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
