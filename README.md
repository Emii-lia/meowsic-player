# jetbrain-music-player

![Build](https://github.com/Emii-lia/jetbrain-music-player/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/28287-meowsic-player.svg)](https://plugins.jetbrains.com/plugin/28287-meowsic-player)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/28287-meowsic-player.svg)](https://plugins.jetbrains.com/plugin/28287-meowsic-player)

<!-- Plugin description -->
Integrate music player and visualizer to your IDE

## Features
- Track players and display the top first listed
- Visualize audio
- Control playback (play/pause, next/previous track)

## Usage
The player daemon is using "playerctl" which is only available on Linux-based OS.
- Install the plugin
- Make sure you have "playerctl" and "cava" installed
- Open the music player tool window
- Start playing music using your favorite music player (e.g. Spotify, VLC, etc.)
- Enjoy the music and the visualizer!

## Requirements

This plugin uses the command line utilities [playerctl](https://github.com/altdesktop/playerctl) (Linux) to play music files ; and [cava](https://github.com/karlstav/cava) to display a visualizer.
Please make sure that the respective utility is installed.

Windows and macOS are not supported at the moment.
<!-- Plugin description end -->

## Sreenshot

![screenshot](./plugin_screenshot.png)

## Installation

- Using the IDE built-in plugin system:
  
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "jetbrain-music-player"</kbd> >
  <kbd>Install</kbd>
  
- Using JetBrains Marketplace:

  Go to [JetBrains Marketplace](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID) and install it by clicking the <kbd>Install to ...</kbd> button in case your IDE is running.

  You can also download the [latest release](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID/versions) from JetBrains Marketplace and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

- Manually:

  Download the [latest release](https://github.com/Emii-lia/jetbrain-music-player/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

## To Do
- [ ] Support for Windows and macOS
- [ ] More playback controls (seek, volume, etc.)
- [ ] More visualizer options
- [ ] Track info display (album art, track length, etc.)

## Licence

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---
Plugin based on the [IntelliJ Platform Plugin Template][template].


[template]: https://github.com/JetBrains/intellij-platform-plugin-template
[docs:plugin-description]: https://plugins.jetbrains.com/docs/intellij/plugin-user-experience.html#plugin-description-and-presentation
