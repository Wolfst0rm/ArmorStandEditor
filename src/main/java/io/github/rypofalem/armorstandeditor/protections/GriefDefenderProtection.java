package io.github.rypofalem.armorstandeditor.protections;


import com.griefdefender.api.GriefDefender;
import com.griefdefender.api.claim.Claim;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import static com.griefdefender.api.claim.TrustTypes.BUILDER;
import static com.griefdefender.api.claim.TrustTypes.RESIDENT;


public class GriefDefenderProtection {

    private boolean gdEnabled;

    public GriefDefenderProtection() {
        gdEnabled = Bukkit.getPluginManager().isPluginEnabled("GriefDefender");
        if (!gdEnabled) return;
    }

    public boolean checkPermission(Block block, Player player) {
        if (!gdEnabled) return true;
        if (player.isOp()) return true;
        if (player.hasPermission("asedit.ignoreProtection.griefDefender")) return true;
        if (player.hasPermission("griefdefender.admin.bypass.border-check")) return true;

        Location blockLoc = block.getLocation();
        if (GriefDefender.getCore().getClaimAt(blockLoc) != null) {
            Claim landClaim = GriefDefender.getCore().getClaimAt(blockLoc);

            if (landClaim == null || landClaim.isWilderness() || landClaim.isAdminClaim()) {
                return true;
            } else if (landClaim.isBasicClaim() &&
                    !landClaim.isUserTrusted(player.getUniqueId(), RESIDENT) &&
                    !landClaim.allowEdit(player.getUniqueId()) ||
                    landClaim.isBasicClaim() &&
                            !landClaim.isUserTrusted(player.getUniqueId(), BUILDER) &&
                            !landClaim.allowEdit(player.getUniqueId())) {
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }
}
