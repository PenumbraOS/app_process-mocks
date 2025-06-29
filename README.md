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

pluginManagement {
    repositories {
        maven {
            url = uri("https://jitpack.io")
        }
        ...
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "com.penumbraos.app-process-mocks") {
                useModule("com.github.PenumbraOS.app_process-mocks:appProcessMocks:${requested.version}")
            }
        }
    }
}
```

```kts
// build.gradle.kts

plugins {
    id("com.penumbraos.app-process-mocks") version "1.0.1"
}
```
