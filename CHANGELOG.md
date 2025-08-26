<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# jetbrain-music-player Changelog

## [Unreleased]

## [0.1.0] - 2025-08-26
### Added
- Keyboard shortcuts for music control:
  - **Shift+Space**: Play/Pause music
  - **Shift+Period (.)**: Next track
  - **Shift+Comma (,)**: Previous track
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