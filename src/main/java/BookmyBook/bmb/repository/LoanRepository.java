package BookmyBook.bmb.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class LoanRepository {

   /* private final EntityManager em;

    public void save(Wish wish){
        em.persist(wish);
    }

    public Wish findOne(Long id){
        return em.find(Wish.class, id);
    }

    *//**
     * JPQL 처리
     * JPQL 쿼리를 문자로 생성하기는 번거롭고, 실수로 인한 버그가 충분히 발생할 수 있다.
     *//*
    public List<Wish> findAllByString(OrderSearch orderSearch) {
        //language=JPAQL
        String jpql = "select o From Order o join o.users u";
        boolean isFirstCondition = true;
        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " o.status = :status";
        }
        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getUserName())) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " u.name like :name";
        }
        TypedQuery<Wish> query = em.createQuery(jpql, Wish.class)
                .setMaxResults(1000); //최대 1000건
        if (orderSearch.getOrderStatus() != null) {
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }
        if (StringUtils.hasText(orderSearch.getUserName())) { query = query.setParameter("name", orderSearch.getUserName());
        }
        return query.getResultList();
    }

    *//**
     * JPA Criteria
     * JPA Criteria는 JPA 표준 스펙이지만 실무에서 사용하기에 너무 복잡하다.
     * 대안 : Querydsl
     *//*
    public List<Wish> findAllByCriteria(OrderSearch orderSearch) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Wish> cq = cb.createQuery(Wish.class);
        Root<Wish> o = cq.from(Wish.class);
        Join<Wish, User> m = o.join("users", JoinType.INNER); //회원과 조인
        List<Predicate> criteria = new ArrayList<>();
        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            Predicate status = cb.equal(o.get("status"),
                    orderSearch.getOrderStatus());
            criteria.add(status);
        }
        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getUserName())) {
            Predicate name =
                    cb.like(m.<String>get("name"), "%" + orderSearch.getUserName()
                            + "%");
            criteria.add(name);
        }
        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Wish> query = em.createQuery(cq).setMaxResults(1000); //최대 1000건
        return query.getResultList();
    }*/
}