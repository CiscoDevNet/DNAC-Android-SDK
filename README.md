This is an intent driven android app to query the list and details of the managed network devices for a given DNAC


---
### Workflow covered by the sample app
The app allows user to login into a DNAC using it's *ip-address* and his/her *user-name* and *password*. Once the user is logged-in, (s)he can:
  * know the authentication token being used,
  * get the count of managed network-devices and
  * details of the managed network-devices list.

The built .apk is `output-apk/dnac-android-app.apk`


---
### Using SDK to create your own app
###### Requirements:
  * JDK-1.8
  * Android studio (>= v3.1)
  * SDK file (available as `input-aar/updated-android-bindings-debug-1.0.0.aar`)

###### Configuration steps:
  * Start a new android project (`sample-app` is built on **API version 21**)
  * Choose **Empty Activity**
  * Import the .aar (*File > New > New Module >  Import .jar/.aar package*)
  * Update Project Settings to include .aar in the depedencies (*File > Project Structure > [Select AppModule ] > Dependency tab > [+] .aar module*)
  * Perform **Gradle Sync**
  * Make sure `app/build.gradle` has the following in *dependencies* section:
   ```gradle
      implementation 'com.android.volley:volley:1.1.0'
      implementation 'io.gsonfire:gson-fire:1.8.0'
      implementation project('updated-android-bindings-debug-1.0.0.aar')
  ```
  
### Next steps:
###### To explore the built application:
 Import into your Android studio: *AndroidStudio -> File -> New -> Open -> `sample-app/DNAC-Android-SDK`*
    
###### To install the built .apk:
 * Load the .apk onto android device and install or
 * CLI:
    > adb install output-apk/dnac-android-app.apk
    
**Note:** If there are issues with login, try restarting the app with *SSL Verification* turned **OFF**
 
