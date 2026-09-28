import java.net.URL
import java.util.TreeSet
import java.util.HashSet

interface IDictionary {
    fun add(word: String): Boolean
    fun find(word: String): Boolean
    fun size(): Int
}

class ListDictionary : IDictionary {
    private val words: MutableList<String>

    init {
        val url = URL("https://www.ms.sapientia.ro/~manyi/dict.txt")
        words = url.openStream().bufferedReader().readLines().toMutableList()
    }

    override fun add(word: String): Boolean {
        return words.add(word)
    }

    override fun find(word: String): Boolean {
        return words.contains(word)
    }

    override fun size(): Int {
        return words.size
    }
}

enum class DictionaryType {
    ARRAY_LIST, TREE_SET, HASH_SET
}

class TreeSetDictionary : IDictionary {
    private val words: TreeSet<String>

    init {
        val url = URL("https://www.ms.sapientia.ro/~manyi/dict.txt")
        words = TreeSet(url.openStream().bufferedReader().readLines())
    }

    override fun add(word: String): Boolean {
        return words.add(word)
    }

    override fun find(word: String): Boolean {
        return words.contains(word)
    }

    override fun size(): Int {
        return words.size
    }
}

class HashSetDictionary : IDictionary {
    private val words: HashSet<String>

    init {
        val url = URL("https://www.ms.sapientia.ro/~manyi/dict.txt")
        words = HashSet(url.openStream().bufferedReader().readLines())
    }

    override fun add(word: String): Boolean {
        return words.add(word)
    }

    override fun find(word: String): Boolean {
        return words.contains(word)
    }

    override fun size(): Int {
        return words.size
    }
}

object DictionaryProvider {
    private var listDictionary: ListDictionary? = null
    private var treeSetDictionary: TreeSetDictionary? = null
    private var hashSetDictionary: HashSetDictionary? = null

    fun createDictionary(type: DictionaryType): IDictionary {
        return when (type) {
            DictionaryType.ARRAY_LIST -> {
                if (listDictionary == null) {
                    listDictionary = ListDictionary()
                }
                listDictionary!!
            }
            DictionaryType.TREE_SET -> {
                if (treeSetDictionary == null) {
                    treeSetDictionary = TreeSetDictionary()
                }
                treeSetDictionary!!
            }
            DictionaryType.HASH_SET -> {
                if (hashSetDictionary == null) {
                    hashSetDictionary = HashSetDictionary()
                }
                hashSetDictionary!!
            }
        }
    }
}

fun main() {
    // Itt módosítható a típus a különböző implementációk teszteléséhez
    val providerType = DictionaryType.HASH_SET
    val dict: IDictionary = DictionaryProvider.createDictionary(providerType)

    println("Number of words: ${dict.size()}")

    var word: String?
    while (true) {
        print("What to find? ")
        word = readLine()
        if (word == "quit" || word == null) {
            break
        }
        println("Result: ${dict.find(word)}")
    }
}