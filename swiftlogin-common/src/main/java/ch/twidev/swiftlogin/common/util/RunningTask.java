/*
 * Copyright (c) 2024. PREZIUSO Matteo - All Rights Reserved
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 *
 * Written by PREZIUSO Matteo, prezmatteo@gmail.com
 */

package ch.twidev.swiftlogin.common.util;

public abstract class RunningTask {

    private boolean isStopped = false;

    private int taskId = -1;

    public RunningTask(boolean isStopped) {
        this.isStopped = isStopped;
    }

    public RunningTask() {
    }

    public abstract void cancel();

    public boolean isStopped() {
        return isStopped;
    }

    public void stop() {
        isStopped = true;

        this.cancel();
    }

    public void setStopped(boolean stopped) {
        isStopped = stopped;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }
}
