package com.example.playlistmaker.search.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.playlistmaker.media.data.db.dao.PlaylistDao
import com.example.playlistmaker.media.data.db.dao.PlaylistSongDao
import com.example.playlistmaker.media.data.db.entity.PlaylistEntity
import com.example.playlistmaker.media.data.db.entity.PlaylistSongEntity
import com.example.playlistmaker.search.data.db.dao.SongDao
import com.example.playlistmaker.search.data.db.entity.SongEntity

@Database(entities = [SongEntity::class, PlaylistEntity::class, PlaylistSongEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun playlistSongDao(): PlaylistSongDao
}