package org.alica.api.services;

import jakarta.el.PropertyNotFoundException;
import org.alica.api.dao.Alumni;
import org.alica.api.dao.Formation;
import org.alica.api.dto.request.RequestFormationDTO;
import org.alica.api.dto.response.ResponseFormationDTO;
import org.alica.api.exceptions.UpdateObjectException;
import org.alica.api.mappers.FormationMapper;
import org.alica.api.repository.AlumniRepository;
import org.alica.api.repository.FormationRepository;
import org.alica.api.security.jwt.UserDetailsImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class FormationService {

    private final FormationRepository formationRepository;
    private final AlumniRepository alumniRepository;

    private static final String FORMATION_NOT_FOUND = "Formation %s Not found !";

    private static final FormationMapper FORMATION_MAPPER = FormationMapper.INSTANCE;
    public FormationService(FormationRepository formationRepository,
                            AlumniRepository alumniRepository) {
        this.formationRepository = formationRepository;
        this.alumniRepository = alumniRepository;
    }

    public static void addHateoasLinks(ResponseFormationDTO formationDTO) {

        Link self = linkTo(methodOn(FormationService.class).findFormationById(formationDTO.getId())).withSelfRel();
        Link alumni = linkTo(methodOn(AlumniService.class).findAlumniById(formationDTO.getAlumniId())).withRel("alumni");
        Link allFromAlumni = linkTo(methodOn(FormationService.class).findFormationByAlumniId(formationDTO.getAlumniId(), null)).withRel("allFromAlumni");

        formationDTO.add(self);
        formationDTO.add(alumni);
        formationDTO.add(allFromAlumni);
    }

    public Page<ResponseFormationDTO> findAll(Pageable page) {

        Page<Formation> formations = formationRepository.findAll(page);

        Page<ResponseFormationDTO> responseFormationDTOS  = formations.map(FORMATION_MAPPER::mapToResponseResponseFormationDTO);
        responseFormationDTOS.forEach(FormationService::addHateoasLinks);

        return responseFormationDTOS;

    }

    public ResponseFormationDTO findFormationById(UUID id) {
        Formation formation = formationRepository.findById(id).orElseThrow(() -> new PropertyNotFoundException(String.format(FORMATION_NOT_FOUND, id)));
        ResponseFormationDTO responseFormationDTO =  FORMATION_MAPPER.mapToResponseResponseFormationDTO(formation);
        addHateoasLinks(responseFormationDTO);
        return responseFormationDTO;
    }

    public ResponseFormationDTO createFormation(RequestFormationDTO requestFormationDTO, UserDetailsImpl user) {

        Alumni alumni = alumniRepository.findById(user.getId()).orElseThrow(() -> new PropertyNotFoundException(String.format(FORMATION_NOT_FOUND, requestFormationDTO.alumniId())));
        Formation formation = FORMATION_MAPPER.mapToFormation(requestFormationDTO, alumni);

        ResponseFormationDTO responseFormationDTO =  FORMATION_MAPPER.mapToResponseResponseFormationDTO(formationRepository.save(formation));
        addHateoasLinks(responseFormationDTO);
        return responseFormationDTO;
    }

    public ResponseFormationDTO updateFormation(RequestFormationDTO requestFormationDTO, UUID id) {

        Formation formation = formationRepository.findById(id).orElseThrow(() -> new UpdateObjectException(String.format(FORMATION_NOT_FOUND, id)));

        if(formation.getAlumni().getId() != requestFormationDTO.alumniId()) throw new UpdateObjectException("You are not allowed to update this formation !");
        if (!alumniRepository.existsById(requestFormationDTO.alumniId())) throw new UpdateObjectException(String.format("Alumni %s Not found !", requestFormationDTO.alumniId()));

        formation.update(requestFormationDTO);

        ResponseFormationDTO responseFormationDTO =  FORMATION_MAPPER.mapToResponseResponseFormationDTO(formationRepository.save(formation));
        addHateoasLinks(responseFormationDTO);
        return responseFormationDTO;
    }

    public void deleteFormation(UUID id,UserDetailsImpl user) {
        Formation formation = formationRepository.findById(id).orElseThrow(()-> new PropertyNotFoundException(String.format(FORMATION_NOT_FOUND, id)));
        if(formation.getAlumni().getId() != user.getId()) throw new UpdateObjectException("You are not allowed to delete this formation !");
        formationRepository.deleteById(id);
    }

    public Page<ResponseFormationDTO> findFormationByAlumniId(UUID id, Pageable page) {
        Alumni alumni = alumniRepository.findById(id).orElseThrow(() -> new PropertyNotFoundException(String.format("Alumni %s Not found !", id)));
        Page<ResponseFormationDTO> responseFormationDTO =  formationRepository.findByAlumni(alumni, page).map(FORMATION_MAPPER::mapToResponseResponseFormationDTO);
        responseFormationDTO.forEach(FormationService::addHateoasLinks);
        return responseFormationDTO;
    }
}
