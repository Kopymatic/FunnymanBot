package kotBot.cookieClicker

import java.math.BigDecimal
import java.math.BigInteger
import java.sql.ResultSet
import kotlin.math.pow

class UserBuilding {
    val building: Building
    val userID: String
    var amount: Int = 0

    constructor(building: Building, userId: String, rs: ResultSet) {
        this.building = building
        this.userID = userId
        amount = rs.getInt(building.dbColumnName)
    }

    fun getCost(): BigInteger {
        val toMulitplyBy = BigDecimal(ClickerReference.costMultiplier.pow(amount).toString())
        val toBeMuliplied = building.baseCost?.toBigDecimal()

        val toBeRounded = toBeMuliplied?.times(toMulitplyBy)
        return toBeRounded!!.toBigIntegerExact()
    }

    fun getMultiCost(amountToBuy: Int): BigInteger {
        var cost = BigInteger("0")
        var hypotheticalAmount = amount
        for (i in amountToBuy downTo 1) {
            cost += getCost()
            hypotheticalAmount++
        }
        return cost
    }

    fun getMaxBuy(cookies: BigInteger): Int {
        var toBuy = 1
        while (true) {
            if (getMultiCost(toBuy) > cookies) {
                return toBuy - 1
            } else {
                toBuy++
            }
        }
    }
}

open class Building(name: String, cost: BigInteger?, cps: Double, dbColumnName: String) {
    /**
     * The name of the Building
     */
    var name = ""
        protected set

    /**
     * The base cost of the Building
     */
    var baseCost: BigInteger? = null
        protected set

    /**
     * The base CPS of the Building
     */
    var cps = 0.0
        protected set

    var dbColumnName: String? = null

    init {
        this.name = name
        baseCost = cost
        this.cps = cps
        this.dbColumnName = dbColumnName
    }

    constructor(name: String, cost: String?, cps: Double, dbColumnName: String) : this(
        name,
        BigInteger(cost),
        cps,
        dbColumnName
    )

    constructor(name: String, cost: String?, cps: Int, dbColumnName: String) : this(
        name,
        BigInteger(cost),
        cps.toDouble(),
        dbColumnName
    )

    constructor(name: String, cost: String?, cps: Long, dbColumnName: String) : this(
        name,
        BigInteger(cost),
        cps.toDouble(),
        dbColumnName
    )
}