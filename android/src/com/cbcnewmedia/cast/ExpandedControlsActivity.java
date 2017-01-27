package com.cbcnewmedia.cast;

import android.content.res.Resources;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.MediaRouteActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.media.widget.ExpandedControllerActivity;

public class ExpandedControlsActivity extends ExpandedControllerActivity {
	
		private static final String TAG = "ExpandedControlsActivity";
	
	 	@Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        super.onCreateOptionsMenu(menu);
	        
	        String packageName = this.getPackageName();
			Resources resources = this.getResources();
			
			int resId_ExpandedView = resources.getIdentifier("expanded_view", "menu", packageName);
			int resId_RouteButton = resources.getIdentifier("media_route_menu_item", "id", packageName);
			
			MenuInflater inflater = new MenuInflater(this);
			inflater.inflate(resId_ExpandedView, menu);
			
			MenuItem routeMenuItem = menu.findItem(resId_RouteButton);
			
			//The MediaRouteActionProvider is set up in the xml file, but for some reason this is returning null so I have to explicitly make one.
			//This should probably be looked at, but for now it is working
			MediaRouteActionProvider localMediaRouteActionProvider = (MediaRouteActionProvider)MenuItemCompat.getActionProvider(routeMenuItem);
			
			if(localMediaRouteActionProvider ==  null)
			{
				Log.d(TAG, "MediaRouteActionProvider is null, creating a new one.");
				MediaRouteActionProvider provider = new MediaRouteActionProvider(this);
				MenuItemCompat.setActionProvider(routeMenuItem, provider);
			}
			
	        CastButtonFactory.setUpMediaRouteButton(this, menu, resId_RouteButton);
	        return true;
	    }

}
