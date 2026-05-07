package com.quanlytuyensinh.project.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.quanlytuyensinh.project.Model.NganhToHop;

public interface NganhToHopRepository extends JpaRepository<NganhToHop, Integer> {
    List<NganhToHop> findByMaNganh(String maNganh);
}
