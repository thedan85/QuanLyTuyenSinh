package com.quanlytuyensinh.project.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.quanlytuyensinh.project.Model.BangQuyDoi;
import com.quanlytuyensinh.project.Service.BangQuyDoiService;

@RestController
@RequestMapping("/api/bang-quy-doi")
public class BangQuyDoiController {
    @Autowired
    private BangQuyDoiService bangQuyDoiService;

    @GetMapping
    public List<BangQuyDoi> getAllBangQuyDois() {
        return bangQuyDoiService.getAllBangQuyDois();
    }

    @GetMapping("/phuong-thuc/{phuongThuc}/to-hop/{toHop}")
    public List<BangQuyDoi> getByPhuongThucAndToHop(
            @PathVariable String phuongThuc,
            @PathVariable String toHop) {
        return bangQuyDoiService.getByPhuongThucAndToHop(phuongThuc, toHop);
    }

    @GetMapping("/to-hop/{toHop}")
    public List<BangQuyDoi> getByToHop(@PathVariable String toHop) {
        return bangQuyDoiService.getByToHop(toHop);
    }
}
