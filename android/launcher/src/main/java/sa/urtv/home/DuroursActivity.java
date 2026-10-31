package sa.urtv.home;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

/** «ديرة الدرور»: the interactive calendar wheel (assets/durour/index.html), full screen. Back closes it. */
public class DuroursActivity extends Activity {
    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        WebView page = new WebView(this);
        page.setBackgroundColor(Color.rgb(246, 242, 234));
        WebSettings s = page.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(false);
        s.setAllowFileAccess(true);
        page.setWebViewClient(new android.webkit.WebViewClient());
        page.loadUrl("file:///android_asset/durour/index.html");
        setContentView(page);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
