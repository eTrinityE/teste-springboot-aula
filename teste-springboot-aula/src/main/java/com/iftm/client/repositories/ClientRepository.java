package com.iftm.client.repositories;

import com.iftm.client.entities.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    @Query(value = "SELECT c FROM Client c WHERE c.id = :id")
    public Optional<Client> findById(Long id);

    @Query(value = "SELECT c FROM Client c WHERE LOWER(c.name) = LOWER(:name)")
    public Optional<Client> findByName(String name);

    @Query(value = "SELECT c FROM Client c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    public List<Client> findByNameContains(String name);

    @Query(value = "SELECT c FROM Client c WHERE c.income > :value")
    public List<Client> findByIncomeBiggerThan(Double value);

    Page<Client> findByIncomeGreaterThan(double salarioI, Pageable pageable);

    @Query(value = "SELECT c FROM Client c WHERE c.income < :value")
    public List<Client> findByIncomeSmallerThan(Double value);
    @Query(value = "SELECT c FROM Client c WHERE c.income BETWEEN :value and :secondValue")
    public List<Client> findByIncomeInterval(Double value, Double secondValue);

    public List<Client> findClientBybirthDateBetween(Instant dataInicio, Instant dataTermino);


}