package com.quanlytuyensinh.project.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.quanlytuyensinh.project.Model.NganhToHop;
import com.quanlytuyensinh.project.Repository.NganhToHopRepository;

@Service
public class NganhToHopService {
    @Autowired
    private NganhToHopRepository nganhToHopRepository;

    public List<NganhToHop> getAllNganhToHops() {
        return nganhToHopRepository.findAll();
    }

    public List<NganhToHop> getByMaNganh(String maNganh) {
        return nganhToHopRepository.findByMaNganh(maNganh);
    }
}
