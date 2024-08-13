package BookmyBook.bmb.service;

import BookmyBook.bmb.domain.Address;
import BookmyBook.bmb.domain.Order;
import BookmyBook.bmb.domain.OrderStatus;
import BookmyBook.bmb.domain.User;
import BookmyBook.bmb.domain.item.Book;
import BookmyBook.bmb.domain.item.Item;
import BookmyBook.bmb.exception.NotEnouchStockException;
import BookmyBook.bmb.repository.OrderRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired EntityManager em;
    @Autowired OrderService orderService;
    @Autowired OrderRepository orderRepository;

    @Test
    public void 상품주문() throws Exception {
        //given
        User user = createUser();

        Book book = createBook("시골 JPA", 10000, 10);

        int orderCount = 2;

        //when
        Long orderId = orderService.order(user.getId(), book.getId(), orderCount);

        //then
        Order getOrder = orderRepository.findOne(orderId);

        assertThat(OrderStatus.ORDER).as("상품 주문시 상태는 ORDER").isEqualTo(getOrder.getStatus());
        assertThat(1).as("주문한 상품 종류 수가 정확해야 한다.").isEqualTo(getOrder.getOrderItems().size());
        assertThat(10000 * 2).as("주문한 가격은 가격 * 수량이다.").isEqualTo(getOrder.getTotalPrice());
        assertThat(8).as("주문 수량만큼 재고가 줄어야 한다").isEqualTo(book.getStockQuantity());
    }

    @Test()
    public void 상품주문_재고수량초과() throws Exception {
        //given
        User user = createUser();
        Item item = createBook("시골 JPA", 10000, 10);

        int orderCount = 11;

        //when

        //then
        assertThrows(NotEnouchStockException.class,
                () -> orderService.order(user.getId(), item.getId(), orderCount), "재고 수량 부족 예외가 발생해야 한다.");
    }
    
    @Test
    public void 주문취소() throws Exception {
        //given
        User user = createUser();
        Book item = createBook("시골 JPA", 10000, 10);

        int orderCount = 2;

        Long orderId = orderService.order(user.getId(), item.getId(), orderCount);

        //when
        orderService.cancelOrder(orderId);

        //then
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals(OrderStatus.CANCEL, getOrder.getStatus(), "주문 취소시 상태는 CANCEL 이다.");
        assertEquals(10, item.getStockQuantity(), "주문이 취소된 상품은 그만큼 재고가 증가해야 한다.");
    }

    private Book createBook(String name, int price, int stockQuantity) {
        Book book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        em.persist(book);
        return book;
    }

    private User createUser() {
        User user = new User();
        user.setNickname("회원1");
        user.setAddress(new Address("서울", "강가", "123-123"));
        em.persist(user);
        return user;
    }
}