class ItemRepository {
    val items = mutableListOf<Item>()

    init {
        save(Item("Melyik kulcsszóval hozunk létre megváltoztathatatlan (read-only) változót?", listOf("var", "val", "const", "let"), 1))
        save(Item("Melyik függvény egy Kotlin program belépési pontja?", listOf("start()", "run()", "main()", "execute()"), 2))
        save(Item("Melyik karaktert használjuk a null-safety (nullable) jelölésére?", listOf("?", "!", "*", "&"), 0))
        save(Item("Mi a Kotlin alapértelmezett láthatósági módosítója?", listOf("private", "protected", "internal", "public"), 3))
        save(Item("Hogyan hozol létre egy csak olvasható listát?", listOf("listOf()", "mutableListOf()", "arrayListOf()", "ArrayList()"), 0))
        save(Item("Melyik jelet használjuk String template (behelyettesítés) esetén?", listOf("%", "&", "*", "$"), 3))
        save(Item("Milyen jellel jelöljük az öröklődést Kotlinban?", listOf("extends", "implements", ":", "inherits"), 2))
        save(Item("Mi a Kotlin forráskódú fájlok kiterjesztése?", listOf(".java", ".kt", ".kot", ".kotlin"), 1))
        save(Item("Mire való az 'is' operátor?", listOf("Értékadásra", "Típusellenőrzésre (type check)", "Összehasonlításra", "Ciklusokhoz"), 1))
        save(Item("Melyik kulcsszóval definiálunk függvényt Kotlinban?", listOf("def", "function", "fun", "func"), 2))
    }

    fun save(item: Item) {
        items.add(item)
    }

    fun randomItem(): Item {
        return items.random()
    }

    fun size(): Int {
        return items.size
    }
}