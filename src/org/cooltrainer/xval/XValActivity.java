package org.cooltrainer.xval;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class XValActivity extends Activity {
	private EditText serial, xval;
	private TextView mfgDateText, mfgDate, factoryText, factory, resultText, result, flags;
	private static final String TAG = "XVal";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        serial = (EditText)findViewById(R.id.serial);
        xval = (EditText)findViewById(R.id.xval);
        
        mfgDateText = (TextView)findViewById(R.id.mfgdatetext);
        mfgDate = (TextView)findViewById(R.id.mfgdate);
        factoryText = (TextView)findViewById(R.id.factorytext);
        factory = (TextView)findViewById(R.id.factory);
        resultText = (TextView)findViewById(R.id.resulttext);
        result = (TextView)findViewById(R.id.result);
        flags = (TextView)findViewById(R.id.flags);
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mainmenu, menu);
		return true;
	}
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.about:
			AboutBox.Show(this);
			break;
		case R.id.help:
			Help.Show(this);
			break;
		default:
			break;
		}
		return true;
	}
    
    public void butanHandler(View view) {
		switch (view.getId()) {
		case R.id.presbutan:
			String inputSerial = serial.getText().toString();
			String inputXVal = xval.getText().toString();
			
			boolean valid = true;
			if(!Xbox360.validSerial(inputSerial))
			{
				serial.setError(this.getString(R.string.invalidSerial));
				valid = false;
			}
			if(!Xbox360.validXVal(inputXVal))
			{
				xval.setError(this.getString(R.string.invalidXval));
				valid = false;
			}
			//Don't continue butan action if input data isn't valid
			if(!valid) {
				CoolLog.i(TAG, "User entered an invalid serial or XVal");
				return;
			}
			
			Xbox360 xbox = new Xbox360(inputSerial);
			
			try {
				XVal xVal = new XVal(inputSerial, inputXVal);

				if (xVal.isValidPair()) {
					resultText.setText(String.valueOf(this.getString(R.string.thisconsoleis)));
				} else {
					resultText.setText(String.valueOf(this.getString(R.string.invalidPair)));
					result.setText(String.valueOf(""));
					mfgDateText.setText(String.valueOf(""));
					mfgDate.setText(String.valueOf(""));
					factoryText.setText(String.valueOf(""));
					factory.setText(String.valueOf(""));
					flags.setText(String.valueOf(""));
					return;
				}

				int tagColour = (xVal.isClean()) ? Color.GREEN : Color.RED;
				int tag = (xVal.isClean()) ? R.string.clean : R.string.flagged;

				mfgDateText.setText(String.valueOf(this.getString(R.string.mfgdate)));
				mfgDate.setText(String.valueOf(this.getString(R.string.weekof) + " " + xbox.mfgDate()));
				factoryText.setText(String.valueOf(this.getString(R.string.factory)));
				factory.setText(String.valueOf(xbox.factoryName()));

				flags.setText(String.valueOf(xVal.flags()));

				result.setText(this.getString(tag));
				result.setTextColor(tagColour);
			} catch (XValException e) {
				CoolLog.e(TAG, e.getMessage(), e);
			}

			break;
		}
	}
}