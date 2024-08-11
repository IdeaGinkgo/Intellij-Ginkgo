<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# Intellij-ginkgo Changelog

## [Unreleased]
### Fixed
- Escape brackets `[]` for focus tests
- Correctly displays test status for test names containing special characters.

### Chore
- Add support for 2024.2

## [0.10.4] - 2024-05-21
### Chore
- Update github actions
- Add support for 2023.1

### Bug
- Log additional details when ginkgo command fails

## [0.10.3] - 2024-02-04

### Chore
- Remove deprecated functions

### Bug
- Remove check for `=== RUN` in V2 processor

## [0.10.2] - 2024-01-04

### Chore
- Add support for 2023.3

## [0.10.1] - 2023-07-28

### Chore
- Add support for 2023.2
- Upgrade build tools

## [0.10.0] - 2023-07-27

### Added
- Ginkgo settings options to use go tools ginkgo version
- Support -vv flag

### Fixed
- Correctly parsing AfterSuite blocks

### Chore
- Update Qodana action

## [0.9.4] - 2023-06-24

### Fixed
- Defer Cleanup Blocks being reported as test failures

## [0.9.3] - 2023-04-05

### Fixed
- Working directory is used from Configuration Template (use with caution)
- Pipe "|" symbol used in test name escaped in Focus expression
- Fixed error preventing test execution in GoLand 2023.1

## [0.9.2] - 2023-03-29

### Chore
- Update support to 2023.1

## [0.9.1] - 2023-03-17

### Fixed
- Bread crumbs default provider

### Chore
- refactor executor for readability
- update build yml

## [0.9.0] - 2023-01-07

### Added
- go tool arguments for improved build tag support

### Fixed
- Show configuration error if package name missing from focus expression

## [0.8.0] - 2023-01-03

### Added
- Rerun Failed Tests Option

### Chore
- Update pipeline
- Update gradle version

### Fixed
- Ginkgo 2.5.x test status indicators
- Additional options not being used from test templates

## [0.7.2] - 2022-12-03

### Chore
- Update build plugin files

### Fixed
- Improved parsing for Ginkgo 2.5.x
- Add Support for 2022.3
- Building with java 17

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

[Unreleased]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.9.3...HEAD

[0.9.3]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.9.2...v0.9.3

[0.9.2]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.9.1...v0.9.2

[0.9.1]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.9.0...v0.9.1

[0.9.0]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.8.0...v0.9.0

[0.8.0]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.7.2...v0.8.0

[0.7.2]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.7.1...v0.7.2

[0.7.1]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.7.0...v0.7.1

[0.7.0]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.6.5...v0.7.0

[0.6.5]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.6.4...v0.6.5

[0.6.4]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.6.3...v0.6.4

[0.6.3]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.6.2...v0.6.3

[0.6.2]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.6.1...v0.6.2

[0.6.1]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.6.0...v0.6.1

[0.6.0]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.5.0...v0.6.0

[0.5.0]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.4.1...v0.5.0

[0.4.1]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.4.0...v0.4.1

[0.4.0]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.3.1...v0.4.0

[0.3.1]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.3.0...v0.3.1

[0.3.0]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.2.1...v0.3.0

[0.2.1]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.2.0...v0.2.1

[0.2.0]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.1.0...v0.2.0

[0.1.0]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.0.3...v0.1.0

[0.0.3]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.0.2...v0.0.3

[0.0.2]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/commits/v0.0.2

[Unreleased]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.9.4...HEAD

[0.9.4]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.9.3...v0.9.4

[0.9.3]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.9.2...v0.9.3

[0.9.2]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.9.1...v0.9.2

[0.9.1]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.9.0...v0.9.1

[0.9.0]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.8.0...v0.9.0

[0.8.0]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.7.2...v0.8.0

[0.7.2]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.7.1...v0.7.2

[0.7.1]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.7.0...v0.7.1

[0.7.0]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.6.5...v0.7.0

[0.6.5]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.6.4...v0.6.5

[0.6.4]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.6.3...v0.6.4

[0.6.3]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.6.2...v0.6.3

[0.6.2]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.6.1...v0.6.2

[0.6.1]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.6.0...v0.6.1

[0.6.0]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.5.0...v0.6.0

[0.5.0]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.4.1...v0.5.0

[0.4.1]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.4.0...v0.4.1

[0.4.0]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.3.1...v0.4.0

[0.3.1]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.3.0...v0.3.1

[0.3.0]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.2.1...v0.3.0

[0.2.1]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.2.0...v0.2.1

[0.2.0]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.1.0...v0.2.0

[0.1.0]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.0.3...v0.1.0

[0.0.3]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.0.2...v0.0.3

[0.0.2]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/commits/v0.0.2

[Unreleased]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.10.0...HEAD

[0.10.0]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.9.4...v0.10.0

[0.9.4]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.9.3...v0.9.4

[0.9.3]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.9.2...v0.9.3

[0.9.2]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.9.1...v0.9.2

[0.9.1]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.9.0...v0.9.1

[0.9.0]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.8.0...v0.9.0

[0.8.0]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.7.2...v0.8.0

[0.7.2]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.7.1...v0.7.2

[0.7.1]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.7.0...v0.7.1

[0.7.0]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.6.5...v0.7.0

[0.6.5]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.6.4...v0.6.5

[0.6.4]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.6.3...v0.6.4

[0.6.3]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.6.2...v0.6.3

[0.6.2]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.6.1...v0.6.2

[0.6.1]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.6.0...v0.6.1

[0.6.0]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.5.0...v0.6.0

[0.5.0]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.4.1...v0.5.0

[0.4.1]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.4.0...v0.4.1

[0.4.0]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.3.1...v0.4.0

[0.3.1]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.3.0...v0.3.1

[0.3.0]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.2.1...v0.3.0

[0.2.1]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.2.0...v0.2.1

[0.2.0]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.1.0...v0.2.0

[0.1.0]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.0.3...v0.1.0

[0.0.3]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.0.2...v0.0.3

[0.0.2]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/commits/v0.0.2

[Unreleased]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.10.1...HEAD

[0.10.1]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.10.0...v0.10.1

[0.10.0]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.9.4...v0.10.0

[0.9.4]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.9.3...v0.9.4

[0.9.3]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.9.2...v0.9.3

[0.9.2]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.9.1...v0.9.2

[0.9.1]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.9.0...v0.9.1

[0.9.0]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.8.0...v0.9.0

[0.8.0]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.7.2...v0.8.0

[0.7.2]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.7.1...v0.7.2

[0.7.1]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.7.0...v0.7.1

[0.7.0]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.6.5...v0.7.0

[0.6.5]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.6.4...v0.6.5

[0.6.4]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.6.3...v0.6.4

[0.6.3]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.6.2...v0.6.3

[0.6.2]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.6.1...v0.6.2

[0.6.1]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.6.0...v0.6.1

[0.6.0]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.5.0...v0.6.0

[0.5.0]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.4.1...v0.5.0

[0.4.1]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.4.0...v0.4.1

[0.4.0]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.3.1...v0.4.0

[0.3.1]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.3.0...v0.3.1

[0.3.0]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.2.1...v0.3.0

[0.2.1]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.2.0...v0.2.1

[0.2.0]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.1.0...v0.2.0

[0.1.0]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.0.3...v0.1.0

[0.0.3]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.0.2...v0.0.3

[0.0.2]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/commits/v0.0.2

[Unreleased]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.10.2...HEAD

[0.10.2]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.10.1...v0.10.2

[0.10.1]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.10.0...v0.10.1

[0.10.0]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.9.4...v0.10.0

[0.9.4]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.9.3...v0.9.4

[0.9.3]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.9.2...v0.9.3

[0.9.2]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.9.1...v0.9.2

[0.9.1]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.9.0...v0.9.1

[0.9.0]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.8.0...v0.9.0

[0.8.0]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.7.2...v0.8.0

[0.7.2]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.7.1...v0.7.2

[0.7.1]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.7.0...v0.7.1

[0.7.0]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.6.5...v0.7.0

[0.6.5]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.6.4...v0.6.5

[0.6.4]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.6.3...v0.6.4

[0.6.3]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.6.2...v0.6.3

[0.6.2]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.6.1...v0.6.2

[0.6.1]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.6.0...v0.6.1

[0.6.0]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.5.0...v0.6.0

[0.5.0]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.4.1...v0.5.0

[0.4.1]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.4.0...v0.4.1

[0.4.0]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.3.1...v0.4.0

[0.3.1]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.3.0...v0.3.1

[0.3.0]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.2.1...v0.3.0

[0.2.1]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.2.0...v0.2.1

[0.2.0]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.1.0...v0.2.0

[0.1.0]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.0.3...v0.1.0

[0.0.3]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.0.2...v0.0.3

[0.0.2]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/commits/v0.0.2

[Unreleased]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.10.3...HEAD

[0.10.3]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.10.2...v0.10.3

[0.10.2]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.10.1...v0.10.2

[0.10.1]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.10.0...v0.10.1

[0.10.0]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.9.4...v0.10.0

[0.9.4]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.9.3...v0.9.4

[0.9.3]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.9.2...v0.9.3

[0.9.2]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.9.1...v0.9.2

[0.9.1]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.9.0...v0.9.1

[0.9.0]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.8.0...v0.9.0

[0.8.0]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.7.2...v0.8.0

[0.7.2]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.7.1...v0.7.2

[0.7.1]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.7.0...v0.7.1

[0.7.0]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.6.5...v0.7.0

[0.6.5]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.6.4...v0.6.5

[0.6.4]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.6.3...v0.6.4

[0.6.3]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.6.2...v0.6.3

[0.6.2]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.6.1...v0.6.2

[0.6.1]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.6.0...v0.6.1

[0.6.0]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.5.0...v0.6.0

[0.5.0]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.4.1...v0.5.0

[0.4.1]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.4.0...v0.4.1

[0.4.0]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.3.1...v0.4.0

[0.3.1]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.3.0...v0.3.1

[0.3.0]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.2.1...v0.3.0

[0.2.1]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.2.0...v0.2.1

[0.2.0]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.1.0...v0.2.0

[0.1.0]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.0.3...v0.1.0

[0.0.3]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/compare/v0.0.2...v0.0.3

[0.0.2]: https://github.com/IdeaGinkgo/Intellij-Ginkgo/commits/v0.0.2
