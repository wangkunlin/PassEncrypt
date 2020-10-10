package com.wcg.string.fog.utils

import org.gradle.api.logging.Logger

/**
 * On 2020-10-10
 */
class FogLogger {
    private Logger mLogger
    private boolean mNeedLog

    FogLogger(Logger logger, boolean needLog) {
        mLogger = logger
        mNeedLog = needLog
    }

    void lifecycle(String log) {
        if (mNeedLog) {
            mLogger.lifecycle(log)
        }
    }
}
