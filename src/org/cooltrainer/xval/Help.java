package org.cooltrainer.xval;

import android.app.Activity;
import android.app.AlertDialog;
import android.text.SpannableString;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Help {
	public static void Show(Activity callingActivity) {
		// Use a Spannable to allow for links highlighting
		SpannableString helpText = new SpannableString(callingActivity.getString(R.string.helpText));
		
		View help;
		TextView tvHelp;
		try {
			LayoutInflater inflater = callingActivity.getLayoutInflater();
			help = inflater.inflate(R.layout.help,	(ViewGroup) callingActivity.findViewById(R.id.helpView));
			tvHelp = (TextView) help.findViewById(R.id.helpText);
		} catch (InflateException e) {
			//Default to TextView if Inflater fails
			help = tvHelp = new TextView(callingActivity);
		}
		tvHelp.setText(helpText);
		
		new AlertDialog.Builder(callingActivity)
				.setTitle(callingActivity.getString(R.string.app_name) + " " + callingActivity.getString(R.string.help))
				.setCancelable(true).setIcon(R.drawable.ic_launcher)
				.setPositiveButton(callingActivity.getString(R.string.ok), null).setView(help).show();
	}
}