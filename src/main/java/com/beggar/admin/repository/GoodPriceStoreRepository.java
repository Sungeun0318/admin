package com.beggar.admin.repository;

import com.beggar.admin.entity.GoodPriceStore;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GoodPriceStoreRepository extends JpaRepository<GoodPriceStore, Long> {

    @Query("""
            SELECT s
              FROM GoodPriceStore s
             WHERE (:visible = 'ALL'
                    OR (:visible = 'VISIBLE' AND s.visible = true)
                    OR (:visible = 'HIDDEN' AND s.visible = false))
               AND (:category = '' OR LOWER(COALESCE(s.category, '')) LIKE LOWER(CONCAT('%', :category, '%')))
               AND (
                    :keyword = ''
                    OR LOWER(COALESCE(s.name, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(COALESCE(s.itemName, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(COALESCE(s.address, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))
               )
            """)
    Page<GoodPriceStore> searchStores(
            @Param("keyword") String keyword,
            @Param("category") String category,
            @Param("visible") String visible,
            Pageable pageable
    );
}
