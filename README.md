# Ludum Dare 41

[![Travis build status](https://travis-ci.org/bploeckelman/LudumDare41.svg)](https://travis-ci.org/bploeckelman/LudumDare41)

## Build Requirements

* [JDK](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2141151.html)

## Setup

### Mac OS X

The easy way to setup Mac OS X to do LibGDX game dev is to utilize [homebrew](http://brew.sh)

Homebrew requires [xcode](https://developer.apple.com/xcode/downloads/).

Install Homebrew:

    ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)"

If you need to install `git` and clone the project, do that first:

    brew install git
    mkdir ~/code && cd ~/code
    git clone git@github.com:bploeckelman/LudumDare41.git
    cd LudumDare41

Install Build requirements:

    brew install caskroom/cask/brew-cask
    brew cask install java

If you don't have a java IDE installed, you can easily download one
(IntelliJ in this example) with `brew cask`:

    brew cask install intellij-idea-ce

Eclipse and Netbeans are also available through `brew cask`.

### Run the game!

    ./gradlew desktop:run

The game should build and run the desktop version.
