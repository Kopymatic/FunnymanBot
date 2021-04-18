package kotBot.utils

import java.sql.*
import kotlin.system.exitProcess

class KopyDB {
    private val url = Config().url
    private val user = Config().userName
    private val password = Config().password
    var connection: Connection

    init {
        connection = connect()
    }
    /**
     * Connect to the PostgreSQL database
     *
     * @return a Connection object
     */
    private fun connect(): Connection {
        try {
            val conn = DriverManager.getConnection(url, user, password)
            println("Connected to the PostgreSQL server successfully.")
            return conn
        } catch (e: SQLException) {
            println(e.message)
            println("The database failed to start, exiting process")
            exitProcess(0)
        }
    }

    /**
     * This will blindly trust whatever you put into it please don't break things
     */
    fun updateSQL(sql: String) {
        val stmt: Statement
        try {
            stmt = connection.createStatement()
            stmt.executeUpdate(sql)
        } catch ( e: Exception ) {
            e.printStackTrace()
            exitProcess(0)
        }
    }

    /**
     * This will blindly trust whatever you put into it please don't break things
     */
    fun querySQL(sql: String): ResultSet {
        val stmt: Statement
        try {
            stmt = connection.createStatement()
            return stmt.executeQuery(sql)
        } catch (e: Exception) {
            e.printStackTrace()
            exitProcess(0)
        }
    }

    /**
     * This will blindly trust whatever you put into it please don't break things
     */
    fun createTable(sql: String): Boolean {
        val stmt: Statement
        return try {
            stmt = connection.createStatement()
            stmt.executeUpdate(sql)
            true
        } catch ( e: Exception ) {
            true
        }
    }
}