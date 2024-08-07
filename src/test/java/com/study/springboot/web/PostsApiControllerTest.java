package com.study.springboot.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.springboot.domain.posts.Posts;
import com.study.springboot.domain.posts.PostsRepository;
import com.study.springboot.web.dto.PostsResponseDto;
import com.study.springboot.web.dto.PostsSaveRequestDto;
import com.study.springboot.web.dto.PostsUpdateRequestDto;
import org.junit.After;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.LinkedHashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
/*
@SpringBootTest 또는 @TestRestTemplate은 JPA까지 테스트할 때 사용
 */
class PostsApiControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PostsRepository postsRepository;

    @After
    public void tearDown() throws Exception{
        postsRepository.deleteAll();
    }
    @Test
    public void Posts_등록된다() throws Exception{
        // given
        String title="title";
        String content="content";
        PostsSaveRequestDto requestDto=PostsSaveRequestDto.builder()
                .title(title)
                .content(content)
                .author("author")
                .build();
        String url="http://localhost:"+port+"/api/v1/posts";

        //when
        // 'url'에 'requestDto'를 body에 담아 post 요청을 보내고, 그 응답을 'Long' 타입으로 받음
        ResponseEntity<Long> responseEntity=restTemplate.postForEntity(url,requestDto,Long.class);

        //then
        assertThat(responseEntity.getStatusCode()).
                isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody())
                .isGreaterThan(0L);
        List<Posts> all=postsRepository.findAll();
        assertThat(all.get(0).getTitle()).isEqualTo(title);
        assertThat(all.get(0).getContent())
                .isEqualTo(content);

    }

    @Test
    public void Posts_수정된다() throws Exception{
        //given
        // 1. 수정하려면 등록해야함
        Posts savePosts=postsRepository.save(Posts.builder()
                .title("title")
                .content("content")
                .author("author")
                .build());

        // 2. 어떤 id의 글을 수정할지 + 어떤 값으로 수정할지 수정할 값 필요
        Long updateId=savePosts.getId();
        String expectedTitle="title2";
        String expectedContent="content2";

        PostsUpdateRequestDto requestDto = PostsUpdateRequestDto.builder()
                .title(expectedTitle)
                .content(expectedContent)
                .build();

        // 3. 요청을 보낼 url 설정
        String url="http://localhost:"+port+"/api/v1/posts/"+updateId;

        // 4. HTTP 요청의 본문과 헤더를 포함하는 객체 생성
        HttpEntity<PostsUpdateRequestDto> requestEntity = new HttpEntity<>(requestDto);

        //when
        // restTemplate.exchange: Post 및 Put, Delete 등 더 많은 메서드와 요청 헤더 설정 가능
        // 'url'에 'requestEntity' 객체를 통해 body와 header를 함께 담아 put 요청을 보내고, 그 응답을 'Long' 타입으로 받음
        ResponseEntity<Long> responseEntity=restTemplate.exchange(url, HttpMethod.PUT,requestEntity,Long.class);

        //then (공통)
        assertThat(responseEntity.getStatusCode())
                .isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody())
                .isGreaterThan(0L);
        List<Posts> all=postsRepository.findAll();
        assertThat(all.get(0).getTitle()).isEqualTo(expectedTitle);
        assertThat(all.get(0).getContent())
                .isEqualTo(expectedContent);
    }

    @Test
    public void Posts_id로_조회한다() throws Exception {
        //given
        // 1. 조회하려면 등록해야함
        String saveTitle="title";
        String saveContent="content";
        String saveAuthor="author";

        Posts savePosts = postsRepository.save(Posts.builder()
                .title(saveTitle)
                .content(saveContent)
                .author(saveAuthor)
                .build());

        Long findId = savePosts.getId();

        // 2. 요청을 보낼 url 설정
        String url = "http://localhost:" + port + "/api/v1/posts/" + findId;

        // when
        ResponseEntity<LinkedHashMap> responseEntity = restTemplate.getForEntity(url, LinkedHashMap.class);
        LinkedHashMap<String, Object> responseBody = responseEntity.getBody();

        // then
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseBody).isNotNull();
        assertThat(Long.valueOf(responseBody.get("id").toString())).isEqualTo(findId);
        assertThat(responseBody.get("title").toString()).isEqualTo(saveTitle);
        assertThat(responseBody.get("content").toString()).isEqualTo(saveContent);
        assertThat(responseBody.get("author")).isEqualTo(saveAuthor);

    }
}