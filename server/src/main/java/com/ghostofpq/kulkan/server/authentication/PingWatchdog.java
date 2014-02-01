package com.ghostofpq.kulkan.server.authentication;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PingWatchdog implements Runnable {

    private long time;
    private Thread thread;
    private boolean activated;

    public PingWatchdog(Thread thread, long time) {
        this.time = time;
        this.thread = thread;
        this.activated = true;
    }

    public void run() {
        while (activated) {
            try {
                Thread.sleep(time);
                thread.interrupt();
                log.debug("Watchdog activation");
            } catch (InterruptedException e) {
                log.warn("Message Wait interrupted");
            }
        }
    }

    public void stop() {
        activated = false;
    }
}
