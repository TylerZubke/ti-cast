package com.cbcnewmedia.cast;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.view.TiUIView;

import android.app.Activity;
import android.view.View;

@Kroll.proxy(creatableInModule=TiCastModule.class)
public class MediaControllerProxy extends TiViewProxy {
	
	private static final String TAG = "MediaControllerProxy";
	View mediaRouterWrapper;
	
	public MediaControllerProxy() {
		super();
		Log.d(TAG, "In constructor");
	}
	
	@Override
	public TiUIView createView(Activity activity)
	{
		MediaControllerView view = new MediaControllerView(this);
		return view;
	}
	
	@Override
	public void handleCreationDict(KrollDict options) 
	{
		super.handleCreationDict(options);
	}
	
	
}
