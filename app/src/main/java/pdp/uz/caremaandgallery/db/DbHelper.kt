package pdp.uz.caremaandgallery.db

import pdp.uz.caremaandgallery.models.User

interface DbHelper {

    fun insertUser(user: User)

    fun getAllUsers(): ArrayList<User>

}