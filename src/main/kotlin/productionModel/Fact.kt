package productionModel

class Fact(val id: Int, val description: String) {
    override fun toString(): String {
        return description
    }
}