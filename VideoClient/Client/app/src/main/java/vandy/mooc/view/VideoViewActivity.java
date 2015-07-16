package vandy.mooc.view;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import vandy.mooc.R;
import vandy.mooc.common.LifecycleLoggingActivity;

public class VideoViewActivity extends LifecycleLoggingActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_view_activity);
    }



}
