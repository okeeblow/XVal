package org.cooltrainer.xval;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager.NameNotFoundException;
import android.text.SpannableString;
import android.text.util.Linkify;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AboutBox {
	static String VersionName(Context context) {
		try {
			return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			return "Unknown";
		}
	}

	public static void Show(final Activity callingActivity) {
		// Use a Spannable to allow for links highlighting
		SpannableString aboutText = new SpannableString(callingActivity.getString(R.string.version) + " " + VersionName(callingActivity) + "\n\n"
				+ callingActivity.getString(R.string.aboutText));
		
		View about;
		TextView tvAbout;
		try {
			LayoutInflater inflater = callingActivity.getLayoutInflater();
			about = inflater.inflate(R.layout.aboutbox,	(ViewGroup) callingActivity.findViewById(R.id.aboutView));
			tvAbout = (TextView) about.findViewById(R.id.aboutText);
		} catch (InflateException e) {
			//Default to TextView if Inflater fails
			about = tvAbout = new TextView(callingActivity);
		}
		tvAbout.setText(aboutText);
		Linkify.addLinks(tvAbout, Linkify.WEB_URLS | Linkify.EMAIL_ADDRESSES);
		
		new AlertDialog.Builder(callingActivity)
				.setTitle(callingActivity.getString(R.string.about) + " " + callingActivity.getString(R.string.app_name))
				.setCancelable(true).setIcon(R.drawable.ic_launcher)
				.setPositiveButton(callingActivity.getString(R.string.ok), null)
				.setNeutralButton(callingActivity.getString(R.string.license), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						License.Show(callingActivity);
					}
				})
				.setView(about).show();
	}
}