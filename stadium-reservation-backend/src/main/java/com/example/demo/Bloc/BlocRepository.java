package com.example.demo.Bloc;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BlocRepository extends JpaRepository< Bloc , Long > {
    Bloc findByName( String name );

}
