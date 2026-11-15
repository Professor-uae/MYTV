package sa.urtv.home.tilawah;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import sa.urtv.home.R;
import java.net.URLDecoder;

/**
 * Where the TV is, for the prayer times: the city picked in the screensaver (its ConfigProvider,
 * same signing key) and, when that app is missing or older, values in res/values/config.xml.
 */
final class Place {
    static final Uri CONFIG = Uri.parse("content://sa.urtv.screensaver.config");

    final double lat, lng;
    final String tz;
    /** «تعديل وقت الصلاة»: minutes added to the five prayers (see PrayerAdjust); zeros unless the settings say so */
    final int[] adj;

    private Place(double lat, double lng, String tz, int[] adj) { this.lat = lat; this.lng = lng; this.tz = tz; this.adj = adj; }

    static Place get(Context c) {
        try (Cursor cur = c.getContentResolver().query(CONFIG, null, null, null, null)) {
            if (cur != null && cur.moveToFirst() && cur.getString(0) != null) {
                Place p = fromFragment(cur.getString(0));
                if (p != null) return p;
            }
        } catch (RuntimeException ignored) {
            // screensaver app missing or signed with another key: fall back to the bundled values
        }
        return bundled(c);
    }

    /** From a settings fragment the caller has already read (one read for a whole tick); null = the screensaver gave none. */
    static Place get(Context c, String fragment) {
        Place p = fragment == null ? null : fromFragment(fragment);
        return p != null ? p : bundled(c);
    }

    private static Place bundled(Context c) {
        return new Place(number(c.getString(R.string.latitude), 24.4539),
                number(c.getString(R.string.longitude), 54.3773),
                orDefault(c.getString(R.string.timezone), "Asia/Dubai"), new int[PrayerAdjust.COUNT]);
    }

    /** "city=..&lat=24.45&lon=54.38&tz=Asia%2FDubai&..." — the same fragment the page reads. */
    private static Place fromFragment(String fragment) {
        try {
            String lat = null, lon = null, tz = null;
            for (String part : fragment.split("&")) {
                int eq = part.indexOf('=');
                if (eq < 0) continue;
                String key = part.substring(0, eq), value = URLDecoder.decode(part.substring(eq + 1), "UTF-8");
                if ("lat".equals(key)) lat = value;
                else if ("lon".equals(key)) lon = value;
                else if ("tz".equals(key)) tz = value;
            }
            if (lat == null || lon == null) return null;
            return new Place(number(lat, 24.4539), number(lon, 54.3773), orDefault(tz, "Asia/Dubai"), PrayerAdjust.fromFragment(fragment));
        } catch (Exception e) {
            return null;
        }
    }

    private static double number(String s, double fallback) {
        try { return Double.parseDouble(s.trim()); } catch (Exception e) { return fallback; }
    }

    private static String orDefault(String s, String fallback) {
        return s == null || s.trim().isEmpty() ? fallback : s.trim();
    }
}
