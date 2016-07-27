# tracker

Tracking the user interation before crash occurs and using that data in crash repoting.

Crash repoting is one of the most important tast that help in the improvement of an application. But sometimes even if we have the stacktrace of crash we are not able to track the cause of the crash , as it might have been due to series of user interaction steps that lead to such a result.

So this library is focussed towards tracking of user interation. Basically tracking the method calls that occured during the whole process.

###Usage

Proguard rules(when proguard enabled) :-

    -keep class com.infoedge.trackerBinder** { *; }
    -dontwarn com.infoedge.trackerBinder**
    -adaptclassstrings


include in build.gradle:-

    apply plugin: 'com.infoedge.tracker'

    buildscript {
        repositories {
            mavenCentral()
            maven {
                url 'https://dl.bintray.com/android-infoedge/maven/'
            }
        }
        
        dependencies {
            classpath 'com.infoedge.tracker:tracker-plugin:1.2'
        }
    }


you can add the annotation on any method or constructor to add the method call in tracking:-

    @TracePath
    public void methodName(){}
    
    @TracePath(TAG = "QD_CLICK")
    public void methodName(){}
    
    @TracePath(TAG = "QD_CLICK" , methodName = "myclick")
    public void methodName(){}
    
    @TracePath(TAG = "QD_CLICK" , methodName = "myclick",className = "myclass")
    public void methodName(){}
    

Enabling and disabling logs and tracing dynamically :-

    TraceAspect.enableTracking(boolean)
    TraceAspect.enableLogging(boolean)

To enable and disable the plugin through gradle file (clean your project if this configuration is changed):-

    tracker{
        enabled true
    }
    
Logs will appear as :-

    07-27 12:45:14.153 17359-17359/com.example.android V/q: QD_CLICK ⇢ [myclass] myclick(v= id/bt_follow_button )

To print all the event list :-

    Log.d("LogTag", CapturedEventsContainer.getInstance().getEventList());

    07-27 12:45:25.054 17359-17359/com.example.android D/LogTag: QD_CLICK ⇢ [myclass] myclick(v= id/tv_tag_1 )
                                                                 QD_CLICK ⇢ [myclass] myclick(v= id/bt_follow_button )
                                                                 QD_CLICK ⇢ [myclass] myclick(v= id/bt_follow_button )

##References

This is an extension to the [Hugo](https://github.com/JakeWharton/hugo) library by [Jake Wharten](https://github.com/JakeWharton)

##License

Copyright 2013

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
