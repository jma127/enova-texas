package enovahack;

import java.util.concurrent.atomic.*;

public class MonteCarloThread implements Runnable {
    private final int [] hand;
    private final int [] community;
    private final int [] deck;
    private final int numCommunity;
    private final int numOther;
    private final long stop;
    private final AtomicInteger won;
    private final AtomicInteger total;
    private final int [] curDeck;
    public MonteCarloThread (int [] hand, int numOther, int [] partialCommunity,
                             long stop, AtomicInteger won, AtomicInteger total) {
        this.hand = hand.clone();
        this.community = new int[7];
        this.numCommunity = partialCommunity.length;
        for (int i = 0; i < numCommunity; i ++)
            this.community[i] = partialCommunity[i];
        this.deck = new int[52];
        PokerLib.init_deck(this.deck);
        this.numOther = numOther;
        this.stop = stop;
        this.won = won;
        this.total = total;
        this.curDeck = new int[52];
    }
    public boolean tryReplace () {

        if (Math.random() < 0.04)
            return true;

        // Instantiate curDeck
        for (int i = 0; i < 52; i ++)
            curDeck[i] = deck[i];
        PokerLib.shuffle_deck(curDeck);
        //System.out.printf("%d %d\n", hand[0], hand[1]);
        for (int i = 0; i < 52; i ++) {
            //System.out.printf("%d %s\n", curDeck[i], PokerLib.cardToString(curDeck[i]));
            if (curDeck[i] == hand[0] || curDeck[i] == hand[1]) {
                curDeck[i] = -1;
                continue;
            }
            for (int j = 0; j < numCommunity; j ++) {
                if (curDeck[i] == community[j]) {
                    curDeck[i] = -1;
                    break;
                }
            }
        }

        int curPos = 0, bestVal = 9999, bestPlayer = 0;

        // Generate missing community cards
        for (int i = numCommunity; i < 5; i ++) {
            while (curDeck[curPos] == -1)
                curPos ++;
            community[i] = curDeck[curPos];
            curDeck[curPos] = -1;
        }

        // Evaluate opponent hands
        for (int i = 0; i < numOther; i ++) {
            for (int j = 5; j < 7; j ++) {
                while (curDeck[curPos] == -1)
                    curPos ++;
                community[j] = curDeck[curPos];
                curDeck[curPos] = -1;
            }
            //System.out.print(i);
            //for (int j = 0; j < 7; j ++)
                //System.out.print(" " + PokerLib.cardToString(community[j]));
            //System.out.println();
            int val = PokerLib.eval_7hand(community);
            //System.out.println(val);
            if (val < bestVal) {
                bestVal = val;
                bestPlayer = i;
            }
        }

        // Evaluate my hand
        community[5] = hand[0];
        community[6] = hand[1];
        //System.out.print("me");
        //for (int j = 0; j < 7; j ++)
            //System.out.print(" " + PokerLib.cardToString(community[j]));
        //System.out.println();
        int myVal = PokerLib.eval_7hand(community);
        //System.out.println(myVal);

        // Return true if win
        if (myVal < bestVal) {
            return true;
        } else if (myVal == bestVal) {
            return Math.random() * (1 + numOther) < 1;
        }
        return false;
    }
    public void run () {
        for (int i = 0;; i ++) {
            if (i % 10 == 0) {
                if (System.currentTimeMillis() >= stop)
                    return;
            }
            total.getAndIncrement();
            if (tryReplace())
                won.getAndIncrement();
        }
    }
}
