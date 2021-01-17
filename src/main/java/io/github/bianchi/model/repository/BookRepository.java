package io.github.bianchi.model.repository;

import io.github.bianchi.model.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {

    //Métodos com o prefixo "existsBY" + "atributo da classe"
    //O Spring JPA implementa em tempo de runtime a implementação do método SQL
    boolean existsByIsbn(String isbn);

    Optional<Book> findByIsbn(String isbn);

}
