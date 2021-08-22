# Intellij-ginkgo

![Build](https://github.com/IdeaGinkgo/Intellij-Ginkgo/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/PLUGIN_ID.svg)](https://plugins.jetbrains.com/plugin/PLUGIN_ID)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/PLUGIN_ID.svg)](https://plugins.jetbrains.com/plugin/PLUGIN_ID)


<!-- Plugin description -->
Adds [Ginkgo Test Framework](https://onsi.github.io/ginkgo/) integration for Intellij and Goland. 
This is very much a work in progress I consider the current version an MVP.  I am sure there is a lot of janky stuff.
I would be interested in collaborating with somebody who knows what they are doing and can help me write some tests and clean up the implementation.

Unfortunately because it relies on the go plugin it is only compatible IDE is IntelliJ IDEA Ultimate and Goland.
<!-- Plugin description end -->

## Features
 - Run specific test specs from the gutter.
 - Ginkgo test run configuration.
 - View tests results if you are using the [Golandreporter](https://github.com/SemanticallyNull/golandreporter)

## ScreenShots
![Overview](https://gist.githubusercontent.com/TaylorOno/ec620609cc965e9d2fa67d74e3a853de/raw/cf219769a7c0ca81d707d482e6d7f8626917054c/Overview.png)
![RunDialog](https://gist.githubusercontent.com/TaylorOno/ec620609cc965e9d2fa67d74e3a853de/raw/cf219769a7c0ca81d707d482e6d7f8626917054c/RunDialog.png)



## Help me?
I am looking for help in general, but some specific topics include:
 - Writing a custom test parser so test results can be displayed with the need to leverage the ginkgo custom reporter functionality that will be deprecated in Ginkgo 2.0
 - Cleaning up implementation and fixing the stuff I undoubtedly did incorrectly
 - Adding tests.


## Installation

- Using IDE built-in plugin system:
  
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "Intellij-ginkgo"</kbd> >
  <kbd>Install Plugin</kbd>
  
- Manually:

  Download the [latest release](https://github.com/IdeaGinkgo/Intellij-Ginkgo/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>


---
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
