package be.alexandre01.feedpatcher.feedpatcher;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class FeedCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
        if(cmd.getName().equalsIgnoreCase("feedpatcher")){
            if(args.length != 0){
                try{
                    Patcher.slow = Integer.parseInt(args[0]);
                    sender.sendMessage("§cVous avez mis "+ args[0]+" en lenteur");

                    Patcher.yamlUtils.getConfig().set("slow", Patcher.slow);
                    Patcher.yamlUtils.save();
                }catch (Exception e){
                    sender.sendMessage("§cVous devez mettre un numéro");
                }
            }else {
                sender.sendMessage("§e- §9/feedpatcher [SLOW NUMBER]");
            }
            return true;
        }
        return false;
    }
}
