package com.cbcnewmedia.cast;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.view.TiUIView;

import android.app.Activity;

@Kroll.proxy(creatableInModule=TiCastModule.class)
public class MediaRouterProxy extends TiViewProxy {
	
	private static final String TAG = "MediaRouterProxy";
	
	public MediaRouterProxy() {
		super();
		Log.d(TAG, "In constructor");
	}
	
	@Override
	public TiUIView createView(Activity activity)
	{
		MediaRouterView view = new MediaRouterView(this);
		return view;
	}
	
	@Override
	public void handleCreationDict(KrollDict options) 
	{
		super.handleCreationDict(options);
	}

}
