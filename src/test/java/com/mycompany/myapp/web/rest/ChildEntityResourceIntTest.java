package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.ExampleWhereAnnotationApp;

import com.mycompany.myapp.domain.ChildEntity;
import com.mycompany.myapp.repository.ChildEntityRepository;
import com.mycompany.myapp.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;


import static com.mycompany.myapp.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the ChildEntityResource REST controller.
 *
 * @see ChildEntityResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ExampleWhereAnnotationApp.class)
public class ChildEntityResourceIntTest {

    private static final String DEFAULT_SOME_FIELD = "AAAAAAAAAA";
    private static final String UPDATED_SOME_FIELD = "BBBBBBBBBB";

    private static final Boolean DEFAULT_DELETED = false;
    private static final Boolean UPDATED_DELETED = true;

    @Autowired
    private ChildEntityRepository childEntityRepository;


    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restChildEntityMockMvc;

    private ChildEntity childEntity;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ChildEntityResource childEntityResource = new ChildEntityResource(childEntityRepository);
        this.restChildEntityMockMvc = MockMvcBuilders.standaloneSetup(childEntityResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ChildEntity createEntity(EntityManager em) {
        ChildEntity childEntity = new ChildEntity()
            .someField(DEFAULT_SOME_FIELD)
            .deleted(DEFAULT_DELETED);
        return childEntity;
    }

    @Before
    public void initTest() {
        childEntity = createEntity(em);
    }

    @Test
    @Transactional
    public void createChildEntity() throws Exception {
        int databaseSizeBeforeCreate = childEntityRepository.findAll().size();

        // Create the ChildEntity
        restChildEntityMockMvc.perform(post("/api/child-entities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(childEntity)))
            .andExpect(status().isCreated());

        // Validate the ChildEntity in the database
        List<ChildEntity> childEntityList = childEntityRepository.findAll();
        assertThat(childEntityList).hasSize(databaseSizeBeforeCreate + 1);
        ChildEntity testChildEntity = childEntityList.get(childEntityList.size() - 1);
        assertThat(testChildEntity.getSomeField()).isEqualTo(DEFAULT_SOME_FIELD);
        assertThat(testChildEntity.isDeleted()).isEqualTo(DEFAULT_DELETED);
    }

    @Test
    @Transactional
    public void createChildEntityWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = childEntityRepository.findAll().size();

        // Create the ChildEntity with an existing ID
        childEntity.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restChildEntityMockMvc.perform(post("/api/child-entities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(childEntity)))
            .andExpect(status().isBadRequest());

        // Validate the ChildEntity in the database
        List<ChildEntity> childEntityList = childEntityRepository.findAll();
        assertThat(childEntityList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllChildEntities() throws Exception {
        // Initialize the database
        childEntityRepository.saveAndFlush(childEntity);

        // Get all the childEntityList
        restChildEntityMockMvc.perform(get("/api/child-entities?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(childEntity.getId().intValue())))
            .andExpect(jsonPath("$.[*].someField").value(hasItem(DEFAULT_SOME_FIELD.toString())))
            .andExpect(jsonPath("$.[*].deleted").value(hasItem(DEFAULT_DELETED.booleanValue())));
    }
    

    @Test
    @Transactional
    public void getChildEntity() throws Exception {
        // Initialize the database
        childEntityRepository.saveAndFlush(childEntity);

        // Get the childEntity
        restChildEntityMockMvc.perform(get("/api/child-entities/{id}", childEntity.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(childEntity.getId().intValue()))
            .andExpect(jsonPath("$.someField").value(DEFAULT_SOME_FIELD.toString()))
            .andExpect(jsonPath("$.deleted").value(DEFAULT_DELETED.booleanValue()));
    }
    @Test
    @Transactional
    public void getNonExistingChildEntity() throws Exception {
        // Get the childEntity
        restChildEntityMockMvc.perform(get("/api/child-entities/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateChildEntity() throws Exception {
        // Initialize the database
        childEntityRepository.saveAndFlush(childEntity);

        int databaseSizeBeforeUpdate = childEntityRepository.findAll().size();

        // Update the childEntity
        ChildEntity updatedChildEntity = childEntityRepository.findById(childEntity.getId()).get();
        // Disconnect from session so that the updates on updatedChildEntity are not directly saved in db
        em.detach(updatedChildEntity);
        updatedChildEntity
            .someField(UPDATED_SOME_FIELD)
            .deleted(UPDATED_DELETED);

        restChildEntityMockMvc.perform(put("/api/child-entities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedChildEntity)))
            .andExpect(status().isOk());

        // Validate the ChildEntity in the database
        List<ChildEntity> childEntityList = childEntityRepository.findAll();
        assertThat(childEntityList).hasSize(databaseSizeBeforeUpdate);
        ChildEntity testChildEntity = childEntityList.get(childEntityList.size() - 1);
        assertThat(testChildEntity.getSomeField()).isEqualTo(UPDATED_SOME_FIELD);
        assertThat(testChildEntity.isDeleted()).isEqualTo(UPDATED_DELETED);
    }

    @Test
    @Transactional
    public void updateNonExistingChildEntity() throws Exception {
        int databaseSizeBeforeUpdate = childEntityRepository.findAll().size();

        // Create the ChildEntity

        // If the entity doesn't have an ID, it will throw BadRequestAlertException 
        restChildEntityMockMvc.perform(put("/api/child-entities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(childEntity)))
            .andExpect(status().isBadRequest());

        // Validate the ChildEntity in the database
        List<ChildEntity> childEntityList = childEntityRepository.findAll();
        assertThat(childEntityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteChildEntity() throws Exception {
        // Initialize the database
        childEntityRepository.saveAndFlush(childEntity);

        int databaseSizeBeforeDelete = childEntityRepository.findAll().size();

        // Get the childEntity
        restChildEntityMockMvc.perform(delete("/api/child-entities/{id}", childEntity.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<ChildEntity> childEntityList = childEntityRepository.findAll();
        assertThat(childEntityList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ChildEntity.class);
        ChildEntity childEntity1 = new ChildEntity();
        childEntity1.setId(1L);
        ChildEntity childEntity2 = new ChildEntity();
        childEntity2.setId(childEntity1.getId());
        assertThat(childEntity1).isEqualTo(childEntity2);
        childEntity2.setId(2L);
        assertThat(childEntity1).isNotEqualTo(childEntity2);
        childEntity1.setId(null);
        assertThat(childEntity1).isNotEqualTo(childEntity2);
    }
}
