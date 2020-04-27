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
            //Set up the array of fault codes returned from OBD. Replace "[" with "(" for SQL Query
            faultCodesArray.contentToString().replace('[', '(').replace(']', ')')

            val codeList = ArrayList<String>() //Set up an array to hold the descriptor results
            val db = readableDatabase
            val qb = SQLiteQueryBuilder()
            val sqlSelect = arrayOf("desc") //An array of descriptions from the DB
            val sqlTables = "codes" //Select the codes table from the DB

            //Where statement that will look for a match between the id in the DB and the values in the fault code array
            val sqlWhere = "id IN (" +
                    TextUtils.join(",", Collections.nCopies(faultCodesArray.size, "?")) +
                    ")"
            qb.tables = sqlTables

            //Set up a cursor using the query builder. Set the DB, The Table, the where clause and the fault code array to compare against the where clause
            val c = qb.query(
                db, sqlSelect, sqlWhere, faultCodesArray, null,
                null, null, null
            )
            //Keep going until all fault codes have been found
            while (c.moveToNext()) {
                codeList.add(c.getString(0))
            }
            c.close()
            return codeList //Return the code list to be displayed
        }

    companion object {
        private const val DATABASE_NAME = "obd-trouble-codes.db"
        private const val DATABASE_VERSION = 1
    }
}