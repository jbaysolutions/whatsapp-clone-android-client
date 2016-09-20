package com.jbaysolutions.tutorial.whatsappclone;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.zxing.Result;
import com.jbaysolutions.tutorial.whatsappclone.comms.ServerFacade;
import com.jbaysolutions.tutorial.whatsappclone.ui.ChatListAdapter;

import java.io.UnsupportedEncodingException;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class MainActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;

    private EditText usernameET ;
    private Button signInButton;

    private LinearLayout chatLayout;
    private EditText messageET;

    private LinearLayout qrScanneLayout;

    private ListView chatList ;

    private MessageBroadcastReceiver mReceiver = new MessageBroadcastReceiver();
    private ChatListAdapter adapter = new ChatListAdapter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameET = (EditText) findViewById(R.id.usernameET);
        signInButton= (Button) findViewById(R.id.signinButton);

        chatLayout= (LinearLayout) findViewById(R.id.chatLayout);
        messageET= (EditText) findViewById(R.id.messageET);

        qrScanneLayout = (LinearLayout) findViewById(R.id.qrScannerLayout);
        chatList = (ListView) findViewById(R.id.chatLListView);
        chatList.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, new IntentFilter("RECEIVEMESSAGE"));
    }

    /**
     * Used only for locking in a username for the user.
     *
     * @param view
     */
    public void signIn(View view) {
        if (usernameET.getText().toString().isEmpty()) {
            Toast.makeText(this, "Must have username defined", Toast.LENGTH_SHORT).show();
            return ;
        }

        // If a username is defined, then lock interface and update
        usernameET.setEnabled(false);
        signInButton.setEnabled(false);
        chatLayout.setVisibility(View.VISIBLE);
    }

    /**
     * Used for sending messages to the chat room server
     *
     * @param v
     */
    public void sendMessage(View v) {
        if (!messageET.getText().toString().isEmpty()) {
            try {
                ServerFacade.sendMessage(
                        this,
                        usernameET.getText().toString(),
                        messageET.getText().toString()
                );
                messageET.setText("");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                Toast.makeText(this, "Problem sending message", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     *
     * @param view
     */
    public void performQrScanner(View view) {

        qrScanneLayout.setVisibility(View.VISIBLE);

        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view<br />

        qrScanneLayout.addView(
                mScannerView
        );

        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();         // Start camera
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mScannerView != null) {
            mScannerView.stopCamera();   // Stop camera on pause
        }
        unregisterReceiver(mReceiver);
    }

    /**
     * Process the result of the QRCode read.
     *
     * @param rawResult
     */
    public void handleResult(Result rawResult) {
        // Do something with the result here
        Log.e("handler", rawResult.getText()); // Prints scan results
        Log.e("handler", rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode)
        // show the scanner result into dialog box.

        mScannerView.stopCamera();

        try {
            ServerFacade.sendRegisterUserToQRCode(
                    this,
                    usernameET.getText().toString(),
                    rawResult.getText()
            );

            qrScanneLayout.removeView(
                    mScannerView
            );
            qrScanneLayout.setVisibility(View.GONE);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Log.e("Main Activity", e.getMessage(), e);
            qrScanneLayout.removeView(
                    mScannerView
            );
            qrScanneLayout.setVisibility(View.GONE);
        }

    }

    class MessageBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String rawData = intent.getStringExtra("rawdata");
            String user = rawData.split(":")[0].trim();
            String message = rawData.split(":")[1].trim();
            adapter.addMessage(
                    user, message,
                    (user.trim().equals(usernameET.getText().toString()))
            );
            Log.d("RAWDATA", "Received RawData : " + rawData);
        }
    }

}
