package com.soccer;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.soccer.Soccer;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

//		try {
//			PackageInfo info = getPackageManager().getPackageInfo(
//					"com.soccer",
//					PackageManager.GET_SIGNATURES);
//			for (Signature signature : info.signatures) {
//				MessageDigest md = MessageDigest.getInstance("SHA");
//				md.update(signature.toByteArray());
//				Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//				System.out.println(Base64.encodeToString(md.digest(), Base64.DEFAULT));
//
//			}
//		} catch (PackageManager.NameNotFoundException e) {
//
//		} catch (NoSuchAlgorithmException e) {
//
//		}


		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.numSamples = 2;
		initialize(new Soccer(), config);
	}
}
