package gle.carpoolspring.repository;

import gle.carpoolspring.model.Waypoint;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WaypointRepository extends JpaRepository<Waypoint, Integer> {
}
