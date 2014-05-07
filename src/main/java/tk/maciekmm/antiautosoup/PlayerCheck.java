/*
 * Copyright (C) 2014 by maciekmm <m.mionskowski@maciekmm.tk>
 * This file is part of AntiAutoSoup project.
 * AntiAutoSoup can not be copied and/or distributed without the express permission of maciekmm
 */

package tk.maciekmm.antiautosoup;

public class PlayerCheck {
    private long lastHeld = Long.MAX_VALUE;
    private long lastInform = Long.MIN_VALUE;
    private long lastInteract = Long.MAX_VALUE;
    private long preClick = Long.MAX_VALUE;
    private long lastClick = Long.MAX_VALUE;
    private long lastClose = Long.MAX_VALUE;
    private int fails = 0;

    //Click held
    //Click->Close->Held->Interact
    public void setHeld() {
        this.lastHeld = System.currentTimeMillis();
    }

    public void setLastInteract() {
        this.lastInteract = System.currentTimeMillis();
    }

    public void setLastClick() {
        this.preClick = this.lastClick;
        this.lastClick = System.currentTimeMillis();
        this.lastClose = Long.MAX_VALUE;
    }

    public void setLastClose() {
        this.lastClose = System.currentTimeMillis();
    }

    public boolean isLegit(CheckMoment moment) {
        if (moment == CheckMoment.INTERACT || moment == CheckMoment.HELD) {
            if (lastClose == Long.MAX_VALUE && lastClick != Long.MAX_VALUE) {
                //System.out.println("Fail #1");
                fails++;
                return fails<2;
            }
            if (this.lastClose != Long.MAX_VALUE&&this.lastClose>this.lastClick) {
                if (this.lastClose - this.lastClick < 10) {
                    //System.out.println("Fail #2");
                    fails++;
                    return fails<2;
                }
            }
        }
        if (moment == CheckMoment.INTERACT) {
            if (this.lastHeld != Long.MAX_VALUE) {
                if (this.lastInteract - this.lastHeld < 5) {
                    //System.out.println("Fail #3");
                    fails++;
                    return fails<2;
                }
            }

            if (this.preClick!=Long.MAX_VALUE&&this.lastClick - this.preClick < 20) {
                //System.out.println("Fail #4");
                fails++;
                return fails<2;
            }
        }
        return fails<2;
    }

    public long getLastInform() {
        return this.lastInform;
    }

    public void setInform() {
        this.lastInform = System.currentTimeMillis();
    }

    public enum CheckMoment {
        CLICK, HELD, INTERACT
    }
}
