# IBIKE Project

The Android application for a smart biking system.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

In order to get the project up and running, you need to have the latest version of Android studio installed.

### Installing

To clone this repo, In Android Studio's main menu click :

``` 
VCS -> Checkout from version control -> Git
```

then enter the URL of this repo and your local direcory.

To commit, click :

```
VCS -> Enable version Control -> Git
```

There does't seem to be a way to add a remote through the GUI. So at the terminal in the root of the project type the following command :

```
git remote add <remote_name> <remote_url>.
```

Now when you do : 

```
VCS -> Commit changes -> Commit & Push 
```

you should see your remote and everything should work through the GUI.





## Built With

* [Volley](https://developer.android.com/training/volley/index.html) - A google library for http networking
* [MaterialDrawer](https://github.com/mikepenz/MaterialDrawer) - The flexible, easy to use, all in one drawer library for Android
* [Otto](http://square.github.io/otto/) - Otto is an event bus designed to allow communication between background services and fragments
* [Firebase](https://firebase.google.com/) - A developement platform i used the geofencing API


