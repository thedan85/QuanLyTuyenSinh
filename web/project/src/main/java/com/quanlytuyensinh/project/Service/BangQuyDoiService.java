package com.quanlytuyensinh.project.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.quanlytuyensinh.project.Model.BangQuyDoi;
import com.quanlytuyensinh.project.Repository.BangQuyDoiRepository;

@Service
public class BangQuyDoiService {
    @Autowired
    private BangQuyDoiRepository bangQuyDoiRepository;

    public List<BangQuyDoi> getAllBangQuyDois() {
        return bangQuyDoiRepository.findAll();
    }

    public List<BangQuyDoi> getByPhuongThucAndToHop(String phuongThuc, String toHop) {
        return bangQuyDoiRepository.findByPhuongThucAndToHop(phuongThuc, toHop);
    }

    public List<BangQuyDoi> getByToHop(String toHop) {
        return bangQuyDoiRepository.findByToHop(toHop);
    }
}
