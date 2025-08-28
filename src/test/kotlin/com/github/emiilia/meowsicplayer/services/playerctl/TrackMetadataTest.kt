package com.github.emiilia.meowsicplayer.services.playerctl

import com.intellij.testFramework.fixtures.BasePlatformTestCase

class TrackMetadataTest : BasePlatformTestCase() {
    
    fun testDefaultTrackMetadata() {
        val metadata = TrackMetadata()
        
        assertEquals("Unknown Track", metadata.title)
        assertEquals("Unknown Artist", metadata.artist)
        assertEquals("Unknown Album", metadata.album)
        assertEquals("", metadata.albumArtUrl)
    }
    
    fun testCustomTrackMetadata() {
        val metadata = TrackMetadata(
            title = "Test Song",
            artist = "Test Artist",
            album = "Test Album",
            albumArtUrl = "http://example.com/album.jpg"
        )
        
        assertEquals("Test Song", metadata.title)
        assertEquals("Test Artist", metadata.artist)
        assertEquals("Test Album", metadata.album)
        assertEquals("http://example.com/album.jpg", metadata.albumArtUrl)
    }
    
    fun testGetDisplayTitle() {
        val defaultMetadata = TrackMetadata()
        assertEquals("No track playing", defaultMetadata.getDisplayTitle())
        
        val customMetadata = TrackMetadata(title = "My Song")
        assertEquals("My Song", customMetadata.getDisplayTitle())
    }
    
    fun testGetDisplayArtist() {
        val defaultMetadata = TrackMetadata()
        assertEquals("No artist", defaultMetadata.getDisplayArtist())
        
        val customMetadata = TrackMetadata(artist = "My Artist")
        assertEquals("My Artist", customMetadata.getDisplayArtist())
    }
    
    fun testGetDisplayAlbum() {
        val defaultMetadata = TrackMetadata()
        assertEquals("No album", defaultMetadata.getDisplayAlbum())
        
        val customMetadata = TrackMetadata(album = "My Album")
        assertEquals("My Album", customMetadata.getDisplayAlbum())
    }
    
    fun testHasAlbumArt() {
        val withoutArt = TrackMetadata()
        assertFalse(withoutArt.hasAlbumArt())
        
        val withArt = TrackMetadata(albumArtUrl = "http://example.com/art.jpg")
        assertTrue(withArt.hasAlbumArt())
    }
    
    fun testHasValidMetadata() {
        val defaultMetadata = TrackMetadata()
        assertFalse(defaultMetadata.hasValidMetadata())
        
        val withTitle = TrackMetadata(title = "Song")
        assertTrue(withTitle.hasValidMetadata())
        
        val withArtist = TrackMetadata(artist = "Artist")
        assertTrue(withArtist.hasValidMetadata())
        
        val withBoth = TrackMetadata(title = "Song", artist = "Artist")
        assertTrue(withBoth.hasValidMetadata())
    }
}