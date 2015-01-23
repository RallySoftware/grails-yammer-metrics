/*
 * Copyright 2013 Jeff Ellis
 */
package org.grails.plugins.metrics.groovy

import org.apache.commons.logging.LogFactory

class Timer {

    @Delegate
    private com.codahale.metrics.Timer timerMetric
    String owner
    String name

    private ownerLog

    Timer(Class<?> owner, String name) {
        this.owner = owner.name
        this.name = name

        ownerLog = LogFactory.getLog(owner)
        timerMetric = Metrics.newTimer(name)
    }

    def time(Closure closure, String maxMessage = null) {
        def oldMax = timerMetric.getSnapshot().getMax()
        com.codahale.metrics.Timer.Context tc = timerMetric.time()
        try {
            closure.call()
        } finally {
            tc.stop()
        }
        def newMax = timerMetric.getSnapshot().getMax()
        if (newMax > oldMax) {
            onNewMax.call()
            if (maxMessage) {
                ownerLog.info("${name} -- New maximum of ${newMax} ns set")
                ownerLog.info(maxMessage)
            }
        }
    }

    @Deprecated
    def time(String maxMessage, Closure closure) {
        this.time(closure, maxMessage)
    }

    def onNewMax = {
    }

}