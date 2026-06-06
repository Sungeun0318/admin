package com.beggar.admin.service;

import com.beggar.admin.dto.GoodPriceStoreForm;
import com.beggar.admin.dto.GoodPriceStoreListItem;
import com.beggar.admin.entity.GoodPriceStore;
import com.beggar.admin.repository.GoodPriceStoreRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
public class AdminGoodPriceStoreService {

    private static final int PAGE_SIZE = 10;
    private static final String VISIBLE_ALL = "ALL";
    private static final String VISIBLE_VISIBLE = "VISIBLE";
    private static final String VISIBLE_HIDDEN = "HIDDEN";
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
    private static final NumberFormat MONEY_FORMATTER = NumberFormat.getNumberInstance(Locale.KOREA);

    private final GoodPriceStoreRepository storeRepository;
    private final AdminActionLogService actionLogService;

    public AdminGoodPriceStoreService(
            GoodPriceStoreRepository storeRepository,
            AdminActionLogService actionLogService
    ) {
        this.storeRepository = storeRepository;
        this.actionLogService = actionLogService;
    }

    @Transactional(readOnly = true)
    public Page<GoodPriceStoreListItem> getStores(String keyword, String category, String visible, int page) {
        int safePage = Math.max(page, 0);
        Pageable pageable = PageRequest.of(
                safePage,
                PAGE_SIZE,
                Sort.by(Sort.Direction.DESC, "updatedAt")
        );
        String trimmedKeyword = keyword == null ? "" : keyword.trim();
        String trimmedCategory = category == null ? "" : category.trim();
        String normalizedVisible = normalizeVisible(visible);

        Page<GoodPriceStore> stores = storeRepository.searchStores(
                trimmedKeyword,
                trimmedCategory,
                normalizedVisible,
                pageable
        );

        return new PageImpl<>(
                stores.getContent().stream().map(this::toListItem).toList(),
                pageable,
                stores.getTotalElements()
        );
    }

    @Transactional(readOnly = true)
    public GoodPriceStoreForm getForm(Long id) {
        GoodPriceStore store = findStore(id);
        GoodPriceStoreForm form = new GoodPriceStoreForm();
        form.setStoreId(store.getStoreId());
        form.setName(store.getName());
        form.setCategory(store.getCategory());
        form.setItemName(store.getItemName());
        form.setPrice(store.getPrice());
        form.setAddress(store.getAddress());
        form.setLat(store.getLat());
        form.setLng(store.getLng());
        form.setPhoneNumber(store.getPhoneNumber());
        form.setVisible(store.getVisible());
        return form;
    }

    @Transactional
    public void createStore(GoodPriceStoreForm form) {
        validate(form);
        GoodPriceStore store = new GoodPriceStore(
                clean(form.getStoreId()),
                clean(form.getName()),
                clean(form.getCategory()),
                clean(form.getItemName()),
                form.getPrice(),
                clean(form.getAddress()),
                form.getLat(),
                form.getLng(),
                clean(form.getPhoneNumber()),
                form.getVisible()
        );
        GoodPriceStore savedStore = storeRepository.save(store);
        actionLogService.record("CREATE", "GOOD_PRICE_STORE", savedStore.getId(), "착한가격업소를 추가했어: " + savedStore.getName());
    }

    @Transactional
    public void updateStore(Long id, GoodPriceStoreForm form) {
        validate(form);
        GoodPriceStore store = findStore(id);
        store.update(
                clean(form.getStoreId()),
                clean(form.getName()),
                clean(form.getCategory()),
                clean(form.getItemName()),
                form.getPrice(),
                clean(form.getAddress()),
                form.getLat(),
                form.getLng(),
                clean(form.getPhoneNumber()),
                form.getVisible()
        );
        actionLogService.record("UPDATE", "GOOD_PRICE_STORE", id, "착한가격업소를 수정했어: " + store.getName());
    }

    @Transactional
    public void toggleVisible(Long id) {
        GoodPriceStore store = findStore(id);
        store.toggleVisible();
        actionLogService.record("TOGGLE_VISIBLE", "GOOD_PRICE_STORE", id, "착한가격업소 노출 여부를 변경했어: " + store.getName());
    }

    @Transactional
    public void deleteStore(Long id) {
        GoodPriceStore store = findStore(id);
        storeRepository.deleteById(id);
        actionLogService.record("DELETE", "GOOD_PRICE_STORE", id, "착한가격업소를 삭제했어: " + store.getName());
    }

    private GoodPriceStore findStore(Long id) {
        return storeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("착한가격업소를 찾을 수 없어."));
    }

    private void validate(GoodPriceStoreForm form) {
        if (!StringUtils.hasText(form.getName())) {
            throw new IllegalArgumentException("업소명은 필수야.");
        }
        if (!StringUtils.hasText(form.getAddress())) {
            throw new IllegalArgumentException("주소는 필수야.");
        }
        if (form.getPrice() != null && form.getPrice() < 0) {
            throw new IllegalArgumentException("가격은 0원 이상이어야 해.");
        }
    }

    private GoodPriceStoreListItem toListItem(GoodPriceStore store) {
        boolean visible = Boolean.TRUE.equals(store.getVisible());
        return new GoodPriceStoreListItem(
                store.getId(),
                blankToDash(store.getName()),
                blankToDash(store.getCategory()),
                blankToDash(store.getItemName()),
                money(store.getPrice()),
                blankToDash(store.getAddress()),
                visible ? "노출" : "비노출",
                visible ? "status-active" : "status-muted",
                formatDateTime(store.getUpdatedAt())
        );
    }

    private String normalizeVisible(String visible) {
        if (VISIBLE_VISIBLE.equals(visible) || VISIBLE_HIDDEN.equals(visible)) {
            return visible;
        }
        return VISIBLE_ALL;
    }

    private String clean(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String blankToDash(String value) {
        if (value == null || value.isBlank()) {
            return "-";
        }
        return value;
    }

    private String money(Integer value) {
        if (value == null) {
            return "-";
        }
        return MONEY_FORMATTER.format(value) + "원";
    }

    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "-";
        }
        return dateTime.format(DATE_TIME_FORMATTER);
    }
}
