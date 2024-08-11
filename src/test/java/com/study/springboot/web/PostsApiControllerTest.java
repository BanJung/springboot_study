package com.study.springboot.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.springboot.domain.posts.Posts;
import com.study.springboot.domain.posts.PostsRepository;
import com.study.springboot.web.dto.PostsSaveRequestDto;
import com.study.springboot.web.dto.PostsUpdateRequestDto;
import jakarta.servlet.http.HttpServletMapping;
import jakarta.servlet.http.MappingMatch;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.mock.web.MockHttpServletMapping;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


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

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @BeforeEach
    // JUnit5일 경우 Before가 아니라 BeforeEach
    public void setup(){
        mvc= MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @AfterEach
    // JUnit5일 경우 After가 아니라 AfterEach. After 사용시 update 테스트에서 AssertionFailedError가 발생
    public void tearDown() throws Exception{
        postsRepository.deleteAll();
    }

    private static RequestPostProcessor mvcMapping() {
        return (request) -> {
            String matchValue = request.getRequestURI();
            String servlet = "dispatcherServlet";
            String pattern = request.getServletContext().getServletRegistration(servlet).getMappings().iterator().next();
            HttpServletMapping mapping = new MockHttpServletMapping(matchValue, pattern, servlet, MappingMatch.PATH);
            request.setHttpServletMapping(mapping);
            return request;
        };
    }

    @Test
    @WithMockUser(roles = "USER")
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


        /*

        //when (before security)
        // 'url'에 'requestDto'를 body에 담아 post 요청을 보내고, 그 응답을 'Long' 타입으로 받음
        ResponseEntity<Long> responseEntity=restTemplate.postForEntity(url,requestDto,Long.class);

        //then (before security)
        assertThat(responseEntity.getStatusCode()).
                isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody())
                .isGreaterThan(0L);

         */

        // when (after security)
        mvc.perform(post(url)
                        .with(mvcMapping())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDto)))
                        .andExpect(status().isOk());

        //then (common)
        List<Posts> all=postsRepository.findAll();
        assertThat(all.get(0).getTitle()).isEqualTo(title);
        assertThat(all.get(0).getContent())
                .isEqualTo(content);

    }

    @Test
    @WithMockUser(roles = "USER")
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

        /*
        //when (before security)
        // restTemplate.exchange: Post 및 Put, Delete 등 더 많은 메서드와 요청 헤더 설정 가능
        // 'url'에 'requestEntity' 객체를 통해 body와 header를 함께 담아 put 요청을 보내고, 그 응답을 'Long' 타입으로 받음
        ResponseEntity<Long> responseEntity=restTemplate.exchange(url, HttpMethod.PUT,requestEntity,Long.class);

        //then (before security)
        assertThat(responseEntity.getStatusCode())
                .isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody())
                .isGreaterThan(0L);

         */

        // when(after security)
        mvc.perform(put(url)
                        .with(mvcMapping())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDto)))
                        .andExpect(status().isOk());

        // then (common)
        List<Posts> all=postsRepository.findAll();
        assertThat(all.get(0).getTitle()).isEqualTo(expectedTitle);
        assertThat(all.get(0).getContent())
                .isEqualTo(expectedContent);
    }

    @Test
    @WithMockUser(roles = "USER")
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

        /*
        // when (before security)
        ResponseEntity<LinkedHashMap> responseEntity = restTemplate.getForEntity(url, LinkedHashMap.class);
        LinkedHashMap<String, Object> responseBody = responseEntity.getBody();

        // then (befor security)
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseBody).isNotNull();
        assertThat(Long.valueOf(responseBody.get("id").toString())).isEqualTo(findId);
        assertThat(responseBody.get("title").toString()).isEqualTo(saveTitle);
        assertThat(responseBody.get("content").toString()).isEqualTo(saveContent);
        assertThat(responseBody.get("author")).isEqualTo(saveAuthor);
         */

        // when (after security)
        mvc.perform(get(url)
                        .with(mvcMapping())
                        .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(findId))
                    .andExpect(jsonPath("$.title").value(saveTitle))
                    .andExpect(jsonPath("$.content").value(saveContent))
                    .andExpect(jsonPath("$.author").value(saveAuthor));

    }
}