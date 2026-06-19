package plugin.pairGame.Command;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jspecify.annotations.NonNull;

import java.net.http.WebSocket;
import java.util.ArrayList;

public class GameStartCommand implements CommandExecutor {
    ArrayList<Material> materials = new ArrayList<>();



    @Override
    public boolean onCommand(@NonNull CommandSender commandSender, @NonNull Command command, @NonNull String s, @NonNull String[] strings) {
        if (commandSender instanceof Player player) {
            Location loc = player.getLocation();
            World world = player.getWorld();
            for (int i = 1; i < 21; i+=2) {
                for (int j = 1; j<21; j+=2) {
                     world.getBlockAt((int) (loc.getX() + i), (int) (loc.getY()), (int) loc.getZ() +j).setType(Material.BAMBOO_BLOCK);materials.add(world.getBlockAt((int)(loc.getX() + i), (int) (loc.getY()), (int) loc.getZ() +j).getType());
                }
            }
        }
        return false;
    }
}
