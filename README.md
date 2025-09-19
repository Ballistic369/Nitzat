NizatSearchApp - Simple version (Search + Barcode scan)

How to use:
1. Unzip this folder.
2. Open in Android Studio (Electric Eel / Flamingo / Giraffe recommended).
3. If Android Studio asks to update Gradle or plugins, accept recommended versions.
4. Build > Build APK(s) to produce an APK for installation.

Notes:
- The project relies on HTML scraping (Jsoup). If product selectors don't match exactly, edit ProductRepository.kt to adjust CSS selectors.
- GitHub Actions workflow included will attempt to install system gradle to build if you push to GitHub (see .github/workflows/android.yml).
