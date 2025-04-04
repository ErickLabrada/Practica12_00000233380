package erick.labrada.practica12

data class PokeData(
    var name: String = "",
    var type: String = "",
    var number: String = "",
    var img: String = ""
) {
    constructor() : this("", "", "","")
}
