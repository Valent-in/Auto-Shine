# Auto Shine

Android app for automatic screen brightness adjustment. Replacement for built-in feature.

WARNING: This app is not one click solution! It requires some configuration to work properly.  
It uses 4 pairs of light sensor data and corresponding screen brightness.
In-between values calculated as simple linear interpolation of those points.

Main intended working mode is "Screen unlock/rotate" - read ambient light data, set brightness once...
and do not annoy with backlight beaming anymore.

Permissions:
- Modify system settings - required to control display brightness
- Phone - keep reading light sensor data while phone ringing in pocket
- Notifications - to prevent being killed by system
- Request ignore battery optimizations - to prevent service suspend
- Run background service - it has "special use" type because does not fit in any category
- Run at startup - ...

[<img src="https://fdroid.gitlab.io/artwork/badge/get-it-on.png"
     alt="Get it on F-Droid"
     height="80">](https://f-droid.org/packages/com.mine.autoshine/)

Or download the latest APK from the [Releases Section](https://github.com/Valent-in/Auto-Shine/releases/latest).

---
This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3.  
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY.
