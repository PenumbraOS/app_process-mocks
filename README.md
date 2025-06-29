# Android Mocks for `app_process`

A custom `Context` implementation and other core features that allows for easy customization of common core Android classes. This allows experimenting with "app level" interactions while using `app_process` instead.

## Usage

Set up your process by calling:

```kotlin
Common.initialize(classLoader)
```

This will set up your thread and initialize some core systems set up by `ActivityThread` during a normal app launch.

You can create a custom mocked `Context` using:

```kotlin
MockContext.createWithAppContext(classLoader, mainThread, "com.android.settings")
```

## Installation

```kts
// settings.gradle.kts

dependencyResolutionManagement {
    repositories {
        maven {
            url = uri("https://jitpack.io")
        }
        ...
    }
}
```

```kts
// build.gradle.kts

dependencies {
    implementation("com.github.PenumbraOS:app_process-mocks:1.0.1")
}
```
