package BookmyBook.bmb.service;

import BookmyBook.bmb.domain.Book;
import BookmyBook.bmb.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    @Transactional
    public void saveItem(Book book){
        bookRepository.save(book);
    }

    @Transactional
    public void updateItem(Long itemId, String name, int price, int stockQuantity){

    }

    public List<Book> findItems(){
        return bookRepository.findAll();
    }

    public Book findOne(Long itemId){
        return bookRepository.findOne(itemId);
    }
}
