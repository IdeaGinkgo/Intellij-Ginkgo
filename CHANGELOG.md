<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# Intellij-ginkgo Changelog

## [Unreleased]
### Added
- Rerun Failed Tests Option
### Fixed
- Improved parsing for Ginkgo 2.5.x

## [0.7.1]
### Chore
- Add functionality to Ginkgo Expression
### Fixed
- Ginkgo 2.5 test output parsing

## [0.7.0]
### Added
- Ginkgo Structure View
### Chore
- Bump intellij gradle plugin to 1.9.0
- Refactor Enums
- Refactor RunLineMarker
- Refactor BreadCrumbs
- Add RunLineMarkerTests
### Fixed
- NPE in Run Configuration Producer

## [0.6.5]
### Fixed
- Panics not being recognized by output parser

## [0.6.4]
### Chore
- Updated to support 2022.2
- Update build plugin to 1.7.0

## [0.6.3]
### Fixed
- Improved test detection for non . imports ie `ginkgo.It` and non `_test.go` files
- Add bound check when computing ginkgo expressions
### Chore
- Add tests for run line marker

## [0.6.2]
### Fixed
- Escape parentheses in spec names

## [0.6.1]
### Fixed
- Focus test expressions persists between restarts
### Chore
- Update build plugin to 1.6.0

## [0.6.0]
### Fixed
- Failed test reporting in Ginkgo V2
- Quoted command lines
### Added
- Test status icon in gutter

## [0.5.0]
### Fixed
- Support for multiple ginkgo command line flags
### Added
- Breadcrumb test navigator (thanks @adammw)

## [0.4.1]
### Fixed
- Log messages being used as test names
### Added
- EAP support
### Chore
- Update build plugin to 1.3.1

## [0.4.0]
### Added
- debug feature

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
