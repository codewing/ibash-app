package de.codewing.utils;

import java.util.ArrayList;

/**
 * Created by codewing on 22.12.2015.
 */
public class ParseResult<T> {

    private boolean lastPage;
    private ArrayList<T> elements;

    public ParseResult(boolean lastPage, ArrayList<T> elements) {
        this.lastPage = lastPage;
        this.elements = elements;
    }

    public boolean isLastPage() {
        return lastPage;
    }

    public void setLastPage(boolean lastPage) {
        this.lastPage = lastPage;
    }

    public ArrayList<T> getElements() {
        return elements;
    }

    public void setElements(ArrayList<T> elements) {
        this.elements = elements;
    }
}
