-keep class com.gorman.network.data.models.** { *; }
-keep class com.gorman.database.data.model.** { *; }
-keep class com.gorman.domainmodel.** { *; }
-keep class com.gorman.ui.states.** { *; }

-keep class * extends androidx.room.Database
-keep class * implements androidx.room.Entity
-dontwarn androidx.room.paging.**
