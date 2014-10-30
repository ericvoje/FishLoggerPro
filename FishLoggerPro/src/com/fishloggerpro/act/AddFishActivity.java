package com.fishloggerpro.act;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.fishloggerpro.R;
import com.fishloggerpro.srv.DBService;
import com.fishloggerpro.srv.ServiceResultReceiver;
import com.fishloggerpro.srv.ServiceResultReceiver.Receiver;

public class AddFishActivity extends Activity implements Receiver {

	private Button button_submit;
	private EditText editText_note, editText_length, editText_weight,
			editText_bait, editText_conditions;
	private Spinner spinner_species;
	private Switch switch_gps;
	private Location location;
	private boolean includeGPS;

	private String connectionKey;

	private ServiceResultReceiver sReceiver;

	private LocationManager locationManager;
	LocationListener locationListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_fish);
		initView();
		Intent i = getIntent();
		connectionKey = i.getStringExtra("connectionKey");
		sReceiver = new ServiceResultReceiver(new Handler());

		// Acquire a reference to the system Location Manager
		locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);

		// Define a listener that responds to location updates
		locationListener = new LocationListener() {
			public void onLocationChanged(Location loc) {
				if (location == null) {
					location = loc;
				} else if (location.hasAccuracy() && loc.hasAccuracy()
						&& loc.getAccuracy() > location.getAccuracy()) {
					location = loc;
				} else if (location.getTime() + 1000 < loc.getTime()) {
					location = loc;
				}
			}

			public void onStatusChanged(String provider, int status,
					Bundle extras) {
			}

			public void onProviderEnabled(String provider) {
				Toast.makeText(getApplicationContext(), "GPS Enabled",
						Toast.LENGTH_SHORT).show();
			}

			public void onProviderDisabled(String provider) {
				Toast.makeText(getApplicationContext(), "GPS Disabled",
						Toast.LENGTH_SHORT).show();
			}
		};
	}

	@Override
	protected void onPause() {
		super.onPause();
		sReceiver.setReceiver(null);
		// Remove the listener you previously added
		locationManager.removeUpdates(locationListener);
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Register the listener with the Location Manager to receive location
		// updates
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1,
				0, locationListener);

		// Service resultReceiver
		sReceiver.setReceiver(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_fish, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * What to do when we receive a result from the ServiceIntent
	 */
	@Override
	public void onReceiveResult(int resultCode, Bundle resultData) {
		if (resultCode == 0) {
			Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, "Failure", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Set up UI elements
	 */
	public void initView() {
		button_submit = (Button) this.findViewById(R.id.button_submit);
		button_submit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				submitFish();
			}

		});
		editText_note = (EditText) this.findViewById(R.id.editText_note);
		editText_length = (EditText) this.findViewById(R.id.editText_length);
		editText_weight = (EditText) this.findViewById(R.id.editText_weight);
		editText_bait = (EditText) this.findViewById(R.id.editText_bait);
		editText_conditions = (EditText) this
				.findViewById(R.id.editText_conditions);

		spinner_species = (Spinner) this.findViewById(R.id.spinner_species);

		switch_gps = (Switch) this.findViewById(R.id.switch_gps);
		includeGPS = false;
		switch_gps.setChecked(false);
		switch_gps.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				includeGPS = !includeGPS;

			}

		});

	}

	/**
	 * Submit button onClick
	 */
	public void submitFish() {
		String note = "";
		double weight = 0;
		double length = 0;
		String bait = "";
		String species = "Lg Mouth Bass";
		String conditions = "";
		String longitude = "null long";
		String latitude = "null lat";

		// Collect info from form
		note = editText_note.getText().toString();
		length = Double.parseDouble(editText_length.getText().toString());
		weight = Double.parseDouble(editText_weight.getText().toString());
		bait = editText_bait.getText().toString();
		conditions = editText_conditions.getText().toString();

		// Get GPS datalocation
		if (includeGPS) {
			System.out.println("GET GPS DATA");
			location = locationManager
					.getLastKnownLocation(locationManager.GPS_PROVIDER);
			if (location != null) {
				System.out.println("location != null");
				longitude = Double.toString(location.getLongitude());
				latitude = Double.toString(location.getLatitude());
			}
		}

		// Error check
		System.out.println("Key: " + connectionKey);

		// Submit
		Intent intent = new Intent(this, DBService.class);
		intent.putExtra("receiver", sReceiver);
		intent.putExtra("connectionKey", connectionKey);
		intent.putExtra("command", "add");
		intent.putExtra("note", note);
		intent.putExtra("species", species);
		intent.putExtra("bait", bait);
		intent.putExtra("conditions", conditions);
		intent.putExtra("longitude", longitude);
		intent.putExtra("latitude", latitude);
		intent.putExtra("length", length);
		intent.putExtra("weight", weight);
		startService(intent);
	}
}
