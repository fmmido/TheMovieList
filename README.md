## The Movie List

![GitHub License](https://img.shields.io/badge/license-MIT-blue.svg)  
![Kotlin](https://img.shields.io/badge/Kotlin-1.9.22-blue.svg)  
![Android](https://img.shields.io/badge/Android-5.0+-green.svg)

**The Movie List** is an Android application that allows users to browse and explore movie details using [The Movie Database (TMDb) API](https://www.themoviedb.org/). The app features a paginated movie list, detailed movie views, favorite toggling, and layout switching between grid and list views. It is built with modern Android development practices, using Kotlin, Jetpack libraries, and clean architecture principles.


## Demo Video (Light Mode)

Watch a quick demo of **The Movie List** app in light mode:

[![Watch the demo](https://img.youtube.com/vi/ypZtET9scDo/0.jpg)](https://youtube.com/shorts/ypZtET9scDo?si=htoQINpAXr7X-8to)

> Click the image or [watch it here directly](https://youtube.com/shorts/ypZtET9scDo?si=htoQINpAXr7X-8to).

## Demo Video (Dark Mode)

Click the image below to watch a short demo of **The Movie List** on YouTube Shorts:

[![Demo Video](https://img.youtube.com/vi/smyCPXrP7Vg/maxresdefault.jpg)](https://youtube.com/shorts/smyCPXrP7Vg)


- **Full Demo**:  
  [![Full Demo](https://img.youtube.com/vi/pSe6rWTTre0/maxresdefault.jpg)](https://www.youtube.com/watch?v=pSe6rWTTre0)

## Features

- **Paginated Movie List**: Browse a list of movies with infinite scrolling, powered by the Paging 3 library.
- **Movie Details**: View detailed information about a movie, including title, poster, release date, genres, runtime, and overview.
- **Favorite Movies**: Toggle movies as favorites, with persistence using Room database.
- **Layout Switching**: Switch between grid and list views for the movie list, with the preference saved using DataStore.
- **Offline Support**: Access locally cached movie details when offline, with online syncing via the TMDb API.
- **Scroll Position Retention**: Preserves the scroll position in the movie list when navigating back from the details screen.

## Screenshots

### Light Mode

| Home Screen | Movie List (Grid) | Movie Details | Favorites |
|-------------|-------------------|----------------|-----------|
| ![Home Light](screenshots/screenshot_6.jpg) | ![Grid Light](screenshots/screenshot_5.jpg) | ![Details Light](screenshots/screenshot_4.jpg) | ![Favorites Light](screenshots/screenshot_7.jpg) |

### Dark Mode

| Home Screen (List View) | Home Screen (Grid View) | Movie Details |
|-------------------------|-------------------------|----------------|
| ![List View](screenshots/screenshot_3.jpg) | ![Grid View](screenshots/screenshot_2.jpg) | ![Movie Details](screenshots/screenshot_1.jpg) |


## Tech Stack

- **Language**: Kotlin
- **Architecture**: MVI (Model-View-Intent) with Clean Architecture principles  
  > **Note**: The movie details screen uses MVVM (Model-View-ViewModel) for simplicity.
- **Dependency Injection**: Dagger Hilt
- **Networking**: Retrofit for API calls to TMDb
- **Database**: Room for local storage
- **Paging**: Jetpack Paging 3 for paginated data loading
- **Preferences**: Jetpack DataStore for storing layout preferences
- **UI**: XML-based layouts
- **Image Loading**: Glide for loading movie posters
- **Coroutines**: For asynchronous operations
- **Testing**: JUnit, Mockito for unit tests

## Prerequisites

- **Android Studio**: Version 2023.1.1 or later
- **Kotlin**: Version 1.9.22
- **Minimum SDK**: API 21 (Android 5.0 Lollipop)
- **TMDb API Key**: Sign up at [The Movie Database](https://www.themoviedb.org/) to get an API key.

## Setup Instructions

1. **Clone the Repository**  
   ```bash
   git clone https://github.com/your-username/the-movie-list.git
   cd the-movie-list
   ```

2. **Add TMDb API Key**  

3. **Sync and Build**  
   - Open the project in Android Studio.  
   - Sync the project with Gradle by clicking **Sync Project with Gradle Files**.  
   - Build the project: **Build > Make Project**.

4. **Run the App**  
   - Connect an Android device or start an emulator.  
   - Run the app: **Run > Run 'app'**.

## Project Structure

The app follows Clean Architecture with MVVM, organized into the following layers:

```
the-movie-list/
│
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/smartpixel/themovielist/
│   │   │   │   ├── data/              # Data layer: API, database, repository
│   │   │   │   ├── domain/            # Domain layer: Models, use cases
│   │   │   │   ├── presentation/      # Presentation layer: ViewModels, UI state
│   │   │   │   └── ui/                # UI: Fragments, adapters, layouts
│   │   │   ├── res/                   # Resources: Layouts, drawables
│   │   │   └── AndroidManifest.xml
│   │   └── test/                      # Unit tests
│   └── build.gradle
└── README.md
```

## Running Tests

The project includes unit tests for the ViewModels and UseCases.

### Run Unit Tests

```bash
./gradlew testDebugUnitTest
```

### Test Coverage

Tests are written using JUnit, Mockito, Truth, and Turbine.  
Key components tested:
- `DetailsViewModel`
- `GetMovieDetailsUseCase`

## Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository.  
2. Create a new branch:  
   ```bash
   git checkout -b feature/your-feature-name
   ```
3. Make your changes and commit:  
   ```bash
   git commit -m "Add your feature"
   ```
4. Push to your branch:  
   ```bash
   git push origin feature/your-feature-name
   ```
5. Open a pull request with a description of your changes.

### Code Style

- Follow the Kotlin Coding Conventions.  
- Use meaningful variable names and comments where necessary.  
- Ensure all tests pass before submitting a pull request.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

## Acknowledgments

- [The Movie Database (TMDb)](https://www.themoviedb.org/) for providing the API.  
- Jetpack Libraries for modern Android development tools.  
