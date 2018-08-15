package com.mycompany.myapp.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import java.io.Serializable;
import java.util.Objects;

/**
 * A ChildEntity.
 */
@Entity
@Table(name = "child_entity")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ChildEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "some_field")
    private String someField;

    @Column(name = "deleted")
    private Boolean deleted;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSomeField() {
        return someField;
    }

    public ChildEntity someField(String someField) {
        this.someField = someField;
        return this;
    }

    public void setSomeField(String someField) {
        this.someField = someField;
    }

    public Boolean isDeleted() {
        return deleted;
    }

    public ChildEntity deleted(Boolean deleted) {
        this.deleted = deleted;
        return this;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChildEntity childEntity = (ChildEntity) o;
        if (childEntity.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), childEntity.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "ChildEntity{" +
            "id=" + getId() +
            ", someField='" + getSomeField() + "'" +
            ", deleted='" + isDeleted() + "'" +
            "}";
    }
}
