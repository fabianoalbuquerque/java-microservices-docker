package br.com.bookservice.controllers;

import br.com.bookservice.model.Book;
import br.com.bookservice.proxy.CambioProxy;
import br.com.bookservice.proxy.repository.BookRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Book endpoint")
@RestController
@RequestMapping("/book-service")
public class BookController {

    @Autowired
    private Environment environment;

    @Autowired
    private BookRepository repository;

    @Autowired
    private CambioProxy proxy;

    @Operation(summary = "Find especific book by your id")
    @GetMapping(value = "{id}/{currency}")
    public Book findBook(@PathVariable("id") long id, @PathVariable("currency") String currency) {

        var book = repository.getById(id);
        if(book == null) {
            throw new RuntimeException("Book Not found");
        }

        var cambio = proxy.getCambio(book.getPrice(), "USD", currency);

        var port = environment.getProperty("local.server.port");
        book.setEnvironment("Book port " + port + " Cambio port " + cambio.getEnvironment());
        book.setPrice(cambio.getConvertedValue());
        return book;
    }
}
