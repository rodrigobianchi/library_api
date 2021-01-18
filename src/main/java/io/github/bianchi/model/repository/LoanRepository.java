package io.github.bianchi.model.repository;

import io.github.bianchi.model.entity.Book;
import io.github.bianchi.model.entity.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    @Query(value = " select case when ( count (l.id) > 0 ) then true else false end " +
            "from Loan l where l.book =:book and ( l.returned is null or l.returned is false )")
    boolean existsByBookNotReturned(@Param("book") Book book);

    @Query(value = " select l from Loan as l join l.book as b where ( b.isbn =:isbn ) or ( l.customer =:customer )")
    Page<Loan> findByIsbnOrCustomer(@Param("isbn") String isbn, @Param("customer") String customer, Pageable pageable);

    /*
    Como foi mapeado o OneToMany na entidade Book, o JPA consegue fazer o vínculo e recuperar  os objetos
     */
    Page<Loan> findByBook(Book book, Pageable pageable);

    @Query(value = "select l from Loan l where l.loanDate <= :threeDaysAgo " +
            "and ( l.returned is null or l.returned is false )")
    List<Loan> findByLoanDateLessThanAndNotReturned(@Param("threeDaysAgo") LocalDate threeDaysAgo);
}
