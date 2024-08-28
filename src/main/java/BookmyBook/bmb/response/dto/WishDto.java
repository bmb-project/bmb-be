package BookmyBook.bmb.response.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class WishDto {

    private Long wish_count;
    private boolean wished;

    public WishDto(Long wish_count, boolean wished) {
        this.wish_count = wish_count;
        this.wished = wished;
    }
}
