TI-CAST
===========================================

This is a module that adds Chromecast support to your Titanium android app.
Right now the module only supports casting video.

USING TI-CAST
-------------------------

1. Open the strings.xml file within the module (path should be something like [PROJECT_ROOT]/modules/android/com.cbcnewmedia.cast/1.0.0/platform/android/res/values/strings.xml)
I wish this could be provided when instantiating the module in the javascript, but the class that uses this value is provided through a meta-data tag in the timodule.xml.

Now require the module.
	var ti_cast = require('com.cbcnewmedia.cast');

COMPONENTS
-------------------------

This module has 3 main components:

1. MediaRouter -- this is just a MediaRouter button that opens a modal and allows a user to start a cast or stop a cast.
Create the view 
	`var media_route_button = ti_cast.createMediaRouter({lifecycleContainer: win});`
and add it to a window in onCreateOptionsMenu
	`win.activity.onCreateOptionsMenu = function(e){
		e.menu.add({
            actionView: media_route_button,
            showAsAction: Titanium.Android.SHOW_AS_ACTION_ALWAYS
        });
	};`
	
NOTE: When creating the view you will most likely need to pass in a lifecycleContainer or you will get an error.

2. MediaController -- this is a MiniControllerFragment, AKA a small bar you can show on each window that only is visible while the user is casting something
Again, create the view
	`var mini_controller = ti_cast.createMediaController({lifecycleContainer: win});`

Give it a bottom of 0 so it is at the bottom of the window and add it
	`mini_controller.bottom = 0;`
	`win.add(mini_controller);`

Once you start casting this will make itself visible.

3. ExpandedControllerActivity -- this is the main view that shows when the user is casting. You don't create it in javascript. Instead, the module makes it when you start casting a video.
You can change the buttons that show up on this view by altering the arrays.xml file in platform/res/. For available buttons check https://developers.google.com/android/reference/com/google/android/gms/cast/framework/media/widget/ExpandedControllerActivity.

4. CastProxy -- this is what sets up all cast contexts and allows you to start a cast and listen to events. YOU MUST INSTANTIATE THIS BEFORE CREATING THE OTHER COMPONENTS.
	
	`var proxy = ti_cast.createCast({lifecycleContainer: win});`

Listen for castStateChange event and once connected to application start casting
	
	`proxy.addEventListener('castStateChange', function(e){
		if(e.state == 4)
		{
			proxy.castVideo(cast_obj);
		} 
	});`

The object you pass to castVideo has the following properties you can pass:

`url` (required): url of the video (String)

`contentType` (I don't think this is required depending on what type of video you are playing but it's good to include it): video content type (String)

`duration` (default: UNKNOWN_DURATION is passed to the cast library): duration of video in milliseconds or -1 if video is live (Number)

`autoplay` (default: true): should video start playing automatically or wait for user to push play (boolean)

`startPosition` (default: 0): what position video starts casting from in milliseconds (Number)

`metadata`: 
	title: String
	subtitle: String
	images: Array of strings containing links to video images
	
Look at app.js for specific example of this object.

The castProxy also has a few other methods exposed:
`int getCastState()`

docs: https://developers.google.com/android/reference/com/google/android/gms/cast/framework/CastState

return values:
1 = NO_DEVICES_AVAILABLE
2 = NOT_CONNECTED
3 = CONNECTING
4 = CONNECTED

`int getSessionState()`

docs: https://developers.google.com/android/reference/com/google/android/gms/cast/framework/media/RemoteMediaClient#getPlayerState()

0 = PLAYER_STATE_UNKNOWN
1 = PLAYER_STATE_IDLE
2 = PLAYER_STATE_PLAYING
3 = PLAYER_STATE_PAUSED

You can also listen for the `sessionEnded` event

docs: https://developers.google.com/android/reference/com/google/android/gms/cast/framework/SessionManagerListener.html#onSessionEnded(T, int)

This will give a status code on why the session ended. Check docs for values

I'm open to any pull requests adding more methods/events or anything that makes the module more customizeable.
