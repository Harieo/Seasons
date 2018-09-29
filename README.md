<h1>Seasons v2</h1>

<h3>Seasons 2.1.0 (the latest version when I write this) now implements Maven Modules</h3>
<p>Please note that this is the first project I've ever used Maven Modules on and it may be messier than others who have loads of experience. With that being said, allow me to explain my thought process!</p>
<p>There are 2 version specific modules; v1_12_2_R1 (which represents all code that is only compatible with Legacy spigot) and v_1_13_R1 (which represents all code which is specific to 1.13+ versions <b>OR</b> is hybrid between both versions). The 2 other modules are for general code; plugin (which is code that all other modules need to be able to reference) and core (which has the Maven Shade plugin in it)</p>
<p>If you are compiling the plugin, compile the root module (Seasons-parent) and use the jar generated in the core module (Spigot-Core) as that is a compilation of all other modules (and it is the JavaPlugin part of the project).</p>

<hr />

If you aren't a developer but you would like additions to this plugin, please
go to the tab on the side called Issues and make a suggestion. If I like your
idea then I will add it to the plugin for everyone!

Compiled version ready to use available on Spigot: 
https://www.spigotmc.org/resources/seasons.39298/

Aside from that, enjoy!

