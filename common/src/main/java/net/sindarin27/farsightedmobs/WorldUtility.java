package net.sindarin27.farsightedmobs;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.chunk.status.ChunkStatus;

public class WorldUtility {
    // Vanilla ServerLevel.getCurrentDifficultyAt waits for a chunk to finish generation.
    // That's a problem if said generation relies on a predicate that uses the local difficulty.
    public static DifficultyInstance getCurrentDifficultySafely(ServerLevel level, BlockPos blockPos) {
        int sectionX = SectionPos.blockToSectionCoord(blockPos.getX());
        int sectionZ = SectionPos.blockToSectionCoord(blockPos.getZ());

        // If a chunk is not fully generated, it won't have a difficulty yet, but we're also sure it's not inhabited.
        if (
                !level.hasChunk(sectionX, sectionZ) || level.getChunk(sectionX, sectionZ, ChunkStatus.EMPTY)
                        .getHighestGeneratedStatus().isBefore(ChunkStatus.FULL)
        ) {
            return new DifficultyInstance(level.getDifficulty(), level.getDayTime(), 0L, level.getMoonBrightness());
        } else {
            return level.getCurrentDifficultyAt(blockPos);
        }
    }
}
