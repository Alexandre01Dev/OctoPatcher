package be.alexandre01.feedpatcher.feedpatcher;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

public class Poubelle implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
        if(sender instanceof Player){

        Player player = (Player) sender;
        if(cmd.getName().equalsIgnoreCase("poubelle")){
            player.openInventory(Bukkit.createInventory(null,54,"§cPoubelle"));
        }
        }
        return false;
    }
}
