<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# jetbrain-music-player Changelog
## Unreleased

## [1.0.0-beta] - 2025-08-28
### Added
- **Cross-platform support** for Windows, macOS, and Linux
- Windows integration using PowerShell and Global System Media Transport Controls
- macOS integration using AppleScript for Music and Spotify applications
- **Structured track metadata**
- **Album art display** with automatic image loading and scaling
- Comprehensive metadata retrieval (title, artist, album, artwork URL)
- Platform detection utility for automatic OS identification

### Changed
- **Major UI redesign** with modern card-based layout
- Refactored player service architecture for cross-platform compatibility
- **Vertical player info card** with integrated playback controls

### Fixed
- PowerShell variable syntax issues in Windows implementation
- AppleScript execution robustness in macOS implementation
- Consistent return values across all platform implementations

## [0.1.0] - 2025-08-26
### Added
- Keyboard shortcuts for music control:
  - **Shift+Space**: Play/Pause music
  - **Alt+Period (.)**: Next track
  - **Alt+Comma (,)**: Previous track
  - **Shift+W**: Show/Hide Music Player tool window
- Local shortcuts within tool window (Space, Arrow keys, N/P)
- DumbAware implementation for availability during IDE indexing

### Changed
- Replaced inefficient thread refresh with Timer-based approach for better performance
- Improved resource management with proper lifecycle disposal
- Reduced CPU usage and eliminated interference with other tool windows
- Updated refresh rates: Player info (1 second), Visualizer (30 FPS)

### Fixed
- Thread performance issues causing excessive CPU usage
- Test failures due to keymap initialization issues