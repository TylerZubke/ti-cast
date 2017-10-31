package com.cbcnewmedia.cast;

import java.util.ArrayList;
import java.util.HashMap;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.AsyncResult;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.common.TiMessenger;
import org.appcelerator.titanium.TiApplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;

import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaStatus;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.CastStateListener;
import com.google.android.gms.cast.framework.SessionManagerListener;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;

@Kroll.proxy(creatableInModule=TiCastModule.class)
public class CastProxy extends KrollProxy{

	private static final String TAG = "CastProxy";
	private CastContext castContext;
	private CastStateListener castStateListener;
	private SessionManagerListener<CastSession> sessionManagerListener; 
	private CastSession castSession;
	private MediaItem mediaItem;
	private int castState;
	private boolean expandedControllerLaunched;
	
	private static final int MSG_CAST_STATE = 10000;
	private static final int MSG_SETUP_CTX = 20000;
	private static final int MSG_START_CAST = 30000;
	private static final int MSG_SESSION_STATE = 40000;
	
	public CastProxy() {
		super();
		Log.d(TAG, "In constructor");
		setupCastContext();
	}
	
	@Override
	public boolean handleMessage(final Message msg){
		switch (msg.what) {
		case MSG_CAST_STATE: {
            ((AsyncResult) msg.obj).setResult(castState);
            return true;
        }
        case MSG_SETUP_CTX: {
        	handleSetupCastContext();
            ((AsyncResult) msg.obj).setResult(null);
            return true;
        }
        case MSG_START_CAST: {
        	AsyncResult result = (AsyncResult)msg.obj;
        	KrollDict data = (KrollDict) result.getArg();
        	MediaInfo mediaInfo = (MediaInfo) data.get("info");
        	boolean autoplay = (boolean) data.getBoolean("autoplay");
        	int startPosition = (int) data.getInt("startPosition");
        	handleCastVideo(mediaInfo, autoplay, startPosition);
        	((AsyncResult) msg.obj).setResult(null);
    		return true;
    		
        }
        case MSG_SESSION_STATE: {
        	int state = -1;
        	if(castSession != null) { 
        		RemoteMediaClient remoteMediaClient = castSession.getRemoteMediaClient();
        		if(remoteMediaClient != null) {
        			state = remoteMediaClient.getPlayerState();
        		}
        	}
			
            ((AsyncResult) msg.obj).setResult(state);
            return true;
        }
		 	default:
	            return super.handleMessage(msg);
		}
		
	}
	
	private void setupCastContext()
	{
		if(!TiApplication.isUIThread()) {
			TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_SETUP_CTX));
		} else {
			handleSetupCastContext();
		}
	}
	
	private void handleSetupCastContext()
	{
		Activity activity = TiApplication.getAppRootOrCurrentActivity();
		castContext = CastContext.getSharedInstance(activity);
		//if app was already connected to application, try to get session
		castSession = castContext.getSessionManager().getCurrentCastSession();
		setupCastStateListener();
		setupSessionListener();
		
	}
	
	@Kroll.method
	public int getCastState()
	{
		if(!TiApplication.isUIThread()) {
			Object result = TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_CAST_STATE));
			if(result instanceof Integer) {
				return (Integer) result;
			}
			else {
				return -1;
			}
		} else {
			return castState;
		}
	}
	
	@Kroll.method
	public int getSessionState()
	{
		if(!TiApplication.isUIThread()) {
			Object result = TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_SESSION_STATE));
			if(result instanceof Integer) {
				return (Integer) result;
			}
			else {
				return -1;
			}
		} else {
			if(castSession == null) { return -1; }
			RemoteMediaClient remoteMediaClient = castSession.getRemoteMediaClient();
			if(remoteMediaClient == null) { return -1; }
			return remoteMediaClient.getPlayerState();
		}
	}
	
	
	@Kroll.method
	public void castVideo(HashMap args){
		
		KrollDict dict = new KrollDict(args);
		
		MediaItem media = new MediaItem();
		
		String url = dict.optString("url", null);
		String contentType = dict.optString("contentType", "videos/mp4");
		int duration = dict.optInt("duration", -1);
		boolean autoplay = dict.optBoolean("autoplay", true);
		int startPosition = dict.optInt("startPosition", 0);
		
		media.setUrl(url);
		media.setContentType(contentType);
		media.setDuration(duration);
		
		KrollDict metadata = dict.getKrollDict("metadata");
		
		if(metadata.containsKey("images"))
		{
			Object[] images = (Object[])metadata.get("images");
			ArrayList<String> imageList = new ArrayList<String>();
			for(Object image : images){
				imageList.add((String)image);
			}	
			media.setImageList(imageList);
		}
		if(metadata.containsKey("title"))
		{
			String title = metadata.optString("title", null);
			media.setTitle(title);
		}
		if(metadata.containsKey("subtitle"))
		{
			String subtitle = metadata.optString("subtitle", null);
			media.setSubTitle(subtitle);
		}
		
		MediaInfo mediaInfo = media.buildMediaInfo();
		
		KrollDict data = new KrollDict();
		data.put("info", mediaInfo);
		data.put("autoplay", autoplay);
		data.put("startPosition", startPosition);
		
		if(!TiApplication.isUIThread()) {
			TiMessenger.sendBlockingMainMessage(getMainHandler().obtainMessage(MSG_START_CAST), data);
		} else {
			handleCastVideo(mediaInfo, autoplay, startPosition);
		}
	}
	
	private void handleCastVideo(MediaInfo mediaInfo, boolean autoplay, long startPosition)
	{
		expandedControllerLaunched = false;
		
		if(castSession == null) { Log.d(TAG, "Unable to cast video. CastSession is null."); return; }
		if(!castSession.isConnected()) { Log.d(TAG, "Unable to cast video. Not connected to device."); return; }
		
		final RemoteMediaClient remoteMediaClient = castSession.getRemoteMediaClient();
		if(remoteMediaClient == null) { Log.d(TAG, "Unable to cast video. RemoteMediaClient is null."); return; }
		
		Log.d(TAG, "Starting cast with values { start position: " + startPosition + ", autoplay: " + autoplay + " }");
		remoteMediaClient.load(mediaInfo, autoplay, startPosition);
		
		remoteMediaClient.addListener(new RemoteMediaClient.Listener() {
            @Override
            public void onStatusUpdated() {
            	Log.d(TAG, "onStatusUpdated");
            	if(!expandedControllerLaunched)
            	{
            		Log.d(TAG, "Launching ExpandedControlsActivity");
            		Intent intent = new Intent(getActivity(), ExpandedControlsActivity.class);
            		getActivity().startActivity(intent);
            		expandedControllerLaunched = true;
            	}
            	else
            	{
            		if(remoteMediaClient.getPlayerState() == MediaStatus.PLAYER_STATE_IDLE
            			&& remoteMediaClient.getPlayerState() == MediaStatus.IDLE_REASON_FINISHED)
            		{
            			fireEvent("videoEnded", null);
            			remoteMediaClient.removeListener(this);
            		}
            	}
            }

            @Override
            public void onMetadataUpdated() { Log.d(TAG, "onMetadataUpdated"); }

            @Override
            public void onQueueStatusUpdated() { Log.d(TAG, "onQueueStatusUpdated"); }

            @Override
            public void onPreloadStatusUpdated() { Log.d(TAG, "onPreloadStatusUpdated"); }

            @Override
            public void onSendingRemoteMediaRequest() { Log.d(TAG, "onSendingRemoteMediaRequest"); }

			@Override
			public void onAdBreakStatusUpdated() { Log.d(TAG, "onAdBreakStatusUpdated"); }

        });
	}
	
	private void setupCastStateListener()
	{
		castStateListener = new CastStateListener() {
	          @Override
	          public void onCastStateChanged(int newState) {
	        	  castState = newState;
	        	  HashMap<String, Object> event = new HashMap<String, Object>();
	              event.put("state", newState);
	              if(newState == 4){ 
		              //the sessionListener fires after the stateChange so if a user is casting based on castListener values, get session here otherwise it may be null in castVideoHandler
		              castSession = castContext.getSessionManager().getCurrentCastSession();
	              }
          		
          		fireEvent("castStateChange", event);
	          }
	      };
	   castContext.addCastStateListener(castStateListener);
	}
	
	private void setupSessionListener()
	{
		sessionManagerListener = new SessionManagerListener<CastSession>() {
			@Override
            public void onSessionEnded(CastSession session, int error) {
	            HashMap<String, Object> event = new HashMap<String, Object>();
	            event.put("error", error);
	            fireEvent("sessionEnded", event);
	            onApplicationDisconnected();
            }

            @Override
            public void onSessionResumed(CastSession session, boolean wasSuspended) {
            	Log.d(TAG, "onSessionResumed");
                onApplicationConnected(session);
            }

            @Override
            public void onSessionResumeFailed(CastSession session, int error) {
            	Log.d(TAG, "onSessionResumeFailed (" + error + ")");
                onApplicationDisconnected();
            }

            @Override
            public void onSessionStarted(CastSession session, String sessionId) {
            	Log.d(TAG, "onSessionStarted (" + sessionId + ")");
                onApplicationConnected(session);
            }

            @Override
            public void onSessionStartFailed(CastSession session, int error) {
            	Log.d(TAG, "onSessionStartFailed (" + error + ")");
                onApplicationDisconnected();
            }

            @Override
            public void onSessionStarting(CastSession session) {
            	Log.d(TAG, "onSessionStarting");
            }

            @Override
            public void onSessionEnding(CastSession session) {
            	Log.d(TAG, "onSessionEnding");
            }

            @Override
            public void onSessionResuming(CastSession session, String sessionId) {
            	Log.d(TAG, "onSessionResuming (" + sessionId + ")");
            }

            @Override
            public void onSessionSuspended(CastSession session, int reason) {
            	Log.d(TAG, "onSessionSuspended  (" + reason + ")");
            }

            private void onApplicationConnected(CastSession session) {
                castSession = session;
                Log.d(TAG, "Application Connected");
            }

            private void onApplicationDisconnected() {
            	Log.d(TAG, "Application Disconnected");
            }
        };
        
        //add listener here in case lifecycle events don't get called properly
        castContext.getSessionManager().addSessionManagerListener(sessionManagerListener, CastSession.class);
        
	}
	
	@Override
    public void onResume(Activity activity) {
        Log.d(TAG, "onResume called");
        super.onResume(activity);
        castContext.addCastStateListener(castStateListener);
        castContext.getSessionManager().addSessionManagerListener(sessionManagerListener, CastSession.class);
    }
	
	@Override
    public void onCreate(Activity activity, Bundle savedInstanceState) {
        Log.d(TAG, "onCreate called");
        super.onCreate(activity, savedInstanceState);
    }
	
	@Override
    public void onPause(Activity activity) {
		Log.d(TAG, "onPause called");
		super.onPause(activity);
		castContext.removeCastStateListener(castStateListener);
        castContext.getSessionManager().removeSessionManagerListener(
                sessionManagerListener, CastSession.class);
    }
	
}
