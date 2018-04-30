package cn.easyar.samples.helloarvideo;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private SessionService  mSessionService;
    private User mUser;

    @BindView(R.id.input_email)
    EditText _emailText;
    @BindView(R.id.input_password) EditText _passwordText;
    @BindView(R.id.btn_login)
    Button _loginButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("登录中...");
        progressDialog.show();

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        // TODO: Implement your own authentication logic here.
        mSessionService = ApiUtils.getSessionService();
        mSessionService.getSession(email, password).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, final Response<User> response) {

                if(response.isSuccessful()) {
                    new android.os.Handler().postDelayed(
                            new Runnable() {
                                public void run() {
                                    mUser = response.body();
                                    onLoginSuccess();
                                    progressDialog.dismiss();
                                }
                            }, 3000);

                }else {
                    //int statusCode  = response.code();
                    // handle request errors depending on status code
                    new android.os.Handler().postDelayed(
                            new Runnable() {
                                public void run() {
                                    onLoginFailed();
                                    progressDialog.dismiss();
                                }
                            }, 3000);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "对不起，登录发生错误:" + t.getMessage(), Toast.LENGTH_SHORT ).show();
                progressDialog.dismiss();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        saveLoginFile();
        Intent intent = new Intent(LoginActivity.this, ImagesActivity.class);
        Gson gson = new Gson();
        String user_json_str = gson.toJson(mUser);
        intent.putExtra("userInfo", user_json_str);
        startActivity(intent);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "登录失败, 错误的邮箱或密码", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("输入您的登录邮箱地址");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("输入您的密码，最短长度6");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    private void saveLoginFile() {
        Gson gson = new Gson();
        String user_json_str = gson.toJson(mUser);


        FileOutputStream out = null;
        BufferedWriter writer = null;
        try {
            out = openFileOutput("login_data", Context.MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(user_json_str);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
