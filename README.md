# Admin App Jetpack Compose for Android
[![License: GPL-3.0](https://img.shields.io/badge/License-GPL%203.0-blue.svg)](https://www.gnu.org/licenses/gpl.html) [![Releases](https://img.shields.io/github/v/release/jarlingvar/admin-android.svg)](https://github.com/jarlingvar/wanderjunk-pub/releases/latest)

Straightforward listings application for travelers without any bloat.

---
## Features
- simple registration with Google or email
- search and manage listings and users
- Firebase data control
- Firebase Cloud Messaging user specific control
- new listings and FCM listening service (in foreground)


## Libraries and technologies
- [**Kotlin**](https://github.com/JetBrains/kotlin) completely with coroutines
- **Jetpack Compose Libraries** for Jetpack compose widget creation
- **Firebase Libraries** Firestore, Auth, RemoteConfig, Cloud Storage, Cloud Messaging, Dynamic links
- **Google Android Libraries** Google maps SDK, Google Places
- **Android Jetpack Libraries** NavigationComponent - for navigation, DataStore - for shared preferences
- **Room DB** for local storage
- **Dagger and Hilt** for dependency injection
- [**Lottie**](https://github.com/airbnb/lottie-android) for animations
- **Coil and Glide** for images
- **LeakCanary** for leak detection
- **Android Support Libraries**


## Usage
1) project requires firebase setup and proper google-service.json
2) to get admin rights userModel.hasFullAccess must be true (find user in firestore db after registration and change this flag manually)
3) to get full FCM control (accessed in MonitoringService.kt) you need configured admin sdk, after getting your admin token place admin config file named "token-service.json" in this directory : "/app/src/main/assets"
4) if any questions/complications arise - contact me at dev.jalringvar@gmail.com or create new issue in this repo
5) app may work more slowly on some devices if you install debug build

## Screenshots
|                      Main Screen                      |
|:-----------------------------------------------------:|
| <img src="/assets/images/admin-anim.gif" width="412"> |

## Related Apps
- [**WanderJunk App**](https://github.com/jarlingvar/wanderjunk-pub)

## License
> Copyright (C) 2023 JarlIngvar.
> Licensed under the [GPL-3.0](https://www.gnu.org/licenses/gpl.html) license.