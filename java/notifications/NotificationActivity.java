package com.example.edmorrowcs360finalsubmissioninventoryapp.notifications;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ToggleButton;

import com.example.edmorrowcs360finalsubmissioninventoryapp.R;
import com.example.edmorrowcs360finalsubmissioninventoryapp.main.MainActivity;
import com.example.edmorrowcs360finalsubmissioninventoryapp.data.DatabaseManager;

/*
 * The Notification Activity
 */
public class NotificationActivity extends AppCompatActivity {

    private boolean notificationsEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        ToggleButton tbtnEnableNotifications;
        Button btnGoBack;

        notificationsEnabled = DatabaseManager.getInstance(getApplicationContext()).getNotificationStatus();

        tbtnEnableNotifications = (ToggleButton) findViewById(R.id.tbtnNotifications);
        tbtnEnableNotifications.setChecked(notificationsEnabled);
        tbtnEnableNotifications.setOnClickListener(l -> setNotificationStatus());

        btnGoBack = (Button) findViewById(R.id.btnGoBack);
        btnGoBack.setOnClickListener(l -> goBack());
    }

    private void goBack() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void setNotificationStatus() {

        notificationsEnabled = !notificationsEnabled;
        DatabaseManager.getInstance(getApplicationContext()).setNotificationStatus(notificationsEnabled);
    }
}