package fun.crasty;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;


public class StockAndWare implements Listener {

    private final List<Material> materialList = new ArrayList<Material>() {
        {
            add(Material.HAY_BLOCK);
            add(Material.BARREL);
            add(Material.HONEYCOMB_BLOCK);
            add(Material.COAL_BLOCK);
            add(Material.BRICKS);
            add(Material.REDSTONE_LAMP);
        }
    };

    private final List<UUID> isUserWork = new ArrayList<>();

    private final Main plugin;

    public StockAndWare(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Location playerLocation = player.getLocation();

        Location wareLocation1 = plugin.getConfig().getLocation("areas.ware.first");
        Location wareLocation2 = plugin.getConfig().getLocation("areas.ware.second");

        if(playerLocation.getWorld().getName().equals("auth")) {
            if (wareLocation1.getBlockX() <= playerLocation.getBlockX() && playerLocation.getBlockX() <= wareLocation2.getBlockX()) {
                if (wareLocation1.getBlockY() <= playerLocation.getBlockY() && playerLocation.getBlockY() <= wareLocation2.getBlockY()) {
                    if (wareLocation1.getBlockZ() <= playerLocation.getBlockZ() && playerLocation.getBlockZ() <= wareLocation2.getBlockZ()) {

                        Material clickedMaterial = event.getClickedBlock().getType();

                        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && materialList.contains(clickedMaterial)) {
                            if (player.getInventory().getItemInMainHand().getType() == Material.AIR &&
                            !isUserWork.contains(player.getUniqueId())) {

                                ItemStack resource = new ItemStack(clickedMaterial);
                                ItemMeta resourceMeta = resource.getItemMeta();
                                resourceMeta.setDisplayName(ChatColor.YELLOW + plugin.getConfig().getString("messages.item_name"));
                                resourceMeta.setLocalizedName("work_resource");
                                resource.setItemMeta(resourceMeta);

                                player.getInventory().setItemInMainHand(resource);
                                player.sendTitle(plugin.getConfig().getString("messages.item_was_take_title"), plugin.getConfig().getString("messages.item_was_take_desc"), 10, 50, 10);
                                player.setWalkSpeed(0.1F);
                                event.setCancelled(true);

                                isUserWork.add(player.getUniqueId());

                            } else {
                                player.sendMessage(ChatColor.RED + plugin.getConfig().getString("messages.already_taken_item"));
                                event.setCancelled(true);
                            }
                        }

                    }
                }
            }
        }
    }

    @EventHandler
    public void playerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location playerLocation = player.getLocation();

        Location salepointLocation1 = plugin.getConfig().getLocation("areas.salepoint.first");
        Location salepointLocation2 = plugin.getConfig().getLocation("areas.salepoint.second");

        if(playerLocation.getWorld().getName().equals("auth")) {
            if (salepointLocation1.getBlockX() <= playerLocation.getBlockX() && playerLocation.getBlockX() <= salepointLocation2.getBlockX()) {
                if (salepointLocation1.getBlockY() <= playerLocation.getBlockY() && playerLocation.getBlockY() <= salepointLocation2.getBlockY()) {
                    if (salepointLocation1.getBlockZ() <= playerLocation.getBlockZ() && playerLocation.getBlockZ() <= salepointLocation2.getBlockZ()) {

                        if(player.getInventory().getItemInMainHand().getItemMeta() != null) {
                            if (player.getInventory().getItemInMainHand().getItemMeta().getLocalizedName().equals("work_resource")) {
                                Economy economy = Main.getEconomy();

                                economy.depositPlayer(player, 2);
                                player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                                player.sendTitle(plugin.getConfig().getString("messages.done_resource_title"), plugin.getConfig().getString("messages.done_resource_desc"), 10, 50, 10);
                                player.setWalkSpeed(0.2F);
                                isUserWork.remove(player.getUniqueId());
                            }
                        }
                    }
                }
            }
        }
    }

}
