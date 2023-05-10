package com.phrase.assignment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.phrase.assignment.dto.LoginDto;
import com.phrase.assignment.dto.LoginResponseDto;
import com.phrase.assignment.model.AccountConfiguration;
import com.phrase.assignment.repository.AccountConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Optional;

@Service
public class PhraseService {
    private static final String LOGIN = "api2/v1/auth/login";
    private static final String LIST_PROJECTS = "api2/v1/projects";


    public final String url;

    private final RestTemplate restTemplate;
    private final AccountConfigRepository repository;
    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public PhraseService(@Value("${phrase.endpoint.url}") String url, RestTemplate restTemplate, AccountConfigRepository repository) {
        this.url = url;
        this.restTemplate = restTemplate;
        this.repository = repository;
    }

    @Transactional
    public ModelAndView setAccount(String login, String password, HttpServletRequest request, ModelAndView modelAndView) throws JsonProcessingException {
        HttpSession session = request.getSession();
        Optional<AccountConfiguration> optional = repository.findById(login);
        if (optional.isPresent()){
            AccountConfiguration accountConfiguration = optional.get();
            if (password.equals(accountConfiguration.getPassword())){
                setSessionAttributes(session, accountConfiguration.getUserName(), accountConfiguration.getToken());
                modelAndView.setViewName("redirect:projects");
                return modelAndView;
            } else {
                return authentication(login, password,session, modelAndView);
            }
        } else {
            return authentication(login, password,session, modelAndView);
        }
    }

    @Transactional
    public ResponseEntity<?> getProjects(HttpServletRequest request){
        HttpSession session = request.getSession();
        String account = (String) session.getAttribute("account");
        if (account == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Optional<AccountConfiguration> optional = repository.findById(account);
        if (optional.isPresent()){
            String token = optional.get().getToken();
            return sendProjectsRequest(token);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    private ResponseEntity<?> sendProjectsRequest(String token){
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "ApiToken " + token);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        return restTemplate.exchange(
                url + LIST_PROJECTS, HttpMethod.GET, requestEntity, String.class);
    }

    private ModelAndView authentication(String login, String password, HttpSession session, ModelAndView modelAndView) throws JsonProcessingException {
        LoginDto loginDto = new LoginDto(login, password);
        ResponseEntity<?> responseEntity = restTemplate.postForEntity(url + LOGIN, loginDto, String.class);
        if (responseEntity.getStatusCodeValue()==200){
            LoginResponseDto loginResponse = mapper.readValue((String) responseEntity.getBody(), LoginResponseDto.class);
            AccountConfiguration accountConfiguration = new AccountConfiguration(loginResponse, password);
            repository.save(accountConfiguration);
            setSessionAttributes(session, accountConfiguration.getUserName(), accountConfiguration.getToken());
            modelAndView.setViewName("redirect:projects");
            return modelAndView;
        } else {
            modelAndView.setViewName("redirect:login");
            return modelAndView;
        }
    }

    private void setSessionAttributes(HttpSession session, String userName, String token){
        session.setAttribute("account", userName);
        session.setAttribute("token", token);
    }
}
