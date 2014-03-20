package com.example.androidhive;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PatientDetailsActivity extends Activity {
	
	private NfcAdapter nfcAdapter;
	
	EditText txtPatientName;
	EditText txtGender;
	EditText txtDOB;
	EditText txtAddress;
	Button btnSave;
	Button btnDelete;
	
	String patientID;
	
	// Progress Dialog
	private ProgressDialog pDialog;
	JSONParser jsonParser = new JSONParser();
	
	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_PATIENT_ID = "patientID";
	private static final String TAG_PATIENT="patient";
	
	private static final String TAG_PATIENT_NAME="Name";
	private static final String TAG_PATIENT_GENDER="Gender";
	private static final String TAG_PATIENT_DOB="DOB";
	private static final String TAG_PATIENT_ADDRESS="Address";
	
	// url to create new product
	//private static String url_getpatient_details = "http://zatika.co/android_connect/get_patient_details.php";
	private static String url_getpatient_details = "http://zatika.co/android_connect/get_patient_details.php";
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_patient_details);
		// save button
		btnSave = (Button) findViewById(R.id.btnSave);
		btnDelete = (Button) findViewById(R.id.btnDelete);
		
		nfcAdapter = NfcAdapter.getDefaultAdapter(this);
		
		if (null == nfcAdapter) {
			Toast.makeText(this, "NFC  feature is not available in device",
					Toast.LENGTH_SHORT).show();
		} else if (!nfcAdapter.isEnabled()) {
			AlertDialog.Builder alert = new AlertDialog.Builder(this);
			alert.setTitle("Enable NFC");
			alert.setMessage("NFC is turned off.\nDo you really want to turn on NFC?");
			alert.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							startActivity(new Intent(
									android.provider.Settings.ACTION_NFC_SETTINGS));
							patientID = handleIntent(getIntent());
						}
					});
			alert.setNegativeButton("Cancel", null);
			alert.show();
		} else if (nfcAdapter.isEnabled()) {
			patientID=handleIntent(getIntent());
		}
		
		// Getting complete product details in background thread
		new GetPatientDetails().execute();

		// save button click event
		btnSave.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// starting background task to update product
				//new SaveProductDetails().execute();
			}
		});

		// Delete button click event
		btnDelete.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// deleting product in background thread
				//new DeleteProduct().execute();
			}
		});
		
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_patient_details, menu);
		return true;
	}
	
	private String handleIntent(Intent intent) {
		if (NfcAdapter.ACTION_NDEF_DISCOVERED == intent.getAction()
				|| NfcAdapter.ACTION_TAG_DISCOVERED == intent.getAction()) {
			if ("text/plain".equalsIgnoreCase(intent.getType())) {
				Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
				try {
					String scanResult = new NdefReaderTask().execute(tag).get();
					Toast.makeText(getBaseContext(), scanResult, Toast.LENGTH_SHORT).show();
					patientID = scanResult;
				} catch (InterruptedException e) {
					throw new RuntimeException(
							"Problem occured while reading text from NFC");
				} catch (ExecutionException e) {
					throw new RuntimeException(
							"Problem occured while reading text from NFC");
				}
			} else {
				Toast.makeText(this, "MIME type is not recognized",
						Toast.LENGTH_SHORT).show();
			}
		} else {
			patientID = intent.getStringExtra(TAG_PATIENT_ID);
		}
		return patientID;
	}
	

	/**
	 * Background Async Task to Login
	 * */
	
	class GetPatientDetails extends AsyncTask<String, String, String> {
		
		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(PatientDetailsActivity.this);
			pDialog.setMessage("Getting Patient Details...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... arg0) {

			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					// Check for success tag
					int success;
					try {
						// Building Parameters
						List<NameValuePair> params = new ArrayList<NameValuePair>();
						params.add(new BasicNameValuePair("patientID", patientID));

						// getting product details by making HTTP request
						// Note that product details url will use GET request
						JSONObject json = jsonParser.makeHttpRequest(
								url_getpatient_details, "GET", params);

						// check your log for json response
						Log.d("Single Product Details", json.toString());
						
						// json success tag
						success = json.getInt(TAG_SUCCESS);
						if (success == 1) {
							// successfully received product details
							JSONArray productObj = json
									.getJSONArray(TAG_PATIENT); // JSON Array
							
							// get first product object from JSON Array
							JSONObject patient = productObj.getJSONObject(0);

							// product with this pid found
							// Edit Text
							txtPatientName = (EditText) findViewById(R.id.txtPatientName);
							txtGender = (EditText) findViewById(R.id.txtGender);
							txtDOB = (EditText) findViewById(R.id.txtDOB);
							txtAddress = (EditText) findViewById(R.id.txtAddress);

							// display product data in EditText
							txtPatientName.setText(patient.getString(TAG_PATIENT_NAME));
							txtGender.setText(patient.getString(TAG_PATIENT_GENDER));
							txtDOB.setText(patient.getString(TAG_PATIENT_DOB));
							txtAddress.setText(patient.getString(TAG_PATIENT_ADDRESS));

						}else{
							// product with pid not found
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});

			return null;
		}
		
		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog once got all details
			pDialog.dismiss();
		}
	}

	private class NdefReaderTask extends AsyncTask<Tag, Void, String> {
		@Override
		protected String doInBackground(Tag... params) {
			String result = null;
			Tag tag = params[0];
			Ndef ndef = Ndef.get(tag);
			if (ndef == null) {
				result = null;
			}
			NdefMessage ndefMessage = ndef.getCachedNdefMessage();
			NdefRecord[] records = ndefMessage.getRecords();
			for (NdefRecord ndefRecord : records) {
				if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN
						&& Arrays.equals(ndefRecord.getType(),
								NdefRecord.RTD_TEXT)) {
					try {
						result = readText(ndefRecord);
					} catch (UnsupportedEncodingException e) {
						throw new RuntimeException(
								"Problem occured while reading text from NFC");
					}
				}
			}
			return result;
		}

		private String readText(NdefRecord record)
				throws UnsupportedEncodingException {
			byte[] payload = record.getPayload();
			String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8"
					: "UTF-16";
			int languageCodeLength = payload[0] & 0063;
			return new String(payload, languageCodeLength + 1, payload.length
					- languageCodeLength - 1, textEncoding);
		}
	}
	
}
