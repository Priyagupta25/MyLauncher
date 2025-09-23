# Launcher App

A **minimal custom Android launcher** built with **Jetpack Compose**, **MVVM**, **Room Database**, and **Hilt Dependency Injection**. This launcher supports displaying installed apps in a grid, creating folders, and persisting data in the database.

## Project Structure

```
app/
 ├── data/
 │    ├── local/
 │    │     ├── LauncherDatabase.kt
 │    │     └── dao/
 │    │           ├── AppDao.kt
 │    │           └── FolderDao.kt
 │    ├── model/
 │    │     ├── AppEntity.kt
 │    │     └── FolderEntity.kt
 │    └── repository/
 │          ├── LauncherRepository
 ├── di/
 │    ├── DatabaseModule.kt
 │    └── RepositoryModule.kt
 │
 ├── ui/
 │    ├── MainActivity.kt
 │    ├── viewmodel/
 │    │     └── LauncherViewModel.kt
 │    └── compose/
 │         
 │
 ├── LauncherApp.kt   # @HiltAndroidApp entry point
 └── AndroidManifest.xml
```

## Features

Features

📱 Show installed apps in a grid layout

📂 Create and manage folders

➕ Add apps to folders

❌ Delete apps/folders from database

💾 Persistence with Room Database

🏗️ MVVM + Clean Architecture

⚡ Hilt for dependency injection

🎨 Jetpack Compose for UI

↔️ Swipe navigation between pages

🔍 Shortcut support via long press popup

## Tech Stack

- Kotlin
- Jetpack Compose (UI)
- Room (Local DB)
- Hilt (DI)
- Coroutines + Flow
- MVVM + Clean Architecture

## Setup

1. Clone the repo:
   ```bash
   git clone https://github.com/Priyagupta25/MyLauncher
   cd launcher-app
   ```

2. Open in **Android Studio**.
3. Sync Gradle.
4. Run the app:
   ```bash
   ./gradlew installDebug
   ```

📱 Usage

Home Screen → Shows installed apps in grid layout.

Swipe Left (-1 Page) → Shows shortcuts screen.

Swipe Up → Opens full app drawer.

Long Press on Icon → Opens popup with option to add shortcut.

Drag & Drop:

Drop app on another → Create folder

Move apps between folders

Delete apps/folders via repository updates.


## Future Improvements
- App drag & drop between screens
- Widgets support
- Icon pack customization



