package com.quanlytuyensinh.project.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.quanlytuyensinh.project.Model.BangQuyDoi;

public interface BangQuyDoiRepository extends JpaRepository<BangQuyDoi, Integer> {
    List<BangQuyDoi> findByPhuongThucAndToHop(String phuongThuc, String toHop);
    List<BangQuyDoi> findByToHop(String toHop);
}
