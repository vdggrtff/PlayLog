# 🎮 PlayLog: The Bounty Hunter (Android)

<p align="center">
  <img src="https://img.shields.io/badge/Jetpack_Compose-Material_3-4285F4?style=for-the-badge&logo=android"/>
  <img src="https://img.shields.io/badge/Architecture-Clean_%7C_MVI-success?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/AI-Google_Gemini-FFA700?style=for-the-badge&logo=google"/>
  <img src="https://img.shields.io/badge/Kotlin-Multiplatform_Ready-7F52FF?style=for-the-badge&logo=kotlin"/>
</p>

PlayLog is a high-performance, native Android application designed for hardcore gamers. It is an ultimate **Meta-Tracker** that aggregates data from **IGDB (Amazon)** and **RetroAchievements**, using **Google Gemini AI** as an automated anti-cheat to verify custom gaming challenges (Bounties) via screenshots.

### 🎯 Community Goal: The $5 Gaben Ransom
* **Status:** 🔒 PC Steam Achievements are currently locked.
* **The Reason:** Lord Gabe Newell (Valve) requires a $5 account verification fee to grant Steam Web API access.
* **The Bounty:** The first legend to donate **$5 on [Boosty] https://boosty.to/playlog_app/donate** pays the Valve toll fee and unlocks official Steam achievements for all PC games across the entire app!

## 🌟 Key Features (v2.0)
* **🤖 AI-Powered Anticheat:** Snap a photo of your screen or upload a screenshot, and the Gemini AI neural network will verify if you completed a custom gaming contract.
* **🌐 Universal Aggregator:** Flawlessly merges 1080p game data from IGDB with community achievements from RetroAchievements into one unified UI.
* **📂 User-Generated Playlists (UGC):** Create, manage, and share your custom game collections with offline-first support.
* **🎛️ Marketplace-grade Filters:** Advanced full-screen dialog to filter by Genres, Platforms, Release Years, and Metascore-style 100-point ratings.
* **🗂 Dynamic Grid System:** Switch your library and search views on the fly between detailed lists, 2-column cards, or a dense 4-column poster grid.
* **📇 Gamer Passport:** Dynamically generate a cyberpunk ID card with your gaming statistics using Capturable.

## 🛠 Tech Stack
* **UI:** Jetpack Compose, Material 3, Custom Canvas animations.
* **Architecture:** Clean Architecture (Domain, Data, Presentation), MVI State Management.
* **Asynchronous:** Kotlin Coroutines & Flow (StateFlow, Combine operators).
* **DI:** Dagger Hilt.
* **Database:** Room (Local), Supabase PostgreSQL (Remote Cloud Cache).
* **Networking:** Retrofit2, OkHttp3 (with Custom Auth Interceptors for Twitch OAuth2).
* **AI:** Google Gemini AI (GenerativeAI SDK).

## 🚀 Download
[**Download the latest APK (v2.0)**] https://github.com/vdggrtff/PlayLog/releases
