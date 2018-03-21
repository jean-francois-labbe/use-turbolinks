package com.example.jeff.blog;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.basecamp.turbolinks.TurbolinksAdapter;
import com.basecamp.turbolinks.TurbolinksSession;
import com.basecamp.turbolinks.TurbolinksView;

public class MainActivity extends AppCompatActivity implements TurbolinksAdapter {

    // Change the BASE_URL to an address that your VM or device can hit.
     private static final String BASE_URL = "http://192.168.1.100:3000";
    private static final String INTENT_URL = "intentUrl";
    public static final String OAUTH_URL = "users/auth/gitlab";

    private String location;
    private TurbolinksView turbolinksView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find the custom TurbolinksView object in your layout
        turbolinksView = (TurbolinksView) findViewById(R.id.turbolinks_view);

        // For this demo app, we force debug logging on. You will only want to do
        // this for debug builds of your app (it is off by default)
        TurbolinksSession.getDefault(this).setDebugLoggingEnabled(true);

        // For this example we set a default location, unless one is passed in through an intent
        location = getIntent().getStringExtra(INTENT_URL) != null ? getIntent().getStringExtra(INTENT_URL) : BASE_URL;

        // Execute the visit
        TurbolinksSession.getDefault(this)
                .activity(this)
                .adapter(this)
                .view(turbolinksView)
                .visit(location);
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        // Since the webView is shared between activities, we need to tell Turbolinks
        // to load the location from the previous activity upon restarting
        TurbolinksSession.getDefault(this)
                .activity(this)
                .adapter(this)
                .restoreWithCachedSnapshot(false)
                .view(turbolinksView)
                .visit(location);
    }

    @Override
    public void onPageFinished() {

    }

    @Override
    public void onReceivedError(int errorCode) {
        handleError(errorCode);
    }

    @Override
    public void pageInvalidated() {

    }

    @Override
    public void requestFailedWithStatusCode(int statusCode) {
        handleError(statusCode);
    }

    @Override
    public void visitCompleted() {

    }

    @Override
    public void visitProposedToLocationWithAction(String location, String action) {
        Log.i("MainActivity",location);
        if(location.contains(OAUTH_URL)) {
            Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
            intent.putExtra("location", location);
            this.startActivity(intent);
        } else {
        TurbolinksSession.getDefault(this)
                .activity(this)
                .adapter(this)
                .view(turbolinksView)
                .visit(location);
        }
    }

    // Simply forwards to an error page, but you could alternatively show your own native screen
    // or do whatever other kind of error handling you want.
    private void handleError(int code) {
        if (code == 404) {
            TurbolinksSession.getDefault(this)
                    .activity(this)
                    .adapter(this)
                    .restoreWithCachedSnapshot(false)
                    .view(turbolinksView)
                    .visit(BASE_URL + "/error");
        }
    }
}
