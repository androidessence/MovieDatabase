package androidessence.moviedatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Creates the Movie database used for this application.
 *
 * Created by adammcneilly on 9/19/15.
 */
public class MovieDBHelper extends SQLiteOpenHelper{
    /**
     * Defines the database version. This variable must be incremented in order for onUpdate to
     * be called when necessary.
     */
    private static final int DATABASE_VERSION = 1;
    /**
     * The name of the database on the device.
     */
    private static final String DATABASE_NAME = "movieList.db";

    /**
     * Default constructor.
     * @param context The application context using this database.
     */
    public MovieDBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Called when the database is first created.
     * @param db The database being created, which all SQL statements will be executed on.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        addGenreTable(db);
        addMovieTable(db);
    }

    /**
     * Called whenever DATABASE_VERSION is incremented. This is used whenever schema changes need
     * to be made or new tables are added.
     * @param db The database being updated.
     * @param oldVersion The previous version of the database. Used to determine whether or not
     *                   certain updates should be run.
     * @param newVersion The new version of the database.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * Inserts the genre table into the database.
     * @param db The SQLiteDatabase the table is being inserted into.
     */
    private void addGenreTable(SQLiteDatabase db){
        db.execSQL(
                "CREATE TABLE " + MovieContract.GenreEntry.TABLE_NAME + " (" +
                        MovieContract.GenreEntry._ID + " INTEGER PRIMARY KEY, " +
                        MovieContract.GenreEntry.COLUMN_NAME + " TEXT UNIQUE NOT NULL);"
        );
    }

    /**
     * Inserts the movie table into the database.
     * @param db The SQLiteDatabase the table is being inserted into.
     */
    private void addMovieTable(SQLiteDatabase db){
        db.execSQL(
                "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME + " (" +
                        MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY, " +
                        MovieContract.MovieEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                        MovieContract.MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                        MovieContract.MovieEntry.COLUMN_GENRE + " INTEGER NOT NULL, " +
                        "FOREIGN KEY (" + MovieContract.MovieEntry.COLUMN_GENRE + ") " +
                        "REFERENCES " + MovieContract.GenreEntry.TABLE_NAME + " (" + MovieContract.GenreEntry._ID + "));"
        );
    }
}
