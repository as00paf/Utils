Utils - Geo Module
====

The *Utils Library* offers a **Geo** module which is use to create apps that use maps, geo-fence,
activity detection and general geo-location stuff.

##Controllers
The geo module offers some controllers that allow for easier use of Android's APIs.  

###GoogleApiController
The GoogleApiController is the heart of most map-based applications. Use it to request location updates 
and get notified about location events.

###GeoFencingController
The GeoFencingController allows for adding and removing geofences. It is normally used with the
GeofenceTransitionsIntentService.

##Services
The geo module offers some services for activity detection and geofence transitions.

###ActivityDetectionIntentService
IntentService for handling incoming intents that are generated as a result of requesting activity 
updates using the ActivityRecognitionApi.

##Utils
The geo module offers some utility classes to use with activity recognition, maps and location applications.