package com.spleefleague.zone.fragments;

import com.spleefleague.core.world.FakeUtils;

import java.util.Objects;

/**
 * @author NickM13
 * @since 2/13/2021
 */
public class Fragment {

    final int entityId = FakeUtils.getNextId();

    // 15-8y, 7-4z, 3-0x
    short pos;
    public long fullId;
    public int x, y, z;

    public Fragment(long chunkShifted, short pos) {
        this.pos = pos;
        this.fullId = chunkShifted + pos;

        this.x = pos & 0xF;
        this.z = (pos >> 4) & 0xF;
        this.y = (pos >> 8) & 0xFF;
    }

}
