package com.github.emiilia.meowsicplayer.services.playerctl

data class TrackMetadata(
    val title: String = "Unknown Track",
    val artist: String = "Unknown Artist", 
    val album: String = "Unknown Album",
    val albumArtUrl: String = ""
) {
    fun getDisplayTitle(): String {
        return if (title != "Unknown Track") title else "No track playing"
    }
    
    fun getDisplayArtist(): String {
        return if (artist != "Unknown Artist") artist else "No artist"
    }
    
    fun getDisplayAlbum(): String {
        return if (album != "Unknown Album") album else "No album"
    }
    
    fun hasAlbumArt(): Boolean {
        return albumArtUrl.isNotBlank()
    }
    
    fun hasValidMetadata(): Boolean {
        return title != "Unknown Track" || artist != "Unknown Artist"
    }
}