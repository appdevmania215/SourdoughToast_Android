package com.gmc.sourdoughtoast.sqlite;

import java.util.LinkedList;
import java.util.List;

import com.gmc.sourdoughtoast.sqlite.Vote;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class VoteSQLiteHelper extends SQLiteOpenHelper {
	
	// Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "VoteDB";
   
	public VoteSQLiteHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);	
		SQLiteDatabase db = this.getWritableDatabase();
		onCreate(db);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// SQL statement to create vote table
		String CREATE_VOTE_TABLE = "CREATE TABLE IF NOT EXISTS votes ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " + 
				"userid INTEGER, " +
				"feudid INTEGER, " +
				"type INTEGER, " +
				"report INTEGER )";
		
		System.out.println("CREATE TABLE");
		// create votes table
		db.execSQL(CREATE_VOTE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older votes table if existed
        db.execSQL("DROP TABLE IF EXISTS votes");
        System.out.println("DROP TABLE");
        // create fresh votes table
        this.onCreate(db);
	}
	//---------------------------------------------------------------------
   
	/**
     * CRUD operations (create "add", read "get", update, delete) vote + get all votes + delete all votes
     */
	
	// Votes table name
    private static final String TABLE_VOTES = "votes";
    
    // Votes Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_USERID = "userid";
    private static final String KEY_FEUDID = "feudid";
    private static final String KEY_TYPE = "type";
    private static final String KEY_REPORT = "report";
    
    private static final String[] COLUMNS = {KEY_ID,KEY_USERID,KEY_FEUDID, KEY_TYPE, KEY_REPORT};
    
	public void addVote(Vote vote){
		Vote temp = getVote(vote.getFeudId());
		if ( temp != null )
		{
			vote.setId(temp.getId());
			updateVote(vote);
		}
		else {
			Log.d("addVote", vote.toString());
			// 1. get reference to writable DB
			SQLiteDatabase db = this.getWritableDatabase();
		
			// 2. create ContentValues to add key "column"/value
	        ContentValues values = new ContentValues();
	        values.put(KEY_USERID, vote.getUserId()); // get userid 
	        values.put(KEY_FEUDID, vote.getFeudId()); // get feudid
	        values.put(KEY_TYPE, vote.getType()); // get type
	        values.put(KEY_REPORT, vote.getReport()); // get type
	 
	        // 3. insert
	        db.insert(TABLE_VOTES, // table
	        		null, //nullColumnHack
	        		values); // key/value -> keys = column names/ values = column values
	        
	        // 4. close
	        db.close();
		}
	}
	
	public Vote getVote(int feudid){

		// 1. get reference to readable DB
		SQLiteDatabase db = this.getReadableDatabase();
		//db.execSQL("DROP TABLE IF EXISTS votes");
		//onCreate(db);
		// 2. build query
        Cursor cursor = 
        		db.query(TABLE_VOTES, // a. table
        		COLUMNS, // b. column names
        		" feudid = ?", // c. selections 
                new String[] { String.valueOf(feudid) }, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit
        
        // 3. if we got results get the first one
        if (cursor == null || cursor.getCount() < 1 )
        	return null;
        else
        	cursor.moveToFirst();
 
        // 4. build vote object
        Vote vote = new Vote();
        vote.setId(Integer.parseInt(cursor.getString(0)));
        vote.setUserId(Integer.parseInt(cursor.getString(1)));
        vote.setFeudId(Integer.parseInt(cursor.getString(2)));
        vote.setType(Integer.parseInt(cursor.getString(3)));
        vote.setReport(Integer.parseInt(cursor.getString(4)));

		Log.d("getVote("+ feudid +")", vote.toString());

        // 5. return vote
        return vote;
	}
	
	// Get All Votes
    public List<Vote> getAllVotes() {
        List<Vote> votes = new LinkedList<Vote>();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE_VOTES;
        
    	// 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery(query, null);
 
        // 3. go over each row, build vote and add it to list
        Vote vote = null;
        if (cursor.moveToFirst()) {
            do {
            	vote = new Vote();
                vote.setId(Integer.parseInt(cursor.getString(0)));
                vote.setUserId(Integer.parseInt(cursor.getString(1)));
                vote.setFeudId(Integer.parseInt(cursor.getString(2)));
                vote.setType(Integer.parseInt(cursor.getString(3)));
                vote.setReport(Integer.parseInt(cursor.getString(4)));

                // Add vote to votes
                votes.add(vote);
            } while (cursor.moveToNext());
        }
        
		Log.d("getAllVotes()", votes.toString());

        // return votes
        return votes;
    }
	
	 // Updating single vote
    public int updateVote(Vote vote) {
    	Log.d("updateVote()", vote.toString());
    	// 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

		// 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put("userid", vote.getUserId()); // get userid 
        values.put("feudid", vote.getFeudId()); // get feudid
        values.put("type", vote.getType()); // get type
        values.put("report", vote.getReport()); // get type
 
        // 3. updating row
        int i = db.update(TABLE_VOTES, //table
        		values, // column/value
        		KEY_ID+" = ?", // selections
                new String[] { String.valueOf(vote.getId()) }); //selection args
        
        // 4. close
        db.close();
        
        return i;
        
    }

    // Deleting single vote
    public void deleteVote(Vote vote) {

    	// 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. delete
        db.delete(TABLE_VOTES,
        		KEY_ID+" = ?",
                new String[] { String.valueOf(vote.getId()) });
        
        // 3. close
        db.close();
        
		Log.d("deleteVote", vote.toString());

    }
}
