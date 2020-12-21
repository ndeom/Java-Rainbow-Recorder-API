package com.rainbowrecorder.api.repositories;

import com.rainbowrecorder.api.models.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface PostRepository extends JpaRepository<Post, String> {

    // should probably extract into /resources/***.sql file for brevity
    @Query(value = "SELECT" +
                "main.posts.post_id," +
                "main.posts.username," +
                "ST_AsGeoJSON(main.posts.location) as location_point," +
                "main.posts.image," +
                "main.posts.caption," +
                "main.posts.user_id," +
                "com.comments," +
                "l.likes," +
                "pic.profile_picture," +
                "FROM main.posts" +
            "LEFT JOIN (" +
                "SELECT user_id, profile_picture" +
                "FROM main.users" +
            ") as pic" +
            "ON (main.posts.user_id = pic.user_id)" +
            "LEFT JOIN (" +
                "SELECT post_id, json_agg(" +
                    "json_build_object(" +
                        "'username', username," +
                        "'comment', comment" +
                        "'comment_id', comment_id" +
                        "'post_id', post_id" +
                        "'timestamp', timestamp" +
                    ")" +
                ") as comments" +
                "FROM main.comments" +
                "GROUP BY post_id" +
            ") as com" +
            "ON (main.posts.post_id = com.post_id)" +
            "LEFT JOIN (" +
                "SELECT post_id, json_object_agg(user_id, post_id) as likes" +
                "FROM main.likes" +
                "GROUP BY post_id" +
            ") as l" +
            "ON (main.posts.post_id = l.post_id)" +
            "WHERE ST_Covers(" +
                "ST_MakeEnvelope(:west, :south, :east, :north)," +
                "main.posts.location" +
            ")", nativeQuery = true)
    List<Post> getPostsInBounds(@Param("west") float w,
                                                   @Param("south") float s,
                                                   @Param("east") float e,
                                                   @Param("north") float n);

    @Query(value = "SELECT" +
                "main.posts.post_id," +
                "main.posts.username," +
                "ST_AsGeoJSON(main.posts.location) as location_point," +
                "main.posts.image," +
                "main.posts.caption," +
                "main.posts.user_id," +
                "com.comments," +
                "l.likes," +
                "pic.profile_picture," +
            "FROM main.posts" +
            "LEFT JOIN (" +
                "SELECT user_id, profile_picture" +
                "FROM main.users" +
            ") as pic" +
            "ON (main.posts.user_id = pic.user_id)" +
            "LEFT JOIN (" +
                "SELECT post_id, json_agg(" +
                    "json_build_object(" +
                    "'username', username," +
                    "'comment', comment" +
                    "'comment_id', comment_id" +
                    "'post_id', post_id" +
                    "'timestamp', timestamp" +
                ")" +
            ") as comments" +
            "FROM main.comments" +
            "GROUP BY post_id" +
            ") as com" +
            "ON (main.posts.post_id = com.post_id)" +
            "LEFT JOIN (" +
                "SELECT post_id, json_object_agg(user_id, post_id) as likes" +
                "FROM main.likes" +
                "GROUP BY post_id" +
            ") as l" +
            "ON (main.posts.post_id = l.post_id)" +
            "WHERE main.posts.post_id = :post_id" , nativeQuery = true)
    Post getPost(@Param("post_id") String post_id);

}