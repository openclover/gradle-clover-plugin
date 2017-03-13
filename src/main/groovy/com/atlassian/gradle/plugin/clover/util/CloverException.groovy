package com.atlassian.gradle.plugin.clover.util

import org.gradle.api.GradleException

class CloverException extends GradleException {

    CloverException() {
    }

    CloverException(String message) {
        super(message)
    }

    CloverException(String message, Throwable cause) {
        super(message, cause)
    }
}
