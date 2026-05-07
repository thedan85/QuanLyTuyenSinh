package com.quanlytuyensinh.project.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.quanlytuyensinh.project.Model.ToHopMon;
import com.quanlytuyensinh.project.Repository.ToHopMonRepository;

@Service
public class ToHopMonService {
    @Autowired
    private ToHopMonRepository toHopMonRepository;

    public List<ToHopMon> getAllToHopMons() {
        return toHopMonRepository.findAll();
    }

    public ToHopMon getToHopMonByMaToHop(String maToHop) {
        return toHopMonRepository.findByMaToHop(maToHop);
    }
}
