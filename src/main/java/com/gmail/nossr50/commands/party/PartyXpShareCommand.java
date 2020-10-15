package com.gmail.nossr50.commands.party;

import com.gmail.nossr50.config.Config;
import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.datatypes.party.PartyFeature;
import com.gmail.nossr50.datatypes.party.ShareMode;
import com.gmail.nossr50.locale.LocaleLoader;
import com.gmail.nossr50.mcMMO;
import com.gmail.nossr50.util.StringUtils;
import com.gmail.nossr50.util.commands.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PartyXpShareCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if(mcMMO.getUserManager().queryMcMMOPlayer((Player) sender) == null)
        {
            sender.sendMessage(LocaleLoader.getString("Profile.PendingLoad"));
            return true;
        }

        Party party = mcMMO.getUserManager().queryMcMMOPlayer((Player) sender).getParty();

        if (party.getLevel() < Config.getInstance().getPartyFeatureUnlockLevel(PartyFeature.XP_SHARE)) {
            sender.sendMessage(LocaleLoader.getString("Party.Feature.Disabled.5"));
            return true;
        }

        if (args.length == 2) {
            if (args[1].equalsIgnoreCase("none") || CommandUtils.shouldDisableToggle(args[1])) {
                handleChangingShareMode(party, ShareMode.NONE);
            } else if (args[1].equalsIgnoreCase("equal") || args[1].equalsIgnoreCase("even") || CommandUtils.shouldEnableToggle(args[1])) {
                handleChangingShareMode(party, ShareMode.EQUAL);
            } else {
                sender.sendMessage(LocaleLoader.getString("Commands.Usage.2", "party", "xpshare", "<NONE | EQUAL>"));
            }

            return true;
        }
        sender.sendMessage(LocaleLoader.getString("Commands.Usage.2", "party", "xpshare", "<NONE | EQUAL>"));
        return true;
    }

    private void handleChangingShareMode(Party party, ShareMode mode) {
        party.setXpShareMode(mode);

        String changeModeMessage = LocaleLoader.getString("Commands.Party.SetSharing", LocaleLoader.getString("Party.ShareType.Xp"), LocaleLoader.getString("Party.ShareMode." + StringUtils.getCapitalized(mode.toString())));

        for (Player member : party.getPartyMembers()) {
            member.sendMessage(changeModeMessage);
        }
    }
}
