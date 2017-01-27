package com.cbcnewmedia.cast;

import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.view.TiUIView;

import android.support.v7.app.MediaRouteButton;
import android.view.View;

import com.google.android.gms.cast.framework.CastButtonFactory;

public class MediaRouterView extends TiUIView {

	private static final String TAG = "MediaRouterView";
	private MediaRouteButton mediaRouteButton;
	View mediaRouterWrapper;

	public MediaRouterView(TiViewProxy proxy) {
		super(proxy);
	
		mediaRouteButton = new MediaRouteButton(proxy.getActivity());
		CastButtonFactory.setUpMediaRouteButton(proxy.getActivity(), mediaRouteButton);
		
		setNativeView(mediaRouteButton);
	
	}	
	
}