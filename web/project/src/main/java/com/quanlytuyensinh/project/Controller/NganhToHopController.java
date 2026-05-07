package com.quanlytuyensinh.project.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.quanlytuyensinh.project.Model.NganhToHop;
import com.quanlytuyensinh.project.Service.NganhToHopService;

@RestController
@RequestMapping("/api/nganh-tohop")
public class NganhToHopController {
    @Autowired
    private NganhToHopService nganhToHopService;

    @GetMapping
    public List<NganhToHop> getAllNganhToHops() {
        return nganhToHopService.getAllNganhToHops();
    }

    @GetMapping("/ma-nganh/{maNganh}")
    public List<NganhToHop> getByMaNganh(@PathVariable String maNganh) {
        return nganhToHopService.getByMaNganh(maNganh);
    }
}
