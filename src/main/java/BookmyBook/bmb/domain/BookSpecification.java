package BookmyBook.bmb.domain;

import org.springframework.data.jpa.domain.Specification;

public class BookSpecification {

    public static Specification<Book> byCategoryAndKeyword(String category, String keyword){
        return (root, _, criteriaBuilder) -> {
            if(keyword == null || category == null){
                return criteriaBuilder.conjunction(); //조건이 없으면 모든 결과를 반환
            }

            return switch (category.toLowerCase()) {
                case "title" -> criteriaBuilder.like(root.get("title"), "%" + keyword + "%");
                case "author" -> criteriaBuilder.like(root.get("author_name"), "%" + keyword + "%");
                case "publisher" -> criteriaBuilder.like(root.get("publisher_name"), "%" + keyword + "%");
                default -> criteriaBuilder.conjunction(); //기본값
            };
        };
    }
}
