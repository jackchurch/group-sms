package au.edu.tafesa.itstudies.groupsms.views;


import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import au.edu.tafesa.itstudies.groupsms.R;
import au.edu.tafesa.itstudies.groupsms.models.*;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/* Updated by Jack Church
    9th June 2020
    Added in ButtonDeleteOnClickHandler and ButtonAddNewOnClickHandler */

public class EditSendTo extends Activity {
    public static final String CLASS_TAG = "EditSendTo";

    // The view objects
    private ListView lvPhoneNumbers;
    private EditText etPhone;
    private Button btnAddNew;
    private Button btnUpdate;
    private Button btnDone;

    // The data (model) we are working with
    private SMSDataModelArray messageData;

    // The handler for the listView events
    private ListViewItemSelectedHandler listViewItemSelectedHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(CLASS_TAG, "onCreate ...");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_send_to);
        // Get the SMSDataModel object that contains the data for this SMS Message
        Intent editIntent;
        editIntent = this.getIntent();
        messageData = (SMSDataModelArray) editIntent.getSerializableExtra("CURRENT_PHONE");
        if (messageData == null) {
            messageData = new SMSDataModelArray("");
        }

        // Set the Adapter for the ListView containing the list of Phone Numbers
        lvPhoneNumbers = (ListView) this.findViewById(R.id.lvNumbers);
        lvPhoneNumbers.setAdapter(new MyListViewAdapter(messageData));

        // Setup the interface objects for this view
        btnDone = (Button) this.findViewById(R.id.btnDonePhoneEdit);
        btnUpdate = (Button) this.findViewById(R.id.btnUpdate);
        btnAddNew = (Button) this.findViewById(R.id.btnAddNew);
        etPhone = (EditText) this.findViewById(R.id.etPhone);

        // Setup the event handlers for the ListView, Buttons and the EditText
        listViewItemSelectedHandler = new ListViewItemSelectedHandler();
        lvPhoneNumbers.setOnItemSelectedListener(listViewItemSelectedHandler);
        lvPhoneNumbers.setOnItemClickListener(listViewItemSelectedHandler);
        btnDone.setOnClickListener(new ButtonDoneOnClickHandler());
        btnUpdate.setOnClickListener(new ButtonUpdateOnClickHandler());
        btnAddNew.setOnClickListener(new ButtonAddNewOnClickHandler());


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_send_to, menu);
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

    private class ButtonDoneOnClickHandler implements OnClickListener {
        public static final String CLASS_TAG = "ButtonDoneOnClickHandler";

        public void onClick(View v) {

            Log.i(CLASS_TAG, "OnClick...");
            Intent intent = new Intent();
            intent.putExtra("NEW_PHONE", messageData);
            setResult(RESULT_OK, intent);
            finish();
        }

    }

    private class ButtonUpdateOnClickHandler implements OnClickListener {
        public static final String CLASS_TAG = "ButtonUpdateOnClickHandler";

        public void onClick(View v) {

            Log.i(CLASS_TAG, "OnClick...");
            String oldNumber;
            String newNumber;
            int currentPositionInListView;

            currentPositionInListView = listViewItemSelectedHandler.getCurrentSelectedPosition();
            newNumber = etPhone.getText().toString();
            if (newNumber.equals("")) {
                Toast
                        .makeText(EditSendTo.this, "Cannot update to nothing. Use Delete to get rid of number!", Toast.LENGTH_SHORT)
                        .show();
            } else {
                oldNumber = messageData.updatePhoneNumber(newNumber, currentPositionInListView);
                //JC add this 2nd level IF
                if (oldNumber == null) {
                    oldNumber = messageData.updatePhoneNumber(newNumber, currentPositionInListView - 1);
                }
                Toast.makeText(
                        EditSendTo.this,
                        "Update of item " + (currentPositionInListView+1) + " which was " + oldNumber + " to value "
                                + messageData.getPhoneNumber(currentPositionInListView), Toast.LENGTH_SHORT).show();
                ((MyListViewAdapter) lvPhoneNumbers.getAdapter()).notifyDataSetChanged();
            }
        }

    }

    /**
     * Adds the phone number from the etPhone EditText to the SMSDataModel object.
     * Extracts the number from the EditText. If there is no data in the EditText it will
     * not add it but display a Toast style message indicating that user must enter a number.
     * If the adding cause the SMSDataModel to become full will inform the user with a Toast
     * message. Will ensure that the ListView is updated by notifying it of a dataset change.
     * Needs to disable the AddNew button if this addition makes the SMSDataModel full.
     * @author sruiz
     *
     */
    private class ButtonAddNewOnClickHandler implements OnClickListener {
        public static final String CLASS_TAG = "ButtonAddNewOnClickHandler";

        public void onClick(View v) {
            Log.i(CLASS_TAG, "OnClick...");
            //TODO See comments above.

            String number;

            number = etPhone.getText().toString();
            //IF number is empty, error
            if (number.equals("")) {
                Toast
                        .makeText(EditSendTo.this, "Cannot add nothing. Use Delete to get rid of number!", Toast.LENGTH_SHORT)
                        .show();
            }
            //ELSE add number to messageData
            else {
                number = messageData.addPhoneNumber(number);
                int currentPositionInListView = messageData.getNumPhoneNumbers();

                Toast.makeText(
                        EditSendTo.this,
                        "Addition  of number " + number + ". Number of numbers: "
                                + currentPositionInListView,  Toast.LENGTH_SHORT).show();
                ((MyListViewAdapter) lvPhoneNumbers.getAdapter()).notifyDataSetChanged();
                btnUpdate.setEnabled(true);

                listViewItemSelectedHandler.position = currentPositionInListView;
                etPhone.setText(messageData.getPhoneNumber(currentPositionInListView -1));
                btnUpdate.setText("Update Entry " + (currentPositionInListView));
                ((MyListViewAdapter) lvPhoneNumbers.getAdapter()).notifyDataSetChanged();

            }
        }
    }

    /**
     * Deletes the item in the row where the Delete button was clicked
     * If the current position in the lisview handler is >= position being deleted
     * then the selected item in the listview handler will need to be adjusted.
     * The AddNew button must be enabled (it may have been disabled!)
     * @author sruiz
     *
     */
    private class ButtonDeleteOnClickHandler implements OnClickListener {
        public static final String CLASS_TAG = "ButtonDeleteOnClickHandler";

        public void onClick(View v) {
            Log.i(CLASS_TAG, "OnClick...");
            //TODO See comments above

            View rowView = (View) v.getParent();
            int lvDeletePosition = lvPhoneNumbers.getPositionForView(rowView);
            String number = messageData.getPhoneNumber(lvDeletePosition);
            int lvSelectedItemPosition = listViewItemSelectedHandler.getCurrentSelectedPosition();

            //IF the list view selected item is less than or equal to the array number to delete.
            if(lvSelectedItemPosition <= lvDeletePosition) {
                messageData.deleteNumber(lvDeletePosition);

                ((MyListViewAdapter) lvPhoneNumbers.getAdapter()).notifyDataSetChanged();
                btnUpdate.setText("Update Entry " + (listViewItemSelectedHandler.position + 1));
                etPhone.setText(messageData.getPhoneNumber(listViewItemSelectedHandler.position));
            }

            //IF the list view selected item is greater the array number to delete. i.e. selected item 3 and delete item 1.
            if(lvSelectedItemPosition > lvDeletePosition) {
                messageData.deleteNumber(lvDeletePosition);

                ((MyListViewAdapter) lvPhoneNumbers.getAdapter()).notifyDataSetChanged();
                int newPosition = listViewItemSelectedHandler.position - 1;
                listViewItemSelectedHandler.position = newPosition;
                btnUpdate.setText("Update Entry " + (newPosition + 1));
                etPhone.setText(messageData.getPhoneNumber(newPosition ));
            }

            btnUpdate.setEnabled(false);
            btnAddNew.setEnabled(true);

            if (messageData.getPhoneNumber(0) != null) {
                btnUpdate.setEnabled(true);
            }

            Toast.makeText(EditSendTo.this, "Phone number " + number + " in list position " +
                    (lvDeletePosition + 1) + " has been deleted.", Toast.LENGTH_LONG).show();
        }
    }

    private class ButtonEditOnClickHandler implements OnClickListener {
        public static final String CLASS_TAG = "ButtonEditOnClickHandler";

        public void onClick(View v) {
            int position;
            View rowView;
            rowView = (View) v.getParent();
            position = lvPhoneNumbers.getPositionForView(rowView);
            listViewItemSelectedHandler.onItemSelected((ListView) rowView.getParent(), rowView, position, position);
            etPhone.requestFocus();
        }
    }

    private class ListViewItemSelectedHandler implements OnItemSelectedListener, OnItemClickListener {
        public static final String CLASS_TAG = "ListViewItemSelectedHandler";
        private int position = 0;

        public int getCurrentSelectedPosition() {
            return position;
        }

        public void onItemSelected(AdapterView<?> parent, View rowView, int position, long id) {

            EditText etPhone;
            etPhone = (EditText) EditSendTo.this.findViewById(R.id.etPhone);
            if (messageData.getPhoneNumber(position) != null) {
                btnUpdate.setEnabled(true);
                btnUpdate.setText("Update Entry " + (position + 1));
                etPhone.setText(messageData.getPhoneNumber(position));
                this.position = position;
            } else {
                RowViewComponents rowViewComponents;
                rowViewComponents = (RowViewComponents) rowView.getTag();
                btnUpdate.setEnabled(false);
                rowViewComponents.btnDelete.setEnabled(false);
                rowViewComponents.btnEdit.setEnabled(false);
            }
        }

        public void onNothingSelected(AdapterView<?> parent) {
            // Nothing to do
        }

        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //	lvPhoneNumbers.setSelection(position);
            onItemSelected(parent, view,position,id);
        }

    }

    private class RowViewComponents {
        public TextView txtCounter;
        public TextView txtPhoneNumber;
        public Button btnEdit;
        public Button btnDelete;
    }

    private class MyListViewAdapter extends BaseAdapter {
        public static final String CLASS_TAG = "MyListViewAdapter";
        private SMSDataModelInterface theData;

        public MyListViewAdapter(SMSDataModelInterface theData) {
            this.theData = theData;
        }

        public int getCount() {
            return theData.getNumPhoneNumbers();
//			return 5;
        }

        public Object getItem(int position) {
            return theData.getPhoneNumber(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView;
            RowViewComponents rowViewComponents;

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) (EditSendTo.this).getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                rowView = inflater.inflate(R.layout.phone_number_row_layout, parent, false);
                rowViewComponents = new RowViewComponents();
                rowViewComponents.txtCounter = (TextView) rowView.findViewById(R.id.txtCounter);
                rowViewComponents.txtPhoneNumber = (TextView) rowView.findViewById(R.id.txtPhone);
                rowViewComponents.btnEdit = (Button) rowView.findViewById(R.id.btnEdit);
                rowViewComponents.btnDelete = (Button) rowView.findViewById(R.id.btnDelete);
                rowView.setTag(rowViewComponents);
                // Register the handlers
                rowViewComponents.btnEdit.setOnClickListener(new ButtonEditOnClickHandler());
                rowViewComponents.btnDelete.setOnClickListener(new ButtonDeleteOnClickHandler());
                //This allows clicking on the row in the ListView to be recognised
                ((ViewGroup)rowView).setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
            } else {
                rowView = convertView;
                rowViewComponents = (RowViewComponents) rowView.getTag();
            }
            // Setup the TextViews showing the row count and phone number
            // and enable/disable the buttons appropriately
            rowViewComponents.txtCounter.setText("" + (position + 1));
            Log.v(CLASS_TAG, "Updating position " + position + " value =" + getItem(position));
            if (getItem(position) == null) {
                rowViewComponents.txtPhoneNumber.setText("");
                rowViewComponents.btnDelete.setEnabled(false);
                rowViewComponents.btnEdit.setEnabled(false);
            } else {
                rowViewComponents.txtPhoneNumber.setText((String) getItem(position));
                rowViewComponents.btnDelete.setEnabled(true);
                rowViewComponents.btnEdit.setEnabled(true);
            }
            return rowView;
        }
    }
}
