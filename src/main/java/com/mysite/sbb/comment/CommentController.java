package com.mysite.sbb.comment;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.answer.AnswerForm;
import com.mysite.sbb.answer.AnswerService;
import com.mysite.sbb.question.Question;
import com.mysite.sbb.question.QuestionService;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@RequestMapping("/comment")
@RequiredArgsConstructor
@Controller
public class CommentController {

    private final AnswerService answerService;
    private final CommentService commentService;
    private final UserService userService;


    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/answer/{id}")
    public String createAboutAnswer(Model model, @PathVariable("id") Integer id,
                                    @RequestParam(value = "so", defaultValue = "recent") String so,
                                    @RequestParam(value = "page", defaultValue = "0") int page,
                                    @Valid CommentForm commentForm,
                                    BindingResult bindingResult, Principal principal, AnswerForm answerForm) {
        Answer answer = this.answerService.getAnswer(id);
        SiteUser siteUser = this.userService.getUser(principal.getName());
        Question question = answer.getQuestion();

        Page<Answer> ansPaging = this.answerService.getList(question, page, so);

        if (bindingResult.hasErrors()) {
            model.addAttribute("question", question);
            model.addAttribute("ansPaging", ansPaging);
            return "question_detail";
        }

        Comment comment = this.commentService.create(answer, commentForm.getContent(), siteUser);
        model.addAttribute("ansPage", page);
        model.addAttribute("so", so);
        return String.format("redirect:/question/detail/%s?ansPage=%s&so=%s#answer-%s", comment.getAnswer().getQuestion().getId(), page, so, comment.getAnswer().getId());

    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String answerDelete(Principal principal, @PathVariable("id") Integer id) {
        Comment comment = this.commentService.getComment(id);
        Answer answer = this.answerService.getAnswer(comment.getAnswer().getId());

        if (!comment.getAuthor().getUsername().equals(principal.getName())
                && !answer.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }

        this.commentService.delete(comment);
        return String.format("redirect:/question/detail/%s", answer.getQuestion().getId());
    }
}
