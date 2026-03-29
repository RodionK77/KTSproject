
 # Kotlin Serialization
  -keepattributes *Annotation*, InnerClasses
  -dontnote kotlinx.serialization.AnnotationsKt
  -keep @kotlinx.serialization.Serializable class * { *; }
  -keepclassmembers class **$$serializer { *; }

  # Firebase Crashlytics — сохранить имена в стектрейсах
  -keepattributes SourceFile,LineNumberTable
  -keep public class * extends java.lang.Exception
  -renamesourcefileattribute SourceFile

  # Room
  -keep class * extends androidx.room.RoomDatabase
  -keep @androidx.room.Entity class *
  -keep @androidx.room.Dao interface *

  # Koin
  -keep class org.koin.** { *; }

  # Ktor
  -keep class io.ktor.** { *; }
  -dontwarn io.ktor.**

  # Coil
  -dontwarn coil.**

  # Маршруты навигации и data-классы
  -keep class com.github.rodionk77.** { *; }