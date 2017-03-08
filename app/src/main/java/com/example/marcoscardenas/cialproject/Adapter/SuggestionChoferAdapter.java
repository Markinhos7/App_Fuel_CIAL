package com.example.marcoscardenas.cialproject.Adapter;

import android.util.Log;
import android.widget.ArrayAdapter;
import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.widget.Filter;

import com.example.marcoscardenas.cialproject.JsonParse;
import com.example.marcoscardenas.cialproject.Model.ChoferGetSet;

/**
 * Created by marcoscardenas on 13-01-17.
 */

public class SuggestionChoferAdapter extends ArrayAdapter<String> {

    protected static final String TAG = "Patente Vehiculo";

    private List<String> suggestions_chofer;
    public SuggestionChoferAdapter(Activity context, String nameFilter) {
            super(context, android.R.layout.simple_dropdown_item_1line);

            suggestions_chofer = new ArrayList<String>();
            }

    @Override
    public int getCount() {
            return suggestions_chofer.size();
            }

    @Override
    public String getItem(int index) {
            return suggestions_chofer.get(index);
            }

    @Override
    public Filter getFilter() {
            Filter myFilter = new Filter() {
    @Override
    public FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            JsonParse jp=new JsonParse();
            if (constraint != null) {

            List<ChoferGetSet> newsuggestions = jp.getParseJsonChofer(constraint.toString());
            suggestions_chofer.clear();
            for (int i=0;i<newsuggestions.size();i++) {
                Log.i("asd",newsuggestions.get(i).getRazon_social());

            suggestions_chofer.add(newsuggestions.get(i).getRut());
            }

                filterResults.values = suggestions_chofer;
            filterResults.count = suggestions_chofer.size();
            }

            return filterResults;
            }

    @Override
    protected void publishResults(CharSequence contraint,
            FilterResults results) {
            if (results != null && results.count > 0) {
            notifyDataSetChanged();
            } else {
            notifyDataSetInvalidated();
            }
            }
            };
            return myFilter;
            }



            }
