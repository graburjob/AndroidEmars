package com.example.androidhive;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

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

public class LoginActivity extends Activity {
	
	EditText userID;
	EditText password;
	Button login;
	
	// Progress Dialog
	private ProgressDialog pDialog;
	JSONParser jsonParser = new JSONParser();
	
	// url to create new product
	private static String url_validate_user = "http://zatika.co/android_connect/login.php";
	//private static String url_validate_user = "http://zatika.co/android_connect/create_product.php";
	
	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	
	private Session session;//global variable 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		// Edit Text
		userID = (EditText) findViewById(R.id.txtLoginid);
		password = (EditText) findViewById(R.id.txtPassword);
		
		session = new Session(getApplicationContext());
		
		// Create button
		login = (Button) findViewById(R.id.btnLogin);
		
		login.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				new ValidateLogin().execute();
				
			}
			
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_login, menu);
		return true;
	}
	
	
	
	
	
	/**
	 * Background Async Task to Login
	 * */
	
	class ValidateLogin extends AsyncTask<String, String, String> {
		
		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(LoginActivity.this);
			pDialog.setMessage("Logging In..");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... arg0) {
			String loginID = userID.getText().toString();
			String loginPassword = password.getText().toString();
			
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("loginID", loginID));
			params.add(new BasicNameValuePair("loginPassword", loginPassword));
			 
			session.setusename(loginID);
			
			JSONObject json = jsonParser.makeHttpRequest(url_validate_user,
					"POST", params);
			
			// check log cat fro response
			Log.d("Create Response", json.toString());
			
			// check for success tag
			try {
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					// successfully created product
					Intent intentedActivity = new Intent(getApplicationContext(), MainScreenActivity.class);
					startActivity(intentedActivity);
					
					// closing this screen
					finish();
				} else {
					// failed to create product
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
			return null;
		}
		
	}


}


