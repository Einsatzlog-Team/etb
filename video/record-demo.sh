#!/usr/bin/env bash
# Records the demo video: Android + iOS, light + dark, English, no audio.
#   video/record-demo.sh [android|ios|all]      output: ~/Desktop/Einsatzlog-Demo-Videos/
# Needs: Maestro (~/.maestro/bin), a booted iPhone simulator with the app built,
# the Pixel_8a emulator (Play image, signed in as a license tester) with the app installed.
set -euo pipefail
cd "$(dirname "$0")"
FLOW="$PWD/demo-flow.yaml"
MAESTRO="${MAESTRO:-$HOME/.maestro/bin/maestro}"
OUT="${OUT:-$HOME/Desktop/Einsatzlog-Demo-Videos}"
ANDROID_SERIAL="${ANDROID_SERIAL:-emulator-5556}"
IOS_UDID="${IOS_UDID:-$(xcrun simctl list devices booted | grep -m1 -oE '[0-9A-F-]{36}')}"
# Freshly installed before every iOS take (Maestro's clearState keeps the Room DB on iOS).
IOS_APP="${IOS_APP:-/private/tmp/claude-503/etb-dd/Build/Products/Release-iphonesimulator/Einsatzlog.app}"
mkdir -p "$OUT"

android() {  # $1 = light|dark
  local a=(adb -s "$ANDROID_SERIAL") f="$OUT/android-$1-en.mp4"
  "${a[@]}" shell pm clear de.einsatzlog.app >/dev/null   # empty app for every take
  # Emulators report a hardware keyboard; show the normal on-screen keyboard instead
  # of the floating IME toolbar that would cover the form.
  "${a[@]}" shell settings put secure show_ime_with_hard_keyboard 1
  "${a[@]}" shell cmd uimode night "$([ "$1" = dark ] && echo yes || echo no)"
  "${a[@]}" shell cmd locale set-app-locales de.einsatzlog.app --locales en-US >/dev/null 2>&1 || true
  # Clean status bar (demo mode): current time, full battery, full signal, no notifications.
  "${a[@]}" shell settings put global sysui_demo_allowed 1
  "${a[@]}" shell am broadcast -a com.android.systemui.demo -e command enter >/dev/null
  "${a[@]}" shell am broadcast -a com.android.systemui.demo -e command clock -e hhmm "$(date +%H%M)" >/dev/null
  "${a[@]}" shell am broadcast -a com.android.systemui.demo -e command battery -e level 100 -e plugged false >/dev/null
  "${a[@]}" shell am broadcast -a com.android.systemui.demo -e command network -e wifi show -e level 4 >/dev/null
  "${a[@]}" shell am broadcast -a com.android.systemui.demo -e command notifications -e visible false >/dev/null
  (cd "$OUT" && "$MAESTRO" --device "$ANDROID_SERIAL" test -e PLATFORM=android -e VIDEO="android-$1-en" "$FLOW")
  "${a[@]}" shell am broadcast -a com.android.systemui.demo -e command exit >/dev/null
  echo "✓ $f"
}

ios_english() {  # simulator-wide language; needs a reboot, so only when not English yet
  if [[ "$(xcrun simctl spawn "$IOS_UDID" defaults read -g AppleLanguages 2>/dev/null | tr -d ' \n()"')" != en* ]]; then
    xcrun simctl spawn "$IOS_UDID" defaults write -g AppleLanguages -array en
    xcrun simctl spawn "$IOS_UDID" defaults write -g AppleLocale en_US
    xcrun simctl spawn "$IOS_UDID" defaults write -g AppleKeyboards -array "en_US@sw=QWERTY;hw=Automatic"
    xcrun simctl spawn "$IOS_UDID" defaults write com.apple.Preferences KeyboardAutocorrection -bool false
    xcrun simctl spawn "$IOS_UDID" defaults write com.apple.Preferences KeyboardPrediction -bool false
    xcrun simctl shutdown "$IOS_UDID"; xcrun simctl boot "$IOS_UDID"; xcrun simctl bootstatus "$IOS_UDID" -b >/dev/null
  fi
}

# Hardware keyboard on: no on-screen keyboard in the video, text appears directly.
defaults write com.apple.iphonesimulator ConnectHardwareKeyboard -bool true

ios() {  # $1 = light|dark
  local f="$OUT/ios-$1-en.mp4"
  ios_english
  xcrun simctl uninstall "$IOS_UDID" de.einsatzlog.app || true
  xcrun simctl install "$IOS_UDID" "$IOS_APP"
  xcrun simctl ui "$IOS_UDID" appearance "$1"
  xcrun simctl status_bar "$IOS_UDID" override --batteryState charged --batteryLevel 100 \
    --wifiBars 3 --cellularMode active --cellularBars 4 --operatorName ""
  (cd "$OUT" && "$MAESTRO" --device "$IOS_UDID" test -e PLATFORM=ios -e VIDEO="ios-$1-en" "$FLOW")
  xcrun simctl status_bar "$IOS_UDID" clear
  echo "✓ $f"
}

target="${1:-all}"
for theme in light dark; do
  [[ $target == all || $target == ios ]] && ios "$theme"
  [[ $target == all || $target == android ]] && android "$theme"
done
echo "Videos in $OUT"
