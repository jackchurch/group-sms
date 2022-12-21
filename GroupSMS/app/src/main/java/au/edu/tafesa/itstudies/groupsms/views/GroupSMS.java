package au.edu.tafesa.itstudies.groupsms.views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import au.edu.tafesa.itstudies.groupsms.R;
import au.edu.tafesa.itstudies.groupsms.models.SMSDataModelArray;

public class GroupSMS extends Activity {
    public static final String CLASS_TAG = "GroupSMS";
    public static final int NEW_MESSAGE_REQUEST = 1;
    public static final int NEW_SENDTO_REQUEST = 2;


    private SMSDataModelArray messageData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_sms);
        // Getting to the views defined in the XML files.
        TextView textView1 = (TextView) findViewById(R.id.textView1);
        textView1.setBackgroundColor(Color.GREEN);
        textView1.setMovementMethod(new ScrollingMovementMethod());
        messageData = new SMSDataModelArray("", 5);
        messageData.addPhoneNumber("0401125172");
        messageData.addPhoneNumber("0401125172");
        setSummary();

        // Responding to an event - the onClick for the Edit Message Button using
        // a named inner class
        Button btnEditMessage;
        btnEditMessage = (Button) this.findViewById(R.id.butEditMessage);
        HandleButtonEditMessageOnClick buttonEditMessageOnClick;
        buttonEditMessageOnClick = new HandleButtonEditMessageOnClick();
        btnEditMessage.setOnClickListener(buttonEditMessageOnClick);

        // The onClick for the Edit Send To Button using a named inner class but no
        // variable
        Button btnEditSendTo;
        btnEditSendTo = (Button) this.findViewById(R.id.btnEditSendTo);
        btnEditSendTo.setOnClickListener(new HandleButtonEditSendToOnClick());

        // Send Button onClick Action starts the implicit intent to
        // send an SMS.
        Button btnSend;
        btnSend = (Button) this.findViewById(R.id.btnSend);
        btnSend.setOnClickListener(new HandleButtonSendOnClick());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_group_sm, menu);
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

    public class HandleButtonSendOnClick implements View.OnClickListener {
        public void onClick(View v) {
            // Sending an SMS
            SmsManager smsManager = SmsManager.getDefault();
            for (int i=0; i< messageData.getNumPhoneNumbers();i++) {
                String phone = messageData.getPhoneNumber(i);
                smsManager.sendTextMessage(phone, null, messageData.getMessage(), null, null);
                Toast.makeText(getApplicationContext(), "SMS Sent to " + phone, Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Handle Edit Button OnClick by starting the activity This is an example of
     * starting another activity using an explicit Intent.
     *
     * @author sruiz
     *
     */
    @SuppressWarnings("rawtypes")
    @SuppressLint("LongLogTag")
    public class HandleButtonEditMessageOnClick implements View.OnClickListener {

        public static final String CLASS_TAG = "HandleButtonEditMessageOnClick";

        public void onClick(View v) {
            Log.i(CLASS_TAG, "onClick started...");

            // Example of an EXPLICIT intent, as we are naming the java class to use
            // (EditMessage.class)
            Intent editIntent;
            Activity sourceActivity;
            Class destinationClass;

            sourceActivity = GroupSMS.this;
            destinationClass = EditMessage.class;
            editIntent = new Intent(sourceActivity, destinationClass);

            // Start the message editing view
            editIntent.putExtra("CURRENT_MESSAGE", messageData.getMessage());
            startActivityForResult(editIntent, NEW_MESSAGE_REQUEST);
        }
    }

    @SuppressWarnings("rawtypes")
    @SuppressLint("LongLogTag")
    public class HandleButtonEditSendToOnClick implements View.OnClickListener {

        public static final String CLASS_TAG = "HandleButtonEditSendToOnClick";

        public void onClick(View v) {
            Log.i(CLASS_TAG, "onClick started...");

            Intent editIntent;
            Activity sourceActivity;
            Class destinationClass;

            sourceActivity = GroupSMS.this;
            destinationClass = EditSendTo.class;
            editIntent = new Intent(sourceActivity, destinationClass);

            // Sending information to the intent receiver through the Intent object
            editIntent.putExtra("CURRENT_PHONE", messageData);

            // startActivity(editIntent);
            startActivityForResult(editIntent, NEW_SENDTO_REQUEST);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check which request we're responding to
        if (requestCode == NEW_MESSAGE_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                    String newMessage =(String) (data.getStringExtra("NEW_MESSAGE"));
                messageData.setMessage(newMessage);
                setSummary();
            }
        } else if (requestCode == NEW_SENDTO_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                this.messageData = (SMSDataModelArray) (data.getSerializableExtra("NEW_PHONE"));
                setSummary();
            }
        }
    }

    /**
     * Put together a summary of the phone and message and display it.
     */
    private void setSummary() {
        StringBuilder summary;
        String phone;

        summary = new StringBuilder("Sending To (");
        summary.append(messageData.getNumPhoneNumbers());
        summary.append(" numbers) : \n");
        for (int i = 0; i < messageData.getNumPhoneNumbers(); i++) {
            phone = messageData.getPhoneNumber(i);
            if (phone != null)
                summary.append(phone + (i!=(messageData.getNumPhoneNumbers()-1)?",":""));
        }
        summary.append("\n\nMessage:\n");
        summary.append(messageData.getMessage());
        TextView textView1 = (TextView) findViewById(R.id.textView1);
        textView1.setText(summary);

    }
}
