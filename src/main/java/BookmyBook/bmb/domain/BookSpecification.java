package BookmyBook.bmb.domain;

import org.springframework.data.jpa.domain.Specification;

public class BookSpecification {

    public static Specification<Book> byCategoryAndKeyword(String category, String keyword){
        return (root, query, criteriaBuilder) -> {
            if (keyword == null || category == null) {
                return criteriaBuilder.conjunction(); // 키워드가 없으면 모든 결과를 반환
            }

            // 대소문자 구분 없이 keyword를 검색
            String lowerCaseKeyword = "%" + keyword.toLowerCase() + "%";

            if (category.equalsIgnoreCase("all")) {
                // 모든 필드에 대해 키워드를 검색
                return criteriaBuilder.or(
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), lowerCaseKeyword),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("author_name")), lowerCaseKeyword),
                        criteriaBuilder.like(criteriaBuilder.lower(root.get("publisher_name")), lowerCaseKeyword)
                );
            }

            return switch (category.toLowerCase()) {
                case "title" -> criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), lowerCaseKeyword);
                case "author" -> criteriaBuilder.like(criteriaBuilder.lower(root.get("author_name")), lowerCaseKeyword);
                case "publisher" -> criteriaBuilder.like(criteriaBuilder.lower(root.get("publisher_name")), lowerCaseKeyword);
                default -> criteriaBuilder.conjunction(); //기본값
            };
        };
    }
}
