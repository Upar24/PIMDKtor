package com.upar.data.database

import com.upar.data.collections.*
import com.upar.data.requests.UpdateUserRequest
import com.upar.util.ListString.upar
import com.upar.util.checkHashForPassword
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

private val client = KMongo.createClient().coroutine
private val database= client.getDatabase("CTF060621")
private val users= database.getCollection<User>()
private val tradings= database.getCollection<Trading>()
private val walls= database.getCollection<Wall>()
suspend fun registerUser(user: User): Boolean{
    val registerUser= User(
        username=user.username,
        password = user.password,
        name="",
        clubName = user.clubName,
        ign=user.ign,
        bio="",
        created = System.currentTimeMillis()
    )
    return users.insertOne(registerUser).wasAcknowledged()
}
suspend fun checkIfUserExists(username: String):Boolean{
    return users.findOne(User::username eq username) != null
}
suspend fun checkPasswordForUsername(username: String,passwordToCheck: String): Boolean{
    val actualPassword= users.findOne(User::username eq username)?.password ?: return false
    return checkHashForPassword(passwordToCheck,actualPassword)
}
suspend fun getUser(username: String): User? {
    return users.findOne(User::username eq username)
}
suspend fun updateUser(username: String,updateUserReq: UpdateUserRequest):Boolean{
    val user = users.findOne(User::username eq username) ?: return false
    val userUpdate= User(
        user.username,
        user.password,
        updateUserReq.name,
        updateUserReq.clubName,
        updateUserReq.ign,
        updateUserReq.bio,
        user.created,
        user._id
    )
    return users.updateOneById(user._id,userUpdate).wasAcknowledged()
}
suspend fun saveTrading(username:String,trading: Trading):Boolean{
    val user=users.findOne(User::username eq username)
    val date = System.currentTimeMillis()
    val trading1= Trading(
        username,
        user?.name,
        user?.ign,
        trading.desc,
        trading.itemBuying.toString().toLowerCase(),
        trading.itemSelling.toString().toLowerCase(),
        date,
        trading._id
    )
    val traidngExists= tradings.findOneById(trading._id) != null
    return if(traidngExists){
        tradings.updateOneById(trading._id,trading1).wasAcknowledged()
    }else{
        tradings.insertOne(trading1).wasAcknowledged()
    }
}
suspend fun deleteTrading(username: String,trading: Trading):Boolean{
    return if(username==trading.username){
        tradings.deleteOneById(trading._id).wasAcknowledged()
    }else{
        false
    }
}
suspend fun getAllTrading():List<Trading>{
    return tradings.find().sort(descending(Trading::date)).limit(1000).toList()
}
suspend fun getAllUserTrading(username: String):List<Trading>{
    return tradings.find(Trading::username eq username).sort(descending(Trading::date)).limit(1000).toList()
}
suspend fun getTrading(trading: Trading): Trading?{
    return tradings.findOne(Trading::_id eq trading._id)
}
suspend fun getBuyingSearch(query:String):List<Trading>{
    val search = if(query=="" || query.isEmpty())"KLKLKLKLKLKL" else query.toLowerCase()
    return tradings.find(Trading::itemBuying regex Regex("(?i).*$search.*")).sort(descending(Trading::date)).limit(1000).toList()
}
suspend fun getSellingSearch(query: String):List<Trading>{
    val search = if(query=="" || query.isEmpty())"klklklklk" else query.toLowerCase()
    return tradings.find(Trading::itemSelling regex Regex("(?i).*$search.*")).sort(descending(Trading::date)).limit(1000).toList()
}
suspend fun getListTradingDesc(oneRequest:String):List<Trading>{
    val request= if(oneRequest=="" || oneRequest.isEmpty())"klklklklk" else oneRequest.toLowerCase()
    return tradings.find(Trading::desc regex Regex("(?i).*$request.*")).sort(descending(Trading::date)).limit(1000).toList()
}
suspend fun saveWall(username: String,wall: Wall):Boolean{
    val user=users.findOne(User::username eq username)
    val wall1= Wall(
        username,
        user?.ign,
        user?.clubName,
        wall.wallOwner,
        wall.chat,
        System.currentTimeMillis()
    )
    val wallExist= walls.findOneById(wall._id) != null
    return if(wallExist){
        walls.updateOneById(wall._id,wall1).wasAcknowledged()
    }else{
        walls.insertOne(wall1).wasAcknowledged()
    }
}
suspend fun getAllWall(wallOwner:String):List<Wall>{
    return walls.find(Wall::wallOwner eq wallOwner).sort(descending(Wall::date)).toList()
}
suspend fun deleteWall(username: String,wall: Wall):Boolean{
    return if(username==wall.wallOwner){
        walls.deleteOneById(wall._id).wasAcknowledged()
    }else{
        false
    }
}
suspend fun deleteTrading(username:String):Boolean{
    val timestart = System.currentTimeMillis() - 345600000
    return if(username==upar){
        tradings.deleteMany(Trading::date lte timestart).wasAcknowledged()
    }else false
}
















