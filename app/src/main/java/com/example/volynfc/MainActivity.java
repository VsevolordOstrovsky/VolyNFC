package com.example.volynfc;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.nfc.Tag;
import android.nfc.TagLostException;
import android.nfc.tech.Ndef;
import android.nfc.tech.TagTechnology;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {

    public static final String Error_Detected = "No NFC Tag Detected";
    public static final String Write_Success = "Text Written Successfully!";
    public static final String Write_Error = "Error during Writing, Try Again!";

    NfcAdapter nfcAdapter;

    IntentFilter writingTagFilters[];
    boolean writeMode;
    Tag myTag;
    Context context;
    PendingIntent pendingIntent;
    EditText edit_message;
    TextView nfc_contents;
    Button ActivateButton;

    Tag tag;

    @SuppressLint("UnspecifiedImmutableFlag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        edit_message = (EditText) findViewById(R.id.edit_message);
        nfc_contents = (TextView) findViewById(R.id.nfc_contents);
        ActivateButton = findViewById(R.id.ActivateButton);


        NdefRecord record = NdefRecord.createTextRecord("en", "Hello, NFC!");
        NdefMessage message = new NdefMessage(new NdefRecord[] { record });

        Intent intent = new Intent(this, getClass());
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Log.i("RRR","22222222222");
        tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);


        ActivateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Ndef ndef = Ndef.get(tag);

                try {
                    ndef.connect();
                    ndef.writeNdefMessage(message);
                    ndef.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (FormatException e) {
                    throw new RuntimeException(e);
                }

            }
        });

        NfcManager manager = (NfcManager) getSystemService(Context.NFC_SERVICE);
        NfcAdapter adapter = manager.getDefaultAdapter();
        if (adapter != null && adapter.isEnabled()) {
            // NFC is available and enabled on the device
            System.out.println("YES");
        } else {
            // NFC is not available or not enabled on the device
            System.out.println("NO");
        }
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if (intent.getAction() != null && intent.getAction().equals(NfcAdapter.ACTION_NDEF_DISCOVERED)) {

            Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            NdefMessage[] messages = getNdefMessages(intent);
            Tag tag = new Tag();

        }
    }
    @Override
    protected void onPause() {
        super.onPause();

        // Выключение поддержки NFC
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();

        // Включение поддержки NFC
        if (nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
        }
    }
    private NdefMessage[] getNdefMessages(Intent intent) {
        NdefMessage[] messages = null;
        Parcelable[] rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        if (rawMessages != null) {
            messages = new NdefMessage[rawMessages.length];
            for (int i = 0; i < rawMessages.length; i++) {
                messages[i] = (NdefMessage) rawMessages[i];
            }
        }
        return messages;
    }


}