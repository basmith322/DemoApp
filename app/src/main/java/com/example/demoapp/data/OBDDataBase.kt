package com.example.demoapp.data

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteQueryBuilder
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper

class OBDDataBase(context: Context?, faultCodesFromOBD: Array<String>) : SQLiteAssetHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {
    private val faultCodesArray = faultCodesFromOBD
    private val i = 0
    val getDBCodes: Cursor
        get() {
            faultCodesArray.contentToString().replace('[', '(').replace(']', ')')

            val db = readableDatabase
            val qb = SQLiteQueryBuilder()
            val sqlSelect = arrayOf("0 _id", "desc")
            val sqlTables = "codes"
            qb.tables = sqlTables

            val c = qb.query(
                db, sqlSelect, "id= $faultCodesArray", null, null,
                null, null, null
            )
            c.moveToFirst()
            return c
        }

    companion object {
        private const val DATABASE_NAME = "obd-trouble-codes.db"
        private const val DATABASE_VERSION = 1
    }
}