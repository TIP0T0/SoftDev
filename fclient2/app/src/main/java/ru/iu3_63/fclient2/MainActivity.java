package ru.iu3_63.fclient2;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements TransactionEvents {

    ActivityResultLauncher activityResultLauncher;

    // Used to load the 'fclient2' library on application startup.
    static {
        System.loadLibrary("fclient2");
        System.loadLibrary("mbedcrypto");
    }

    //todo -------------------LAB_3-------------------
    private String pin;

    @Override
    public String enterPin(int ptc, String amount) {
        pin = new String();
        Intent it = new Intent(MainActivity.this, PinpadActivity.class);
        it.putExtra("ptc", ptc);
        it.putExtra("amount", amount);
        synchronized (MainActivity.this) {
            activityResultLauncher.launch(it);
            try {
                MainActivity.this.wait();
            } catch (Exception ex) {
                //todo: log error
            }
        }
        return pin;
    }

    @Override
    public void transactionResult(boolean result) {
        runOnUiThread(() -> {
            Toast.makeText(MainActivity.this, result ? "ok" : "failed",
                    Toast.LENGTH_LONG).show();
        });
    }
    //todo -------------------LAB_3-------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //todo -------------------LAB_3-------------------

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            // обработка результата
                            //String pin = data.getStringExtra("pin");
                            //Toast.makeText(MainActivity.this, pin, Toast.LENGTH_SHORT);
                            pin = data.getStringExtra("pin");
                            synchronized (MainActivity.this) {
                                MainActivity.this.notifyAll();
                            }
                        }
                    }
                });

        int res = initRng();
        byte[] rnd = randomBytes(10);

        //todo -------------------LAB_3-------------------


        //todo -------------------LAB_2-------------------
/*
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            // обработка результата
                            String pin = data.getStringExtra("pin");
                            Toast.makeText(MainActivity.this, pin,
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        int res = initRng();
        byte[] rnd = randomBytes(10);
*/
        //todo -------------------LAB_2-------------------


        //todo -------------------LAB_1-------------------
/*
        int res = initRng();
        byte[] key = randomBytes(16);
        byte[] data = randomBytes(200);

        byte[] enc = encrypt(key, data);
        byte[] dec = decrypt(key, enc);

        boolean result = true;
        if (dec.length != data.length) {
            result = false;
        }
        for (int i = 0; i < dec.length; i++) {
            if (dec[i] != data[i])
                result = false;
        }
        System.out.println(result);

        // Example of a call to a native method
        TextView tv = findViewById(R.id.sample_button);
        tv.setText(stringFromJNI());
*/
        //todo -------------------LAB_1-------------------

    }


    //todo -------------------LAB_2-------------------
/*
    public void onButtonClick(View v)
    {
        Intent it = new Intent(this, PinpadActivity.class);
        Toast.makeText(this, "Enter pin", Toast.LENGTH_SHORT).show();
        //startActivity(it);
        activityResultLauncher.launch(it);
    }
*/
    //todo -------------------LAB_2-------------------


    //todo -------------------LAB_3-------------------
/*
    public void onButtonClick(View v)
    {
        // comment from here
        Intent it = new Intent(this, PinpadActivity.class);
        Toast.makeText(this, "Enter pin", Toast.LENGTH_SHORT).show();
        //startActivity(it);
        activityResultLauncher.launch(it);

        new Thread(()-> {
            try {
                byte[] trd = stringToHex("9F0206000000000100");
                boolean ok = transaction(trd);
                runOnUiThread(()-> {
                    Toast.makeText(MainActivity.this, ok ? "ok" : "failed", Toast.LENGTH_SHORT);
                });
            } catch (Exception ex) {
                // todo: log error
            }
        }).start();
        // comment to here

        //byte[] trd = stringToHex("9F0206000000000100");
        //transaction(trd);
    }
*/
    //todo -------------------LAB_3-------------------

    public void onButtonClick(View v) {
        byte[] trd = stringToHex("9F0206000000000100");
        testHttpClient();
    }


    //todo -------------------LAB_4-------------------

    protected String getPageTitle(String html)
    {
        Pattern pattern = Pattern.compile("<title>(.+?)</title>", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(html);
        String p;
        if (matcher.find())
            p = matcher.group(1);
        else
            p = "Not found";
        return p;
    }

    protected void testHttpClient()
    {
        new Thread(() -> {
            try {
                HttpURLConnection uc = (HttpURLConnection)
                        (new URL("http://10.0.2.2:8081/api/v1/title").openConnection());
                InputStream inputStream = uc.getInputStream();
                String html = IOUtils.toString(inputStream);
                String title = getPageTitle(html);
                runOnUiThread(() ->
                {
                    Toast.makeText(this, title, Toast.LENGTH_LONG).show();
                });
            } catch (Exception ex) {
                Log.e("fapptag", "Http client fails", ex);
            }
        }).start();
    }

    //todo -------------------LAB_4-------------------


    public static byte[] stringToHex(String s)
    {
        byte[] hex;
        try
        {
            hex = Hex.decodeHex(s.toCharArray());
        }
        catch (DecoderException ex)
        {
            hex = null;
        }
        return hex;
    }

    /**
     * A native method that is implemented by the 'fclient2' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
    public static native int initRng();
    public static native byte[] randomBytes(int no);
    public static native byte[] encrypt(byte[] key, byte[] data);
    public static native byte[] decrypt(byte[] key, byte[] data);
    public native boolean transaction(byte[] trd);
}