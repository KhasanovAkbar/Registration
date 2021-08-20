package pdp.uz.caremaandgallery.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import pdp.uz.caremaandgallery.models.User
import pdp.uz.caremaandgallery.utils.Constant

class MyDbHelper(context: Context) :
    SQLiteOpenHelper(context, Constant.DB_NAME, null, Constant.DB_VERSION),
    DbHelper {

    override fun onCreate(db: SQLiteDatabase?) {
        val query =
            "create table ${Constant.USER_TABLE} (${Constant.ID} integer primary key autoincrement not null, ${Constant.NAME} text not null, ${Constant.PHONE_NUMBER} text not null, ${Constant.COUNTRY} text not null, ${Constant.ADDRESS} text not null, ${Constant.PASSWORD} text not null, ${Constant.IMAGE} text)"
        db?.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

    override fun insertUser(user: User) {
        val database = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(Constant.NAME, user.name)
        contentValues.put(Constant.PHONE_NUMBER, user.phoneNumber)
        contentValues.put(Constant.COUNTRY, user.country)
        contentValues.put(Constant.ADDRESS, user.address)
        contentValues.put(Constant.PASSWORD, user.password)
        contentValues.put(Constant.IMAGE, user.image)
        database.insert(Constant.USER_TABLE, null, contentValues)
        database.close()
    }



    override fun getAllUsers(): ArrayList<User> {
        var arrayList = ArrayList<User>()
        val database = this.readableDatabase
        val query = "select * from ${Constant.USER_TABLE}"
        val cursor = database.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                val user = User()
                user.id = cursor.getInt(0)
                user.name = cursor.getString(1)
                user.phoneNumber = cursor.getString(2)
                user.country = cursor.getString(3)
                user.address = cursor.getString(4)
                user.password = cursor.getString(5)
                user.image = cursor.getString(6)
                arrayList.add(user)
            } while (cursor.moveToNext())
        }
        return arrayList
    }
}