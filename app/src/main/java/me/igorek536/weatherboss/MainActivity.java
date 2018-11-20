package me.igorek536.weatherboss;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import me.igorek536.weatherboss.utils.HTTPUtils;

/**
 * WeatherBOSS main class
 *
 * @author Igorek536
 * @version 1.0
 */

public class MainActivity extends AppCompatActivity {
    private Button searchButton;
    private TextView nameText;

    private Core core = new Core();
    private static HTTPUtils httpUtils = new HTTPUtils();

    public MainActivity() {
        /*
         * PrettyLogger configuration
         * see: https://github.com/orhanobut/logger
         */
        Logger.addLogAdapter(
                new AndroidLogAdapter(PrettyFormatStrategy.newBuilder()
                        .showThreadInfo(false)    // (Optional) Whether to show thread info or not. Default true
                        .methodCount(2)           // (Optional) How many method line to show. Default 2
                        .methodOffset(7)          // (Optional) Hides internal method calls up to offset. Default 5
                        //.logStrategy(customLog) // (Optional) Changes the log strategy to print out. Default LogCat
                        .tag("WBOSS_LOG")         // (Optional) Global tag for every log. Default PRETTY_LOGGER
                        .build()) {
                    @Override public boolean isLoggable(int priority, String tag) {
                        return BuildConfig.DEBUG;
                    }
                });
    }

    private static class httpRequestTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... urls) {
            String result = null;
            if (urls.length >= 1) {
                try {
                    result = httpUtils.httpRequest(urls[0]);
                } catch (final IOException e) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Logger.e("HTTP REQUEST ERROR!", e);
                            Toast.makeText(new Core().getAppContext(), "[HTTP REQUEST ERROR]", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject reader = new JSONObject(result);

                if ((reader.getInt("count") > 0) && (reader.getInt("cod") == 200)) {
                    JSONArray list = reader.getJSONArray("list");
                    for (int i = 0; i < list.length(); i++) {
                        JSONObject listObj = list.getJSONObject(i);
                        String id = listObj.getString("id");
                        String name = listObj.getString("name");

                        // Тут у нас уже есть результат парсинга и мы что-то делаем, например
                        // заполняем список городов


                        Toast.makeText(new Core().getAppContext(), name, Toast.LENGTH_LONG).show();
                    }
                } else {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(new Core().getAppContext(), "NO CITY FOUND!", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } catch (final JSONException e) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Logger.e("JSON PARSING ERROR", e);
                        Toast.makeText(new Core().getAppContext(), "[JSON PARSING ERROR]", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        core.setAppContext(getApplicationContext());

        searchButton = findViewById(R.id.searchButton);
        nameText = findViewById(R.id.nameText);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new httpRequestTask().execute("http://api.openweathermap.org/data/2.5/find?q=" +
                        nameText.getText() + "&units=metric&appid=ca72f4372bb745ccd9537e31a65145be");
            }
        });
    }
}
