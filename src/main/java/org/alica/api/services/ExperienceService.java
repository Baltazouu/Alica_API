package org.alica.api.services;

import jakarta.el.PropertyNotFoundException;
import org.alica.api.dao.Alumni;
import org.alica.api.dao.Experience;
import org.alica.api.dto.request.RequestExperienceDTO;
import org.alica.api.dto.response.ResponseExperienceDTO;
import org.alica.api.mappers.ExperienceMapper;
import org.alica.api.repository.AlumniRepository;
import org.alica.api.repository.ExperienceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class ExperienceService {

    private static final ExperienceMapper experienceMapper = ExperienceMapper.INSTANCE;

    private final ExperienceRepository experienceRepository;

    private final AlumniRepository alumniRepository;

    private final Logger logger = Logger.getLogger(ExperienceService.class.getName());


    public ExperienceService(ExperienceRepository experienceRepository,
                             AlumniRepository alumniRepository) {
        this.experienceRepository = experienceRepository;
        this.alumniRepository = alumniRepository;
    }


    public Page<ResponseExperienceDTO> findAll(Pageable p, Optional<String> title) {

        return title.map(s -> experienceRepository.findByTitleContaining(s, p).map(experienceMapper::mapToResponseExperienceDTO))
                .orElseGet(() -> experienceRepository.findAll(p).map(experienceMapper::mapToResponseExperienceDTO));

    }

    public ResponseExperienceDTO findById(UUID id) {
        return experienceRepository.findById(id).map(experienceMapper::mapToResponseExperienceDTO).orElseThrow(() -> new PropertyNotFoundException("Experience not found"));
    }

    public void deleteById(UUID id,UUID alumniId) {

        Experience experience = experienceRepository.findById(id).orElseThrow(() -> new PropertyNotFoundException("Experience not found"));

        logger.info("Experience alumni id: "+experience.getAlumni().getId());
        logger.info("Alumni id: "+alumniId);

        if(!alumniId.equals(experience.getAlumni().getId())){
            throw new AccessDeniedException("Access denied for this resource");
        }
        experienceRepository.deleteById(id);
    }


    public ResponseExperienceDTO createExperience(RequestExperienceDTO experienceDTO, UUID userId) {

        logger.info(String.format("Experience current %s",experienceDTO.current()));

        Alumni alumni = alumniRepository.findById(userId).orElseThrow(() -> new PropertyNotFoundException("Alumni not found"));


        return experienceMapper.mapToResponseExperienceDTO(experienceRepository.save(experienceMapper.mapToExperience(experienceDTO,alumni)));
    }

    public void update(RequestExperienceDTO experienceDTO,UUID alumniId) {

        Alumni alumni = alumniRepository.findById(alumniId).orElseThrow(() -> new PropertyNotFoundException("Alumni not found"));

        experienceRepository.save(experienceMapper.mapToExperience(experienceDTO,alumni));
    }

    public List<ResponseExperienceDTO> findByAlumniId(UUID id) {

        List<Experience> experiences = experienceRepository.findByAlumniId(id);

        logger.info(String.format("Experiences %s",experiences));

        return experienceRepository.findByAlumniId(id).stream().map(experienceMapper::mapToResponseExperienceDTO).toList();
    }


}
