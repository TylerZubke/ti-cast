package com.cbcnewmedia.cast;

import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.view.TiUIView;

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;

public class MediaControllerView extends TiUIView {

	private static final String TAG = "MediaControllerView";

	public MediaControllerView(TiViewProxy proxy) {
		super(proxy);
		
		String packageName = proxy.getActivity().getPackageName();
		Resources resources = proxy.getActivity().getResources();
		int resId_ControllerWrapper = resources.getIdentifier("media_controller", "layout", packageName);
				
		LayoutInflater inflater = LayoutInflater.from(proxy.getActivity());
		
		View miniControllerWrapper = inflater.inflate(resId_ControllerWrapper, null);
		
		setNativeView(miniControllerWrapper);
	
	}	
	
}