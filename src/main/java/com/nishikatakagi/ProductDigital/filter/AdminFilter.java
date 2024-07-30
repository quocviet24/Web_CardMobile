package com.nishikatakagi.ProductDigital.filter;

import java.io.IOException;

import com.nishikatakagi.ProductDigital.dto.UserSessionDto;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class AdminFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse respond, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse res = (HttpServletResponse) respond;
        HttpServletRequest req = (HttpServletRequest) request;
        HttpSession session = req.getSession();
        UserSessionDto user = (UserSessionDto) session.getAttribute("user_sess");
        if (user == null) {
            res.sendRedirect("/login");
        } else {
            if (user.getRoleId()==1) {
                chain.doFilter(request, respond);
            } else {
                res.sendRedirect("/");
            }
        }
    }

}
