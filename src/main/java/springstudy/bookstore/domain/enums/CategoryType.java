package springstudy.bookstore.domain.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum CategoryType {
    BOOK("BOOK","책"),
    MUSIC("MUSIC", "음반"),
    STATIONERY("STATIONERY", "문구류");

    private String typeCode;
    private String categoryName;

    CategoryType(String code, String categoryName) {
        this.typeCode = code;
        this.categoryName = categoryName;
    }

    public static CategoryType enumOf(String typeCode) {
        //CategoryType의 Enum 상수들을 순회하며
        return Arrays.stream(CategoryType.values())
                //typeCode를 갖고 있는게 있는지 확인한다.
                .filter(t->t.getTypeCode().equals(typeCode))
                .findAny().orElse(null);
    }

}