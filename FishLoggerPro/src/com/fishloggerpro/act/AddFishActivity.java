package com.fishloggerpro.act;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.fishloggerpro.R;

public class AddFishActivity extends Activity {

	Button button_submit;
	EditText editText_note, editText_length, editText_weight;
	Spinner spinner_species;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_fish);
		initView();
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
	}

	public void submitFish() {
		String note = "";
		int weight = 0;
		int length = 0;

		// Collect info from form
		note = editText_note.getText().toString();
		length = Integer.getInteger(editText_length.getText().toString(), 0);
		weight = Integer.getInteger(editText_weight.getText().toString(), 0);

		// Error check

		// Build database string

		// Submit

		Toast t = Toast.makeText(getApplicationContext(), "Submitted",
				Toast.LENGTH_SHORT);
		t.show();
	}

}
