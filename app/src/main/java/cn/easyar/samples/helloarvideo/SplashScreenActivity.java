package cn.easyar.samples.helloarvideo;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class SplashScreenActivity extends AppCompatActivity {
    private static long SPLASH_MILLIS = 450;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        LayoutInflater inflater = LayoutInflater.from(this);
        RelativeLayout layout = (RelativeLayout) inflater.inflate(
                R.layout.activity_splash_screen, null, false);

        addContentView(layout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable()
        {

            @Override
            public void run()
            {
                Intent intent = null;
                String userInfo = getUserInfo();
                if(userInfo.isEmpty()) {
                    intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
                } else {
                    intent = new Intent(SplashScreenActivity.this, ImagesActivity.class);
                    intent.putExtra("userInfo", userInfo);
                }

                startActivity(intent);
            }

        }, SPLASH_MILLIS);
    }

    public String getUserInfo() {
        FileInputStream in = null;
        BufferedReader reader = null;
        StringBuilder content = new StringBuilder();

        try{
            in = openFileInput("login_data");
            reader = new BufferedReader( new InputStreamReader(in));
            String line = "";
            while((line = reader.readLine()) != null)  {
                content.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return content.toString();
    }
}
