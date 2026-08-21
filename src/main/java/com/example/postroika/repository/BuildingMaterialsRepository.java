package com.example.postroika.repository;

import com.example.postroika.entity.BuildingMaterials;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BuildingMaterialsRepository extends JpaRepository<BuildingMaterials, Long> {
}
