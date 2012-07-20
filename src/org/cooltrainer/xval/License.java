package org.cooltrainer.xval;

import java.io.InputStream;
import android.app.Activity;
import android.app.AlertDialog;
import android.text.SpannableString;
import android.text.util.Linkify;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class License {
	private static String convertStreamToString(java.io.InputStream is) {
		try {
			return new java.util.Scanner(is).useDelimiter("\\A").next();
		} catch (java.util.NoSuchElementException e) {
			return "";
		}
	}

	
	public static void Show(Activity callingActivity) {
		InputStream is = callingActivity.getResources().openRawResource(R.raw.gpl3);
		
		// Use a Spannable to allow for links highlighting
		SpannableString licenseText = new SpannableString(convertStreamToString(is));
		
		View licenseView;
		TextView tvLicense;
		try {
			LayoutInflater inflater = callingActivity.getLayoutInflater();
			licenseView = inflater.inflate(R.layout.license,	(ViewGroup) callingActivity.findViewById(R.id.licenseView));
			tvLicense = (TextView) licenseView.findViewById(R.id.licenseText);
		} catch (InflateException e) {
			//Default to TextView if Inflater fails
			licenseView = tvLicense = new TextView(callingActivity);
		}
		tvLicense.setText(licenseText);
		Linkify.addLinks(tvLicense, Linkify.WEB_URLS | Linkify.EMAIL_ADDRESSES);
		
		new AlertDialog.Builder(callingActivity)
				.setTitle(callingActivity.getString(R.string.app_name) + " " + callingActivity.getString(R.string.license))
				.setCancelable(true).setIcon(R.drawable.ic_launcher)
				.setPositiveButton(callingActivity.getString(R.string.ok), null).setView(licenseView).show();
	}
}