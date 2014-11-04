package com.fishloggerpro.act;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.fishloggerpro.R;
import com.fishloggerpro.srv.DBService;
import com.fishloggerpro.srv.ServiceResultReceiver;
import com.fishloggerpro.srv.ServiceResultReceiver.Receiver;

public class SplashActivity extends Activity implements Receiver {

	private ServiceResultReceiver sReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		sReceiver = new ServiceResultReceiver(new Handler());

		Intent intent = new Intent(this, DBService.class);
		intent.putExtra("command", "login");
		intent.putExtra("receiver", sReceiver);
		startService(intent);
	}

	@Override
	protected void onPause() {
		super.onPause();
		sReceiver.setReceiver(null);
	}

	@Override
	protected void onResume() {
		super.onResume();
		sReceiver.setReceiver(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.splash, menu);
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

	@Override
	public void onReceiveResult(int resultCode, Bundle resultData) {
		Intent intent = new Intent(this, AddFishActivity.class);
		intent.putExtra("connectionKey", resultData.getString("connectionKey"));
		startActivity(intent);
		/*
		 * if (resultCode == 0) { } else {
		 * Toast.makeText(getApplicationContext(), "Login Failed",
		 * Toast.LENGTH_SHORT).show(); }
		 */

	}
}
