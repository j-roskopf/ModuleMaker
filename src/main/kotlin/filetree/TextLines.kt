package filetree

interface TextLines {
    val size: Int
    fun get(index: Int): String
}