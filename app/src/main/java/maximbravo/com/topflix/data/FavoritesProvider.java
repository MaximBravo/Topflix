package maximbravo.com.topflix.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static maximbravo.com.topflix.MoviesActivity.favorites;

/**
 * Created by Kids on 4/9/2017.
 */

public class FavoritesProvider extends ContentProvider {
    private static final String LOG_TAG = FavoritesProvider.class.getSimpleName();
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private FavoritesDBHelper mOpenHelper;


    private static final int FAVORITE = 100;
    private static final int SINGLE_FAVORITE = 200;
    private static UriMatcher buildUriMatcher(){
        // Build a UriMatcher by adding a specific code to return based on a match
        // It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = FavoritesContract.CONTENT_AUTHORITY;

        // add a code for each type of URI you want
        matcher.addURI(authority, FavoritesContract.FavoritesEntry.TABLE_FAVORITES, FAVORITE);
        matcher.addURI(authority, FavoritesContract.FavoritesEntry.TABLE_FAVORITES + "/#", SINGLE_FAVORITE);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new FavoritesDBHelper(getContext());

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor retCursor;
        switch(sUriMatcher.match(uri)){
            // All Flavors selected
            case FAVORITE:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        FavoritesContract.FavoritesEntry.TABLE_FAVORITES,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                return retCursor;
            }
            // Individual flavor based on movie id selected
            case SINGLE_FAVORITE:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        FavoritesContract.FavoritesEntry.TABLE_FAVORITES,
                        projection,
                        FavoritesContract.FavoritesEntry.COLUMN_MOVIE_ID + " = ?",
                        new String[] {String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder);
                return retCursor;
            }
            default:{
                // By default, we assume a bad URI
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match){
            case FAVORITE:{
                return FavoritesContract.FavoritesEntry.CONTENT_DIR_TYPE;
            }
            case SINGLE_FAVORITE:{
                return FavoritesContract.FavoritesEntry.CONTENT_ITEM_TYPE;
            }
            default:{
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Uri returnUri;
        switch (sUriMatcher.match(uri)) {
            case FAVORITE: {
                long movieId = db.insert(FavoritesContract.FavoritesEntry.TABLE_FAVORITES, null, values);
                // insert unless it is already contained in the database
                Cursor cursor = query(FavoritesContract.FavoritesEntry.CONTENT_URI,
                        new String[]{FavoritesContract.FavoritesEntry.COLUMN_MOVIE_ID},
                        null,
                        null,
                        null);
                boolean contains = false;
                if(cursor != null) {
                    while (cursor.moveToNext()) {
                        int data = cursor.getInt(cursor.getColumnIndex(FavoritesContract.FavoritesEntry.COLUMN_MOVIE_ID));
                        if(data == movieId){
                            contains = true;
                            break;
                        }
                    }
                    cursor.close();
                }
                if (!contains) {
                    returnUri = FavoritesContract.FavoritesEntry.buildFavoritesUri(movieId);
                } else {
                    throw new android.database.SQLException("Failed to insert row into: " + uri);
                }
                break;
            }

            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);

            }
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int numDeleted;
        switch(match){
            case FAVORITE:
                numDeleted = db.delete(
                        FavoritesContract.FavoritesEntry.TABLE_FAVORITES, selection, selectionArgs);
                // reset _ID
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                        FavoritesContract.FavoritesEntry.TABLE_FAVORITES + "'");
                break;
            case SINGLE_FAVORITE:
                numDeleted = db.delete(FavoritesContract.FavoritesEntry.TABLE_FAVORITES,
                        FavoritesContract.FavoritesEntry.COLUMN_MOVIE_ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                // reset _ID
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                        FavoritesContract.FavoritesEntry.TABLE_FAVORITES + "'");

                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return numDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int numUpdated = 0;

        if (values == null){
            throw new IllegalArgumentException("Cannot have null content values");
        }

        switch(sUriMatcher.match(uri)){
            case FAVORITE:{
                numUpdated = db.update(FavoritesContract.FavoritesEntry.TABLE_FAVORITES,
                        values,
                        selection,
                        selectionArgs);
                break;
            }
            case SINGLE_FAVORITE: {
                numUpdated = db.update(FavoritesContract.FavoritesEntry.TABLE_FAVORITES,
                        values,
                        FavoritesContract.FavoritesEntry.COLUMN_MOVIE_ID + " = ?",
                        new String[] {String.valueOf(ContentUris.parseId(uri))});
                break;
            }
            default:{
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        if (numUpdated > 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numUpdated;
    }
}
