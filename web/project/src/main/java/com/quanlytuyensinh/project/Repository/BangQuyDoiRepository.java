package com.quanlytuyensinh.project.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.quanlytuyensinh.project.Model.BangQuyDoi;

public interface BangQuyDoiRepository extends JpaRepository<BangQuyDoi, Integer> {
    @Query("SELECT b FROM BangQuyDoi b WHERE b.phuongThuc = :phuongThuc "
            + "AND (b.toHop = :toHop OR b.toHop = '' OR b.toHop IS NULL)")
    List<BangQuyDoi> findByPhuongThucAndToHop(
            @Param("phuongThuc") String phuongThuc,
            @Param("toHop") String toHop);
    List<BangQuyDoi> findByToHop(String toHop);
}
