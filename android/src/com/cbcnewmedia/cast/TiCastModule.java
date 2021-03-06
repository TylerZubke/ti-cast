/**
 * This file was auto-generated by the Titanium Module SDK helper for Android
 * Appcelerator Titanium Mobile
 * Copyright (c) 2009-2010 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 *
 */
package com.cbcnewmedia.cast;

import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.common.TiConfig;
import org.appcelerator.titanium.TiApplication;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

@Kroll.module(name="TiCast", id="com.cbcnewmedia.cast")
public class TiCastModule extends KrollModule
{

	// Standard Debugging variables
	private static final String LCAT = "TiCastModule";
	private static final boolean DBG = TiConfig.LOGD;

	// You can define constants with @Kroll.constant, for example:
	// @Kroll.constant public static final String EXTERNAL_NAME = value;

	public TiCastModule()
	{
		super();
	}

	@Kroll.onAppCreate
	public static void onAppCreate(TiApplication app)
	{
		Log.d(LCAT, "inside onAppCreate");
		// put module init code that needs to run when the application is created
	}

	// Methods
	@Kroll.method
	public boolean isGooglePlayServicesAvailable()
	{
		GoogleApiAvailability googleApi = GoogleApiAvailability.getInstance();
		int result = googleApi.isGooglePlayServicesAvailable(TiApplication.getAppRootOrCurrentActivity());
		if(result != ConnectionResult.SUCCESS)
		{
		    return false;
		}
		
		return true;
	}
}

