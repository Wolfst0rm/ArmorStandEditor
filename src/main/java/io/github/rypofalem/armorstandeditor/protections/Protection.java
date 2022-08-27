package io.github.rypofalem.armorstandeditor.protections;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public interface Protection {
    boolean checkPermission(Block block, Player player);
}
