package ru.iu3_63.fclient2;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity implements TransactionEvents {

    ActivityResultLauncher activityResultLauncher;

    // Used to load the 'fclient2' library on application startup.
    static {
        System.loadLibrary("fclient2");
        System.loadLibrary("mbedcrypto");
    }


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
        runOnUiThread(()-> {
            Toast.makeText(MainActivity.this, result ? "ok" : "failed",
                    Toast.LENGTH_LONG).show();
        });
    }


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
                            //Toast.makeText(MainActiviеy.this, pin, Toast.LENGTH_SHORT);
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
        activityResultLauncher  = registerForActivityResult(
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
        */
        //todo -------------------LAB_1-------------------

        // Example of a call to a native method
        //TextView tv = findViewById(R.id.sample_button);
        //tv.setText(stringFromJNI());

    }

    //todo -------------------LAB_3-------------------
    public void onButtonClick(View v)
    {
        /* ---- pt1
        Intent it = new Intent(this, PinpadActivity.class);
        Toast.makeText(this, "Enter pin", Toast.LENGTH_SHORT).show();
        //startActivity(it);
        activityResultLauncher.launch(it);

        new Thread(()-> {
            try {
                byte[] trd = stringToHex("9F0206000000000228");
                boolean ok = transaction(trd);
                runOnUiThread(()-> {
                    Toast.makeText(MainActivity.this, ok ? "ok" : "failed", Toast.LENGTH_SHORT);
                });
            } catch (Exception ex) {
                // todo: log error
            }
        }).start();
        ----- end of pt1 */
        byte[] trd = stringToHex("9F0206000000000228");
        transaction(trd);
    }
    //todo -------------------LAB_3-------------------


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