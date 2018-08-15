package com.mycompany.myapp.domain;

import org.hibernate.annotations.Where;

import javax.persistence.MappedSuperclass;
import java.io.Serializable;

/**
 * @author Anthony Richir
 */
@MappedSuperclass
@Where(clause = "deleted=0")
public class AbstractParent implements Serializable {
}
