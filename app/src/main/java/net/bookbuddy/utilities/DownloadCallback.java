package net.bookbuddy.utilities;

/**
 * Created by Jenni on 3.5.2017.
 */

public interface DownloadCallback {

    /**
     * Handles result from download task.
     *
     * @param result DownloadTask.Result with document and status
     */
    void processFinish(DownloadXmlTask.Result result);

}
