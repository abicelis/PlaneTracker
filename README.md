# PLEASE READ #

Sadly.. FlightAware made some changes to its API, basically removing the free tier. As of now (Sept 2018) Plane Tracker has been unpublished from the Play Store :(
I may look into swapping out FlightAware, sometime in the future.
Sorry folks.


# Plane Tracker #

I don't like huge, slow, complicated, non-intuitive, odd-looking, bloated, add-riddled flight tracking apps. Plane Tracker is a simple, lightweight yet good-looking plane/flight tracking app.

Its free, has NO ADS, looks great, lets you set trips with multiple flights. That's it.


Please check out the prototype by clicking [right here!](https://abicelis.github.io/PlaneTracker/ "PlaneTracker Prototype")

<a target="_blank" href='https://play.google.com/store/apps/details?id=ve.com.abicelis.planetracker&pcampaignid=MKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1'><img alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png' width="240px"/></a>

## Screenshots


[ ![](https://github.com/abicelis/PlaneTracker/blob/master/graphics/play_store/screens/v1.0/nexus5X/thumbs/1%20home.jpg) ](https://github.com/abicelis/PlaneTracker/blob/master/graphics/play_store/screens/v1.0/nexus5X/1%20home.png)
[ ![](https://github.com/abicelis/PlaneTracker/blob/master/graphics/play_store/screens/v1.0/nexus5X/thumbs/2%20trip%20detail.jpg) ](https://github.com/abicelis/PlaneTracker/blob/master/graphics/play_store/screens/v1.0/nexus5X/2%20trip%20detail.png)
[ ![](https://github.com/abicelis/PlaneTracker/blob/master/graphics/play_store/screens/v1.0/nexus5X/thumbs/3%20airport.jpg) ](https://github.com/abicelis/PlaneTracker/blob/master/graphics/play_store/screens/v1.0/nexus5X/3%20airport.png)
[ ![](https://github.com/abicelis/PlaneTracker/blob/master/graphics/play_store/screens/v1.0/nexus5X/thumbs/4%20flight%20route.jpg) ](https://github.com/abicelis/PlaneTracker/blob/master/graphics/play_store/screens/v1.0/nexus5X/4%20flight%20route.png)
[ ![](https://github.com/abicelis/PlaneTracker/blob/master/graphics/play_store/screens/v1.0/nexus5X/thumbs/5%20add%20a%20flight.jpg) ](https://github.com/abicelis/PlaneTracker/blob/master/graphics/play_store/screens/v1.0/nexus5X/5%20add%20a%20flight.png)
[ ![](https://github.com/abicelis/PlaneTracker/blob/master/graphics/play_store/screens/v1.0/nexus5X/thumbs/6%20add%20a%20flight.jpg) ](https://github.com/abicelis/PlaneTracker/blob/master/graphics/play_store/screens/v1.0/nexus5X/6%20add%20a%20flight.png)



## Fun stuff!

- MVP: Model View Presenter with configuration change survival
- New fancy MVP+RxJava+DataLayer architecture, loosely based on [ribot-boilerplate](https://github.com/ribot/android-boilerplate)
- RxJava 2 and Rx Android!
- Java lambdas (retrolambda)
- Retrofit
- Dagger 2
- Room database


## Code dependencies

This Project uses external dependencies and SDK's

* [Butterknife](https://github.com/JakeWharton/butterknife) - Android view binder library by JakeWharton
* [Timber](https://github.com/JakeWharton/timber) - Logger by JakeWharton
* [Dagger 2](https://github.com/google/dagger) - Dependency Injection by Square
* [TransitionsEverywhere](https://github.com/andkulikov/Transitions-Everywhere) - Backported Transitions library by andkulikov
* [CircleImageView](https://github.com/hdodenhof/CircleImageView) - Circular ImageView by hdodenhof
* [Gradle Retrolambda](https://github.com/evant/gradle-retrolambda) - Java lambda support by evant
* [RxJava 2](https://github.com/ReactiveX/RxJava) - Reactive Extensions by ReactiveX
* [RxAndroid](https://github.com/ReactiveX/RxAndroid) - RxJava bindings for Android by ReactiveX
* [Retrofit](https://github.com/square/retrofit) - Type-safe HTTP client for Android by square
* [Room](https://developer.android.com/topic/libraries/architecture/room.html) - Room mobile database by google


## External APIs

* [FlightAware API](http://flightxml.flightaware.com/json/FlightXML3/) - Flight Data API
* [ADS-B Exchange](https://www.adsbexchange.com) - Flight Data API
* [Google Maps API](https://developers.google.com/maps/) - Google Maps API
* [Google Maps Utils](https://github.com/googlemaps/android-maps-utils) - Google Maps Utils API


## Drawable sizes

- Regular icons. Size 24dp, padding 2dp.
- Large icons. Size 48dp, padding 2dp.


## Software used

* [Android Studio 2.3.3 IDE](https://developer.android.com/studio/index.html) - Android IDE
* [Pencil 3.0.3](https://github.com/evolus/pencil) - Prototyping by evolus


## Authors

* **Alejandro Bicelis** - *Coding and app design* - [abicelis](https://github.com/abicelis)


## License

This project is licensed under the MIT License - see the [LICENSE](https://github.com/abicelis/PlaneTracker/blob/master/LICENSE) file for details

