package gle.carpoolspring.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ADMIN")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Admin extends User {

}
