package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.ExampleWhereAnnotationApp;

import com.mycompany.myapp.domain.MyLovelyEntity;
import com.mycompany.myapp.repository.MyLovelyEntityRepository;
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
 * Test class for the MyLovelyEntityResource REST controller.
 *
 * @see MyLovelyEntityResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ExampleWhereAnnotationApp.class)
public class MyLovelyEntityResourceIntTest {

    private static final String DEFAULT_SOME_FIELD = "AAAAAAAAAA";
    private static final String UPDATED_SOME_FIELD = "BBBBBBBBBB";

    private static final Boolean DEFAULT_DELETED = false;
    private static final Boolean UPDATED_DELETED = true;

    @Autowired
    private MyLovelyEntityRepository myLovelyEntityRepository;


    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restMyLovelyEntityMockMvc;

    private MyLovelyEntity myLovelyEntity;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final MyLovelyEntityResource myLovelyEntityResource = new MyLovelyEntityResource(myLovelyEntityRepository);
        this.restMyLovelyEntityMockMvc = MockMvcBuilders.standaloneSetup(myLovelyEntityResource)
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
    public static MyLovelyEntity createEntity(EntityManager em) {
        MyLovelyEntity myLovelyEntity = new MyLovelyEntity()
            .someField(DEFAULT_SOME_FIELD)
            .deleted(DEFAULT_DELETED);
        return myLovelyEntity;
    }

    @Before
    public void initTest() {
        myLovelyEntity = createEntity(em);
    }

    @Test
    @Transactional
    public void createMyLovelyEntity() throws Exception {
        int databaseSizeBeforeCreate = myLovelyEntityRepository.findAll().size();

        // Create the MyLovelyEntity
        restMyLovelyEntityMockMvc.perform(post("/api/my-lovely-entities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(myLovelyEntity)))
            .andExpect(status().isCreated());

        // Validate the MyLovelyEntity in the database
        List<MyLovelyEntity> myLovelyEntityList = myLovelyEntityRepository.findAll();
        assertThat(myLovelyEntityList).hasSize(databaseSizeBeforeCreate + 1);
        MyLovelyEntity testMyLovelyEntity = myLovelyEntityList.get(myLovelyEntityList.size() - 1);
        assertThat(testMyLovelyEntity.getSomeField()).isEqualTo(DEFAULT_SOME_FIELD);
        assertThat(testMyLovelyEntity.isDeleted()).isEqualTo(DEFAULT_DELETED);
    }

    @Test
    @Transactional
    public void createMyLovelyEntityWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = myLovelyEntityRepository.findAll().size();

        // Create the MyLovelyEntity with an existing ID
        myLovelyEntity.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restMyLovelyEntityMockMvc.perform(post("/api/my-lovely-entities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(myLovelyEntity)))
            .andExpect(status().isBadRequest());

        // Validate the MyLovelyEntity in the database
        List<MyLovelyEntity> myLovelyEntityList = myLovelyEntityRepository.findAll();
        assertThat(myLovelyEntityList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllMyLovelyEntities() throws Exception {
        // Initialize the database
        myLovelyEntityRepository.saveAndFlush(myLovelyEntity);

        // Get all the myLovelyEntityList
        restMyLovelyEntityMockMvc.perform(get("/api/my-lovely-entities?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(myLovelyEntity.getId().intValue())))
            .andExpect(jsonPath("$.[*].someField").value(hasItem(DEFAULT_SOME_FIELD.toString())))
            .andExpect(jsonPath("$.[*].deleted").value(hasItem(DEFAULT_DELETED.booleanValue())));
    }
    

    @Test
    @Transactional
    public void getMyLovelyEntity() throws Exception {
        // Initialize the database
        myLovelyEntityRepository.saveAndFlush(myLovelyEntity);

        // Get the myLovelyEntity
        restMyLovelyEntityMockMvc.perform(get("/api/my-lovely-entities/{id}", myLovelyEntity.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(myLovelyEntity.getId().intValue()))
            .andExpect(jsonPath("$.someField").value(DEFAULT_SOME_FIELD.toString()))
            .andExpect(jsonPath("$.deleted").value(DEFAULT_DELETED.booleanValue()));
    }
    @Test
    @Transactional
    public void getNonExistingMyLovelyEntity() throws Exception {
        // Get the myLovelyEntity
        restMyLovelyEntityMockMvc.perform(get("/api/my-lovely-entities/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateMyLovelyEntity() throws Exception {
        // Initialize the database
        myLovelyEntityRepository.saveAndFlush(myLovelyEntity);

        int databaseSizeBeforeUpdate = myLovelyEntityRepository.findAll().size();

        // Update the myLovelyEntity
        MyLovelyEntity updatedMyLovelyEntity = myLovelyEntityRepository.findById(myLovelyEntity.getId()).get();
        // Disconnect from session so that the updates on updatedMyLovelyEntity are not directly saved in db
        em.detach(updatedMyLovelyEntity);
        updatedMyLovelyEntity
            .someField(UPDATED_SOME_FIELD)
            .deleted(UPDATED_DELETED);

        restMyLovelyEntityMockMvc.perform(put("/api/my-lovely-entities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedMyLovelyEntity)))
            .andExpect(status().isOk());

        // Validate the MyLovelyEntity in the database
        List<MyLovelyEntity> myLovelyEntityList = myLovelyEntityRepository.findAll();
        assertThat(myLovelyEntityList).hasSize(databaseSizeBeforeUpdate);
        MyLovelyEntity testMyLovelyEntity = myLovelyEntityList.get(myLovelyEntityList.size() - 1);
        assertThat(testMyLovelyEntity.getSomeField()).isEqualTo(UPDATED_SOME_FIELD);
        assertThat(testMyLovelyEntity.isDeleted()).isEqualTo(UPDATED_DELETED);
    }

    @Test
    @Transactional
    public void updateNonExistingMyLovelyEntity() throws Exception {
        int databaseSizeBeforeUpdate = myLovelyEntityRepository.findAll().size();

        // Create the MyLovelyEntity

        // If the entity doesn't have an ID, it will throw BadRequestAlertException 
        restMyLovelyEntityMockMvc.perform(put("/api/my-lovely-entities")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(myLovelyEntity)))
            .andExpect(status().isBadRequest());

        // Validate the MyLovelyEntity in the database
        List<MyLovelyEntity> myLovelyEntityList = myLovelyEntityRepository.findAll();
        assertThat(myLovelyEntityList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteMyLovelyEntity() throws Exception {
        // Initialize the database
        myLovelyEntityRepository.saveAndFlush(myLovelyEntity);

        int databaseSizeBeforeDelete = myLovelyEntityRepository.findAll().size();

        // Get the myLovelyEntity
        restMyLovelyEntityMockMvc.perform(delete("/api/my-lovely-entities/{id}", myLovelyEntity.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<MyLovelyEntity> myLovelyEntityList = myLovelyEntityRepository.findAll();
        assertThat(myLovelyEntityList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MyLovelyEntity.class);
        MyLovelyEntity myLovelyEntity1 = new MyLovelyEntity();
        myLovelyEntity1.setId(1L);
        MyLovelyEntity myLovelyEntity2 = new MyLovelyEntity();
        myLovelyEntity2.setId(myLovelyEntity1.getId());
        assertThat(myLovelyEntity1).isEqualTo(myLovelyEntity2);
        myLovelyEntity2.setId(2L);
        assertThat(myLovelyEntity1).isNotEqualTo(myLovelyEntity2);
        myLovelyEntity1.setId(null);
        assertThat(myLovelyEntity1).isNotEqualTo(myLovelyEntity2);
    }
}
