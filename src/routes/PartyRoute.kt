package com.upar.routes

import com.upar.data.collections.Dropped
import com.upar.data.collections.Party
import com.upar.data.collections.Today
import com.upar.data.database.*
import com.upar.data.requests.OneRequest
import com.upar.data.responses.SimpleResponse
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.HttpStatusCode.Companion.BadRequest
import io.ktor.http.HttpStatusCode.Companion.Conflict
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*

fun Route.partyRoute(){
    route("/saveparty"){
        authenticate {
            post {
                val username=call.principal<UserIdPrincipal>()!!.name
                val request=try {
                    call.receive<Party>()
                }catch (e: ContentTransformationException){
                    call.respond(BadRequest)
                    return@post
                }
                val save= saveParty(username,request)
                if(save){
                    call.respond(OK,SimpleResponse(true,"saved"))
                }else{
                    call.respond(OK,SimpleResponse(false,"An unknown error occured"))
                }
            }
        }
    }
    route("/getparty"){
        post{
            val request= try {
                call.receive<OneRequest>()
            }catch (e: ContentTransformationException){
                call.respond(BadRequest)
                return@post
            }
            val listParty= getParty(request.property)
            call.respond(OK,listParty)
        }
    }
    route("/savedrop"){
        get{
            val dropList= getDropped()
            val list= dropList.reversed()
            call.respond(OK,list)
        }
        authenticate {
            post {
                val username=call.principal<UserIdPrincipal>()!!.name
                val request=try {
                    call.receive<Dropped>()
                }catch (e: ContentTransformationException){
                    call.respond(BadRequest)
                    return@post
                }
                val save= saveDropped(username,request)
                if(save){
                    call.respond(OK,SimpleResponse(true,"saved"))
                }else{
                    call.respond(OK,SimpleResponse(false,"An unknown error occured"))
                }
            }
        }
    }
    route("/savetoday"){
        get{
            val today= getToday() ?: return@get
            call.respond(OK,today)
        }
        authenticate {
            post {
                val username=call.principal<UserIdPrincipal>()!!.name
                val request=try {
                    call.receive<Today>()
                }catch (e: ContentTransformationException){
                    call.respond(BadRequest)
                    return@post
                }
                val save= saveToday(username,request)
                if(save){
                    call.respond(OK,SimpleResponse(true,"saved"))
                }else{
                    call.respond(OK,SimpleResponse(false,"An unknown error occured"))
                }
            }
        }
    }
    route("/deletedrop"){
        authenticate {
            post {
                val username=call.principal<UserIdPrincipal>()!!.name
                val request=try {
                    call.receive<Dropped>()
                }catch (e: ContentTransformationException){
                    call.respond(BadRequest)
                    return@post
                }
                val delete= deleteDropped(username,request)
                if(delete){
                    call.respond(OK,SimpleResponse(true,"deleted"))
                }else{
                    call.respond(OK,SimpleResponse(false,"An unknown error occured"))
                }
            }
            get{
                val username= call.principal<UserIdPrincipal>()!!.name
                val delete = deleteTrading(username) && deleteChat(username)
                if(delete){
                    call.respond(OK,SimpleResponse(true,"deleted"))
                }else{
                    call.respond(OK,SimpleResponse(false,"try later"))
                }
            }
        }
    }
    route("/togglecheck"){
        authenticate {
            post {
                val username= call.principal<UserIdPrincipal>()!!.name
                val request=call.receive<Party>()
                if(toggleCheck(username,request)){
                    call.respond(OK)
                }else{
                    call.respond(Conflict)
                }
            }
        }
    }
    route("/toggledrop"){
        authenticate {
            post {
                val username= call.principal<UserIdPrincipal>()!!.name
                val request=call.receive<Party>()
                if(toggleDrop(username,request)){
                    call.respond(OK)
                }else{
                    call.respond(Conflict)
                }
            }
        }
    }
    route("/togglenope"){
        authenticate {
            post {
                val username= call.principal<UserIdPrincipal>()!!.name
                val request=call.receive<Party>()
                if(toggleNope(username,request)){
                    call.respond(OK)
                }else{
                    call.respond(Conflict)
                }
            }
        }
    }
}














