package shopping.member.dto;

import shopping.member.entity.Member;

public record MemberRegisterResponse(String email) {
    public static MemberRegisterResponse from(Member member){
        return new MemberRegisterResponse(member.getEmail());
    }
}
