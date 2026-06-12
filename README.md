# PlayLog — Hardcore Gaming Tracker 🎮🔥

PlayLog is a high-performance, native Android application designed for hardcore gamers who want to organize their backlog, track their progress, and verify their 100% completions using advanced AI.

---

## 🌟 Key Features

*   **AI-Powered Anticheat:** Scan and verify your 100% completion screenshots on-device using **Google Gemini AI SDK**. No fake achievements allowed.
*   **Offline-First Architecture:** Full offline access to your games library powered by **Room database** and **Kotlin Flow** state synchronization.
*   **Absolute Caching (BaaS Proxy):** Saves up to 85% of third-party API limits (RAWG) and reduces loading speed to 0.1s by proxying and caching requests through **Supabase (PostgreSQL)**.
*   **Gamer License Card:** Dynamically generate a customizable, shareable ID card with your gaming statistics (total games, completed, peak difficulty) using **Capturable**.
*   **Advanced Gamification:** An animated, responsive "Hall of Fame" with different difficulty tiers (Easy, Medium, Hard, Demon, Impossible, Mythical).
*   **Smart Search:** Dual search engine featuring both standard exact-match and smart AI semantic search.
*   **Deals Aggregation:** Live integration with **CheapShark API** to find the absolute best deal for games on GOG and Humble Store.

---

## 🛠 Tech Stack

*   **UI:** Jetpack Compose, Material 3, Custom Canvas, Custom Shapes (`CutCornerShape`).
*   **Architecture:** Clean Architecture (Domain, Data, Presentation), MVVM / MVI.
*   **Asynchronous:** Kotlin Coroutines & Flow (StateFlow, SharedFlow).
*   **DI:** Dagger Hilt.
*   **Database:** Room (Local), Supabase (Remote).
*   **Networking:** Retrofit2, OkHttp3 (Interceptors, Custom User-Agent Injection).
*   **AI:** Google Gemini AI (GenerativeAI SDK).
*   **Monetization & Sharing:** Chrome Custom Tabs (CCT), Android Share Intent (FileProvider).
