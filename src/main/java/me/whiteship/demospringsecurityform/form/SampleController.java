package me.whiteship.demospringsecurityform.form;

import me.whiteship.demospringsecurityform.account.AccountContext;
import me.whiteship.demospringsecurityform.account.AccountRepository;
import me.whiteship.demospringsecurityform.book.BookRepository;
import me.whiteship.demospringsecurityform.common.SecurityLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.concurrent.Callable;

/**
 * 스프링시큐리티를 깔기전 문제점 1. 인증할 방법이 없다 2. 인증을 못하니 누군지 알 수가 없다
 * 스크링시큐리티 아이디 : user 비밀번호 : 콘솔에 있는 비밀번호
 * principal 로 권한 확인 가능
 * 로그에 보안 관련된 것을 찍으면 안된다. > 로그 탈취당할 경우 심각한 보안문제 발생
 *
 */
@Controller
public class SampleController {

    @Autowired
    SampleService sampleService;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    BookRepository bookRepository;

    @GetMapping("/")
    public String index(Model model, Principal principal) {
        if (principal == null) {
            model.addAttribute("message", "Hello Spring Security");
        } else {
            model.addAttribute("message", "Hello " + principal.getName());
        }

        // view의 이름을 리턴하면 스프링부트가 제공하는 기본설정에 따라서
        // 이름을 가지고 패스를 조합해서 템플릿 아래있는 파일에 담아준다
        return "index";
    }

    @GetMapping("/info")
    public String info(Model model) {
        model.addAttribute("message", "Info");
        return "info";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        model.addAttribute("message", "Hello " + principal.getName());
//        AccountContext.setAccount(accountRepository.findByUsername(principal.getName()));
        sampleService.dashboard();
        return "dashboard";
    }

    @GetMapping("/admin")
    public String admin(Model model, Principal principal) {
        model.addAttribute("message", "Hello, Admin " + principal.getName());
        return "admin";
    }

    @GetMapping("/user")
    public String user(Model model, Principal principal) {
        model.addAttribute("message", "Hello, User " + principal.getName());
        model.addAttribute("books", bookRepository.findCurrentUserBooks());
        return "user";
    }

    // webAsyncManagerIntegrationFilter
    // 스프링 mvc async 핸드러를 지원하는 기능
    // 스프링 mvc async는 다른 스레드를 사용하지만 그 스레드에서도 동일한 시큐리티 컨텍스트를 사용할 수 있도록 지원해준다.
    // 원래는 스레드 로컬을 사용하기 때문에 자신과 동일한
    // callable 로 리턴하면 콜러블안에서 처리하는 일들을 처리하기 전에 이미 응답을 내보낸다
    // 이 리퀘스트를 처리하고 잇던 스레드를 반환하고 콜러블 안에서 하는일이 완료가 됐을 때 쯤 그 응답을 보낸다.
    // 별도 스레드를 사용하지만 같은 principal를 사용할 수 있도록 해준다.
    // 실행 단축키 : control + R
    // 람다 자동 변경 : option + enter
    @GetMapping("/async-handler")
    @ResponseBody
    public Callable<String> asyncHandler() {
        SecurityLogger.log("MVC");

//        return new Callable<String>() {
//            @Override
//            public String call() throws Exception {
//                SecurityLogger.log("Callable");
//                return "Async Handler";
//            }
//        };
        return () -> {
            SecurityLogger.log("Callable");
            return "Async Handler";
        };
    }

    @GetMapping("/async-service")
    @ResponseBody
    public String asyncService() {
        SecurityLogger.log("MVC, before async service");
        sampleService.asyncService();
        SecurityLogger.log("MVC, after async service");
        sampleService.asyncService();
        return "Async Service";
    }

}
