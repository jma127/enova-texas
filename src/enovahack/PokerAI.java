package enovahack;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class PokerAI {
    public static int CORES = 4;
    private static String getCallString () {
        return "action_name=call";
    }
    private static String getFoldString () {
        return "action_name=fold";
    }
    private static String getRaiseString (int amount) {
        return "action_name=bet&amount="+amount;
    }
    public static String getBestBet (State state, long timeMS) {
        // -1 for fold, 0 for call, other are raises
        // We will probabilistically decide odds of winning and whether to call, raise, or fold
        int numOther = state.getNonFolded();
        int [] hand = state.getHand();
        int [] community = state.getCommunityCards();
        long endMS = System.currentTimeMillis() + timeMS;

        // Run monte carlo & calc prob 
        ExecutorService threads = Executors.newFixedThreadPool(CORES);
        AtomicInteger won = new AtomicInteger(0);
        AtomicInteger total = new AtomicInteger(0);
        for (int i = 0; i < CORES; i ++) {
            MonteCarloThread thread = new MonteCarloThread(hand, numOther, community, endMS, won, total);
            threads.submit(thread);
        }
        threads.shutdown();
        try {
            threads.awaitTermination(1, TimeUnit.MINUTES);
        } catch (Exception e) {}
        double add = 0.065;
        if (community.length >= 5)
            add = 0.001;
        else if (community.length >= 4)
            add = 0.010;
        else if (community.length >= 3)
            add = 0.025;
        double prob = won.doubleValue() / total.doubleValue();
        double deficit = 1.0 - prob;
        prob += deficit * add;
        System.out.printf("Ran %d simulations\n", total.intValue());

        // Evaluate other players & calculate move
        Player [] players = state.players;
        int activePlayers = 0;
        int highWager = 0;
        for (Player p : players) {
            if (!p.isFolded())
                activePlayers ++;
            highWager = Math.max(p.getCurrentBet(), highWager);
        }
        int activeIncludingSelf = activePlayers + 1;
        int wager = (int) ((state.getInitialStack() * (prob * activeIncludingSelf - 1)) / activePlayers * 0.8);
        int maxW = (int) (state.getInitialStack() / 1.8);
        int prevWager = 0;
        try {
            prevWager = state.getCurrentBet();
        } catch (Exception e) {
            prevWager = 0;
        }
        if (wager > maxW)
            wager = maxW;
        System.out.printf("  activeIncludingSelf %d wager %d maxWager %d prevWager %d prob %.4f\n", activeIncludingSelf, wager, maxW, prevWager, prob);
        if (wager < highWager) {
            if (state.getInitialStack() < highWager)
                highWager = state.getInitialStack() - 1;
            if (prob * Math.log(((double) state.getInitialStack() + ((double) activePlayers * highWager)) / state.getInitialStack()) + (1 - prob) * Math.log((state.getInitialStack() - highWager) / ((double) state.getInitialStack())) >= Math.log(((double) state.getInitialStack() - prevWager)/state.getInitialStack())) {
                System.out.printf("  Calling\n");
                return getCallString();
            }
            else {
                System.out.printf("  Fold\n");
                return getFoldString();
            }
        }
        else {
            System.out.printf("  Raise %d\n", wager - prevWager);
            return getRaiseString(wager - prevWager);
        }
    }
}
