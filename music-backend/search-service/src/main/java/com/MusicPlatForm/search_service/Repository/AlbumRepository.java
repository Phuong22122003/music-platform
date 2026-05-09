package com.MusicPlatForm.search_service.Repository;

import java.util.List;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import com.MusicPlatForm.search_service.Entity.Album;
@Repository
public interface AlbumRepository extends ElasticsearchRepository<Album,String>{
    List<Album> findByTitle(String name);
    List<Album> findByDescription(String description);
    
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
    List<Album> findAlbums(String query);
    void deleteByAlbumId(String albumId);
    Album findByAlbumId(String albumId);

    List<Album> findAll();
}
