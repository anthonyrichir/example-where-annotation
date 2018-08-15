package com.mycompany.myapp.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.mycompany.myapp.domain.MyLovelyEntity;
import com.mycompany.myapp.repository.MyLovelyEntityRepository;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import com.mycompany.myapp.web.rest.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing MyLovelyEntity.
 */
@RestController
@RequestMapping("/api")
public class MyLovelyEntityResource {

    private final Logger log = LoggerFactory.getLogger(MyLovelyEntityResource.class);

    private static final String ENTITY_NAME = "myLovelyEntity";

    private final MyLovelyEntityRepository myLovelyEntityRepository;

    public MyLovelyEntityResource(MyLovelyEntityRepository myLovelyEntityRepository) {
        this.myLovelyEntityRepository = myLovelyEntityRepository;
    }

    /**
     * POST  /my-lovely-entities : Create a new myLovelyEntity.
     *
     * @param myLovelyEntity the myLovelyEntity to create
     * @return the ResponseEntity with status 201 (Created) and with body the new myLovelyEntity, or with status 400 (Bad Request) if the myLovelyEntity has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/my-lovely-entities")
    @Timed
    public ResponseEntity<MyLovelyEntity> createMyLovelyEntity(@RequestBody MyLovelyEntity myLovelyEntity) throws URISyntaxException {
        log.debug("REST request to save MyLovelyEntity : {}", myLovelyEntity);
        if (myLovelyEntity.getId() != null) {
            throw new BadRequestAlertException("A new myLovelyEntity cannot already have an ID", ENTITY_NAME, "idexists");
        }
        MyLovelyEntity result = myLovelyEntityRepository.save(myLovelyEntity);
        return ResponseEntity.created(new URI("/api/my-lovely-entities/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /my-lovely-entities : Updates an existing myLovelyEntity.
     *
     * @param myLovelyEntity the myLovelyEntity to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated myLovelyEntity,
     * or with status 400 (Bad Request) if the myLovelyEntity is not valid,
     * or with status 500 (Internal Server Error) if the myLovelyEntity couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/my-lovely-entities")
    @Timed
    public ResponseEntity<MyLovelyEntity> updateMyLovelyEntity(@RequestBody MyLovelyEntity myLovelyEntity) throws URISyntaxException {
        log.debug("REST request to update MyLovelyEntity : {}", myLovelyEntity);
        if (myLovelyEntity.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        MyLovelyEntity result = myLovelyEntityRepository.save(myLovelyEntity);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, myLovelyEntity.getId().toString()))
            .body(result);
    }

    /**
     * GET  /my-lovely-entities : get all the myLovelyEntities.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of myLovelyEntities in body
     */
    @GetMapping("/my-lovely-entities")
    @Timed
    public List<MyLovelyEntity> getAllMyLovelyEntities() {
        log.debug("REST request to get all MyLovelyEntities");
        return myLovelyEntityRepository.findAll();
    }

    /**
     * GET  /my-lovely-entities/:id : get the "id" myLovelyEntity.
     *
     * @param id the id of the myLovelyEntity to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the myLovelyEntity, or with status 404 (Not Found)
     */
    @GetMapping("/my-lovely-entities/{id}")
    @Timed
    public ResponseEntity<MyLovelyEntity> getMyLovelyEntity(@PathVariable Long id) {
        log.debug("REST request to get MyLovelyEntity : {}", id);
        Optional<MyLovelyEntity> myLovelyEntity = myLovelyEntityRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(myLovelyEntity);
    }

    /**
     * DELETE  /my-lovely-entities/:id : delete the "id" myLovelyEntity.
     *
     * @param id the id of the myLovelyEntity to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/my-lovely-entities/{id}")
    @Timed
    public ResponseEntity<Void> deleteMyLovelyEntity(@PathVariable Long id) {
        log.debug("REST request to delete MyLovelyEntity : {}", id);

        myLovelyEntityRepository.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
