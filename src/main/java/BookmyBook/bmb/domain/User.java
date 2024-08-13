package BookmyBook.bmb.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "Users")
@Getter @Setter
public class User{

    @Id  @GeneratedValue //@Id: primary key @GeneratedValue : 값 자동 생성
    private Long id; //사용자 고유 ID

    @Column(unique = true, length = 10)
    @NotNull
    private String user_id; //사용자 ID

    @Column(unique = true, length = 10)
    @NotNull
    private String nickname; //사용자 이름

    @Column(length = 15)
    @NotNull
    private String password; //비밀번호

    @Column(updatable = false)
    @NotNull
    private LocalDateTime created_at; //가입 일시

    @Enumerated(EnumType.STRING)
    private UserRole role;//사용자 역할

    @PrePersist
    protected void onCreate(){
        this.created_at = LocalDateTime.now();
    }

    /*@Embedded
    private Address address;   */

   /* @OneToMany(mappedBy = "user")
    private List<Order> orders = new ArrayList<>();*/
}