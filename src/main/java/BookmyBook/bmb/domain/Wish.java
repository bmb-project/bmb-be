package BookmyBook.bmb.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import static jakarta.persistence.FetchType.*;

@Entity
@Table(name = "Wish")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Wish {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //좋아요 기록 고유 ID

    @Column(name = "user_id", length = 10)
    private String userId; // 외래 키로 사용할 사용자 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private User user;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "book_id")
    private Book book; //도서 ID

    @Column(nullable = false)
    private LocalDateTime added_at; //추가 일시
}