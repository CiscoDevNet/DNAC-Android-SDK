This app showcases the utility of android SDK (packaged and published as .aar) for building android apps


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

###### *continue coding your business logic and build your .apk* (reference code under `sample-app/DNAC-Android-SDK`)

