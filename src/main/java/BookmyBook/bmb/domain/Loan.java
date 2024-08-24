package BookmyBook.bmb.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.*;

@Entity
@Table(name = "Loan")
@Getter @Setter
public class Loan {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //대여 기록 고유 ID

    @Column(name = "book_isbn", length = 13)
    private String isbn; //외래 키로 사용할 도서 isbn

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "book_isbn", referencedColumnName = "isbn", insertable = false, updatable = false)
    private Book book; //도서 isbn

    @Column(name = "user_id", length = 10)
    private String userId; // 외래 키로 사용할 사용자 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private User user;

    @Column(name = "loan_at", nullable = false)
    private LocalDateTime loan_at; //대여 일시

    @Column(name = "return_at")
    private LocalDateTime returnAt; //반납 일시, db 칼럼을 찾지 못해서 이름 변경
}
