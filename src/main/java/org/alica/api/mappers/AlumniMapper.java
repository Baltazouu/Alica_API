package org.alica.api.mappers;

import org.alica.api.dao.Alumni;
import org.alica.api.dao.Role;
import org.alica.api.dto.request.RequestAlumniDTO;
import org.alica.api.dto.request.SignupRequestDTO;
import org.alica.api.dto.response.ResponseAlumniDTO;
import org.alica.api.dto.response.ResponseAlumniRestrictedDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AlumniMapper {
    AlumniMapper INSTANCE = Mappers.getMapper(AlumniMapper.class);

    @Mapping(target = "id", ignore = true) // ignore bc generated with db
    @Mapping(target = "formations", ignore = true)
    @Mapping(target = "events", ignore = true)
    @Mapping(target = "articles", ignore = true)
    @Mapping(target = "offers", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "eventOrganized", ignore = true)
    Alumni mapToAlumni(RequestAlumniDTO alumniDTO, Role role);

    @Mapping(target = "id", source = "id")
    ResponseAlumniDTO mapResponseAlumniDTO(Alumni alumni);


    @Mapping(target = "id", ignore = true) // ignore bc generated with db
    @Mapping(target = "formations", ignore = true)
    @Mapping(target = "events", ignore = true)
    @Mapping(target = "articles", ignore = true)
    @Mapping(target = "offers", ignore = true)
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    Alumni mapToAlumni(SignupRequestDTO signupRequestDTO);


    ResponseAlumniRestrictedDTO mapResponseAlumniRestricted(Alumni alumni);

}
