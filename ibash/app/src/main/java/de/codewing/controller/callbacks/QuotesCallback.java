package de.codewing.controller.callbacks;

import java.util.ArrayList;

import de.codewing.model.Quote;

/**
 * Created by codewing on 03.12.2015.
 */
public interface QuotesCallback {
    void onReceiveQuotes(ArrayList<Quote> quoteList);
}
