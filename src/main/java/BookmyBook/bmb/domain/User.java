package BookmyBook.bmb.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Users")
@Getter @Setter
public class User{

    @Id  @GeneratedValue(strategy = GenerationType.IDENTITY) //@Id: primary key @GeneratedValue : 값 자동 생성
    private Long id; //사용자 고유 ID

    @Column(name = "user_id", unique = true, length = 10, nullable = false)
    private String user_id; //사용자 ID

    @Column(unique = true, length = 10, nullable = false)
    private String nickname; //사용자 이름

    @Column(length = 60, nullable = false)
    private String password; //비밀번호

    @Column(updatable = false, nullable = false)
    private LocalDateTime created_at; //가입 일시

    @Enumerated(EnumType.STRING)
    private UserRole role;//사용자 역할

    @PrePersist
    protected void onCreate(){
        this.created_at = LocalDateTime.now();
    }

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Wish> wishes = new ArrayList<>(); // User가 가진 Wish 목록
}