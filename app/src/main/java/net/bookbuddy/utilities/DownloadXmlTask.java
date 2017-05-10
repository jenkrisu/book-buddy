package net.bookbuddy.utilities;

import android.os.AsyncTask;

import org.w3c.dom.Document;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Jenni on 3.5.2017.
 */

public class DownloadXmlTask extends AsyncTask<URL, Integer, DownloadXmlTask.Result> {

    /**
     * Handler of DownloadCallback.
     */
    public DownloadCallback callback = null;

    /**
     * Result from DownloadXmlTask.
     */
    public static class Result {
        /**
         * Xml document.
         */
        public Document document;

        /**
         * Http status.
         */
        public int status;

        /**
         * Creates result.
         *
         * @param document document
         * @param status   http status
         */
        public Result(Document document, int status) {
            this.document = document;
            this.status = status;
        }
    }

    /**
     * Downloads document.
     *
     * @param args URLs
     * @return Result
     */
    @Override
    protected Result doInBackground(URL... args) {
        URL url = args[0];
        Document document = null;
        InputStream stream = null;
        int status = 0;
        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            status = connection.getResponseCode();

            if (status == HttpURLConnection.HTTP_OK) {
                stream = connection.getInputStream();
                document = InputStreamParser.streamToXmlDoc(stream);
            }

            if (stream != null) {
                stream.close();
            }


            connection.disconnect();

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return new Result(document, status);
    }

    /**
     * Sends result to callback.
     *
     * @param result Result
     */
    @Override
    protected void onPostExecute(Result result) {
        callback.processFinish(result);
    }
}
