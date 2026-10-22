# 🌍 LinGua — Language Learning App

**Learn languages, one word at a time.**

LinGua is an Android language-learning application developed using **Kotlin and Jetpack Compose** as part of the CodeAlpha App Development Internship. The app is designed to help users explore languages, practice vocabulary, and improve their everyday communication skills through a simple, modern interface.

## ✨ Features

- 🌐 **25 Languages** — English, Sindhi, Urdu, Arabic, Spanish, French, German, Chinese, Japanese, Korean, Turkish, Persian, Hindi, Italian, Portuguese, Russian, Bengali, Punjabi, Pashto, Malay, Indonesian, Thai, Dutch, Swahili, and Greek.
- 📚 **Vocabulary Lessons** — Learn common words and everyday phrases.
- 🗣️ **Multilingual Learning Structure** — Organize English meanings, target-language words, and native-language translations separately.
- 🔐 **Authentication** — Email/password authentication and Google Sign-In using Firebase Authentication.
- 👤 **User Profile** — Access your account and profile information.
- 📖 **Lesson Navigation** — Browse lessons and practice vocabulary.
- 🎨 **Modern UI** — Dark-themed interface with mint-green accents.
- 🚀 **Splash Screen** — Branded startup screen featuring the LinGua logo.
- 📱 **Android App** — Built natively for Android using Jetpack Compose.

> **Note:** The language selector supports 25 languages. Translation coverage depends on the available lesson data; not every lesson is necessarily translated into every language yet.

## 🛠️ Tech Stack

| Technology | Purpose |
|---|---|
| Kotlin | Main programming language |
| Jetpack Compose | Modern Android UI |
| Android Studio | Development environment |
| Firebase Authentication | User authentication |
| Google Sign-In | Google account login |
| Gradle | Build and dependency management |

## 🌎 Supported Languages

1. English
2. Sindhi
3. Urdu
4. Arabic
5. Spanish
6. French
7. German
8. Chinese
9. Japanese
10. Korean
11. Turkish
12. Persian
13. Hindi
14. Italian
15. Portuguese
16. Russian
17. Bengali
18. Punjabi
19. Pashto
20. Malay
21. Indonesian
22. Thai
23. Dutch
24. Swahili
25. Greek

## 🎨 App Design

LinGua uses a clean, modern visual style focused on readability and ease of navigation.

- **Primary background:** Dark navy
- **Accent color:** Mint green
- **Interface:** Material Design with Jetpack Compose
- **Branding:** Custom LinGua language-exchange logo

## 📋 Requirements

- Android Studio
- JDK compatible with the project's Android Gradle Plugin
- Android SDK installed through Android Studio
- An Android device or emulator
- Firebase project configuration for authentication features

## ⚙️ Installation and Setup

### 1. Clone the repository

```bash
git clone https://github.com/FarazMemon004/CodeAlpha_LanguageLearningApp.git
```

### 2. Open the project

Open the cloned folder in Android Studio and allow Gradle to sync.

### 3. Configure Firebase

1. Create or open a project in the [Firebase Console](https://console.firebase.google.com/).
2. Register an Android app using your project's application ID.
3. Enable Email/Password and Google authentication providers.
4. Add the required SHA-1 and SHA-256 fingerprints for Google Sign-In.
5. Download `google-services.json` and place it in the app module directory.

**Important:** Never upload private credentials, service-account keys, or secrets to a public repository.

### 4. Run the application

Connect an Android device or start an emulator, then click **Run** in Android Studio.

## 📁 Project Structure

```text
CodeAlpha_LanguageLearningApp/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       │   └── com/example/languagelearningapp/
│   │       │       └── MainActivity.kt
│   │       ├── res/
│   │       └── AndroidManifest.xml
│   └── build.gradle.kts
├── gradle/
│   └── libs.versions.toml
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
```

*The structure above shows the main project files; your local project may contain additional files and packages.*

## 🔒 Security

- Keep Firebase configuration and API secrets out of public repositories where appropriate.
- Do not commit signing keys, passwords, or service-account credentials.
- Configure Firebase Authentication and authorized app fingerprints before testing sign-in.

## 🚧 Future Improvements

- Expand lesson coverage across all 25 languages.
- Add pronunciation audio and text-to-speech.
- Add quizzes, scores, and learning progress tracking.
- Introduce daily learning goals and streaks.
- Add offline lesson support.
- Include bookmarks and favorite words.
- Improve native-language translation coverage.
- Add more interactive vocabulary exercises.

## 👨‍💻 Developer

**Muhammad Faraz**  
Android App Developer | Software Engineering Student | AI Enthusiast

- GitHub: [FarazMemon004](https://github.com/FarazMemon004)
- LinkedIn: [Muhammad Faraz](https://www.linkedin.com/in/muhammad-faraz-memon-92582a43a/)

## 🏢 Internship

Developed as part of the **CodeAlpha App Development Internship**.

## 📄 License

This project is intended for educational and portfolio purposes. Add a `LICENSE` file to the repository if you want to publish it under a specific open-source license.

---

⭐ **If you find this project interesting, consider giving the repository a star!**

*LinGua — Learn a new language, discover a new world.*
