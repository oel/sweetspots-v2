## Sweet Spots v2 - an Android board game

---

This Git repo contains source code for the Android board game, Sweet Spots, updated to work with Android API levels 23+ and target SDK 33.

The board game can be downloaded from [Google Play](https://play.google.com/store/apps/details?id=com.genuine.android.sweetspots)

The app was initially developed in 2013, refactored and published in 2015.  The old Android app is at this [GitHub repo](https://github.com/oel/sweetspots).  Included in the old repo is also the Java app that solves and creates games of any board size.  For a quick overview of the Sweet Spots game, please visit the [Genuine Blog](https://blog.genuine.com/2015/12/an-android-board-game-sweet-spots/)

The underlying game-playing logic follows an interesting board game called Alberi that is based on some mathematical puzzle designed by Giorgio Dendi.

As a quick recap, the game consists of a square board composed of N rows x N columns of cells.  The board is also partitioned into N contiguous colored-zones.  The goal is to distribute a number of treasure chests into the area with the following rules:

1. Each row must have exactly 1 treasure chest
2. Each column must have exactly 1 treasure chest
3. Each zone must have exactly 1 treasure chest
4. No treasure chests can be adjacent row-wise, column-wise or diagonally, to each other
5. Some of the games are set up with rule of 2 treasure chests per row/column/zone

---
