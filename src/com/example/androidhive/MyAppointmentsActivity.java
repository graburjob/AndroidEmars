package com.example.androidhive;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class MyAppointmentsActivity extends ListActivity {
	
	// Progress Dialog
	private ProgressDialog pDialog;

	// Creating JSON Parser object
	JSONParser jParser = new JSONParser();

	List<HashMap<String, String>> myAppointments;
	
	private Session session;

	// url to get all products list
	private static String url_all_appointments= "http://zatika.co/android_connect/get_my_appointments.php";

	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_APPOINTMENTS = "appointments";
	private static final String TAG_APPTIME = "AppTime";
	private static final String TAG_DOCTORS_NAME = "DoctorName";

	// products JSONArray
	JSONArray appointments = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_appointments);
		
		// Hashmap for ListView
		myAppointments = new ArrayList<HashMap<String, String>>();
		session = new Session(getApplicationContext());

		// Loading products in Background Thread
		new LoadAllMyAppointments().execute();

		// Get listview
		ListView lv = getListView();

		// on seleting single product
		// launching Edit Product Screen
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
	
			}
		});

	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_my_appointments, menu);
		return true;
	}
	
	/**
	 * Background Async Task to Load all product by making HTTP Request
	 * */
	class LoadAllMyAppointments extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(MyAppointmentsActivity.this);
			pDialog.setMessage("Loading your Appointments. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * getting All products from url
		 * */
		protected String doInBackground(String... args) {
			// Building Parameters
			String doctorsID = session.getusename();
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("doctorsID", doctorsID));
			// getting JSON string from URL
			JSONObject json = jParser.makeHttpRequest(url_all_appointments, "GET", params);
			
			// Check your log cat for JSON reponse
			Log.d("All Appointments: ", json.toString());

			try {
				// Checking for SUCCESS TAG
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					// products found
					// Getting Array of Products
					appointments = json.getJSONArray(TAG_APPOINTMENTS);

					// looping through All Products
					for (int i = 0; i < appointments.length(); i++) {
						JSONObject jsonappointments = appointments.getJSONObject(i);

						// Storing each json item in variable
						String doctorName = jsonappointments.getString(TAG_DOCTORS_NAME);
						String appTime = jsonappointments.getString(TAG_APPTIME);

						// creating new HashMap
						HashMap<String, String> map = new HashMap<String, String>();

						// adding each child node to HashMap key => value
						map.put("item", appTime+"--"+doctorName);

						// adding HashList to ArrayList
						myAppointments.add(map);
					}
				} else {
/*					// no products found
					// Launch Add New product Activity
					Intent i = new Intent(getApplicationContext(),
							NewProductActivity.class);
					// Closing all previous activities
					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(i);*/
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all products
			pDialog.dismiss();
			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					/**
					 * Updating parsed JSON data into ListView
					 * */
					ListAdapter adapter = new SimpleAdapter(
							MyAppointmentsActivity.this, myAppointments,
							R.layout.list_item, new String[] {"item"},
							new int[] { R.id.item });
					// updating listview
					setListAdapter(adapter);
				}
			});

		}

	}

}
