package com.example.edmorrowcs360finalsubmissioninventoryapp.data;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.DatabaseUtils;
import android.database.Cursor;
import android.content.ContentValues;
import android.content.Context;

/*
 * The database manager
 */
public class DatabaseManager extends SQLiteOpenHelper {

    private static DatabaseManager instance;
    private static final String DATABASE_NAME = "data.db";
    private static final int VERSION = 1;

    private static final class LoginDetailsTable {
        private static final String TABLE = "loginDetails";
        private static final String COL_ID = "_id";
        private static final String COL_USERNAME = "username";
        private static final String COL_PASSWORD = "password";
        private static final String COL_ACTIVE = "active";
        private static final String COL_NOTIFICATIONS = "notifications";
    }

    private static final class InventoryDetailsTable {
        private static final String TABLE = "inventoryDetails";
        private static final String COL_ID = "_id";
        private static final String COL_DESCRIPTION = "description";
        private static final String COL_QUANTITY = "quantity";
    }

    /*
     * Method returns singleton instance of DatabaseManager
     * @param context - the context
     * @return - @DatabaseManger singleton instance
     */
    public static DatabaseManager getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseManager(context);
        }
        return instance;
    }

    private DatabaseManager(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + LoginDetailsTable.TABLE + " (" +
                LoginDetailsTable.COL_ID + " integer primary key autoincrement, " +
                LoginDetailsTable.COL_USERNAME + " text, " +
                LoginDetailsTable.COL_PASSWORD + " text," +
                LoginDetailsTable.COL_ACTIVE + " integer," +
                LoginDetailsTable.COL_NOTIFICATIONS + " integer)");

        db.execSQL("CREATE TABLE " + InventoryDetailsTable.TABLE + " (" +
                InventoryDetailsTable.COL_ID + " integer primary key autoincrement, " +
                InventoryDetailsTable.COL_DESCRIPTION + " text, " +
                InventoryDetailsTable.COL_QUANTITY + " integer)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + LoginDetailsTable.TABLE);
        db.execSQL("drop table if exists " + InventoryDetailsTable.TABLE);
        onCreate(db);
    }

    /*
     * Method authenticates user credentials
     * @param username - the username
     * @param password - the password
     * @return - true is authenticated, false is not
     */
    public boolean authenticateLogin(String username, String password) {
        boolean isAuthenticated = false;

        SQLiteDatabase db = getReadableDatabase();

        String sql = "Select * from " + LoginDetailsTable.TABLE +
                " WHERE " + LoginDetailsTable.COL_USERNAME + " = ? AND " +
                LoginDetailsTable.COL_PASSWORD + " = ? ";

        Cursor cursor = db.rawQuery(sql, new String[]{username, password});
        if (cursor.moveToFirst()) {
            isAuthenticated = true;
        }
        cursor.close();

        ContentValues values = new ContentValues();
        // first set all values to zero
        values.put(LoginDetailsTable.COL_ACTIVE, 0);
        db.update(LoginDetailsTable.TABLE, values, null, null);
        // then set the value corresponding to the active user to 1
        values.put(LoginDetailsTable.COL_ACTIVE, 1);
        db.update(LoginDetailsTable.TABLE, values,  LoginDetailsTable.COL_USERNAME + "= ?",
                new String[] {username});

        return isAuthenticated;
    }

    /*
     * method verifies username exists in database
     * @param username - the username
     * @return - true is verified, false is not
     */
    public boolean verifyUsername(String username) {
        boolean isVerified = false;

        SQLiteDatabase db = getReadableDatabase();

        String sql = "Select * from " + LoginDetailsTable.TABLE +
                " WHERE " + LoginDetailsTable.COL_USERNAME + " = ?";

        Cursor cursor = db.rawQuery(sql, new String[]{username});
        if (cursor.moveToFirst()) {
            isVerified = true;
        }
        cursor.close();

        return isVerified;
    }

    /*
     * method verifies an item exists in the database
     * @param description - the item description
     * @return - true indicates item exists, false that it does not
     */
    public boolean itemExists(String description) {
        boolean exists = false;

        SQLiteDatabase db = getReadableDatabase();

        String sql = "Select * from " + InventoryDetailsTable.TABLE +
                " WHERE " + InventoryDetailsTable.COL_DESCRIPTION + " = ?";

        Cursor cursor = db.rawQuery(sql, new String[]{description});
        if (cursor.moveToFirst()) {
            exists = true;
        }
        cursor.close();

        return exists;
    }

    /*
     * method returns quantity of item
     * @param id - the item's id
     * @return quantity - the item's quantity
     */
    public String getQuantityById(String id) {

        // return zero if id not found
        int quantity = 0;

        SQLiteDatabase db = getReadableDatabase();

        String sql = "Select " + InventoryDetailsTable.COL_QUANTITY + " from " + InventoryDetailsTable.TABLE +
                " WHERE " + InventoryDetailsTable.COL_ID + " = ?";

        Cursor cursor = db.rawQuery(sql, new String[]{id});

        if (cursor.getCount() > 0)
        {
            cursor.moveToFirst();
            quantity = cursor.getInt(cursor.getColumnIndexOrThrow(InventoryDetailsTable.COL_QUANTITY));
        }
        cursor.close();

        return String.valueOf(quantity);
    }

    /*
     * method returns description of item
     * @param id - the item's id
     * @return - description of the item
     */
    public String getDescriptionById(String id) {

        // return empty string if id not found
        String description = "";

        SQLiteDatabase db = getReadableDatabase();

        String sql = "Select " + InventoryDetailsTable.COL_DESCRIPTION + " from " + InventoryDetailsTable.TABLE +
                " WHERE " + InventoryDetailsTable.COL_ID + " = ?";

        Cursor cursor = db.rawQuery(sql, new String[]{id});

        if (cursor.getCount() > 0)
        {
            cursor.moveToFirst();
            description = cursor.getString(cursor.getColumnIndexOrThrow(InventoryDetailsTable.COL_DESCRIPTION));
        }
        cursor.close();

        return description;
    }

    /*
     * method returns total number of items in database
     * @return - the number of items
     */
    public long countEntries() {

        SQLiteDatabase db = getReadableDatabase();
        return DatabaseUtils.queryNumEntries(db, InventoryDetailsTable.TABLE, InventoryDetailsTable.COL_QUANTITY + " > ?"
                , new String[] {String.valueOf(0)});
    }

    /*
     * method returns highest id key in database
     * @return - the highest id number
     */
    public int getHighestId() {

        SQLiteDatabase db = getReadableDatabase();
        return (int) DatabaseUtils.longForQuery(db, "SELECT MAX(" + InventoryDetailsTable.COL_ID + ") FROM " + InventoryDetailsTable.TABLE, null);
    }

    /*
     * method adds a new user to the database
     * @param username - the new user's username
     * @param password - the user's password
     */
    public void addUser(String username, String password) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(LoginDetailsTable.COL_USERNAME, username);
        values.put(LoginDetailsTable.COL_PASSWORD, password);
        values.put(LoginDetailsTable.COL_ACTIVE, 0);
        values.put(LoginDetailsTable.COL_NOTIFICATIONS, 0);

        db.insert(LoginDetailsTable.TABLE, null, values);
    }

    /*
     * method adds a new item to the inventory
     * @param description - the item description
     * @param quantity - the quantity of the item
     */
    public void addInventoryItem(String description, String quantity) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(InventoryDetailsTable.COL_DESCRIPTION, description);
        values.put(InventoryDetailsTable.COL_QUANTITY, quantity);

        db.insert(InventoryDetailsTable.TABLE, null, values);
    }

    /*
     * method updates the quantity of an item already in the database
     * @param description - the item description
     * @param - the (new) quantity of the item
     */
    public void updateInventoryItem(String description, String quantity) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(InventoryDetailsTable.COL_QUANTITY, quantity);

        db.update(InventoryDetailsTable.TABLE, values, InventoryDetailsTable.COL_DESCRIPTION + "= ?",
                new String[] {description});
    }

    /*
     * method removes an item from the database
     * @param od - the item's id number
     */
    public void deleteInventoryItem(long id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(InventoryDetailsTable.TABLE, InventoryDetailsTable.COL_ID + " =?",
                new String[] { Long.toString(id) });
    }

    /*
     * returns status of notifications permission
     * @return - ture indicates notifications enabled, false disabled
     */
    public boolean getNotificationStatus() {

        // assume false until verified true
        boolean enabled = false;

        SQLiteDatabase db = getReadableDatabase();

        String sql = "Select " + LoginDetailsTable.COL_NOTIFICATIONS + " from " + LoginDetailsTable.TABLE +
                " WHERE " + LoginDetailsTable.COL_ACTIVE + " = ?";

        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(1)});

        if (cursor.getCount() > 0)
        {
            cursor.moveToFirst();
            enabled = cursor.getInt(cursor.getColumnIndexOrThrow(LoginDetailsTable.COL_NOTIFICATIONS)) == 1;
        }
        cursor.close();

        return enabled;
    }

    /*
     * method sets users notifications permission in the database
     * @param enabled - true indicates notifications enabled, false disabled
     */
    public void setNotificationStatus(boolean enabled) {

        int isEnabled = 0;
        if (enabled) {
            isEnabled = 1;
        }

        SQLiteDatabase db = getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(LoginDetailsTable.COL_NOTIFICATIONS, isEnabled);
        db.update(LoginDetailsTable.TABLE, values,  LoginDetailsTable.COL_ACTIVE + "= ?",
                new String[] {String.valueOf(1)});
    }
}