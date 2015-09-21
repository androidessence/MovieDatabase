package androidessence.moviedatabase;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;

import java.util.Map;
import java.util.Set;

/**
 * Created by adammcneilly on 9/20/15.
 */
public class ProviderTest extends AndroidTestCase {
    private static final String TEST_GENRE_NAME = "Family";
    private static final String TEST_UPDATE_GENRE_NAME = "Adventure";
    private static final String TEST_MOVIE_NAME = "Harry Potter and the Sorcerer's Stone";
    private static final String TEST_UPDATE_MOVIE_NAME = "Harry Potter and the Philosopher's Stone";
    private static final String TEST_MOVIE_RELEASE_DATE = "2001-11-14";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        testDeleteAllRecords();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        testDeleteAllRecords();
    }

    public void testDeleteAllRecords(){
        // Delete movies
        mContext.getContentResolver().delete(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null
        );

        // Delete genres
        mContext.getContentResolver().delete(
                MovieContract.GenreEntry.CONTENT_URI,
                null,
                null
        );

        // Ensure movies were deleted
        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals(0, cursor.getCount());
        cursor.close();

        // Ensure genres were deleted
        cursor = mContext.getContentResolver().query(
                MovieContract.GenreEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals(0, cursor.getCount());
        cursor.close();
    }

    public void testGetType(){
        // content_authority = "content://com.androidessence.moviedatabase/:

        //-- GENRE --//
        // content_authority + genre
        String type = mContext.getContentResolver().getType(MovieContract.GenreEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.androidessence.moviedatabase/genre
        assertEquals(MovieContract.GenreEntry.CONTENT_TYPE, type);

        //-- GENRE_ID --//
        // content_authority + genre/id
        type = mContext.getContentResolver().getType(MovieContract.GenreEntry.buildGenreUri(0));
        // vnd.android.cursor.item/com.androidessence.moviedatabase/genre
        assertEquals(MovieContract.GenreEntry.CONTENT_ITEM_TYPE, type);

        //-- MOVIE --//
        type = mContext.getContentResolver().getType(MovieContract.MovieEntry.CONTENT_URI);
        assertEquals(MovieContract.MovieEntry.CONTENT_TYPE, type);

        //-- MOVIE_ID --//
        type = mContext.getContentResolver().getType(MovieContract.MovieEntry.buildMovieUri(0));
        assertEquals(MovieContract.MovieEntry.CONTENT_ITEM_TYPE, type);
    }

    public void testInsertReadGenre(){
        ContentValues genreContentValues = getGenreContentValues();
        Uri genreInsertUri = mContext.getContentResolver().insert(MovieContract.GenreEntry.CONTENT_URI, genreContentValues);
        long genreRowId = ContentUris.parseId(genreInsertUri);

        // Verify we inserted a row
        assertTrue(genreRowId > 0);

        // Query for all rows and validate cursor
        Cursor genreCursor = mContext.getContentResolver().query(
                MovieContract.GenreEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        validateCursor(genreCursor, genreContentValues);
        genreCursor.close();

        // Query for specific row and validate cursor
        genreCursor = mContext.getContentResolver().query(
                MovieContract.GenreEntry.buildGenreUri(genreRowId),
                null,
                null,
                null,
                null
        );
        validateCursor(genreCursor, genreContentValues);
        genreCursor.close();
    }

    public void testInsertReadMovie(){
        // We first insert a Genre
        // No need to verify this, we already have a test for inserting genre
        ContentValues genreContentValues = getGenreContentValues();
        Uri genreInsertUri = mContext.getContentResolver().insert(MovieContract.GenreEntry.CONTENT_URI, genreContentValues);
        long genreRowId = ContentUris.parseId(genreInsertUri);

        ContentValues movieContentValues = getMovieContentValues(genreRowId);
        Uri movieInsertUri = mContext.getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, movieContentValues);
        long movieRowId = ContentUris.parseId(movieInsertUri);

        // Verify we got a row back
        assertTrue(movieRowId > 0);

        // Query for all and validate
        Cursor movieCursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        validateCursor(movieCursor, movieContentValues);
        movieCursor.close();

        movieCursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.buildMovieUri(movieRowId),
                null,
                null,
                null,
                null
        );
        validateCursor(movieCursor, movieContentValues);
        movieCursor.close();
    }

    public void testUpdateGenre(){
        // Insert the genre first.
        // No need to verify, we have a test for that.
        ContentValues genreContentValues = getGenreContentValues();
        Uri genreInsertUri = mContext.getContentResolver().insert(MovieContract.GenreEntry.CONTENT_URI, genreContentValues);
        long genreRowId = ContentUris.parseId(genreInsertUri);

        // UpdateValues
        ContentValues updatedGenreContentValues = new ContentValues(genreContentValues);
        updatedGenreContentValues.put(MovieContract.GenreEntry._ID, genreRowId);
        updatedGenreContentValues.put(MovieContract.GenreEntry.COLUMN_NAME, TEST_UPDATE_GENRE_NAME);
        mContext.getContentResolver().update(
                MovieContract.GenreEntry.CONTENT_URI,
                updatedGenreContentValues,
                MovieContract.GenreEntry._ID + " = ?",
                new String[]{String.valueOf(genreRowId)}
        );

        // Query for that specific row and verify it
        Cursor genreCursor = mContext.getContentResolver().query(
                MovieContract.GenreEntry.buildGenreUri(genreRowId),
                null,
                null,
                null,
                null
        );
        validateCursor(genreCursor, updatedGenreContentValues);
        genreCursor.close();
    }

    public void testUpdateMovie(){
        // We first insert a Genre
        // No need to verify this, we already have a test for inserting genre
        ContentValues genreContentValues = getGenreContentValues();
        Uri genreInsertUri = mContext.getContentResolver().insert(MovieContract.GenreEntry.CONTENT_URI, genreContentValues);
        long genreRowId = ContentUris.parseId(genreInsertUri);

        ContentValues movieContentValues = getMovieContentValues(genreRowId);
        Uri movieInsertUri = mContext.getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, movieContentValues);
        long movieRowId = ContentUris.parseId(movieInsertUri);

        // Update
        ContentValues updatedMovieContentValues = new ContentValues(movieContentValues);
        updatedMovieContentValues.put(MovieContract.MovieEntry._ID, movieRowId);
        updatedMovieContentValues.put(MovieContract.MovieEntry.COLUMN_NAME, TEST_UPDATE_MOVIE_NAME);
        mContext.getContentResolver().update(
                MovieContract.MovieEntry.CONTENT_URI,
                updatedMovieContentValues,
                MovieContract.MovieEntry._ID + " = ?",
                new String[]{String.valueOf(movieRowId)}
        );

        Cursor movieCursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.buildMovieUri(movieRowId),
                null,
                null,
                null,
                null
        );
        validateCursor(movieCursor, updatedMovieContentValues);
        movieCursor.close();
    }

    private ContentValues getGenreContentValues(){
        ContentValues values = new ContentValues();
        values.put(MovieContract.GenreEntry.COLUMN_NAME, TEST_GENRE_NAME);
        return values;
    }

    private ContentValues getMovieContentValues(long genreID){
        ContentValues values = new ContentValues();
        values.put(MovieContract.MovieEntry.COLUMN_GENRE, genreID);
        values.put(MovieContract.MovieEntry.COLUMN_NAME, TEST_MOVIE_NAME);
        values.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, TEST_MOVIE_RELEASE_DATE);
        return values;
    }

    private void validateCursor(Cursor valueCursor, ContentValues expectedValues){
        assertTrue(valueCursor.moveToFirst());

        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();

        for(Map.Entry<String, Object> entry : valueSet){
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse(idx == -1);
            switch(valueCursor.getType(idx)){
                case Cursor.FIELD_TYPE_FLOAT:
                    assertEquals(entry.getValue(), valueCursor.getDouble(idx));
                    break;
                case Cursor.FIELD_TYPE_INTEGER:
                    assertEquals(Integer.parseInt(entry.getValue().toString()), valueCursor.getInt(idx));
                    break;
                case Cursor.FIELD_TYPE_STRING:
                    assertEquals(entry.getValue(), valueCursor.getString(idx));
                    break;
                default:
                    assertEquals(entry.getValue().toString(), valueCursor.getString(idx));
                    break;
            }
        }
        valueCursor.close();
    }
}
