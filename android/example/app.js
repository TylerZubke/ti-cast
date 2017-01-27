// This is a test harness for your module
// You should do something interesting in this harness
// to test out the module and to provide instructions
// to users on how to use it by example.


// open a single window
var win = Ti.UI.createWindow({
	backgroundColor:'white'
});
var label = Ti.UI.createLabel();
win.add(label);
win.open();

// TODO: write your module tests here
var ti_cast = require('com.cbcnewmedia.cast');
Ti.API.info("module is => " + ti_cast);

var proxy = ti_cast.createCast({lifecycleContainer: win});
//There may be unexpected behavior on devices that don't have the newest google play services (10.0.1 as of writing this)
if(ti_cast.isGooglePlayServicesAvailable()){ 
	
	var cast_obj = {
		url: 'http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4', 
		contentType: 'videos/mp4',
		duration: '60000', //in ms (-1 if it's a live video)
		metadata: {
			title: 'Test Video',
			subtitle: 'This is a fake video for Chromecast.',
			images: [
				'http://vignette4.wikia.nocookie.net/logopedia/images/a/ae/Chromecast-logo.jpg/revision/latest?cb=20150629154316'
			]
		},
		autoplay: true,
		startPosition: 0 //in ms
	};
	
	var media_route_button = ti_cast.createMediaRouter({lifecycleContainer: win});	
	win.activity.onCreateOptionsMenu = function(e){
		e.menu.add({
            actionView: media_route_button,
            showAsAction: Titanium.Android.SHOW_AS_ACTION_ALWAYS
        });
	};
	
	var mini_controller = ti_cast.createMiniController({lifecycleContainer: win});
	mini_controller.bottom = 0;
	win.add(mini_controller);
	
	proxy.addEventListener('castStateChange', function(e){
		/*
		 * https://developers.google.com/android/reference/com/google/android/gms/cast/framework/CastState
		 * 1 = NO_DEVICES_AVAILABLE
		 * 2 = NOT_CONNECTED
		 * 3 = CONNECTING
		 * 4 = CONNECTED
		 */
		if(e.state == 4)
		{
			proxy.castVideo(cast_obj);
		} 
	});
}



