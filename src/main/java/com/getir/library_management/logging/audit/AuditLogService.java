package com.getir.library_management.logging.audit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class AuditLogService {

    // Logs an audit message including the user email, action performed, detail, and timestamp
    public void logAction(String userEmail, String action, String detail) {
        log.info("AUDIT | user={} | action={} | detail={} | at={}", userEmail, action, detail, LocalDateTime.now());
    }

    // Retrieves the currently authenticated user's email from the security context
    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (authentication != null && authentication.isAuthenticated())
                ? authentication.getName()
                : "anonymous";
    }

}
