package de.codingair.warpsystem.spigot.features.globalwarps.commands;

import de.codingair.codingapi.server.commands.BaseComponent;
import de.codingair.codingapi.server.commands.CommandBuilder;
import de.codingair.codingapi.server.commands.CommandComponent;
import de.codingair.codingapi.server.commands.MultiCommandComponent;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Example;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.globalwarps.guis.GGlobalWarpList;
import de.codingair.warpsystem.spigot.features.globalwarps.guis.affiliations.GlobalWarp;
import de.codingair.warpsystem.spigot.features.globalwarps.managers.GlobalWarpManager;
import de.codingair.warpsystem.spigot.features.warps.guis.affiliations.Category;
import de.codingair.warpsystem.spigot.features.warps.guis.affiliations.utils.Action;
import de.codingair.warpsystem.spigot.features.warps.managers.IconManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CGlobalWarp extends CommandBuilder {
    public CGlobalWarp() {
        super("GlobalWarp", new BaseComponent(WarpSystem.PERMISSION_USE) {
            @Override
            public void noPermission(CommandSender sender, String label, CommandComponent child) {
                sender.sendMessage(Lang.getPrefix() + Lang.get("No_Permission", new Example("GER", "&cDu hast keine Berechtigungen für diese Aktion!"), new Example("ENG", "&cYou don't have permissions for that action!"), new Example("FRE", "&cDésolé mais vous ne possédez la permission pour exécuter cette action!")));
            }

            @Override
            public void onlyFor(boolean player, CommandSender sender, String label, CommandComponent child) {
                sender.sendMessage(Lang.getPrefix() + Lang.get("Only_For_Players"));
            }

            @Override
            public void unknownSubCommand(CommandSender sender, String label, String[] args) {
                sender.sendMessage(Lang.getPrefix() + Lang.get("GlobalWarp_Teleport_Help", new Example("ENG", "&7Use: &e/GlobalWarp <warp>"), new Example("GER", "&7Benutze: &e/GlobalWarp <warp>")));
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                sender.sendMessage(Lang.getPrefix() + Lang.get("GlobalWarp_Teleport_Help", new Example("ENG", "&7Use: &e/GlobalWarp <warp>"), new Example("GER", "&7Benutze: &e/GlobalWarp <warp>")));
                return false;
            }
        }, true);

        getBaseComponent().addChild(new MultiCommandComponent() {
            @Override
            public void addArguments(CommandSender sender, List<String> suggestions) {
                if(GlobalWarpManager.getInstance().isGlobalWarpsOfGUI()) {
                    for(GlobalWarp globalWarp : IconManager.getInstance().getGlobalWarps()) {
                        if(!globalWarp.hasPermission() || sender.hasPermission(globalWarp.getPermission())) {
                            suggestions.add((globalWarp.getNameWithoutColor() + (globalWarp.getCategory() == null ? "" : "@" + globalWarp.getCategory().getNameWithoutColor())).replace(" ", "_"));
                        }
                    }
                } else {
                    suggestions.addAll(GlobalWarpManager.getInstance().getGlobalWarps().keySet());
                }
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String argument, String[] args) {
                if(GlobalWarpManager.getInstance().isGlobalWarpsOfGUI()) {
                    argument = argument.replace("_", " ");

                    Category category = null;
                    boolean failure = false;

                    if(argument.contains("@")) {
                        category = IconManager.getInstance().getCategory(argument.split("@")[1]);
                        argument = argument.split("@")[0];

                        failure = category == null;
                    }

                    GlobalWarp gw = IconManager.getInstance().getGlobalWarp(argument, category);

                    if(gw == null || failure) {
                        sender.sendMessage(Lang.getPrefix() + Lang.get("GlobalWarp_Does_Not_Exist", new Example("ENG", "&cThis GlobalWarp does not exist."), new Example("GER", "&cDieser GlobalWarp existiert nicht.")));
                        return false;
                    }

                    gw.perform((Player) sender, false, Action.RUN_COMMAND);
                } else {
                    if(GlobalWarpManager.getInstance().exists(argument)) {
                        WarpSystem.getInstance().getTeleportManager().teleport((Player) sender, argument, argument, 0);
                    } else sender.sendMessage(Lang.getPrefix() + Lang.get("GlobalWarp_Does_Not_Exist", new Example("ENG", "&cThis GlobalWarp does not exist."), new Example("GER", "&cDieser GlobalWarp existiert nicht.")));
                }
                return false;
            }
        });
    }
}