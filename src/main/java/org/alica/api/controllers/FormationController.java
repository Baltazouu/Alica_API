package org.alica.api.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.alica.api.dto.request.RequestFormationDTO;
import org.alica.api.dto.response.ResponseFormationDTO;
import org.alica.api.security.jwt.JWTUtils;
import org.alica.api.security.jwt.UserDetailsImpl;
import org.alica.api.services.FormationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/formations")
public class FormationController {

    private final FormationService formationService;

    public FormationController(FormationService formationService) {
        this.formationService = formationService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Page<ResponseFormationDTO>> findAll(@PageableDefault Pageable page) {
        return new ResponseEntity<>(this.formationService.findAll(page), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ResponseFormationDTO> findFormationById(@PathVariable UUID id) {
        return new ResponseEntity<>(this.formationService.findFormationById(id), HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('USER', 'MODERATOR', 'ADMIN')")
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ResponseFormationDTO> createFormation(@Valid @RequestBody RequestFormationDTO formation,@AuthenticationPrincipal UserDetailsImpl user) {

        return new ResponseEntity<>(this.formationService.createFormation(formation, user), HttpStatus.CREATED);
    }

    @PreAuthorize("isAuthenticated() ")
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ResponseFormationDTO> updateFormation(@Valid @RequestBody RequestFormationDTO formation, @PathVariable UUID id, @AuthenticationPrincipal UserDetailsImpl user) {
        return new ResponseEntity<>(this.formationService.updateFormation(formation, id,user.getId()), HttpStatus.OK);
    }



    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFormation(HttpServletRequest request,@PathVariable UUID id) {
        this.formationService.deleteFormation(id,JWTUtils.getUserAuthenticate(request));
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping(value= "/alumni", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<ResponseFormationDTO> findFormationByAlumniId(@AuthenticationPrincipal UserDetailsImpl user) {
        return this.formationService.findFormationByAlumniId(user.getId());
    }

}
