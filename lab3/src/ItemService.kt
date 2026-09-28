class ItemService(private val itemRepository: ItemRepository) {

    fun selectRandomItems(count: Int): List<Item> {
        val totalQuestions = itemRepository.size()
        val takeCount = if (count > totalQuestions) totalQuestions else count

        return itemRepository.items.shuffled().take(takeCount)
    }
}