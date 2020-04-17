package com.example.demoapp.data

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteQueryBuilder
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper

class OBDDataBase(context: Context?, codeFromOBD: String) : SQLiteAssetHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {
    private val codeFromFault = codeFromOBD
    val getDBCodes: Cursor
        get() {
            val db = writableDatabase
            val qb = SQLiteQueryBuilder()
            val sqlSelect = arrayOf("0 _id", "desc")
            val sqlTables = "codes"
            val sqlWhere = "id"
            val sqlArgs = "= $codeFromFault"
            qb.tables = sqlTables
            val c = qb.query(
                db, sqlSelect,  sqlWhere, null,
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