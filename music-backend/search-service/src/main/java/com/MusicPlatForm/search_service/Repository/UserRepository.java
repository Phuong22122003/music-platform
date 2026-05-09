package com.MusicPlatForm.search_service.Repository;

import java.util.List;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.MusicPlatForm.search_service.Entity.User;

public interface UserRepository extends ElasticsearchRepository<User,String>{
     @Query("""
        {
            "bool":{
                "should":[
                    {"match": {"displayName":{"query": "?0","fuzziness":"AUTO"}}},
                    {"match": {"fullname":{"query": "?0","fuzziness":"AUTO"}}}
                ]
            }
        }
    """)
    List<User> findUsers(String query);

    
    @Query("""
        {
            "bool": {
                "should": [
                    {"term": {"displayName": "?0"}},
                    {"term": {"fullname": "?0"}}
                ]
            }
        }
    """)
    List<User> findUserByName(String name);

    void deleteByUserId(String userId);
    User findUserByUserId(String userId);
    List<User> findAll();
}
