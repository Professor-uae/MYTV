@echo off
chcp 65001 >nul
title UrTV install for the TV at 192.168.1.75

set "DIR=%USERPROFILE%\UrTV-install"
set "TV=192.168.1.75:5555"
mkdir "%DIR%" 2>nul
cd /d "%DIR%"

echo.
echo [1/5] Downloading Android platform tools (adb) from Google...
curl.exe -L -o platform-tools.zip https://dl.google.com/android/repository/platform-tools-latest-windows.zip
if not exist platform-tools\adb.exe tar -xf platform-tools.zip
set "ADB=%DIR%\platform-tools\adb.exe"

echo.
echo [2/5] Downloading the UrTV apps from the MYTV release...
curl.exe -L -o urtv-screensaver.apk https://github.com/Professor-uae/MYTV/releases/download/apk-latest/urtv-screensaver.apk
curl.exe -L -o urtv-home.apk https://github.com/Professor-uae/MYTV/releases/download/apk-latest/urtv-home.apk

echo.
echo [3/5] Connecting to the TV...
"%ADB%" disconnect >nul 2>nul
"%ADB%" connect %TV%
echo.
echo LOOK AT THE TV: if it asks "Allow USB debugging?", tick "Always allow" and press Allow.
echo Waiting 20 seconds for you to do that...
timeout /t 20 >nul
"%ADB%" devices

echo.
echo [4/5] Installing the screensaver app...
"%ADB%" install -r urtv-screensaver.apk
if errorlevel 1 (
  echo The old version is signed with another key. Removing it and installing again...
  "%ADB%" uninstall sa.urtv.screensaver
  "%ADB%" install urtv-screensaver.apk
)

echo.
echo [5/5] Installing the home app...
"%ADB%" install -r urtv-home.apk
if errorlevel 1 (
  echo Removing the old home app and installing again...
  "%ADB%" uninstall sa.urtv.home
  "%ADB%" install urtv-home.apk
)

echo.
echo DONE. Copy everything printed in this window and send it to Claude.
pause
