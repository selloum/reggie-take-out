package com.selloum.filter;

import com.alibaba.fastjson2.JSON;
import com.selloum.common.BaseContext;
import com.selloum.common.Result;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;

/**
 * 检查用户是否完成登录
 */

@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {

    //专门进行路径比较的类,路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //1.获取本次请求的uri
        String uri = request.getRequestURI();

        log.info("本次拦截请求的路径为：{}",uri);
        //将无需处理的url放入string数组
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                //静态资源
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",//移动端发送短信
                "/user/login"//移动端登录
        };


        //2.判断该uri是否要处理
        boolean checkResult = check(urls, uri);
        //3.不处理则放行
        if (checkResult) {
            log.info("本次不需要拦截");
            filterChain.doFilter(request, response);
            return;
        }
        //4.判断登录状态，若登录，放行
        if(request.getSession().getAttribute("employee")!=null){
            log.info("本次已登陆");

            Long employeeId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(employeeId);


            filterChain.doFilter(request, response);
            return;
        }
        //4-2、判断登录状态，如果已登录，则直接放行
        if(request.getSession().getAttribute("user") != null){
            log.info("用户已登录，用户id为：{}",request.getSession().getAttribute("user"));

            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);

            filterChain.doFilter(request,response);
            return;
        }


        //5.若未登录返回未登录结果，通过输出流的方式向客户端页面响应数据
        //将Result对象返回给前端，error中msg必须与前端匹配
        log.info("未登录");
        response.getWriter().write(JSON.toJSONString(Result.error("NOTLOGIN")));

    }

    //路径匹配，检查本次请求是否需要被放行
    public boolean check(String[] urls, String requestURL) {
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURL);
            if(match){
                return true;
            }//匹配成功，放行
        }
        return false;//匹配不成功，需要拦截
    }


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
