package de.codewing.sqlite;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteHelper extends SQLiteOpenHelper {

	public static final String TABLE_IBASH_FAVOURITES = "favourites";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_DATE_ADDED = "date_added";

	private static final String DATABASE_NAME = "ibash_favi.db";
	private static final int DATABASE_VERSION = 1;

	public SQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		// Database creation sql statement
		String DATABASE_CREATE = "create table "
				+ TABLE_IBASH_FAVOURITES + "(" + COLUMN_ID
				+ " integer primary key, " + COLUMN_DATE_ADDED
				+ " date not null);";
		database.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(SQLiteHelper.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_IBASH_FAVOURITES);
		onCreate(db);
	}

	// CRUD
	// Adding new Quote
	public void addFaviQuote(int id) {
		SQLiteDatabase db = this.getWritableDatabase();
		 
	    ContentValues values = new ContentValues();
	    // Favi ID
	    values.put(COLUMN_ID, id); 
	    
	    // Date added
	    // set the format to sql date time
	    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
	    Date date = new Date();
	    values.put(COLUMN_DATE_ADDED, dateFormat.format(date));			
	 
	    // Inserting Row
	    db.insert(TABLE_IBASH_FAVOURITES, null, values);
	    db.close(); // Closing database connection
	}
	 
	// Getting single Quote
	public FaviQuote getSingleQuote(int id) {
		SQLiteDatabase db = this.getReadableDatabase();
		 
		try{
	    Cursor cursor = db.query(TABLE_IBASH_FAVOURITES, new String[] { COLUMN_ID, COLUMN_DATE_ADDED}, COLUMN_ID + "=?",
	            new String[] { String.valueOf(id) }, null, null, null, null);
	    if (cursor != null)
	        cursor.moveToFirst();
	    else
	    	return null;
	    
	    
	    FaviQuote fquote = new FaviQuote();
	    fquote.setId(Integer.parseInt(cursor.getString(0)));
	    fquote.setDate(cursor.getString(1));
	    
	    // return Quote
	    return fquote;
		}catch(Exception e){
			return null;
		}
	 
	    
	}
	 
	// Getting All Quotes
	public List<FaviQuote> getFaviQuotes(int number, int page) {
		List<FaviQuote> quoteList = new ArrayList<FaviQuote>();
		int pos = number * page +1;
	    // Select All Query
	    String selectQuery = "SELECT  * FROM " + TABLE_IBASH_FAVOURITES + " WHERE ROWID >= "+pos+" LIMIT " + number;
	 
	    SQLiteDatabase db = this.getWritableDatabase();
	    Cursor cursor = db.rawQuery(selectQuery, null);
	 
	    // looping through all rows and adding to list
	    if (cursor.moveToFirst()) {
	        do {
	        	FaviQuote fquote = new FaviQuote();
	        	fquote.setId(Integer.parseInt(cursor.getString(0)));
	        	fquote.setDate(cursor.getString(1));
	            // Adding contact to list
	        	quoteList.add(fquote);
	        } while (cursor.moveToNext());
	    }
	 
	    // return contact list
	    return quoteList;
	}
	 
	// Getting Favi Count
	public int getFaviCount() {
		String countQuery = "SELECT  * FROM " + TABLE_IBASH_FAVOURITES;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();
 
        // return count
        return cursor.getCount();
	
	}
	
	 
	// Deleting single quote
	public void removeFaviQuote(int id) {
		SQLiteDatabase db = this.getWritableDatabase();
	    db.delete(TABLE_IBASH_FAVOURITES, COLUMN_ID + " = ?",
	            new String[] { String.valueOf(id) });
	    db.close();
	}

}