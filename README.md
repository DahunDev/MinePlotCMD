# MinePlotCMD
extention of Minecraft PlotSqaured plugin for minecraft 1.12.2

This plugin was mainly made for Korean users.

**Commands in this plugin:**

  - /mineplotcmd: reload the plugin settings
  - /땅바이옴: change / lookup the biome in the plot. (Price depends on the game world. It has confirmation feature for change biome to prevent change biome by mistake.)
  - /땅초기화: clear the plot by spending certain price. This command resets the plot to its initial state. (Buildings, etc. will disappear. However, added members/biomes and expiration dates will not be reset.)  (It has confirmation feature which prevent player clear the plot by mistakes.)
  - /땅삭제: Reset the plot and remove the infomration in the plot. The ownership will be removed and it will be changed to unclaimed plot. (If player have administration permission, it allows to remove other's plot. So, this command used for both administrator and users.) (It has confirmation feature for change biome to prevent change biome by mistake.)
  - /땅멤버: add trusted member in the plot with certain price.
  - /땅약식멤버: add member in the plot with certain price.
  - /땅계정이전: Migrate the player data to other players. (Mainly administration command)
  - /땅보존기간: Check the expiration date of the plot.
  - /땅정보: Check the information of the plot. The information includes, plotID, owner, members of the plot, biome of plot, expiration dates.
  - /땅목록: Displays a list of plots owned by the player and plots belonging to members.
  - /땅해제: remove members in the plot. Also, this command allows blacklist certain player to prevent access anything in the plot.
  - /만기일자지난땅청소: clear/reset the plot that expired due to long inactiviety. (Mainly administration command)
  - /땅채팅스파이: See chat messages in plot chatting. (For administrator, something like chat spy feature) (Mainly administration command)
  - /땅채팅 : local chat with players that locate in same plot, send message to those players. (aliases: pc, plotchat, 플롯채팅)
   
