package ro.doan.auto.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AutoController {

    private List<String> autos = Arrays.asList("Dacia", "Trabant");

    @GetMapping("/auto")
    @PreAuthorize("#oauth2.hasScope('auto-read')")
    public List<String> getAutos() {
        return autos;
    }

    @PostMapping("/auto")
    @PreAuthorize("#oauth2.hasScope('auto-write')")
    public void updateAutos(@RequestBody List<String> autos) {
        this.autos = autos;
    }
}
