# Intellij-Ginkgo

![Build](https://github.com/IdeaGinkgo/Intellij-Ginkgo/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/17554-ginkgo.svg)](https://plugins.jetbrains.com/plugin/17554-ginkgo)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/17554-ginkgo.svg)](https://plugins.jetbrains.com/plugin/17554-ginkgo)


<!-- Plugin description -->
Adds [Ginkgo Test Framework](https://onsi.github.io/ginkgo/) integration for Intellij Ultimate and Goland.  
This is still in beta but should be usable.  Help me make this betting submit any bugs you run into or features you would like to see.  

## Features:
- Run specific test specs from the gutter.
- Ginkgo test run configuration.
- View tests results.

Unfortunately because it relies on the go plugin it is only compatible with IntelliJ IDEA Ultimate and Goland.
<!-- Plugin description end -->

## ScreenShots
![Overview](https://gist.githubusercontent.com/TaylorOno/ec620609cc965e9d2fa67d74e3a853de/raw/abd13daf2e4b0dbc1c58c0d63a27849baeee0e59/Overview.png)
![RunDialog](https://gist.githubusercontent.com/TaylorOno/ec620609cc965e9d2fa67d74e3a853de/raw/abd13daf2e4b0dbc1c58c0d63a27849baeee0e59/RunDialog.png)


## Help me?
I am looking for help in general, but some specific topics include:
 - Improving the ginkgo test parser
 - Adding debug capabilities
 - Cleaning up implementation and fixing the stuff I undoubtedly did incorrectly
 - Adding tests.


## Installation

- Using IDE built-in plugin beta channel system:  
  - Adding the beta channel: <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Manage Plugin Repositories...</kbd> add link `https://plugins.jetbrains.com/plugins/beta/list`
  - Installing plugin: <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "Ginkgo"</kbd> >
  <kbd>Install Plugin</kbd>
  
- Manually:

  Download the [latest release](https://github.com/IdeaGinkgo/Intellij-Ginkgo/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>


---
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
