package com.radomirribic.top10downloader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.os.AsyncTask;
import android.os.Bundle;

import android.os.PersistableBundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    public static final String URL_KEY="urlKey";
    private String feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml";
    public static final String LIMIT_KEY="limitKey";
    private int feedLimit = 10;
    private ListView listApps;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.feeds_menu, menu);
        if (feedLimit == 10) {
            menu.findItem(R.id.mnu10).setChecked(true);
        } else {
            menu.findItem(R.id.mnu25).setChecked(true);
        }
        return super.onCreateOptionsMenu(menu);
    }

    private String getUrls(String choice) {
        String feedUrl = "";
        switch (choice) {
            case "free":
                feedUrl = getString(R.string.urlFree);
                break;
            case "paid":
                feedUrl = getString(R.string.urlPaid);
                break;
            case "songs":
                feedUrl = getString(R.string.urlSongs);
                break;

        }
        return feedUrl;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.mnuFree:
                feedUrl=getUrls("free");

                break;
            case R.id.mnuPaid:
                feedUrl=getUrls("paid");

                break;
            case R.id.mnuSongs:
                feedUrl=getUrls("songs");

                break;
            case R.id.mnuRefreash:
                downloadUrl(String.format(feedUrl, feedLimit));
                return true;

            case R.id.mnu10:
            case R.id.mnu25:
                if (!item.isChecked()) {
                    item.setChecked(true);
                    feedLimit = 35 - feedLimit;

                } else {
                    Log.d(TAG, "onOptionsItemSelected: " + item.getTitle() + " setting feedLimit to " + feedLimit);
                }
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        downloadUrl(String.format(feedUrl, feedLimit));
        Log.d(TAG, "Download done from menu, limit :"+feedLimit+" url: "+feedUrl);
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState!=null){
            Log.d(TAG, "OnCreate, bundle NOT null ");
            feedUrl=savedInstanceState.getString(URL_KEY);
            feedLimit=savedInstanceState.getInt(LIMIT_KEY);
        }
        setContentView(R.layout.activity_main);
        Toast.makeText(this, feedUrl, Toast.LENGTH_LONG).show();


        listApps = findViewById(R.id.xmlListView);

        downloadUrl(String.format(feedUrl, feedLimit));
        Log.d(TAG, "onCreate, download called, limit "+feedLimit+" url "+feedUrl);


    }
//on save instance states
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(URL_KEY,feedUrl);
        outState.putInt(LIMIT_KEY,feedLimit);
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState called,limit "+feedLimit+" url:"+feedUrl);
    }


//on restore instance states
//    @Override
//    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        feedUrl=savedInstanceState.getString(URL_KEY);
//        feedLimit=savedInstanceState.getInt(LIMIT_KEY);
//        Log.d(TAG, "onRestoreInstanceState: "+feedUrl);
//    }

    //Makes a new async and calls it with the provided url
    private void downloadUrl(String feedUrl) {
        DownloadData downloadData = new DownloadData();
        downloadData.execute(feedUrl);
        //Log.d(TAG, "async called downloadUrl: done " +feedUrl);
        Log.d(TAG, "async called,download method with url " +feedUrl);
    }

    private class DownloadData extends AsyncTask<String, Void, String> {
        //private static final String TAG = "DownloadData";


//Starts the thread
        @Override
        protected String doInBackground(String... strings) {
            Log.d(TAG, "Async, doInBackground calls downloadXML with url" + strings[0]);


            String rssFeed = downloadXML(strings[0]);


            if (rssFeed == null) {
                Log.e(TAG, "doInBackground: Error downloading + NOT GETTING ANYTHING");
            }
            return rssFeed;
        }

//parses the rss
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Log.d(TAG, "onPostExecute: parameter is " + s);

            ParseApplications parseApplications = new ParseApplications();
            parseApplications.parse(s);

            FeedAdapter feedAdapter = new FeedAdapter(MainActivity.this, R.layout.list_record, parseApplications.getApplications());
            listApps.setAdapter(feedAdapter);


        }

//downloads rss, returns it as a string
        private String downloadXML(String path) {
            StringBuilder xmlResult = new StringBuilder();
            try {
                URL url = new URL(path);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                int response = connection.getResponseCode();
                Log.d(TAG, "downloadXML: The response code was :" + response);
                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                int charRead;
                char[] inputBuffer = new char[500];
                while (true) {
                    charRead = reader.read(inputBuffer);
                    if (charRead < 0) {
                        break;
                    }
                    if (charRead > 0) {
                        xmlResult.append(String.copyValueOf(inputBuffer, 0, charRead));
                    }
                }
                reader.close();
                return xmlResult.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                Log.e(TAG, "downloadXML: THIS IS Security Exception" + e.getMessage());
            }
            return null;
        }
    }


}



