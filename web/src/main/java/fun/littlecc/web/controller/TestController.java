package fun.littlecc.web.controller;

import fun.littlecc.common.response.R;
import fun.littlecc.domain.dto.TestDTO;
import fun.littlecc.domain.dto.TestListDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author heyi
 * @date 2020-07-01 16:20
 */
@Slf4j
@RestController
public class TestController {

    @RequestMapping("/test")
    public void test() {
    }

    @PostMapping("/post")
    public R testPost(@RequestBody TestDTO a) {
        log.info("post 入参：{}", a);
        return R.success(a);
    }
}
