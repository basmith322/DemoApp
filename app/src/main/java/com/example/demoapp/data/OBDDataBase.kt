package com.example.demoapp.data

import android.content.Context
import android.database.sqlite.SQLiteQueryBuilder
import android.text.TextUtils
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper
import java.util.*
import kotlin.collections.ArrayList

class OBDDataBase(context: Context?, faultCodesFromOBD: Array<String>) : SQLiteAssetHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {
    private val faultCodesArray = faultCodesFromOBD
    val getDBCodes: MutableList<String>
        get() {
            faultCodesArray.contentToString().replace('[', '(').replace(']', ')')

            val codeList = ArrayList<String>()
            val db = readableDatabase
            val qb = SQLiteQueryBuilder()
            val sqlSelect = arrayOf("desc")
            val sqlTables = "codes"
            val sqlWhere = "id IN (" +
                    TextUtils.join(",", Collections.nCopies(faultCodesArray.size - 1, "?")) +
                    ")"
            qb.tables = sqlTables

            val c = qb.query(
                db, sqlSelect, sqlWhere, faultCodesArray, null,
                null, null, null
            )
            while (c.moveToNext()) {
                codeList.add(c.getString(0))
            }
            c.close()
            return codeList
        }

    companion object {
        private const val DATABASE_NAME = "obd-trouble-codes.db"
        private const val DATABASE_VERSION = 1
    }
}