package BookmyBook.bmb.service;

import BookmyBook.bmb.repository.BookRepository;
import BookmyBook.bmb.repository.UserRepository;
import BookmyBook.bmb.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    /**
     * 주문
     */
 /*   @Transactional
    public Long order(Long userId, Long itemId, int count){

        //엔티티 조회
        User user = userRepository.findOne(userId);
        Book book = bookRepository.findOne(itemId);

        //배송정보 생성
        Delivery delivery = new Delivery();
       // delivery.setAddress(user.getAddress());
        delivery.setStatus(DeliveryStatus.READY);

        //주문상품 생성
        //OrderItem orderItem = OrderItem.createOrderItem(book, book.getPrice(), count);

        //주문 생성
       // Order order = Order.createOrder(user, delivery, orderItem);

        //주문 저장
        //orderRepository.save(order);

        return order.getId();
    }*/

    /**
     * 취소
     */
  /*  @Transactional
    public void cancelOrder(Long orderId){
        //주문 엔티티 조회
        Wish wish = orderRepository.findOne(orderId);

        //주문 취소
        wish.cancel();
    }


    *//**
     * 검색
     *//*
    public List<Wish> findOrders(OrderSearch orderSearch){
        return orderRepository.findAllByString(orderSearch);
    }*/

}