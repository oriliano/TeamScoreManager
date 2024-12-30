// com.example.finalproject.data.PlayerDeserializer.kt
package com.example.finalproject.data

import com.google.gson.*
import java.lang.reflect.Type

class PlayerDeserializer : JsonDeserializer<Player> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Player {
        val jsonObject = json.asJsonObject

        val name = jsonObject.get("name").asString
        val team = jsonObject.get("team").asString
        val kitNumber = jsonObject.get("kitNumber").asInt

        return Player(
            name = name,
            team = team,
            kitNumber = kitNumber
            // guessStatus otomatik olarak UNANSWERED olacaktır
        )
    }
}
