package com.MusicPlatForm.search_service.Repository;

import java.util.List;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import com.MusicPlatForm.search_service.Entity.Track;

@Repository
public interface TrackRepository extends ElasticsearchRepository<Track,String>{
    List<Track> findByName(String name);
    List<Track> findByDescription(String description);
    
    @Query("""
        {
            "bool":{
                "should":[
                    {"match": {"name":{"query": "?0","fuzziness":"AUTO"}}},
                    {"match": {"description":{"query": "?0","fuzziness":"AUTO"}}}
                ]
            }
        }
    """)
    List<Track> findTracks(String query);
    
    void deleteByTrackId(String trackId);
    List<Track> findAll();
}


