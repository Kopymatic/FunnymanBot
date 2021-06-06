package kotBot.cookieClicker

import kotBot.utils.Reference
import java.math.BigInteger

class ClickerProfile
    (val userID: String) {
    private val cursor: UserBuilding
    private val grandma: UserBuilding
    private val farm: UserBuilding
    private val mine: UserBuilding
    private val factory: UserBuilding
    private val bank: UserBuilding
    private val temple: UserBuilding
    private val wizardTower: UserBuilding
    private val shipment: UserBuilding
    private val alchemyLab: UserBuilding
    private val portal: UserBuilding
    private val timeMachine: UserBuilding
    private val antimatterCondenser: UserBuilding
    private val prism: UserBuilding
    private val chancemaker: UserBuilding
    private val fractalEngine: UserBuilding
    private val javascriptConsole: UserBuilding
    private val idleverse: UserBuilding
    var cookies: BigInteger = BigInteger("0")
    var lastKnownUserName: String?

    init {
        val ps = Reference.connection.prepareStatement(
            """
            SELECT * FROM ClickerUsers WHERE userID = ?
        """.trimIndent()
        )
        ps.setString(1, userID)
        var rs = ps.executeQuery()
        if (!rs.next()) {
            newProfile(userID)
            rs = ps.executeQuery()
            if (!rs.next()) {
                throw Exception("Couldn't create profile!") //If we get here, we're fucked
            }
        }
        cursor = UserBuilding(ClickerReference.cursor, userID, rs)
        grandma = UserBuilding(ClickerReference.grandma, userID, rs)
        farm = UserBuilding(ClickerReference.farm, userID, rs)
        mine = UserBuilding(ClickerReference.mine, userID, rs)
        factory = UserBuilding(ClickerReference.factory, userID, rs)
        bank = UserBuilding(ClickerReference.bank, userID, rs)
        temple = UserBuilding(ClickerReference.temple, userID, rs)
        wizardTower = UserBuilding(ClickerReference.wizardTower, userID, rs)
        shipment = UserBuilding(ClickerReference.shipment, userID, rs)
        alchemyLab = UserBuilding(ClickerReference.alchemyLab, userID, rs)
        portal = UserBuilding(ClickerReference.portal, userID, rs)
        timeMachine = UserBuilding(ClickerReference.timeMachine, userID, rs)
        antimatterCondenser = UserBuilding(ClickerReference.antimatterCondenser, userID, rs)
        prism = UserBuilding(ClickerReference.prism, userID, rs)
        chancemaker = UserBuilding(ClickerReference.chancemaker, userID, rs)
        fractalEngine = UserBuilding(ClickerReference.fractalEngine, userID, rs)
        javascriptConsole = UserBuilding(ClickerReference.javascriptConsole, userID, rs)
        idleverse = UserBuilding(ClickerReference.idleverse, userID, rs)
        cookies = BigInteger(rs.getString("cookies"))
        lastKnownUserName = rs.getString("lastKnownUsername")
    }

    /**
     * Makes a new database profile, returns a boolean for success or failure
     */
    private fun newProfile(userID: String): Boolean {
        val ps = Reference.connection.prepareStatement(
            """
            INSERT INTO ClickerUsers 
            VALUES (userID = ?, lastKnownUsername = ?);
        """.trimIndent()
        )
        ps.setString(1, userID)
        //Usernames can be customized, use PreparedStatement to prevent sql injection
        ps.setString(2, (Reference.jda.getUserById(userID)?.asTag))
        val affected = ps.executeUpdate()
        if (affected == 1) {
            return true
        } else {
            throw Exception("Affected rows should be 1, not $affected")
        }
    }

    fun push() {
        val ps = Reference.connection.prepareStatement(
            """
            UPDATE ClickerUsers
            SET lastKnownUsername = ?,
            cookies = ?,
            cursors = ?,
            grandmas = ?,
            farms = ?,
            mines = ?,
            factories = ?,
            banks = ?,
            temples = ?,
            wizardTowers = ?,
            shipments = ?,
            alchemyLabs = ?,
            portals = ?,
            timeMachines = ?,
            antimatterCondensers = ?,
            prisms = ?,
            chancemakers = ?,
            fractalEngines = ?,
            javascriptConsoles = ?,
            idleverses = ?
            WHERE userID = ?
        """.trimIndent()
        )
        ps.setString(1, lastKnownUserName)
        ps.setString(2, cookies.toString())
        ps.setInt(3, cursor.amount)
        ps.setInt(4, grandma.amount)
        ps.setInt(5, farm.amount)
        ps.setInt(6, mine.amount)
        ps.setInt(7, factory.amount)
        ps.setInt(8, bank.amount)
        ps.setInt(9, temple.amount)
        ps.setInt(10, wizardTower.amount)
        ps.setInt(11, shipment.amount)
        ps.setInt(12, alchemyLab.amount)
        ps.setInt(13, portal.amount)
        ps.setInt(14, timeMachine.amount)
        ps.setInt(15, antimatterCondenser.amount)
        ps.setInt(16, prism.amount)
        ps.setInt(17, chancemaker.amount)
        ps.setInt(18, fractalEngine.amount)
        ps.setInt(19, javascriptConsole.amount)
        ps.setInt(20, idleverse.amount)
        ps.setString(21, userID)
        ps.executeUpdate()

    }

    fun getRefreshed(): ClickerProfile {
        return ClickerProfile(userID)
    }
}