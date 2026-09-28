fun main() {
    val repository = ItemRepository()
    val service = ItemService(repository)
    val controller = ItemController(service)

    controller.quiz(5)
}