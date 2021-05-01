# foursquare
The implementation of Foursquare api. When the user moves, the new data around the current location is being fetched, showed in View and saved in Room database. (offline first architecture!) if the location changes more than 100 meter, we assume a new location for the user.  

Kotlin language
MVVM architecture
Livedata for reactive programming
Kotlin coroutines for multithreading
ROOM persistence library for caching the data
Retrofit for network calls
and other components such as androidX, butterknife, calligraphy and etc... .

My approach is as below:

At first I tried to make each component structure, and then wiring all the components to each other till reaching to the View!


1. preparing application requirements and choosing the right architecture
2. adding all components to the gradle
3. packaging the classes
4. preparing foursquare API and making all the models
5. observing location change using Livedata
6. retrofit call foursquare interface using coroutines in Repository
7. adding viewmodel
8. foursquare api test response failure and success livedata handling
9. adding room and model, DAO and other configs
10. adding sharedprefs for caching last location, offset and last updated time
11. implementing the main logic of application inside viewmodel and repository
12. adding ROOM to the main login and make use of it
13. adding the last update time to the main logic, which was only had having internet and changing location
14. showing in view: place details
15. refactoring and adding some comments
