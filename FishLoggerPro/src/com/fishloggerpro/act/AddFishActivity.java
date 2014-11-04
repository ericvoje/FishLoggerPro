package com.fishloggerpro.act;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.fishloggerpro.R;
import com.fishloggerpro.srv.DBService;
import com.fishloggerpro.srv.ServiceResultReceiver;
import com.fishloggerpro.srv.ServiceResultReceiver.Receiver;

public class AddFishActivity extends Activity implements Receiver {

	private Button button_submit, button_photo;
	private EditText editText_note, editText_length, editText_weight,
			editText_bait, editText_conditions;
	private Spinner spinner_species;
	private Switch switch_gps;
	private Location location;
	private File image;
	private Bitmap bitmap;
	private ImageView imageView_photo;
	private boolean includeGPS;

	private String connectionKey;

	private ServiceResultReceiver sReceiver;

	private LocationManager locationManager;
	LocationListener locationListener;

	static final int REQUEST_IMAGE_CAPTURE = 1;
	static final int REQUEST_SAVED_IMAGE = 2;
	static final int RESULT_OK = 3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_fish);
		initView();
		image = null;
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
	protected void onPause() {
		super.onPause();
		sReceiver.setReceiver(null);
		// Remove the listener you previously added
		locationManager.removeUpdates(locationListener);
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
		/* Submit button */
		button_submit = (Button) this.findViewById(R.id.button_submit);
		button_submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				submitFish();
			}
		});

		/* Select photo button and dialog */
		imageView_photo = (ImageView) this.findViewById(R.id.imageView_photo);
		button_photo = (Button) this.findViewById(R.id.button_photo);
		button_photo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						v.getContext());
				alertDialogBuilder.setMessage(R.string.dialog_photo);
				alertDialogBuilder.setPositiveButton(R.string.dialog_new,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								Intent intent = new Intent(
										MediaStore.ACTION_IMAGE_CAPTURE);
								if (intent.resolveActivity(getPackageManager()) != null) {
									/*
									 * String filename = genFilename(); File
									 * file = new File(AddFishActivity.this
									 * .getBaseContext().getFilesDir(),
									 * filename);
									 */
									startActivityForResult(intent,
											REQUEST_IMAGE_CAPTURE);
								}
							}
						});
				alertDialogBuilder.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {

							}
						});
				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
			}
		});

		/* Text fields */
		editText_note = (EditText) this.findViewById(R.id.editText_note);
		editText_length = (EditText) this.findViewById(R.id.editText_length);
		editText_weight = (EditText) this.findViewById(R.id.editText_weight);
		editText_bait = (EditText) this.findViewById(R.id.editText_bait);
		editText_conditions = (EditText) this
				.findViewById(R.id.editText_conditions);

		/* Species spinner */
		spinner_species = (Spinner) this.findViewById(R.id.spinner_species);

		/* GPS */
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == REQUEST_IMAGE_CAPTURE) {
			if (resultCode == RESULT_OK) {
				Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
				imageView_photo.setImageBitmap(imageBitmap);
			} else {

			}
		} else if (requestCode == REQUEST_SAVED_IMAGE) {
			if (resultCode == RESULT_OK) {

			} else {

			}
		}
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

	/* Creates a filename based on the current time */
	private String genFilename() {
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		int month = cal.get(Calendar.MONTH);
		int day = cal.get(Calendar.DAY_OF_MONTH);
		int hour = cal.get(Calendar.HOUR);
		int minute = cal.get(Calendar.MINUTE);
		int second = cal.get(Calendar.SECOND);

		StringBuilder sb = new StringBuilder();
		if (month < 10) {
			sb.append("0");
			sb.append(month);
		} else {
			sb.append(month);
		}
		if (day < 10) {
			sb.append("0");
			sb.append(day);
		} else {
			sb.append(day);
		}
		sb.append(year);
		sb.append("_");
		if (hour < 10) {
			sb.append("0");
			sb.append(hour);
		} else {
			sb.append(hour);
		}
		if (minute < 10) {
			sb.append("0");
			sb.append(minute);
		} else {
			sb.append(minute);
		}
		if (second < 10) {
			sb.append("0");
			sb.append(second);
		} else {
			sb.append(second);
		}
		return sb.toString();
	}
}
