    package BookmyBook.bmb.domain;

    import jakarta.persistence.*;
    import lombok.Getter;
    import lombok.Setter;

    import java.time.LocalDate;
    import java.time.LocalDateTime;

    @Entity
    @Table(name = "Book")
    @Getter @Setter
    public class Book {

        @Id
        @GeneratedValue
        private Long id; //도서 고유 ID

        @Column(unique = true, length = 13, nullable = false)
        private String isbn; //ISBN 번호

        @Column(nullable = false)
        private String title; //도서 제목

        @Column(nullable = false)
        private String description; //도서 설명

        @Column(nullable = false)
        private String thumbnail; //도서 표지 썸네일 URL

        @Column(nullable = false)
        private String author_name; //저자 이름

        @Column(nullable = false)
        private String publisher_name; //출판사 이름

        @Column(nullable = false)
        private LocalDate published_date; //출판 날짜

        @Column(updatable = false, nullable = false)
        private LocalDateTime created_at; //등록 일시

        @Enumerated(EnumType.STRING)
        private BookStatus status; //도서 대여 상태

        @PrePersist
        protected void onCreate(){
            this.created_at = LocalDateTime.now();
        }
    }
