package com.upar

import com.upar.data.database.checkPasswordForUsername
import com.upar.routes.*
import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.gson.*
import io.ktor.routing.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
fun Application.module() {
    install(DefaultHeaders)
    install(CallLogging)
    install(ContentNegotiation){
        gson {
            setPrettyPrinting()
        }
    }
    install(Authentication){
        configureAuth()
    }
    install(Routing){
        userRoute()
        chatRoute()
        tradingRoute()
    }

}


private fun Authentication.Configuration.configureAuth(){
    basic {
        realm = "CTF Server"
        validate {crudentials ->
            val username = crudentials.name
            val password = crudentials.password
            if(checkPasswordForUsername(username,password)){
                UserIdPrincipal(username)
            }else null
        }
    }
}