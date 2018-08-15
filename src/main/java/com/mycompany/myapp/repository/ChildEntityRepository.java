package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.ChildEntity;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the ChildEntity entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ChildEntityRepository extends JpaRepository<ChildEntity, Long> {

}
