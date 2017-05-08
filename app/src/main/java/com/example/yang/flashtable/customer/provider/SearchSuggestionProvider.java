package com.example.yang.flashtable.customer.provider;

import android.content.SearchRecentSuggestionsProvider;

/**
 * Created by Yang on 2017/5/8.
 */

public class SearchSuggestionProvider extends SearchRecentSuggestionsProvider {

    public static String AUTHORITY = "com.example.yang.flashtable.customer.provider.SuggestionProvider";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public SearchSuggestionProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}
