package com.example.sebastiaorealino.otpgen;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Formatter;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generateOTP();
            }
        });

        generateOTP();

    }
    public void generateOTP(){
        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);

        byte[] otpByte = generateOtp("QYDVQQLEwpUZWNoIERlcHQuMSgwJgYDV");
        byte[] byteRange = Arrays.copyOfRange(otpByte, 10, 14);

        String hexValue = truncateWhenUTF8("0x"+toHexString(byteRange),31);

        Log.d("OTP HEx", hexValue);

        int otp = getDecimal(hexValue);
        String fullOtp = String.valueOf(otp);
        String sOtpGenereted = fullOtp.substring(fullOtp.length() -6, fullOtp.length());

        Log.d("OTP OTP", sOtpGenereted);
        tv.setText(sOtpGenereted);
    }

    public static int getDecimal(String hex){
        String digits = "0123456789ABCDEF";
        hex = hex.toUpperCase();
        int val = 0;
        for (int i = 0; i < hex.length(); i++)
        {
            char c = hex.charAt(i);
            int d = digits.indexOf(c);
            val = 16*val + d;
        }
        return val;
    }

    public static String truncateWhenUTF8(String s, int maxBytes) {
        int b = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            int skip = 0;
            int more;
            if (c <= 0x007f) {
                more = 1;
            }
            else if (c <= 0x07FF) {
                more = 2;
            } else if (c <= 0xd7ff) {
                more = 3;
            } else if (c <= 0xDFFF) {
                more = 4;
                skip = 1;
            } else {
                more = 3;
            }
            if (b + more > maxBytes) {
                return s.substring(0, i);
            }
            b += more;
            i += skip;
        }
        return s;
    }

    private static String toHexString(byte[] bytes) {
        Formatter formatter = new Formatter();

        for (byte b : bytes) {
            formatter.format("%02x", b);
        }

        return formatter.toString();
    }
    public static int truncateUtf8(String input, byte[] output) {

        ByteBuffer outBuf = ByteBuffer.wrap(output);
        CharBuffer inBuf = CharBuffer.wrap(input.toCharArray());

        Charset utf8 = Charset.forName("UTF-8");
        utf8.newEncoder().encode(inBuf, outBuf, true);
        System.out.println("encoded " + inBuf.position() + " chars of " + input.length() + ", result: " + outBuf.position() + " bytes");
        return outBuf.position();
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

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native byte[] generateOtp(String key);
}
