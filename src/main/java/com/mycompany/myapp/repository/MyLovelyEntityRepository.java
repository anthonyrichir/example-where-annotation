package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.MyLovelyEntity;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the MyLovelyEntity entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MyLovelyEntityRepository extends JpaRepository<MyLovelyEntity, Long> {

}
