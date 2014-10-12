package com.fishloggerpro.act;

import com.fishloggerpro.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MenuActivity extends Activity {

	Button buttonAddFish, buttonSearchLog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
		initView();

	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu, menu);
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
	 * Initializes layout vars
	 */
	public void initView() {

		buttonAddFish = (Button) this.findViewById(R.id.button_add_fish);
		buttonAddFish.setOnClickListener(new OnClickListener() {
			/**
			 * Go to add fish page
			 */
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MenuActivity.this,
						AddFishActivity.class);
				MenuActivity.this.startActivity(intent);
			}

		});

		buttonSearchLog = (Button) this.findViewById(R.id.button_search_log);
		// buttonSearchLog.setOnClickListener(new OnClickListener() {
		// /**
		// * Query fish database
		// */
		// @Override
		// public void onClick(View arg0) {
		// // Intent intent = new Intent(MenuActivity.this,
		// // AddFishActivity.class);
		// // MenuActivity.this.startActivity(intent);
		// }
		//
		// });

	}

}
