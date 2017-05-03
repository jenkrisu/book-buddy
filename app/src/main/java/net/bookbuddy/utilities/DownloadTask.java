package net.bookbuddy.utilities;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Jenni on 3.5.2017.
 */

public class DownloadTask extends AsyncTask<URL, Integer, DownloadTask.Result> {

    public DownloadCallback callback = null;

    public static class Result {
        public InputStream stream;
        public int status;

        public Result (InputStream stream, int status) {
            this.stream = stream;
            this.status = status;
        }
    }

    @Override
    protected DownloadTask.Result doInBackground(URL... args) {
        URL url = args[0];
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
            }

            if (stream != null) {
                stream.close();
            }

            if (connection != null) {
                connection.disconnect();
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return new Result(stream, status);
    }

    @Override
    protected void onPostExecute(Result result) {
        callback.processFinish(result);
    }
}
