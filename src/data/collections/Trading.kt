package com.upar.data.collections

import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

data class Trading(
    val username:String?="",
    val name:String?="",
    val ign:String?="",
    val desc:String?="",
    val itemBuying:String?="",
    val itemSelling:String?="",
    val date:Long,
    @BsonId
    val _id:String= ObjectId().toString()

)