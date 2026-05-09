package com.MusicPlatForm.search_service.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import com.MusicPlatForm.search_service.Entity.Playlist;

@Repository
public interface PlaylistRepository extends ElasticsearchRepository<Playlist,String> {
    List<Playlist> findByTitle(String name);
    List<Playlist> findByDescription(String description);
    
    @Query("""
        {
            "bool":{
                "should":[
                    {"match": {"title":{"query": "?0","fuzziness":"AUTO"}}},
                    {"match": {"description":{"query": "?0","fuzziness":"AUTO"}}}
                ]
            }
        }
    """)
    List<Playlist> findPlaylists(String query);

    void deleteByPlaylistId(String playlistId);

    Optional<Playlist> findByPlaylistId(String playlistId);

    List<Playlist> findAll();
}
