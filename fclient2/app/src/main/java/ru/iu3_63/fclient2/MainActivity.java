package ru.iu3_63.fclient2;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import ru.iu3_63.fclient2.databinding.ActivityMainBinding;

//import org.apache.commons.codec.DecoderException;

public class MainActivity extends AppCompatActivity {

    ActivityResultLauncher activityResultLauncher;

    // Used to load the 'fclient2' library on application startup.
    static {
        System.loadLibrary("fclient2");
        System.loadLibrary("mbedcrypto");
    }

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
        TextView tv = binding.sampleText;
        tv.setText(stringFromJNI());
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
}