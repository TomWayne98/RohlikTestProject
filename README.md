# TomWayne - Rohlik Test Project

Simple app used as showcase of my variation of MVVM architecture.
App consists of two screens. First allows you to fetch a random dog picture from API via Retrofit. Second screen allows you to scroll through all the fetched pictures.

App consist of several modules. Some of them are newly created ("homescreen", "db" and "app") 
and some of them are buildings blocks used in my other apps ("ui", "architecture", "core"). These modules contains common stuff used for building android apps with MVVM architecture.
For that reason there are methods which are not used in this specific app.

Tech stack:
Kotlin,
Koin,
Jetpack,
Room,
Retrofit
Gradle Kotlin DSL,
Coroutines,
Navigation...

Modules:

App: Contains DI
Homescreen: Contains the activities, fragments, viewmodels and repos used in the app
DB: Contains all SQL DB stuff used in app

UI: commonly used extensions, views and all the other UI related stuff
Architecture: Base classes for activities, fragments, viewmodels etc. Containing the basic logic such as proper lifecycle handling
Core: Contains all the interfaces necessary for DI and bunch of extensions used across the whole app
BuildSrc: Contains files necessary for Kotlin DSL 

