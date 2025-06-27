package com.example.demo.repository;

import com.example.demo.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepo extends JpaRepository<Invoice, Long> {
    
    @Query("SELECT i FROM Invoice i WHERE i.customer.id = :customerId")
    List<Invoice> findByCustomerId(@Param("customerId") Long customerId);
    
    @Query("SELECT i FROM Invoice i WHERE i.user.id = :userId")
    List<Invoice> findByUserId(@Param("userId") Long userId);
    
    @Query("SELECT i FROM Invoice i WHERE i.quotation.id = :quatId")
    Optional<Invoice> findByQuotationId(@Param("quatId") Long quatId);
    
    boolean existsByInvoiceNo(String invoiceNo);
    
    @Query("SELECT i FROM Invoice i WHERE i.status = :status")
    List<Invoice> findByStatus(@Param("status") Invoice.InvoiceStatus status);
}