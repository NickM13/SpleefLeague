package com.spleefleague.proxycore.game.challenge;

/**
 * @author NickM13
 * @since 2/11/2021
 */
public class ChallengeParty extends Challenge {

    private int size;

    public ChallengeParty(String mode, String query, int size) {
        super(mode, query);
        this.size = size;
    }

    public int getSize() {
        return size;
    }

}
