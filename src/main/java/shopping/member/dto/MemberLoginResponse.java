package shopping.member.dto;

import shopping.member.entity.Member;

public record MemberLoginResponse(
        String email
) {
    public static MemberLoginResponse from(Member member){
        return new MemberLoginResponse(member.getEmail());
    }
}
