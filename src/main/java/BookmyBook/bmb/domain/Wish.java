package BookmyBook.bmb.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.FetchType.*;

@Entity
@Table(name = "Wish")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Wish {

    @Id @GeneratedValue
    private Long id; //좋아요 기록 고유 ID

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user; //사용자 ID

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "book_id")
    private Book book; //도서 ID

    @Column(nullable = false)
    private LocalDateTime added_at; //추가 일시





}