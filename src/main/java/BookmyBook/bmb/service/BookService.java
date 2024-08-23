package BookmyBook.bmb.service;

import BookmyBook.bmb.domain.Book;
import BookmyBook.bmb.domain.BookSpecification;
import BookmyBook.bmb.repository.BookRepository;
import BookmyBook.bmb.repository.WishRepository;
import BookmyBook.bmb.response.BookResponse;
import BookmyBook.bmb.response.dto.BookDto;
import BookmyBook.bmb.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final WishRepository wishRepository;
    private final JwtUtil jwtUtil;

    @Transactional
    public void saveItem(Book book){
        bookRepository.save(book);
    }

    //도서 목록 조회
    public BookResponse getBooks(int page, int size, String category, String keyword, String token){
        //페이징 요청에 따른 페이징 처리
        Pageable pageable = PageRequest.of(page - 1, size);

        //검색 조건 설정
        Specification<Book> spec = BookSpecification.byCategoryAndKeyword(category, keyword);

        String user_id = jwtUtil.getUserId(token);

        //도서 목록 조회
        Page<Book> bookPage = bookRepository.findAll(spec, pageable);
        List<Book> books = bookPage.getContent();

        // 도서 ID 리스트 가져오기
        List<Long> bookIds = books.stream()
                .map(Book::getId)
                .collect(Collectors.toList());

        // 찜 수를 가져오기
        List<Object[]> wishCounts = wishRepository.countWishesByBookIds(bookIds);
        Map<Long, Long> wishCountMap = wishCounts.stream()
                .collect(Collectors.toMap(row -> (Long) row[0], row -> (Long) row[1]));

        // BookDto로 변환
        List<BookDto> bookDtos = books.stream().map(book -> {
            Long wishCount = wishCountMap.getOrDefault(book.getId(), 0L);
            boolean wished = user_id != null && wishRepository.existsByBookIdAndUserId(book.getId(), user_id);
            return new BookDto(
                    book.getIsbn(),
                    book.getTitle(),
                    book.getThumbnail(),
                    book.getAuthor_name(),
                    book.getPublisher_name(),
                    book.getStatus(),
                    wishCount,
                    wished
            );
        }).collect(Collectors.toList());


        // 응답 객체 생성
        BookResponse response = new BookResponse();
        response.setTotalPages(bookPage.getTotalPages());
        response.setCurrentPage(pageable.getPageNumber() + 1);
        response.setPageSize(pageable.getPageSize());
        response.setTotalItems(bookPage.getTotalElements());
        response.setCategory(category);
        response.setKeyword(keyword);
        response.setBooks(bookDtos);

        return response;
    }
}
