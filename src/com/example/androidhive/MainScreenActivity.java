package com.example.androidhive;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainScreenActivity extends Activity{
	
	Button btnWRS;
	Button btnMyAppointments;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_screen);
		
		// Buttons
		btnWRS = (Button) findViewById(R.id.btnWRS);
		btnMyAppointments = (Button) findViewById(R.id.btnMyAppointments);
		
		// view products click event
		btnWRS.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				// Launching All products Activity
				Intent i = new Intent(getApplicationContext(), WRSHomeActivity.class);
				startActivity(i);
				
			}
		});
		
		// view products click event
		btnMyAppointments.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View view) {
				// Launching create new product activity
				Intent i = new Intent(getApplicationContext(), NewProductActivity.class);
				startActivity(i);
				
			}
		});
	}
}
