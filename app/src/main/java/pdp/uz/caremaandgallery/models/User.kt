package pdp.uz.caremaandgallery.models


class User {
    var id: Int? = null
    var name: String? = null
    var phoneNumber: String? = null
    var country: String? = null
    var address: String? = null
    var password: String? = null
    var image:String? = null


    constructor()

    constructor(
        name: String?,
        phoneNumber: String?,
        country: String?,
        address: String?,
        password: String?,
        image: String?
    ) {
        this.name = name
        this.phoneNumber = phoneNumber
        this.country = country
        this.address = address
        this.password = password
        this.image = image
    }
}