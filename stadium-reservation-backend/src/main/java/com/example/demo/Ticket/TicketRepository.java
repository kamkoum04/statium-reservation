package com.example.demo.Ticket;

import com.example.demo.User.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByUtilisateur(Utilisateur utilisateur);
}
