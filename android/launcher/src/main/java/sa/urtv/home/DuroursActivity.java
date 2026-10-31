package sa.urtv.home;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;

/** «ديرة الدرور»: the calendar page shown whole, full screen. Any click or Back closes it. */
public class DuroursActivity extends Activity {
    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        ImageView page = new ImageView(this);
        page.setImageResource(R.drawable.durour);
        page.setScaleType(ImageView.ScaleType.FIT_CENTER);
        page.setBackgroundColor(Color.rgb(11, 10, 8));
        page.setOnClickListener(v -> finish());
        setContentView(page);
    }
}
