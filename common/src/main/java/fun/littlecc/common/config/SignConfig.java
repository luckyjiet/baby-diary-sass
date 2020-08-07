package fun.littlecc.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author heyi
 * @date 2020-07-02 16:10
 * 签名配置 bean
 */

@Data
@Component
@ConfigurationProperties(prefix = "sign")
public class SignConfig {

    private boolean debug;

    private List<String> allowUrl;

    private List<String> formDataUrl;

    private String appKey;

}
