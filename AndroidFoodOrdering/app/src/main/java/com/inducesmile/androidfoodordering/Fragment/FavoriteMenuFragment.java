package com.inducesmile.androidfoodordering.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.inducesmile.androidfoodordering.R;
import com.inducesmile.androidfoodordering.adapter.FavouriteAdapter;
import com.inducesmile.androidfoodordering.database.Query;
import com.inducesmile.androidfoodordering.entities.FavoriteObject;
import com.inducesmile.androidfoodordering.util.Helper;

import java.util.List;


public class FavoriteMenuFragment extends Fragment {

    private static final String TAG = FavoriteMenuFragment.class.getSimpleName();

    private RecyclerView recyclerView;
    private Query databaseQuery;

    public FavoriteMenuFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle(getString(R.string.favorite_menu_items));
        View view = inflater.inflate(R.layout.fragment_favorite_menu, container, false);

        databaseQuery = new Query(getActivity());
        List<FavoriteObject> favoriteObjects = databaseQuery.listFavoriteMenu();

        recyclerView = (RecyclerView)view.findViewById(R.id.favorite_menu);
        GridLayoutManager mGrid = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(mGrid);
        recyclerView.setHasFixedSize(true);

        if(favoriteObjects == null){
            Helper.displayErrorMessage(getActivity(), "Your favorite list is empty");
        }else {
            FavouriteAdapter mAdapter = new FavouriteAdapter(getActivity(), favoriteObjects);
            recyclerView.setAdapter(mAdapter);
        }

        return view;
    }

}
