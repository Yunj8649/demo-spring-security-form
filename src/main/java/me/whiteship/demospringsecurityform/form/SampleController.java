package me.whiteship.demospringsecurityform.form;

import me.whiteship.demospringsecurityform.account.AccountContext;
import me.whiteship.demospringsecurityform.account.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

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
}
