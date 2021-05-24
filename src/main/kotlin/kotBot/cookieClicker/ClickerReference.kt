package kotBot.cookieClicker


object ClickerReference {
    const val costMultiplier = 1.15
    const val cpsMultiplier = 1.0
    const val dbTableName = "ClickerUsers"

    // cursor
    val cursor = Building("Cursor", "15", 0.1, "cursors")

    // grandma
    val grandma = Building("Grandma", "100", 1, "grandmas")

    // farm
    val farm: Building = Building("Farm", "1100", 8, "farms")

    // mine
    val mine: Building = Building("Mine", "12000", 47, "mines")

    // factory
    val factory: Building = Building("Factory", "130000", 260, "factories")

    // bank
    val bank: Building = Building("Bank", "1400000", 1400, "banks")

    // TEMPLE
    val temple: Building = Building("Temple", "20000000", 7800, "temples")

    // WIZARD_TOWER
    val wizardTower: Building = Building("Wizard Tower", "330000000", 44000, "wizardTowers")

    // SHIPMENT
    val shipment: Building = Building("Shipment", "5100000000", 260000, "shipments")

    // ALCHEMY_LAB
    val alchemyLab: Building = Building("Alchemy Lab", "75000000000", 1600000, "alchemyLabs")

    // PORTAL
    val portal: Building = Building("Portal", "1000000000000", 10000000, "portals")

    // TIME_MACHINE
    val timeMachine: Building = Building("Time Machine", "14000000000000", 65000000, "timeMachines")

    // ANTIMATTER_CONDENSER
    val antimatterCondenser: Building =
        Building("Antimatter Condenser", "170000000000000", 430000000, "antimatterCondensers")

    // PRISM
    val prism: Building = Building("Prism", "2100000000000000", 2900000000L, "prisms")

    // CHANCEMAKER
    val chancemaker: Building = Building("Chancemaker", "26000000000000000", 21000000000L, "chancemakers")

    // FRACTAL_ENGINE
    val fractalEngine: Building = Building("Fractal Engine", "310000000000000000", 150000000000L, "fractalEngines")

    // JAVASCRIPT_CONSOLE
    val javascriptConsole: Building =
        Building("Javascript Console", "71000000000000000000", 1100000000000L, "javascriptConsoles")

    // Idleverse
    val idleverse: Building = Building("Idleverse", "12000000000000000000000", 8300000000000L, "idleverses")

    val buildings = arrayOf(
        cursor, grandma, farm, mine, factory, bank, temple, wizardTower, shipment, alchemyLab, portal, timeMachine,
        antimatterCondenser, prism, chancemaker, fractalEngine, javascriptConsole, idleverse
    )
}