package productionModel

fun main() {
    val model = Model("src/main/resources/facts.txt", "src/main/resources/rulesBase.txt")
    val forward = model.forward(arrayOf("Оружие", "Удача", "Карта"))
//    forward.forEach { fact -> println(fact.description) }

    model.backward(arrayOf("Оружие", "Щит", "Удача", "Карта", "Вода"), arrayOf("Вино"))
}
