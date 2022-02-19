<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# Intellij-ginkgo Changelog

## [Unreleased]
## [0.3.1]
### Fixed
- `When` nodes now generated a focus regex for V1/V2 Compatability

## [0.3.0]
### Added
- Debug capabilities

## [0.3.0]
### Added
- Table Extension Support
- Handle before block output
- Add Warning for Incompatible Ginkgo Version

## [0.2.1]
### Fixed
- Run configuration from project settings not being respected

## [0.2.0]
### Added
- Pending spec markers with action to enable/disable
- Updated compatability version to included 213

### Fixed
- IndexOutOfBoundsException when getting spec name

### Chore
- General code quality

## [0.1.0]
### Added
- Pending Test output processing
- Locate Ginkgo by intellij config
- Console runs with intellij GoPath

### Fixed
- `When` focus expression
- Ginkgo null icon exception
- Linux test completion indicators

## [0.0.3]
### Added 
- Improved run dialog
- Added test reporter functionality
- Improved focus test selection

## [0.0.2]
### Added
- Initial scaffold created from [IntelliJ Platform Plugin Template](https://github.com/JetBrains/intellij-platform-plugin-template)
- `When` spec line marker
- Focused spec line markers
- Improved ginkgo executable detection
- Added configuration checks

### Fixed
- Hopefully improved test runner experience
- Removed recursive flag for focus tests
