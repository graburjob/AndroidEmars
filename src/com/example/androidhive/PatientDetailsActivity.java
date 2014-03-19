package com.example.androidhive;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.androidhive.EditProductActivity.DeleteProduct;
import com.example.androidhive.EditProductActivity.GetProductDetails;
import com.example.androidhive.EditProductActivity.SaveProductDetails;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class PatientDetailsActivity extends Activity {
	
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
	private static String url_getpatient_details = "http://10.0.2.2/android_connect/get_patient_details.php";
	//private static String url_validate_user = "http://zatika.co/android_connect/get_patient_details.php";
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_patient_details);
		// save button
		btnSave = (Button) findViewById(R.id.btnSave);
		btnDelete = (Button) findViewById(R.id.btnDelete);

		// getting product details from intent
		Intent intent = getIntent();
		
		// getting product id (pid) from intent
		patientID = intent.getStringExtra(TAG_PATIENT_ID);

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

}
