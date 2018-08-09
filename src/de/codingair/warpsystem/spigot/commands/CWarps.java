package de.codingair.warpsystem.spigot.commands;

import de.codingair.codingapi.server.Sound;
import de.codingair.codingapi.server.commands.BaseComponent;
import de.codingair.codingapi.server.commands.CommandBuilder;
import de.codingair.codingapi.server.commands.CommandComponent;
import de.codingair.codingapi.server.commands.MultiCommandComponent;
import de.codingair.warpsystem.spigot.WarpSystem;
import de.codingair.warpsystem.gui.affiliations.Category;
import de.codingair.warpsystem.spigot.gui.guis.GWarps;
import de.codingair.warpsystem.spigot.language.Example;
import de.codingair.warpsystem.spigot.language.Lang;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CWarps extends CommandBuilder {
    public CWarps() {
        super("Warps", new BaseComponent(WarpSystem.PERMISSION_USE) {
            @Override
            public void noPermission(CommandSender sender, String label, CommandComponent child) {
                sender.sendMessage(Lang.getPrefix() + Lang.get("Player_Cannot_Use_System", new Example("ENG", "&cYou are not allowed to use warps!"), new Example("GER", "&cSie dürfen keine Warps benutzen!")));
            }

            @Override
            public void onlyFor(boolean player, CommandSender sender, String label, CommandComponent child) {
                sender.sendMessage(Lang.getPrefix() + Lang.get("Only_For_Players", new Example("ENG", "This action is only for players!"), new Example("GER", "Diese Aktion ist nur für Spieler!")));
            }

            @Override
            public void unknownSubCommand(CommandSender sender, String label, String[] args) {
                run(sender, null);
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                run(sender, null);
                return false;
            }
        }.setOnlyPlayers(true), true);

        try {
            setHighestPriority(WarpSystem.getInstance().getFileManager().getFile("Config").getConfig().getBoolean("WarpSystem.Dominate_In_Commands.Highest_Priority.Warps", true));
        } catch(Exception e) {
            e.printStackTrace();
        }

        getBaseComponent().addChild(new MultiCommandComponent() {
            @Override
            public void addArguments(CommandSender sender, List<String> suggestions) {
                for(Category c : WarpSystem.getInstance().getIconManager().getCategories()) {
                    suggestions.add(c.getNameWithoutColor());
                }
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String argument, String[] args) {
                run(sender, WarpSystem.getInstance().getIconManager().getCategory(argument));
                return false;
            }
        });
    }

    static void run(CommandSender sender, Category category) {
        Player p = (Player) sender;

        if(!WarpSystem.activated) return;

        if(WarpSystem.maintenance && !p.hasPermission(WarpSystem.PERMISSION_ByPass_Maintenance)) {
            p.sendMessage(Lang.getPrefix() + Lang.get("Warning_Maintenance",
                    new Example("ENG", "&cThe WarpSystem is currently in maintenance mode, please try it later again."),
                    new Example("GER", "&cDas WarpSystem ist momentan im Wartungs-Modus, bitte versuche es später erneut.")));
            return;
        }

        new GWarps(p, category, false).open();
        Sound.LEVEL_UP.playSound(p);
    }
}
