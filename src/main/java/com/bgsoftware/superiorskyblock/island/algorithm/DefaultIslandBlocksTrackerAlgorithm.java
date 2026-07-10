package com.bgsoftware.superiorskyblock.island.algorithm;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.island.Island;
import com.bgsoftware.superiorskyblock.api.island.algorithms.IslandBlocksTrackerAlgorithm;
import com.bgsoftware.superiorskyblock.api.key.Key;
import com.bgsoftware.superiorskyblock.api.key.KeyMap;
import com.bgsoftware.superiorskyblock.core.ServerVersion;
import com.bgsoftware.superiorskyblock.core.key.KeyIndicator;
import com.bgsoftware.superiorskyblock.core.key.map.KeyMaps;
import com.bgsoftware.superiorskyblock.core.key.MaterialKeySource;
import com.bgsoftware.superiorskyblock.core.key.types.MaterialKey;
import com.bgsoftware.superiorskyblock.core.logging.Debug;
import com.bgsoftware.superiorskyblock.core.logging.Log;
import com.bgsoftware.superiorskyblock.island.upgrade.IslandUpgradeConstants;
import com.google.common.base.Preconditions;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Map;

public class DefaultIslandBlocksTrackerAlgorithm implements IslandBlocksTrackerAlgorithm {

    private static final SuperiorSkyblockPlugin plugin = SuperiorSkyblockPlugin.getPlugin();

    private final KeyMap<BigInteger> blockCounts = KeyMaps.createConcurrentHashMap(KeyIndicator.MATERIAL);

    private final Island island;
    private boolean loadingDataMode = false;

    public DefaultIslandBlocksTrackerAlgorithm(Island island) {
        this.island = island;
    }

    @Override
    public boolean trackBlock(Key key, BigInteger amount) {
        Preconditions.checkNotNull(key, "key parameter cannot be null.");
        Preconditions.checkNotNull(amount, "amount parameter cannot be null.");

        if (amount.compareTo(BigInteger.ZERO) == 0)
            return false;

        if (!ServerVersion.isLegacy() && key instanceof MaterialKey &&
                ((MaterialKey) key).getMaterialKeySource() == MaterialKeySource.ITEM)
            key = ((MaterialKey) key).toGlobalKey();

        boolean hasBlockLimit = island.getBlockLimit(key) != IslandUpgradeConstants.NO_LIMIT_VALUE;
        boolean valuesMenu = plugin.getBlockValues().isValuesMenu(key);

        if (hasBlockLimit || valuesMenu) {
            Log.debug(Debug.BLOCK_PLACE, island.getOwner().getName(), key, amount);

            addCounts(key, amount);

            return true;
        }

        return false;
    }

    @Override
    public boolean untrackBlock(Key key, BigInteger amount) {
        Preconditions.checkNotNull(key, "key parameter cannot be null.");
        Preconditions.checkNotNull(amount, "amount parameter cannot be null.");

        if (amount.compareTo(BigInteger.ZERO) == 0)
            return false;

        if (!ServerVersion.isLegacy() && key instanceof MaterialKey &&
                ((MaterialKey) key).getMaterialKeySource() == MaterialKeySource.ITEM)
            key = ((MaterialKey) key).toGlobalKey();

        boolean hasBlockLimit = island.getBlockLimit(key) != IslandUpgradeConstants.NO_LIMIT_VALUE;
        boolean valuesMenu = plugin.getBlockValues().isValuesMenu(key);

        if (hasBlockLimit || valuesMenu) {
            Log.debug(Debug.BLOCK_BREAK, island.getOwner().getName(), key, amount);

            Key valueKey = plugin.getBlockValues().getBlockKey(key);
            removeCounts(valueKey, amount);

            if (loadingDataMode)
                return true;

            Key limitKey = island.getBlockLimitKey(valueKey);
            boolean limitCount = false;

            if (!limitKey.equals(valueKey)) {
                removeCounts(limitKey, amount);
                limitCount = true;
            }

            // Fall back: if we counted a specific key under a global limit key, also
            // decrement the global key entry so the limit totals stay consistent.
            Key globalKey = ((com.bgsoftware.superiorskyblock.core.key.BaseKey<?>) valueKey).toGlobalKey();
            if (!globalKey.equals(valueKey) && (!limitCount || !globalKey.equals(limitKey)) &&
                    blockCounts.containsKey(globalKey)) {
                removeCounts(globalKey, amount);
            }

            return true;
        }

        return false;
    }

    @Override
    public BigInteger getBlockCount(Key key) {
        Preconditions.checkNotNull(key, "key parameter cannot be null.");
        return blockCounts.getOrDefault(key, BigInteger.ZERO);
    }

    @Override
    public BigInteger getExactBlockCount(Key key) {
        Preconditions.checkNotNull(key, "key parameter cannot be null.");
        return blockCounts.getRaw(key, BigInteger.ZERO);
    }

    @Override
    public Map<Key, BigInteger> getBlockCounts() {
        return Collections.unmodifiableMap(this.blockCounts);
    }

    @Override
    public void clearBlockCounts() {
        this.blockCounts.clear();
    }

    @Override
    public void setLoadingDataMode(boolean loadingDataMode) {
        this.loadingDataMode = loadingDataMode;
    }

    private void addCounts(Key key, BigInteger amount) {
        Key valueKey = plugin.getBlockValues().getBlockKey(key);

        Log.debug(Debug.BLOCK_COUNT_INCREASE, island.getOwner().getName(), key, amount);

        BigInteger currentAmount = blockCounts.getRaw(valueKey, BigInteger.ZERO);
        blockCounts.put(valueKey, currentAmount.add(amount));

        if (loadingDataMode)
            return;

        Key limitKey = island.getBlockLimitKey(valueKey);

        if (!limitKey.equals(valueKey)) {
            Log.debugResult(Debug.BLOCK_COUNT_INCREASE, "Limit Key", limitKey);
            currentAmount = blockCounts.getRaw(limitKey, BigInteger.ZERO);
            blockCounts.put(limitKey, currentAmount.add(amount));
        }
    }

    private void removeCounts(Key key, BigInteger amount) {
        Log.debug(Debug.BLOCK_COUNT_DECREASE, island.getOwner().getName(), key, amount);
        BigInteger currentAmount = blockCounts.getRaw(key, BigInteger.ZERO);
        if (currentAmount.compareTo(amount) <= 0)
            blockCounts.remove(key);
        else
            blockCounts.put(key, currentAmount.subtract(amount));
    }

}
