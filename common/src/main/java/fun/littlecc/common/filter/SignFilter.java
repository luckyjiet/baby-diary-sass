package fun.littlecc.common.filter;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import fun.littlecc.common.config.SignConfig;
import fun.littlecc.common.response.REnum;
import fun.littlecc.common.response.RException;
import fun.littlecc.common.support.BodyReaderRequestWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.TreeMap;

/**
 * @author heyi
 * @date 2020-07-01 18:27
 * @desc 全局签名过滤器
 */

@Slf4j
public class SignFilter implements Filter {

    @Autowired
    SignConfig signConfig;
    @Autowired
    @Qualifier("handlerExceptionResolver")
    private HandlerExceptionResolver resolver;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        // 签名 debug 模式, 或者允许访问放行的 url 不进行校验
        if (signConfig.isDebug() || signConfig.getAllowUrl().contains(request.getRequestURI())) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        String requestSign = request.getHeader("request-sign");
        String requestTimestamp = request.getHeader("request-timestamp");
        ServletRequest requestFilter = request;
        boolean flag;
        if (request.getMethod().equals(RequestMethod.GET.name())) {
            flag = this.checkFormData(request, requestSign, requestTimestamp);
            if (!flag) {
                resolver.resolveException(request, response, null, new RException(REnum.ILLEGAL_SIGN));
                return;
            }
        }

        if (request.getMethod().equals(RequestMethod.POST.name())) {
            BodyReaderRequestWrapper requestWrapper = new BodyReaderRequestWrapper(request);
            flag = this.checkJson(requestWrapper, requestSign, requestTimestamp);
            requestFilter = requestWrapper;
            if (!flag) {
                resolver.resolveException(request, response, null, new RException(REnum.ILLEGAL_SIGN));
                return;
            }
        }
        filterChain.doFilter(requestFilter, response);
    }

    /**
     * 检查 formdata 参数
     *
     * @param request          请求
     * @param requestSign      请求签名
     * @param requestTimestamp 请求时间戳
     * @return
     */
    @SuppressWarnings("unchecked")
    private boolean checkFormData(HttpServletRequest request, String requestSign, String requestTimestamp) {
        String params = "";
        TreeMap treeMap = new TreeMap();
        // 去除文件流参数后排序
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String s = parameterNames.nextElement();
            treeMap.put(s, request.getParameter(s));
        }
        if (MapUtil.isNotEmpty(treeMap)) {
            params = JSONUtil.toJsonStr(treeMap);
        }
        return checkSign(params, requestSign, requestTimestamp);
    }

    /**
     * 检查 json 参数
     *
     * @param requestWrapper   请求
     * @param requestSign      请求签名
     * @param requestTimestamp 请求时间戳
     * @return
     */
    private boolean checkJson(BodyReaderRequestWrapper requestWrapper, String requestSign, String requestTimestamp) {
        String bodyString = requestWrapper.getBodyString(requestWrapper);
        String params = "";
        if (StringUtils.isNotEmpty(bodyString)) {
            // key 从小到大排序
            TreeMap treeMap = JSONUtil.toBean(bodyString, TreeMap.class);
            params = JSONUtil.toJsonStr(treeMap);
        } else {
            // 如果为空兼容不传参数的情况
            params = JSONUtil.toJsonStr(JSONUtil.createObj());
        }
        return checkSign(params, requestSign, requestTimestamp);
    }

    /**
     * 参数校验
     *
     * @param params           请求参数
     * @param requestSign      请求签名
     * @param requestTimestamp 请求时间戳
     * @return
     */
    private boolean checkSign(String params, String requestSign, String requestTimestamp) {
        // 将params按key从小到大排序后 转为json 拼接上时间戳（毫秒），拼接上 appKey 进行md5 加密
        String sign = params + requestTimestamp + signConfig.getAppKey();
        String signMD5 = DigestUtil.md5Hex(sign);

        log.info("params: {}, requestTimestamp:{}", params, requestTimestamp);
        log.info("sign:{}", sign);
        log.info("signmd5:{}, reqsign:{}", signMD5, requestSign);

        // 允许时间差当前时间的前后一分钟内，认为是有效请求，在进行签名验证
        Date date = new Date();
        long before = DateUtil.offsetMinute(date, -10).getTime();
        long after = DateUtil.offsetMinute(date, 10).getTime();
        long requestTime = Long.valueOf(requestTimestamp);
        return requestSign.equals(signMD5) && before <= requestTime && after >= requestTime;
    }
}
