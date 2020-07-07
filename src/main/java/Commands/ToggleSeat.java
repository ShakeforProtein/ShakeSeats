package Commands;

import me.shakeforprotein.shakeseats.ShakeSeats;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ToggleSeat implements CommandExecutor {


    private ShakeSeats pl;

    public ToggleSeat(ShakeSeats main){
        this.pl = main;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if(sender instanceof Player){
            //sender.sendMessage("are player");
            if(pl.noSit.contains(sender.getName())){
                pl.noSit.remove(sender.getName());
                sender.sendMessage(pl.badge + "You will now be able to sit on stairs, slabs and carpets.");
                //sender.sendMessage(sender.getName());
                //sender.sendMessage(pl.noSit.toString());
            }
            else{
                pl.noSit.add(sender.getName());
                //sender.sendMessage(pl.badge + "You will no longer sit on stairs, slabs and carpets");
                sender.sendMessage(pl.noSit.toString());
            }
        }else{sender.sendMessage(pl.err + "Only players can use seats silly.");}
        return true;
    }
}
