package BookmyBook.bmb.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Users")
@Getter @Setter
public class User{

    @Id  @GeneratedValue //@Id: primary key @GeneratedValue : 값 자동 생성
    private Long id; //사용자 고유 ID

    @Column(unique = true, length = 10)
    @NotEmpty
    private String user_id; //사용자 ID

    @Column(unique = true, length = 10)
    @NotEmpty
    private String nickname; //사용자 이름

    @Embedded
    private Address address; //비밀번호

    @OneToMany(mappedBy = "user")
    private List<Order> orders = new ArrayList<>();

}