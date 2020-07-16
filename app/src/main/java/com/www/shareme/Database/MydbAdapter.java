package com.www.shareme.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class MydbAdapter {
    myDbHelper myDbHelper;

    public MydbAdapter(Context context) {
      myDbHelper = new myDbHelper(context);
    }

    public long insertData(String email, String phone, String pass){
        SQLiteDatabase db=myDbHelper.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(myDbHelper.Email,email);
        contentValues.put(myDbHelper.Phone,phone);
        contentValues.put(myDbHelper.Password,pass);
        long id= db.insert(myDbHelper.TableName,null,contentValues);
        return id;
    }

public int delete(String uemail){
        SQLiteDatabase db= myDbHelper.getWritableDatabase();
        String[] whereArgs ={uemail};
        int count= db.delete(myDbHelper.TableName,myDbHelper.Email+" = ?",whereArgs);
        return count;
}
public int updateEmail(String oldEmail,String newEmail){
        SQLiteDatabase db= myDbHelper.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(myDbHelper.Email,newEmail);
        String[] whereArgs= {oldEmail};
        int count =db.update(myDbHelper.TableName,contentValues,myDbHelper.Email+" = ?",whereArgs);
        return count;
}

public String getData(){
        SQLiteDatabase db= myDbHelper.getWritableDatabase();
        String[] columns= {myDbHelper.UID,myDbHelper.Email,myDbHelper.Password};
    Cursor cursor= db.query(myDbHelper.TableName,columns,null,null,null,null,null);
    StringBuffer buffer=new StringBuffer();
    while (cursor.moveToNext()){
        int cid=cursor.getInt(cursor.getColumnIndex(myDbHelper.UID));
        String email= cursor.getString(cursor.getColumnIndex(myDbHelper.Email));
        String password= cursor.getString(cursor.getColumnIndex(myDbHelper.Password));
        buffer.append(cid+" "+email+" "+password+" \n");
    }

    return buffer.toString();
}

public String search(Context context, String s_email,String s_pass){

        SQLiteDatabase db= myDbHelper.getWritableDatabase();
    String sql = "SELECT * FROM " + myDbHelper.TableName + " WHERE " +" ("+ myDbHelper.Email + " LIKE '%" + s_email + "%'"+ " )"+
            " AND"+" ("+ myDbHelper.Password+" LIKE '%" + s_pass + "%'"+ " )";
    StringBuffer buffer=new StringBuffer();

    Cursor cursor= db.rawQuery(sql,null);

    if(cursor.getCount() >0) {
        // means search has returned data

        if (cursor.moveToFirst()) {
            do {
                String email = cursor.getString(cursor.getColumnIndex(myDbHelper.Email));
                String password = cursor.getString(cursor.getColumnIndex(myDbHelper.Password));
                //display your search result in any manner you want
                buffer.append(email.trim()+" "+password.trim());

            }

            while (cursor.moveToNext());
        }
    }
    else{
        Toast.makeText(context, "No data was found in the system!", Toast.LENGTH_LONG).show();

    }

//    cursor.close();
    return buffer.toString();

    }


    static class myDbHelper extends SQLiteOpenHelper {

        private static final String DatabaseName = "shareMeDatabase";
        private static final String TableName = "mytable";
        private static final int Database_version = 1;
        private static final String UID = "Id";
        private static final String Email = "Email";
        private static final String Phone="Phone";
        private static final String Password = "Password";
        private static final String Create_table = "CREATE TABLE " + TableName + " (" + UID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                + Email + " VARCHAR(255), "+Phone+ " INTEGER(18) NOT NULL, " + Password + " VARCHAR(255));";
        private static final String Drop_Table = "DROP TABLE IF EXISTS " + TableName;
        private Context context;


        public myDbHelper(@Nullable Context context) {
            super(context, DatabaseName, null, Database_version);
            this.context = context;
        }

        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(Create_table);
            } catch (Exception e) {
                Toast.makeText(context, "Database is not created", Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                Toast.makeText(context, "OnUpgrade", Toast.LENGTH_SHORT).show();
                db.execSQL(Drop_Table);
                onCreate(db);
            } catch (Exception e) {
                Toast.makeText(context, "Upgrade failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
