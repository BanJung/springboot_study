package com.study.springboot.service.posts;

import com.study.springboot.domain.posts.Posts;
import com.study.springboot.domain.posts.PostsRepository;
import com.study.springboot.web.dto.PostsListResponseDto;
import com.study.springboot.web.dto.PostsResponseDto;
import com.study.springboot.web.dto.PostsSaveRequestDto;
import com.study.springboot.web.dto.PostsUpdateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
/*
Bean은 생성자로 주입받는디(@Autowired는 권장하지 않음) : @RequiredArgsConstructor
@RequiredArgsConstructor는 final로 선언된 모든 필드를 인자값으로 하는 생성자를 대신 생성
 */
public class PostsService {
    private final PostsRepository postsRepository;
    @Transactional
    public Long save(PostsSaveRequestDto requestDto){
        return postsRepository.save(requestDto.toEntity()).getId();
    }

    @Transactional
    public Long update(Long id, PostsUpdateRequestDto requestDto){
        Posts posts=postsRepository.findById(id).orElseThrow(()->new IllegalArgumentException("해당 게시글이 없습니다. id="+id));
        posts.update(requestDto.getTitle(), requestDto.getContent());
        return id;
    }
    /*
    update는 repository를 통해 쿼리를 보내지 않음 : JPA의 영속성 컨텍스트 때문

    JPA의 영속성 컨텍스트 ; 엔티티를 영구 저장하는 환경
    엔티티가 영속성 컨텍스트에 포함되어 있냐 아니냐에 따라
    데이터베이스에 대한 자동 변경 반영 여부와 객체 상태 관리 방식이 달라짐

    트랜잭션 내 DB에서 데이터를 가져오면, 이 데이터는 영속성 컨텍스트가 유지된 상태
    즉, 엔티티 객체가 영속성 컨텍스트에 포함되어 있는 상태
    = DB와 연결되어 변경 사항이 추적되는 데이터

    이 상테에서 해당 데이터의 값을 변경하면 트랜잭션이 끝나는 시점에서 해당 테이블에 변경분을 반영(더티 체킹)
     */

    public PostsResponseDto findById(Long id){
        Posts entity=postsRepository.findById(id).orElseThrow(()->new IllegalArgumentException("해당 게시글이 없습니다. id="+id));
        return new PostsResponseDto(entity);
    }

    @Transactional(readOnly = true)
    public List<PostsListResponseDto> findAllDesc(){
        return postsRepository.findAllDesc().stream()
                .map(PostsListResponseDto::new)
                .collect(Collectors.toList());
    }
    /*
    트랜잭션 범위는 유지하되, 조회 기능만 남겨두어 조회 속도가 개선됨
    Posts의 Stream을 map을 통해 PostsListResponseDto로 변환, 그 후 List로 반환
     */

    @Transactional
    public void delete (Long id){
        Posts posts=postsRepository.findById(id)
                .orElseThrow(()->new
                        IllegalArgumentException("해당 게시글이 없습니다. id=" + id));
        postsRepository.delete(posts);
    }


}
