# Seasons v2 
![Java CI](https://github.com/Harieo/Seasons/workflows/Java%20CI/badge.svg)

Seasons is a little quirky plugin I made a few years ago to simulate real-life seasons in Minecraft, adding some
customised effects and weathers to add a new experience to playing survival

You can download the latest release of the plugin via SpigotMC by [Clicking Here](https://www.spigotmc.org/resources/seasons.39298/)!

## Modules Summary

Seasons implements a _modular Maven_ design which allows multiple versions of Spigot to be run with only 1 plugin  
The modules are as follows:  
* Core - Foundation of the plugin which detemines the version, holds the initial JavaPlugin class and runs Maven Shade
* Plugin - The core functionality of Seasons which is **not** version-specific
* v1_12_2_R1 - Implements functionality for Spigot versions 1.9-1.12
* v1_13_R1 - Implements functionality for Spigot versions 1.13-1.14

If you are compiling the plugin locally, compile the root module (Seasons-parent) and use the shaded jar generated in the 
  core module (Seasons-Core) as that is a compilation of all other modules.
   
## Contributions

This project is funded only by generous donations from users of the plugin and _all development for the project is volunteered_
  by developers who donate their time **free of charge**. If you wish to contribute to the development of Seasons, consider donating
  on [Patreon](https://www.patreon.com/harieo) as it goes a long way!
  
If you are a Developer and wish to help maintain Seasons, you have the option of donating external Pull Requests or contacting me directly to be added as a maintainer. While the work is unpaid, we aim to have fun and you will be given credit for the work

Thanks to everyone who downloads, enjoys and contributes to the development of Seasons!

## Extra Support

If you need extra support regarding Seasons or want to get in contact, joining my personal Discord is the best way!  

[![Discord Server](https://discordapp.com/api/guilds/679733506427191330/embed.png?style=banner2)](https://discord.gg/zTwWZAR)
