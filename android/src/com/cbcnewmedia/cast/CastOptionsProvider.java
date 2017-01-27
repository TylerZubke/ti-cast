package com.cbcnewmedia.cast;

import java.util.List;

import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.util.TiRHelper;
import org.appcelerator.titanium.util.TiRHelper.ResourceNotFoundException;

import android.content.Context;

import com.google.android.gms.cast.framework.CastOptions;
import com.google.android.gms.cast.framework.OptionsProvider;
import com.google.android.gms.cast.framework.SessionProvider;
import com.google.android.gms.cast.framework.media.CastMediaOptions;
import com.google.android.gms.cast.framework.media.NotificationOptions;


public class CastOptionsProvider implements OptionsProvider {
	
	private static final String TAG = "CastOptionsProvider";
	private int appIdResource = 0;
	
	 CastMediaOptions mediaOptions = new CastMediaOptions.Builder()
	 	.setExpandedControllerActivityClassName(ExpandedControlsActivity.class.getName())
	 	.build();
	 
	@Override
    public CastOptions getCastOptions(Context context) {
		 try{
			 appIdResource = TiRHelper.getResource("string.app_id");
		 } catch(ResourceNotFoundException e){
			 Log.e(TAG, "You must pass declare a string resource called app_id containing your the id of your Chromecast application");
		 }
        
		String appId = context.getString(appIdResource);
		return new CastOptions.Builder()	
	        .setReceiverApplicationId(appId)
	        .setCastMediaOptions(mediaOptions)
	        .build();
    }

    @Override
    public List<SessionProvider> getAdditionalSessionProviders(Context context) {
        return null;
    }
}