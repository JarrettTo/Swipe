package com.swipe.application

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull
import com.google.common.reflect.TypeToken
import com.google.gson.Gson

const val TABLE_GAMES = "games"
const val COLUMN_GAME_ID = "gameId"
const val COLUMN_IMAGE_ID = "imageId"
const val COLUMN_IMAGE_URL = "imageURL"
const val COLUMN_GAME_NAME = "gameName"
const val COLUMN_DESC = "description"
const val COLUMN_GENRE = "genre"
const val COLUMN_PLATFORM= "platform"
const val COLUMN_PRICE = "price"
const val COLUMN_VIDEOID = "videoId"
const val COLUMN_VIDEOURL = "videoUrl"
const val COLUMN_SIMILARGAMES = "similarGames"
const val COLUMN_POPULARPLAYERS = "popularPlayers"
const val COLUMN_REVIEWS = "reviews"

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "NEW_LIKED_GAMES", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        val createGamesTable = ("CREATE TABLE " + TABLE_GAMES + "("
                + COLUMN_GAME_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_IMAGE_ID + " INTEGER,"
                + COLUMN_IMAGE_URL + " TEXT,"
                + COLUMN_GAME_NAME + " TEXT,"
                + COLUMN_DESC + " TEXT,"
                + COLUMN_GENRE + " TEXT,"
                + COLUMN_PLATFORM + " TEXT,"
                + COLUMN_PRICE + " TEXT,"
                + COLUMN_VIDEOID + " TEXT,"
                + COLUMN_VIDEOURL + " TEXT,"
                + COLUMN_SIMILARGAMES + " TEXT,"
                + COLUMN_POPULARPLAYERS + " TEXT,"
                + COLUMN_REVIEWS + " TEXT"

                + ")")
        db.execSQL(createGamesTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_GAMES")
        onCreate(db)
    }
    fun saveGame(game: Games) {
        val db = this.writableDatabase
        val contentValues = ContentValues()

        contentValues.put(COLUMN_GAME_ID, game.gameId)
        contentValues.put(COLUMN_IMAGE_ID, game.imageId)
        contentValues.put(COLUMN_IMAGE_URL, game.imageURL)
        contentValues.put(COLUMN_GAME_NAME, game.gameName)
        contentValues.put(COLUMN_DESC, game.description)
        val gson = Gson()
        contentValues.put(COLUMN_GENRE, gson.toJson(game.genre))
        contentValues.put(COLUMN_PLATFORM, gson.toJson(game.platform))
        contentValues.put(COLUMN_PRICE, game.price)
        contentValues.put(COLUMN_VIDEOID, game.videoId)
        contentValues.put(COLUMN_VIDEOURL, game.videoUrl)
        contentValues.put(COLUMN_SIMILARGAMES, gson.toJson(game.similarGames))
        contentValues.put(COLUMN_POPULARPLAYERS, gson.toJson(game.popularPlayers))
        contentValues.put(COLUMN_REVIEWS, gson.toJson(game.reviews))

        db.insert(TABLE_GAMES, null, contentValues)
        db.close()
    }

    fun getGame(gameId: Int): Games? {
        val db = this.readableDatabase
        val cursor = db.query(TABLE_GAMES, null, "$COLUMN_GAME_ID=?", arrayOf(gameId.toString()), null, null, null)
        val genreIndex = cursor.getColumnIndex(COLUMN_GENRE)
        val platIndex = cursor.getColumnIndex(COLUMN_PLATFORM)
        val similarIndex= cursor.getColumnIndex(COLUMN_SIMILARGAMES)
        val popularIndex= cursor.getColumnIndex(COLUMN_POPULARPLAYERS)
        val reviewIndex= cursor.getColumnIndex(COLUMN_REVIEWS)
        if (cursor != null && cursor.moveToFirst()) {
            val gson = Gson()
            val game = Games(
                gameId = cursor.getIntOrNull(cursor.getColumnIndex(COLUMN_GAME_ID))!!,
                imageId = cursor.getIntOrNull(cursor.getColumnIndex(COLUMN_IMAGE_ID))!!,
                imageURL = cursor.getStringOrNull(cursor.getColumnIndex(COLUMN_IMAGE_URL))!!,
                gameName = cursor.getStringOrNull(cursor.getColumnIndex(COLUMN_GAME_NAME))!!,
                description = cursor.getStringOrNull(cursor.getColumnIndex(COLUMN_DESC))!!,
                genre = if (genreIndex != -1) gson.fromJson(cursor.getString(genreIndex), object : TypeToken<ArrayList<String>>() {}.type) else arrayListOf(),
                platform = if (platIndex != -1) gson.fromJson(cursor.getString(platIndex), object : TypeToken<ArrayList<String>>() {}.type) else arrayListOf(),
                price = cursor.getStringOrNull(cursor.getColumnIndex(COLUMN_PRICE))!!,
                videoId = cursor.getIntOrNull(cursor.getColumnIndex(COLUMN_VIDEOID))!!,
                videoUrl = cursor.getStringOrNull(cursor.getColumnIndex(COLUMN_VIDEOURL))!!,
                similarGames = if (similarIndex!= -1) gson.fromJson(cursor.getString(similarIndex), object : TypeToken<ArrayList<String>>() {}.type) else arrayListOf(),
                popularPlayers = if (popularIndex!= -1) gson.fromJson(cursor.getString(popularIndex), object : TypeToken<ArrayList<String>>() {}.type) else arrayListOf(),
                reviews = if (reviewIndex!= -1) gson.fromJson(cursor.getString(reviewIndex), object : TypeToken<ArrayList<String>>() {}.type) else arrayListOf(),

            )
            cursor.close()
            return game
        }
        cursor?.close()
        return null
    }
    fun getGames(): ArrayList<Games>? {
        val db = this.readableDatabase
        if(db==null){
            return null
        }
        val games : ArrayList<Games> = arrayListOf()
        val cursor = db.query(TABLE_GAMES,
            null,
            null,
            null,
            null,
            null,
            null)

        if (cursor != null && cursor.moveToFirst()) {
            val genreIndex = cursor.getColumnIndex(COLUMN_GENRE)
            val similarIndex= cursor.getColumnIndex(COLUMN_SIMILARGAMES)
            val popularIndex= cursor.getColumnIndex(COLUMN_POPULARPLAYERS)
            val platIndex = cursor.getColumnIndex(COLUMN_PLATFORM)
            val reviewIndex= cursor.getColumnIndex(COLUMN_REVIEWS)
            do {
                // Assuming you have a method to create a game object from a cursor
                val gson = Gson()
                val game = Games(
                    gameId = cursor.getIntOrNull(cursor.getColumnIndex(COLUMN_GAME_ID))!!,
                    imageId = cursor.getIntOrNull(cursor.getColumnIndex(COLUMN_IMAGE_ID))!!,
                    imageURL = cursor.getStringOrNull(cursor.getColumnIndex(COLUMN_IMAGE_URL))!!,
                    gameName = cursor.getStringOrNull(cursor.getColumnIndex(COLUMN_GAME_NAME))!!,
                    description = cursor.getStringOrNull(cursor.getColumnIndex(COLUMN_DESC))!!,
                    // ... other fields ...
                    genre = if (genreIndex != -1) gson.fromJson(cursor.getString(genreIndex), object : TypeToken<ArrayList<String>>() {}.type) else arrayListOf(),
                    platform = if (platIndex != -1) gson.fromJson(cursor.getString(platIndex), object : TypeToken<ArrayList<String>>() {}.type) else arrayListOf(),
                    price = cursor.getStringOrNull(cursor.getColumnIndex(COLUMN_PRICE))!!,
                    videoId = cursor.getIntOrNull(cursor.getColumnIndex(COLUMN_VIDEOID))!!,
                    videoUrl = cursor.getStringOrNull(cursor.getColumnIndex(COLUMN_VIDEOURL))!!,
                    similarGames = if (similarIndex!= -1) gson.fromJson(cursor.getString(similarIndex), object : TypeToken<ArrayList<String>>() {}.type) else arrayListOf(),
                    popularPlayers = if (popularIndex!= -1) gson.fromJson(cursor.getString(popularIndex), object : TypeToken<ArrayList<String>>() {}.type) else arrayListOf(),
                    reviews = if (reviewIndex!= -1) gson.fromJson(cursor.getString(reviewIndex), object : TypeToken<ArrayList<String>>() {}.type) else arrayListOf(),
                    // Deserialize JSON string back to ArrayList
                )
                games.add(game)
            } while (cursor.moveToNext())
            cursor.close()
            return games
        }
        cursor?.close()
        return null
    }
    fun searchGamesByName(searchQuery: String): ArrayList<Games> {
        val gamesList = ArrayList<Games>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_GAMES WHERE $COLUMN_GAME_NAME LIKE ?", arrayOf("%$searchQuery%"))

        if (cursor.moveToFirst()) {
            val genreIndex = cursor.getColumnIndex(COLUMN_GENRE)
            val similarIndex= cursor.getColumnIndex(COLUMN_SIMILARGAMES)
            val popularIndex= cursor.getColumnIndex(COLUMN_POPULARPLAYERS)
            val platIndex = cursor.getColumnIndex(COLUMN_PLATFORM)
            val reviewIndex= cursor.getColumnIndex(COLUMN_REVIEWS)
            do {

                val gson = Gson()
                val game = Games(
                    gameId = cursor.getIntOrNull(cursor.getColumnIndex(COLUMN_GAME_ID))!!,
                    imageId = cursor.getIntOrNull(cursor.getColumnIndex(COLUMN_IMAGE_ID))!!,
                    imageURL = cursor.getStringOrNull(cursor.getColumnIndex(COLUMN_IMAGE_URL))!!,
                    gameName = cursor.getStringOrNull(cursor.getColumnIndex(COLUMN_GAME_NAME))!!,
                    description = cursor.getStringOrNull(cursor.getColumnIndex(COLUMN_DESC))!!,
                    genre = if (genreIndex != -1) gson.fromJson(cursor.getString(genreIndex), object : TypeToken<ArrayList<String>>() {}.type) else arrayListOf(),
                    platform = if (platIndex != -1) gson.fromJson(cursor.getString(platIndex), object : TypeToken<ArrayList<String>>() {}.type) else arrayListOf(),
                    price = cursor.getStringOrNull(cursor.getColumnIndex(COLUMN_PRICE))!!,
                    videoId = cursor.getIntOrNull(cursor.getColumnIndex(COLUMN_VIDEOID))!!,
                    videoUrl = cursor.getStringOrNull(cursor.getColumnIndex(COLUMN_VIDEOURL))!!,
                    similarGames = if (similarIndex!= -1) gson.fromJson(cursor.getString(similarIndex), object : TypeToken<ArrayList<String>>() {}.type) else arrayListOf(),
                    popularPlayers = if (popularIndex!= -1) gson.fromJson(cursor.getString(popularIndex), object : TypeToken<ArrayList<String>>() {}.type) else arrayListOf(),
                    reviews = if (reviewIndex!= -1) gson.fromJson(cursor.getString(reviewIndex), object : TypeToken<ArrayList<String>>() {}.type) else arrayListOf(),

                )
                gamesList.add(game)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return gamesList
    }
    fun clearDatabase() {
        val db = this.writableDatabase


        db.execSQL("DELETE FROM $TABLE_GAMES")

        db.close()
    }
    fun saveGames(arrayList: ArrayList<Games>) {
        for(i in arrayList){
            saveGame(i)
        }
    }


}