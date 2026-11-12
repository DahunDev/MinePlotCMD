# MinePlotCMD

**MinePlotCMD** is an extension of the Minecraft PlotSquared plugin, designed specifically for Minecraft version **1.12.2**. This plugin provides additional plot management functionality, primarily tailored for **Korean users**.

## Features

This plugin enhances the functionality of PlotSquared with several new commands for managing and customizing plots. It includes features like:
- Reloading plugin settings
- Biome management with price settings
- Plot clearing and reset functionalities
- Member management (adding, removing, and blacklisting)
- Local plot chatting and chat spying for administrators
- Plot expiration date management
  
**Customization:** Prices for plot actions (such as buying/selling, adding members, and changing biomes) and plot expiration dates (how long a plot should be inactive before it expires) can be customized by modifying the `config.yml` file.

**Commands in this plugin:**
- **/mineplotcmd**: Reload the plugin settings.
- **/땅바이옴**: Change or look up the biome in the plot (with a price depending on the game world). Includes a confirmation feature to prevent accidental changes.
- **/땅초기화**: Clear the plot by spending a certain price. Resets the plot to its initial state while retaining members, biomes, and expiration dates.
- **/땅삭제**: Reset the plot, removing all information and changing it to an unclaimed plot. Administrators can use this to remove others' plots.
- **/땅멤버**: Add a trusted member to the plot for a price.
- **/땅약식멤버**: Add a regular member to the plot for a price.
- **/땅계정이전**: Migrate player data to another player (administration command).
- **/땅보존기간**: Check the expiration date of the plot.
- **/땅정보**: View plot information, including plot ID, owner, members, biome, and expiration dates.
- **/땅목록**: Display a list of plots owned by the player and plots the player is a member of.
- **/땅해제**: Remove members from the plot or blacklist players from accessing it.
- **/만기일자지난땅청소**: Reset plots that have expired due to inactivity (administration command).
- **/땅채팅스파이**: Spy on chat messages within a plot (administration command).
- **/땅채팅**: Send messages to players located in the same plot (aliases: /pc, /plotchat, /플롯채팅).

## Why MinePlotCMD was Needed

While operating my Minecraft server, I encountered limitations with the existing plot plugin, **PlotMe**, which was no longer maintained. I switched to a new plot plugin with better performance and compatibility, but it lacked some key features that were important for my server's operation.

To address these gaps, I developed **MinePlotCMD** as an extension plugin. It was designed to:
- **Add and enhance features**: Features like plot buying/selling and trusted member management using virtual game currency were essential for player-driven plot management, reducing the need for manual administrative tasks.
- **Reduce administrative tasks**: Tools for land resets, plot ownership migration, and automatic plot expiration management significantly cut down on the time required for server maintenance, minimizing manual work. 

## Why MinePlotCMD was Developed as an Addon

I chose to develop **MinePlotCMD** as an addon plugin, rather than modifying the PlotSquared codebase directly, for several reasons:

1. **Future Compatibility**: PlotSquared is actively updated, and the codebase can change frequently. However, since its method and API names rarely change, developing MinePlotCMD as an addon minimizes the need for frequent updates to accommodate changes in the dependent plugin.

2. **API Usage**: PlotSquared provides a stable and well-documented API, which allowed me to extend its functionality without altering its core. This keeps both plugins modular and ensures that MinePlotCMD can work seamlessly with future versions of PlotSquared.

3. **Maintainability**: By maintaining MinePlotCMD as a separate plugin, I can focus on enhancing and maintaining it independently, without worrying about breaking changes in PlotSquared's development.

## License
This project is licensed under the GPL-3.0 License.
