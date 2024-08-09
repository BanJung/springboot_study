package com.study.springboot.web;

import com.study.springboot.config.auth.dto.SessionUser;
import com.study.springboot.service.posts.PostsService;
import com.study.springboot.web.dto.PostsResponseDto;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RequiredArgsConstructor
@Controller
public class IndexController {
    /*
    타임리프 스타터로 컨트롤러에서 문자열을 반환할 때 앞의 경로(src/main/resources/templates)와
     뒤의 파일 확장자(.html)은 자동 지정
     */
    private final PostsService postsService;
    private final HttpSession httpSession;

    @GetMapping("/")
    public String index(Model model){
        model.addAttribute("posts",postsService.findAllDesc());
        SessionUser user=(SessionUser) httpSession.getAttribute("user");
        if(user !=null){
            model.addAttribute("userName",user.getName());
        }
        return "index";
    }
    // src/main/resources/templates/index.html로 전환되어 View Resolver가 처리

    @GetMapping("/posts/save")
    public String postsSave(){
        return "posts-save";
    }

    @GetMapping("/posts/update/{id}")
    public String postsUpdate(@PathVariable Long id, Model model){
        PostsResponseDto dto=postsService.findById(id);
        model.addAttribute("post",dto);
        return "posts-update";
    }

}
