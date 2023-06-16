package com.example.edmorrowcs360finalsubmissioninventoryapp.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.example.edmorrowcs360finalsubmissioninventoryapp.R;
import com.example.edmorrowcs360finalsubmissioninventoryapp.data.DatabaseManager;

/*
 * The Create Login Activity
 */
public class CreateLoginActivity extends AppCompatActivity {

    private EditText etxtUsername;
    private EditText etxtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_login);

        etxtUsername = findViewById(R.id.etxtUsername);
        etxtPassword = findViewById(R.id.etxtPassword);
        Button btnCreateLogin = (Button)findViewById(R.id.btnCreateLogin);
        Button btnGoBack = (Button)findViewById(R.id.btnGoBack);

        etxtUsername.setOnClickListener(l -> {etxtUsername.setText(""); etxtPassword.setText("");});
        etxtPassword.setOnClickListener(l -> etxtPassword.setText(""));
        btnCreateLogin.setOnClickListener(l -> createLogin());
        btnGoBack.setOnClickListener(l -> goBack());
    }

    private void goBack() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    private void createLogin() {

        String username;
        String password;

        username = etxtUsername.getText().toString();
        password = etxtPassword.getText().toString();

        DatabaseManager.getInstance(getApplicationContext()).addUser(username, password);

        etxtUsername.setText(getString(R.string.userAddedNotification));
        etxtPassword.setText("");
    }
}