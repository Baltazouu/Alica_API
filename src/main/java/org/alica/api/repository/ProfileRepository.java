package org.alica.api.repository;

import org.alica.api.Dao.Alumni;
import org.alica.api.Dao.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, UUID> {


    Optional<Profile> findByEmail(String email);

    Optional<Profile> findByAlumni(Alumni alumni);

}
