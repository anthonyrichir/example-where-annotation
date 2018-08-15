package com.mycompany.myapp.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.mycompany.myapp.domain.AbstractParent;
import com.mycompany.myapp.domain.ChildEntity;
import com.mycompany.myapp.repository.ChildEntityRepository;
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
 * REST controller for managing ChildEntity.
 */
@RestController
@RequestMapping("/api")
public class ChildEntityResource extends AbstractParent {

    private final Logger log = LoggerFactory.getLogger(ChildEntityResource.class);

    private static final String ENTITY_NAME = "childEntity";

    private final ChildEntityRepository childEntityRepository;

    public ChildEntityResource(ChildEntityRepository childEntityRepository) {
        this.childEntityRepository = childEntityRepository;
    }

    /**
     * POST  /child-entities : Create a new childEntity.
     *
     * @param childEntity the childEntity to create
     * @return the ResponseEntity with status 201 (Created) and with body the new childEntity, or with status 400 (Bad Request) if the childEntity has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/child-entities")
    @Timed
    public ResponseEntity<ChildEntity> createChildEntity(@RequestBody ChildEntity childEntity) throws URISyntaxException {
        log.debug("REST request to save ChildEntity : {}", childEntity);
        if (childEntity.getId() != null) {
            throw new BadRequestAlertException("A new childEntity cannot already have an ID", ENTITY_NAME, "idexists");
        }
        ChildEntity result = childEntityRepository.save(childEntity);
        return ResponseEntity.created(new URI("/api/child-entities/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /child-entities : Updates an existing childEntity.
     *
     * @param childEntity the childEntity to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated childEntity,
     * or with status 400 (Bad Request) if the childEntity is not valid,
     * or with status 500 (Internal Server Error) if the childEntity couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/child-entities")
    @Timed
    public ResponseEntity<ChildEntity> updateChildEntity(@RequestBody ChildEntity childEntity) throws URISyntaxException {
        log.debug("REST request to update ChildEntity : {}", childEntity);
        if (childEntity.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        ChildEntity result = childEntityRepository.save(childEntity);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, childEntity.getId().toString()))
            .body(result);
    }

    /**
     * GET  /child-entities : get all the childEntities.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of childEntities in body
     */
    @GetMapping("/child-entities")
    @Timed
    public List<ChildEntity> getAllChildEntities() {
        log.debug("REST request to get all ChildEntities");
        return childEntityRepository.findAll();
    }

    /**
     * GET  /child-entities/:id : get the "id" childEntity.
     *
     * @param id the id of the childEntity to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the childEntity, or with status 404 (Not Found)
     */
    @GetMapping("/child-entities/{id}")
    @Timed
    public ResponseEntity<ChildEntity> getChildEntity(@PathVariable Long id) {
        log.debug("REST request to get ChildEntity : {}", id);
        Optional<ChildEntity> childEntity = childEntityRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(childEntity);
    }

    /**
     * DELETE  /child-entities/:id : delete the "id" childEntity.
     *
     * @param id the id of the childEntity to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/child-entities/{id}")
    @Timed
    public ResponseEntity<Void> deleteChildEntity(@PathVariable Long id) {
        log.debug("REST request to delete ChildEntity : {}", id);

        childEntityRepository.deleteById(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
