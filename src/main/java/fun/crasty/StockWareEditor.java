package fun.crasty;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class StockWareEditor implements CommandExecutor, TabCompleter, Listener {

    private final Main plugin;

    private ItemStack[] userItems;

    private Location wareLocation1;
    private Location wareLocation2;

    private Location salepointLocation1;
    private Location salepointLocation2;

    public StockWareEditor(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if(commandSender instanceof Player) {
            Player player = (Player) commandSender;

            if(s.equals("job_editor") && strings.length == 1) {
                if(strings[0].equals("done")) {
                    if(player.hasPermission("jobs.editor")) {
                        player.getInventory().setContents(userItems);

                        if (wareLocation1 != null && wareLocation2 != null && salepointLocation1 != null &&
                                salepointLocation2 != null) {
                            player.sendMessage(ChatColor.YELLOW + plugin.getConfig().getString("messages.changes_done"));

                            plugin.getConfig().set("areas.ware.first", wareLocation1);
                            plugin.getConfig().set("areas.ware.second", wareLocation2);
                            plugin.getConfig().set("areas.salepoint.first", salepointLocation1);
                            plugin.getConfig().set("areas.salepoint.second", salepointLocation2);

                            plugin.saveConfig();
                            plugin.reloadConfig();

                            wareLocation1 = null;
                            wareLocation2 = null;
                            salepointLocation1 = null;
                            salepointLocation2 = null;
                        } else {
                            player.sendMessage(ChatColor.RED + plugin.getConfig().getString("messages.some_points_not_selected"));
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + plugin.getConfig().getString("messages.dont_have_persmission"));
                    }
                }
            } else if(s.equals("job_editor")){
                if(player.hasPermission("jobs.editor")) {
                    player.sendMessage(ChatColor.AQUA + plugin.getConfig().getString("messages.plugin_desc1"));
                    player.sendMessage(ChatColor.AQUA + plugin.getConfig().getString("messages.plugin_desc2"));

                    userItems = player.getInventory().getContents();
                    for (int i = 0; i < userItems.length; i++) {
                        player.getInventory().remove(userItems[i]);
                    }

                    ItemStack itemWare = new ItemStack(Material.DIAMOND_AXE);
                    ItemMeta itemWareMeta = itemWare.getItemMeta();
                    itemWareMeta.setLocalizedName("ware");
                    itemWareMeta.setDisplayName("Выбор региона: " + ChatColor.YELLOW + "Склад");
                    itemWare.setItemMeta(itemWareMeta);

                    ItemStack itemSalepoint = new ItemStack(Material.DIAMOND_SHOVEL);
                    ItemMeta itemSalepointMeta = itemSalepoint.getItemMeta();
                    itemSalepointMeta.setLocalizedName("salepoint");
                    itemSalepointMeta.setDisplayName("Выбор региона: " + ChatColor.YELLOW + "Точка сбыта");
                    itemSalepoint.setItemMeta(itemSalepointMeta);

                    player.getInventory().addItem(itemWare, itemSalepoint);
                } else {
                    player.sendMessage(ChatColor.RED + plugin.getConfig().getString("messages.dont_have_persmission"));
                }
            }

        }

        return true;
    }


    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        List<String> hints = new ArrayList<>();
        if(strings.length == 1) {
            hints.add("done");
        }
        return hints;
    }

    @EventHandler
    public void areaSelect(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if(player.getInventory().getItemInMainHand().getItemMeta() != null) {
            if(player.getInventory().getItemInMainHand().getItemMeta().getLocalizedName().equals("ware")) {
                if(event.getAction() == Action.LEFT_CLICK_BLOCK) {
                    wareLocation1 = event.getClickedBlock().getLocation();
                    player.sendMessage(ChatColor.GOLD + plugin.getConfig().getString("messages.first_point_was_set"));
                    event.setCancelled(true);
                } else if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    wareLocation2 = event.getClickedBlock().getLocation();
                    player.sendMessage(ChatColor.GOLD + plugin.getConfig().getString("messages.second_point_was_set"));
                }
            } else if(player.getInventory().getItemInMainHand().getItemMeta().getLocalizedName().equals("salepoint")) {
                if(event.getAction() == Action.LEFT_CLICK_BLOCK) {
                    salepointLocation1 = event.getClickedBlock().getLocation();
                    player.sendMessage(ChatColor.GOLD + plugin.getConfig().getString("messages.first_point_was_set"));
                    event.setCancelled(true);
                } else if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    salepointLocation2 = event.getClickedBlock().getLocation();
                    player.sendMessage(ChatColor.GOLD + plugin.getConfig().getString("messages.second_point_was_set"));
                }
                event.setCancelled(true);
            }
        }
    }
}
