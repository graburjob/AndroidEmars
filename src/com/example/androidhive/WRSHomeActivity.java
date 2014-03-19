package com.example.androidhive;


import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.androidhive.LoginActivity.ValidateLogin;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class WRSHomeActivity extends Activity {
	
	EditText txtPatientID;
	Button btnSearch;
	
	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_Patient = "patientID";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wrshome);
		
		txtPatientID = (EditText) findViewById(R.id.txtPatientID);
		btnSearch = (Button) findViewById(R.id.btnSearch);
		
		btnSearch.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// getting values from selected ListItem
				String patientID = txtPatientID.getText().toString();

				// Starting new intent
				Intent in = new Intent(getApplicationContext(),
						PatientDetailsActivity.class);
				// sending pid to next activity
				in.putExtra("patientID", patientID);
				
				// starting new activity and expecting some response back
				startActivityForResult(in, 100);
				
			}
			
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_wrshome, menu);
		return true;
	}
}
