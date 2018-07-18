package com.example.sebastiaorealino.otpgen;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.sebastiaorealino.otpgen.databinding.ActivityMainBinding;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.mvvm.data.model.QRCodeResponse;
import com.mvvm.utils.OtpGenerator;
import com.mvvm.utils.SharePref;

import io.reactivex.Observable;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding mMainBinding;
    private String mOtp;

    static {
        System.loadLibrary("native-lib");
    }

    OtpGenerator mOtpGenerator;
    QRCodeResponse mQRCodeResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mOtpGenerator = OtpGenerator.getInstance();

        mMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mMainBinding.setIsTokenSaved(true);
        String key = SharePref.getInstance(this).getAnyKey();
        if (key.equals("")) {
            mMainBinding.setIsTokenSaved(false);
        } else {
            mMainBinding.setIsTokenSaved(true);
            createTask(key);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null && scanResult.getContents() != null) {
            String data[] = scanResult.getContents().split("\n");
            if (data[0] != null) {
                Gson gson = new Gson();
                mQRCodeResponse = gson.fromJson(data[0], QRCodeResponse.class);
                createTask(mQRCodeResponse.getKey());
            }
        }
    }

    @SuppressLint("CheckResult")
    private void createTask(String key) {
        SharePref.getInstance(this).saveKey(key);
        Observable obsOtpInterval = mOtpGenerator.generateOtpInterval(key);
        obsOtpInterval.subscribe((Otp) -> {
            mOtp = Otp.toString();
            mMainBinding.setIsTokenSaved(true);
            mMainBinding.setOtpText(mOtp);
        });

    }

    private void scanQrCode() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setPrompt("Scan a barcode");
        integrator.setCameraId(0);  // Use a specific camera of the device
        integrator.setBeepEnabled(false);
        integrator.initiateScan();
    }

    private void clearLocalStorage() {
        SharePref.getInstance(this).clearAll();
        mMainBinding.setIsTokenSaved(false);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void scanQrCodeClick(View v) {
        scanQrCode();
    }

    public void clearLocalStorageClick(View v) {
        clearLocalStorage();
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
