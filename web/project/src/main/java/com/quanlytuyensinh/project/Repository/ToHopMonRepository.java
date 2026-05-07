package com.quanlytuyensinh.project.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.quanlytuyensinh.project.Model.ToHopMon;

public interface ToHopMonRepository extends JpaRepository<ToHopMon, Integer> {
    ToHopMon findByMaToHop(String maToHop);
}
