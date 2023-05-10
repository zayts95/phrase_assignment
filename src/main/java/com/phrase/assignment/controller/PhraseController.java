package com.phrase.assignment.controller;

import com.phrase.assignment.service.PhraseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
public class PhraseController {

    private final PhraseService phraseService;

    @Autowired
    public PhraseController(PhraseService phraseService) {
        this.phraseService = phraseService;
    }

    @GetMapping
    public String mainPage(){
        return "redirect:projects";
    }


    @GetMapping("login")
    public ModelAndView login(ModelAndView modelAndView) {
        modelAndView.setViewName("login");
        return modelAndView;
    }

    @GetMapping("projects")
    public ModelAndView projects(ModelAndView modelAndView, HttpServletRequest httpServletRequest){
        HttpSession session = httpServletRequest.getSession();
        String account = (String) session.getAttribute("account");
        if (account==null){
            modelAndView.setViewName("redirect:login");
        } else {
            modelAndView.setViewName("projects");
        }
        return modelAndView;
    }

    @PostMapping("set_account")
    public ModelAndView setAccount(@RequestParam String login, @RequestParam String password, HttpServletRequest httpServletRequest, ModelAndView modelAndView) throws IOException {
        return phraseService.setAccount(login, password, httpServletRequest, modelAndView);
    }


    @GetMapping("getProjects")
    @ResponseBody
    public ResponseEntity<?> getProjects(HttpServletRequest request){
        return phraseService.getProjects(request);
    }

    @ExceptionHandler({Exception.class})
    public String exceptionHandler(Exception e){
        return "redirect:login";
    }
}
