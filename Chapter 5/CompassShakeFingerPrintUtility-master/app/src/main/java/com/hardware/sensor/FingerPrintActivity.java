package com.hardware.sensor;


import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;


public class FingerPrintActivity extends Activity {

    private static final int FINGERPRINT_PERMISSION_REQUEST_CODE = 0;
    private FingerprintManager mFingerprintManager;
    //Alias for our key in the Android Key Store
    private static final String KEY_NAME = "my_key";
    private KeyStore mKeyStore;
    private KeyGenerator mKeyGenerator;
    private Cipher mCipher;
    private CancellationSignal mCancellationSignal;
    private Dialog mFingerPrintDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fingerprint_layout);
        mFingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
        //As soon as Activity starts, check for the finger print conditions
        checkFingerPrintConditions();
    }

    public void initiateFingerPrintSensor(View v) {
        //Called from Layout button
        checkFingerPrintConditions();
    }

    public void checkFingerPrintConditions() {
        if(mFingerprintManager.isHardwareDetected()) {
            if(mFingerprintManager.hasEnrolledFingerprints()) {
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT)!= PackageManager.PERMISSION_GRANTED) {
                    //Requesting runtime finger print permission
                    requestPermissions(new String[]{Manifest.permission.USE_FINGERPRINT}, FINGERPRINT_PERMISSION_REQUEST_CODE);
                } else {
                    //After all 3 conditions are met, then show FingerPrint Dialog
                    showFingerPrintDialog();
                }
            } else {
                showAlertDialog("Finger Print Not Registered!", "Go to 'Settings -> Security -> Fingerprint' and register at least one fingerprint");
            }
        } else {
            showAlertDialog("Finger Print Sensor Not Found!", "Finger Print Sensor could not be found on your phone.");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] state) {
        //show FingerPrint Dialog, when runtime permission is granted
        if (requestCode == FINGERPRINT_PERMISSION_REQUEST_CODE && state[0] == PackageManager.PERMISSION_GRANTED) {

            showFingerPrintDialog();
        }
    }

    public void showAlertDialog(String title, String message)
    {
        new android.app.AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton)
                    {
                        dialog.dismiss();
                    }})
                .show();
    }

    public void showFingerPrintDialog() {
        //First Initialize the FingerPrint Settings
        if(initFingerPrintSettings())
        {
            //Init Custom FingerPrint Dialog from xml
            mFingerPrintDialog = new Dialog(this);
            View view = LayoutInflater.from(this).inflate(R.layout.fingerpring_dialog, null, false);
            mFingerPrintDialog.setContentView(view);
            Button cancel = (Button) view.findViewById(R.id.cancelbutton);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    mCancellationSignal.cancel();
                    mFingerPrintDialog.dismiss();
                }
            });
            //Stops the cancelling of the fingerprint dialog
            //by back press or touching accidentally on screen
            mFingerPrintDialog.setCanceledOnTouchOutside(false);
            mFingerPrintDialog.setCancelable(false);
            mFingerPrintDialog.show();
        }
        else
        {
            showAlertDialog("Error!", "Error in initiating Finger Print Cipher or Key!");
        }
    }

    public boolean initFingerPrintSettings() {
        //CancellationSignal requests authenticate api to stop scanning
        mCancellationSignal = new CancellationSignal();
        if(initKey() && initCipher()) {
            mFingerprintManager.authenticate(new FingerprintManager.CryptoObject(mCipher), mCancellationSignal, 0, new AuthenticationListener(), null);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Initialize the Cipher instance with the created key in the initKey() method.
     * return true if initialization is successful and false if the lock screen has
     * been disabled or reset after the key was generated, or if a fingerprint got enrolled after
     * the key was generated.
     */
    public boolean initCipher() {
        try {
            mKeyStore.load(null);
            SecretKey key = (SecretKey) mKeyStore.getKey(KEY_NAME, null);
            mCipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
            mCipher.init(Cipher.ENCRYPT_MODE, key);
            return true;
        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException | NoSuchPaddingException e) {
            return false;
        }
    }

    // Set the alias of the entry in Android KeyStore where the key will appear
    // and the constrains (purposes) in the constructor of the Builder
    // The enrolling flow for fingerprint. This is where you ask the user to set up fingerprint
    // for your flow. Use of keys is necessary if you need to know if the set of
    // enrolled fingerprints has changed.
    // Require the user to authenticate with a fingerprint to authorize every use
    // of the key
    /**
     * Creates a symmetric key in the Android Key Store which can only be used after the user has
     * authenticated with fingerprint.
     */
    public boolean initKey() {
        try {
            mKeyStore = KeyStore.getInstance("AndroidKeyStore");
            mKeyStore.load(null);
            mKeyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
            mKeyGenerator.init(new KeyGenParameterSpec.Builder(KEY_NAME, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build());
            mKeyGenerator.generateKey();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    class AuthenticationListener extends FingerprintManager.AuthenticationCallback{

        @Override
        public void onAuthenticationError(int errMsgId, CharSequence errString) {

            Toast.makeText(getApplicationContext(), "Authentication Error!", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {

            Toast.makeText(getApplicationContext(), "Authentication Success!", Toast.LENGTH_LONG).show();
            mFingerPrintDialog.dismiss();
        }

        @Override
        public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {

        }

        @Override
        public void onAuthenticationFailed() {

            Toast.makeText(getApplicationContext(), "Authentication Failed!", Toast.LENGTH_LONG).show();
        }

    }

}
