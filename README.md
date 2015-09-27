# cordova-plugin-music-controller
Interactive multimedia controls

## Supported platforms
- Android (4.1+)
- iOS (under development)
- Windows (10+)

## Installation
`cordova plugin add https://github.com/filfat-Studios-AB/cordova-plugin-music-controller`

## Methods
Create the media controller:
```javascript
MusicController.create({
    track: 'Speak Now',
	artist: 'Taylor Swift',
    cover: 'albums/speak-now.jpg',
    isPlaying: true
}, onSuccess, onError);
```

Destroy the media controller:
```javascript
MusicController.destory(onSuccess, onError);
```

Subscribe to the media controller events:
```javascript
function events(action) {
	switch(action){
		case 'music-controller-next':
			//Do something
			break;
		case 'music-controller-previous':
			//Do something
			break;
		case 'music-controller-pause':
			//Do something
			break;
		case 'music-controller-play':
			//Do something
			break;

		//Headset events
		case 'music-controller-headset-unplugged':
			//Do something
			break;
		case 'music-controller-headset-plugged':
			//Do something
			break;
		default:
			break;
	}
}
//Register callback
MusicController.subscribe(events);
//Start listening for events
//The plugin will run the events function each time an event is fired
Musiccontroller.listen();
```

##Quirks
* Cordova 5.0 or higher is required for Windows support.
* Windows currently only supports locally stored covers.
* Windows does currently not support headset plugg events.
* This plugin is still under development which means that it's not yet "production ready".


##Screenshots
![Android](http://i.imgur.com/Qe1a8ZJ.png)
![Windows](http://i.imgur.com/Y4HsM0s.png)
