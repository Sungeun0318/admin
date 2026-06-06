package com.beggar.admin.service;

import com.beggar.admin.dto.AdminAccountForm;
import com.beggar.admin.dto.AdminAccountListItem;
import com.beggar.admin.entity.AdminAccount;
import com.beggar.admin.repository.AdminAccountRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class AdminAccountService {

    private static final int PAGE_SIZE = 10;
    private static final String STATUS_ALL = "ALL";
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");

    private final AdminAccountRepository adminAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminActionLogService actionLogService;

    public AdminAccountService(
            AdminAccountRepository adminAccountRepository,
            PasswordEncoder passwordEncoder,
            AdminActionLogService actionLogService
    ) {
        this.adminAccountRepository = adminAccountRepository;
        this.passwordEncoder = passwordEncoder;
        this.actionLogService = actionLogService;
    }

    @Transactional(readOnly = true)
    public Page<AdminAccountListItem> getAccounts(String keyword, String status, int page) {
        int safePage = Math.max(page, 0);
        Pageable pageable = PageRequest.of(
                safePage,
                PAGE_SIZE,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
        String trimmed = keyword == null ? "" : keyword.trim();
        String normalizedStatus = normalizeStatus(status);
        Page<AdminAccount> accounts = adminAccountRepository.searchAccounts(trimmed, normalizedStatus, pageable);

        return new PageImpl<>(
                accounts.getContent().stream().map(this::toListItem).toList(),
                pageable,
                accounts.getTotalElements()
        );
    }

    @Transactional(readOnly = true)
    public AdminAccountForm getForm(Long adminId) {
        AdminAccount account = findAccount(adminId);
        AdminAccountForm form = new AdminAccountForm();
        form.setUsername(account.getUsername());
        form.setDisplayName(account.getDisplayName());
        form.setRole(account.getRole());
        form.setStatus(account.getStatus());
        return form;
    }

    @Transactional
    public void createAccount(AdminAccountForm form) {
        validateRequired(form, true);
        String username = clean(form.getUsername());
        if (adminAccountRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("이미 사용 중인 관리자 아이디야.");
        }

        AdminAccount account = new AdminAccount(
                username,
                passwordEncoder.encode(form.getPassword()),
                clean(form.getDisplayName()),
                form.getRole(),
                form.getStatus()
        );
        AdminAccount savedAccount = adminAccountRepository.save(account);
        actionLogService.record("CREATE", "ADMIN_ACCOUNT", savedAccount.getAdminId(), "관리자 계정을 생성했어: " + savedAccount.getUsername());
    }

    @Transactional
    public void updateAccount(Long adminId, AdminAccountForm form) {
        validateRequired(form, false);
        AdminAccount account = findAccount(adminId);
        account.updateProfile(clean(form.getDisplayName()), form.getRole(), form.getStatus());
        if (StringUtils.hasText(form.getPassword())) {
            account.updatePassword(passwordEncoder.encode(form.getPassword()));
        }
        actionLogService.record("UPDATE", "ADMIN_ACCOUNT", adminId, "관리자 계정을 수정했어: " + account.getUsername());
    }

    @Transactional
    public void disableAccount(Long adminId) {
        AdminAccount account = findAccount(adminId);
        account.disable();
        actionLogService.record("DISABLE", "ADMIN_ACCOUNT", adminId, "관리자 계정을 비활성화했어: " + account.getUsername());
    }

    private AdminAccount findAccount(Long adminId) {
        return adminAccountRepository.findById(adminId)
                .orElseThrow(() -> new IllegalArgumentException("관리자 계정을 찾을 수 없어."));
    }

    private void validateRequired(AdminAccountForm form, boolean passwordRequired) {
        if (!StringUtils.hasText(form.getUsername())) {
            throw new IllegalArgumentException("관리자 아이디는 필수야.");
        }
        if (!StringUtils.hasText(form.getDisplayName())) {
            throw new IllegalArgumentException("표시 이름은 필수야.");
        }
        if (passwordRequired && !StringUtils.hasText(form.getPassword())) {
            throw new IllegalArgumentException("비밀번호는 필수야.");
        }
        if (StringUtils.hasText(form.getPassword()) && form.getPassword().length() < 6) {
            throw new IllegalArgumentException("비밀번호는 6자 이상이어야 해.");
        }
    }

    private AdminAccountListItem toListItem(AdminAccount account) {
        boolean active = account.getStatus() == AdminAccount.Status.ACTIVE;
        return new AdminAccountListItem(
                account.getAdminId(),
                account.getUsername(),
                account.getDisplayName(),
                roleLabel(account.getRole()),
                active ? "활성" : "비활성",
                active ? "status-active" : "status-muted",
                formatDateTime(account.getCreatedAt())
        );
    }

    private String roleLabel(AdminAccount.Role role) {
        if (role == AdminAccount.Role.SUPER_ADMIN) {
            return "최고 관리자";
        }
        if (role == AdminAccount.Role.OPERATOR) {
            return "운영자";
        }
        return "조회 전용";
    }

    private String normalizeStatus(String status) {
        if ("ACTIVE".equals(status) || "DISABLED".equals(status)) {
            return status;
        }
        return STATUS_ALL;
    }

    private String clean(String value) {
        return value == null ? null : value.trim();
    }

    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "-";
        }
        return dateTime.format(DATE_TIME_FORMATTER);
    }
}
