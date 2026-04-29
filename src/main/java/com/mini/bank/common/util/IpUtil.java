package com.mini.bank.common.util;

import jakarta.servlet.http.HttpServletRequest;

public class IpUtil {

    public static String getClientIp(HttpServletRequest request) {

        String ip = request.getHeader("x-forwarded-for");
        System.out.println("IP Method: " + ip);

        if (ip != null && !ip.isEmpty()) {
            return ip.split(",")[0];
        }

        return request.getRemoteAddr();
    }
}
