import java.util.Scanner

class ItemController(private val itemService: ItemService) {

    fun quiz(numQuestions: Int) {
        val questions = itemService.selectRandomItems(numQuestions)
        var correctAnswers = 0
        val scanner = Scanner(System.`in`)

        println("=== KOTLIN KVÍZ INDUL ===")
        println("Kérlek, a helyes válasz sorszámát (1-${questions[0].answers.size}) írd be!\n")

        for ((index, item) in questions.withIndex()) {
            println("Kérdés ${index + 1}/${questions.size}:${item.question}")

            item.answers.forEachIndexed { i, answer ->
                println("  ${i + 1}.$answer")
            }

            print("A te válaszod: ")

            var userAnswer = -1
            if (scanner.hasNextInt()) {
                userAnswer = scanner.nextInt()
            } else {
                scanner.next()
            }

            val userIndex = userAnswer - 1

            if (userIndex == item.correct) {
                println("Helyes!\n")
                correctAnswers++
            } else {
                val correctAnswerText = item.answers[item.correct]
                println("Helytelen! A helyes válasz: ${item.correct + 1}.$correctAnswerText\n")
            }
        }

        println("=== Eredmény ===")
        println("$correctAnswers/${questions.size}")
    }
}