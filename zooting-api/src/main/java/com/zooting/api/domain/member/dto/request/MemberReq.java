package com.zooting.api.domain.member.dto.request;

import com.zooting.api.domain.member.dto.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.List;

@Schema(description = "추가 정보 저장 요청 DTO")
public record MemberReq(
        @Schema(description = "닉네임")
        @NotNull
       String nickname,
        @Schema(description = "생년월일, yyyy-MM-dd")
        @Pattern(regexp = "^(19|20)\\d{2}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[0-1])$")
        @NotNull
        String birth,
       @Schema(description = "주소")
       String address,
       @Schema(description = "성별")
        Gender gender,

       @Schema(description = "관심사 리스트")
       List<String> interest,
       @Schema(description = "이상형 동물 리스트")
       List<String> idealAnimal
) {
}
