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
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the ChildEntityResource REST controller.
 *
 * @see ChildEntityResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ExampleWhereAnnotationApp.class)
public class WhereAnnotationOnParentTest {

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
    public void getAllChildEntities() throws Exception {
        // Initialize the database
        childEntityRepository.saveAndFlush(childEntity);

        ChildEntity otherEntity = createEntity(em)
            .deleted(true);
        childEntityRepository.saveAndFlush(otherEntity);

        // Get all the childEntityList
        restChildEntityMockMvc.perform(get("/api/child-entities?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$.[*].id").value(hasItem(childEntity.getId().intValue())))
            .andExpect(jsonPath("$.[*].someField").value(hasItem(DEFAULT_SOME_FIELD.toString())))
            .andExpect(jsonPath("$.[*].deleted").value(hasItem(DEFAULT_DELETED.booleanValue())));
    }

}
