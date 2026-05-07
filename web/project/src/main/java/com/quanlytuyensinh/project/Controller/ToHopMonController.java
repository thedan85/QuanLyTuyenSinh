package com.quanlytuyensinh.project.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.quanlytuyensinh.project.Model.ToHopMon;
import com.quanlytuyensinh.project.Service.ToHopMonService;

@RestController
@RequestMapping("/api/to-hop")
public class ToHopMonController {
    @Autowired
    private ToHopMonService toHopMonService;

    @GetMapping
    public List<ToHopMon> getAllToHopMons() {
        return toHopMonService.getAllToHopMons();
    }

    @GetMapping("/ma/{maToHop}")
    public ResponseEntity<ToHopMon> getToHopMonByMaToHop(@PathVariable String maToHop) {
        ToHopMon toHopMon = toHopMonService.getToHopMonByMaToHop(maToHop);
        if (toHopMon == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toHopMon);
    }
}
