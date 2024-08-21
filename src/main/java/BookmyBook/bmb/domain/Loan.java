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

    @Id @GeneratedValue
    private Long id; //대여 기록 고유 ID

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "book_id")
    private Book book; //도서 ID

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user; //사용자 ID

    @Column(nullable = false)
    private LocalDateTime loan_at; //대여 일시

    private LocalDateTime return_at; //반납 일시
}
