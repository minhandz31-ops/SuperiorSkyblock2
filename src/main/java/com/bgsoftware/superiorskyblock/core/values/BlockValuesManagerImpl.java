package com.bgsoftware.superiorskyblock.core.values;

import com.bgsoftware.common.annotations.Nullable;
import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.key.CustomKeyParser;
import com.bgsoftware.superiorskyblock.api.key.Key;
import com.bgsoftware.superiorskyblock.api.key.KeyMap;
import com.bgsoftware.superiorskyblock.api.key.KeySet;
import com.bgsoftware.superiorskyblock.api.objects.Pair;
import com.bgsoftware.superiorskyblock.core.Manager;
import com.bgsoftware.superiorskyblock.core.key.BaseKey;
import com.bgsoftware.superiorskyblock.core.key.KeyIndicator;
import com.bgsoftware.superiorskyblock.core.key.map.KeyMaps;
import com.bgsoftware.superiorskyblock.core.key.set.KeySets;
import com.google.common.base.Preconditions;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Map;

public class BlockValuesManagerImpl extends Manager {

    private static final KeyMap<CustomKeyParser> customKeyParsers = KeyMaps.createArrayMap(KeyIndicator.MATERIAL);
    private static final KeySet valuesMenuBlocks = KeySets.createHashSet(KeyIndicator.MATERIAL);
    private static final KeySet customBlockKeys = KeySets.createHashSet(KeyIndicator.MATERIAL);

    public BlockValuesManagerImpl(SuperiorSkyblockPlugin plugin) {
        super(plugin);
    }

    @Override
    public void loadData() {
        // No worth/level data to load anymore.
        // Custom key parsers, values-menu blocks and custom block keys are
        // registered externally (Hooks, settings, upgrades) at their own load time.
    }

    public Key getBlockKey(Key key) {
        Preconditions.checkNotNull(key, "key parameter cannot be null.");

        if (isValuesMenu(key)) {
            return markAsAPIIfNeeded(key, getValuesKey(key));
        }

        return markAsAPIIfNeeded(key, customBlockKeys.getKey(key, key));
    }

    private Key markAsAPIIfNeeded(Key original, Key newKey) {
        if (original != newKey && ((BaseKey<?>) original).isAPIKey())
            newKey = ((BaseKey<?>) newKey).markAPIKey();

        return newKey;
    }

    public void registerKeyParser(CustomKeyParser customKeyParser, Key... blockTypes) {
        Preconditions.checkNotNull(customKeyParser, "customKeyParser parameter cannot be null.");
        Preconditions.checkNotNull(blockTypes, "blockTypes parameter cannot be null.");

        for (Key blockType : blockTypes)
            customKeyParsers.put(blockType, customKeyParser);
    }

    public void registerMenuValueBlocks(KeySet blocks) {
        valuesMenuBlocks.addAll(blocks);
    }

    public boolean isValuesMenu(Key key) {
        return valuesMenuBlocks.contains(key);
    }

    public Key getValuesKey(Key key) {
        return valuesMenuBlocks.getKey(key, key);
    }

    public void addCustomBlockKey(Key key) {
        customBlockKeys.add(key);
    }

    public void addCustomBlockKeys(Collection<Key> blocks) {
        customBlockKeys.addAll(blocks);
    }

    public Key convertKey(Key original, Location location) {
        CustomKeyParser customKeyParser = customKeyParsers.get(original);

        if (customKeyParser == null)
            return original;

        Key key = customKeyParser.getCustomKey(location);

        return key == null ? original : key;
    }

    public Key convertKey(Key original, ItemStack itemStack) {
        CustomKeyParser customKeyParser = customKeyParsers.get(original);

        if (customKeyParser == null)
            return original;

        return customKeyParser.getCustomKey(itemStack, original);
    }

    public Key convertKey(Key original, Entity entity) {
        CustomKeyParser customKeyParser = customKeyParsers.get(original);

        if (customKeyParser == null)
            return original;

        Key key = customKeyParser.getCustomKey(entity);

        return key == null ? original : key;
    }

    @Nullable
    public Pair<Key, ItemStack> convertCustomKeyItem(Key original) {
        for (Map.Entry<Key, CustomKeyParser> entry : customKeyParsers.entrySet()) {
            if (entry.getValue().isCustomKey(original)) {
                return new Pair<>(entry.getKey(), entry.getValue().getCustomKeyItem(original));
            }
        }

        return new Pair<>(original, null);
    }

}
