fun main() {
    val a = 2
    val b = 3
    println("$a + $b = ${a + b}")

    val daysOfWeek = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

    println("--- A hét minden napja ---")
    for (day in daysOfWeek) {
        println(day)
    }

    println("\n--- 'T' betűvel kezdődő napok ---")
    println(daysOfWeek.filter { it.startsWith("T") })

    println("\n--- 'e' betűt tartalmazó napok ---")
    println(daysOfWeek.filter { it.contains("e") })

    println("\n--- 6 betűből álló napok ---")
    println(daysOfWeek.filter { it.length == 6 })

    val range = 1..50

    println("Prímszámok a $range tartományban (for ciklussal):")
    for (number in range) {
        if (isPrime(number)) {
            print("$number ")
        }
    }
    println()

    val primeList = range.filter { isPrime(it) }
    println("\nPrímszámok listaként: $primeList")

    val originalMessage = "Hello Kotlin!"

    println("Eredeti üzenet: $originalMessage")

    val encoded = messageCoding(originalMessage, ::encode)
    println("Kódolt üzenet:  $encoded")

    val decoded = messageCoding(encoded, ::decode)
    println("Dekódolt üzenet: $decoded")

    val numbers = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)

    print("\nPáros számok: ")
    printEvenNumbers(numbers)

    val numbers2 = listOf(1, 2, 3, 4, 5)
    val doubled = numbers2.map { it * 2 }
    println("Duplázott számok: $doubled")

    val capitalizedDays = daysOfWeek.map { it.uppercase() }
    println("Nagybetűs napok: $capitalizedDays")

    val firstChars = daysOfWeek.map { it.first().lowercaseChar() } // 'm' (vagy uppercaseChar() -> 'M')
    println("Első karakterek: $firstChars")

    val dayLengths = daysOfWeek.map { it.length }
    println("Napok hossza: $dayLengths")

    val averageLength = daysOfWeek.map { it.length }.average()
    println("Napok átlagos hossza: $averageLength")

    
}

fun isPrime(number: Int): Boolean {
    if (number < 2) return false
    for (i in 2..Math.sqrt(number.toDouble()).toInt()) {
        if (number % i == 0) return false
    }
    return true
}

fun encode(msg: String): String {
    return msg.map { (it.code + 1).toChar() }.joinToString("")
}

fun decode(msg: String): String {
    return msg.map { (it.code - 1).toChar() }.joinToString("")
}

fun messageCoding(msg: String, func: (String) -> String): String {
    return func(msg)
}

fun printEvenNumbers(numbers: List<Int>) = println(numbers.filter { it % 2 == 0 })