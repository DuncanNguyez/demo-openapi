package DemoOpenapi.books;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;

@Entity
@AllArgsConstructor
public class Book {
    @Id
    @GeneratedValue
    private Integer id;

    private String name;

    private String author;

}
