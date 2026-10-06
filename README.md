# JARVIS Android

A practical Android JARVIS client designed around Gemini, voice input, local settings, and a permission-gated future tool layer.

## Build APK

1. Install Android Studio or a JDK + Android SDK.
2. Open this repository as a Gradle project.
3. Run: `./gradlew assembleDebug`
4. APK: `app/build/outputs/apk/debug/app-debug.apk`

## Architecture

UI -> Conversation Manager -> Gemini Client -> Intent/Tool Planner -> Permission Manager -> Tool Executor.

Financial transactions, password/OTP handling, credential access, hidden surveillance, and unrestricted shell/root execution are intentionally excluded.

See the supplied product requirements for the full behavior specification.
