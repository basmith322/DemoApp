package com.example.demoapp.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteQueryBuilder
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper

class OBDDataBase(context: Context?, codeFromOBD: Array<String>) : SQLiteAssetHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {
    private val codeFromFault= codeFromOBD
    private val i = 0
    val getDBCodes: Cursor
        get() {
            codeFromFault.contentToString().replace('[', '(').replace(']',')')
            val db = readableDatabase
            val qb = SQLiteQueryBuilder()
            val sqlSelect = arrayOf("0 _id", "desc")
            val sqlTables = "codes"
            qb.tables = sqlTables
            val c = qb.query(
                db, sqlSelect,  "id=?", codeFromFault, null,
                null, null, null
            )
            c.moveToFirst()
            while (!c.isAfterLast){
                codeFromFault[i]
                i.inc()
                c.moveToNext()
            }
            return c
        }

    companion object {
        private const val DATABASE_NAME = "obd-trouble-codes.db"
        private const val DATABASE_VERSION = 1
    }
}