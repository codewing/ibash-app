package de.codewing.utils;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;

import de.codewing.model.FaviQuote;
import de.codewing.model.Quote;

/**
 * Created by codewing on 22.12.2015.
 */
public class FaviQuoteComparator implements Comparator<Quote> {

    List<FaviQuote> faviQuotes;
    Activity activity;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public FaviQuoteComparator(List<FaviQuote> faviQuotes, Activity activity) {
        this.faviQuotes = faviQuotes;
        this.activity = activity;
    }

    @Override
    public int compare(Quote lhs, Quote rhs) {
        FaviQuote q0 = getQuote(lhs.getIdent());
        FaviQuote q1 = getQuote(rhs.getIdent());
        try {
            long t0 = sdf.parse(q0.getTs()).getTime();
            long t1 = sdf.parse(q1.getTs()).getTime();
            SharedPreferences sharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(activity);
            if (sharedPreferences.getBoolean("pref_key_favi_desc", true)) {
                return (int) (t1 - t0);
            } else {
                return (int) (t0 - t1);
            }
        } catch (ParseException e) {
            Log.e(this.getClass().getName() + "#compare", "Error: " + e);
        }
        return 0;
    }

    private FaviQuote getQuote(int id) {
        for (FaviQuote q : faviQuotes) {
            if (q.getIdent() == id) {
                System.out.println(q.getTs());
                return q;
            }
        }
        return new FaviQuote("21.12.2015 06:00", -1);
    }
}
