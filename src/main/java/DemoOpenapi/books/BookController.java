package DemoOpenapi.books;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class BookController {
    @Autowired
    BookRepository bookRepository;

    @GetMapping("/books.json")
    public List<Book> getBooks() {
        return bookRepository.findAll();
    }

    @PostMapping("/books.json")
    public Book createBook(@RequestBody Book book) {
        return bookRepository.save(book);
    }

    @PutMapping("/books/{id}.json")
    public Book updateBook(@RequestBody Book book, @PathVariable("id") Integer id) {
        book.setId(id);
        return bookRepository.save(book);
    }

    @GetMapping("/books/{id}.json")
    public Book getBook(@PathVariable("id") Integer id) throws Exception {
        return bookRepository.findById(id).orElseThrow(() -> new Exception("book have " + id + " not found"));
    }

    @DeleteMapping("/books/{id}.json")
    public void deleteBook(@PathVariable("id") Integer id) {
        bookRepository.deleteById(id);
    }

}
